package test.java.pacman.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import pacman.controllers.Controller;
import pacman.controllers.agents.QLearningAgent_template;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(4)
@TestMethodOrder(OrderAnnotation.class)
class QLearningAgentTest {

    private static final int GAMES = 5;

    @Test
    @Order(1)
    @DisplayName("QLearningAgent baseline vs StarterGhosts (5000 episodes)")
    void qLearningBaseline() {
        QLearningAgent_template agent = new QLearningAgent_template();
        double alpha = getDoubleField(agent, "alpha");
        double gamma = getDoubleField(agent, "gamma");
        double epsilon = getDoubleField(agent, "epsilon");
        int episodes = getIntField(agent, "episodes");

        System.out.printf("[QLearning#1] Worth 2/20 points in coursework 2.%n");
        System.out.printf("[QLearning#1] Parameters: episodes=%d, alpha=%.2f, gamma=%.2f, epsilon=%.2f%n",
                episodes, alpha, gamma, epsilon);
        System.out.printf("[QLearning#1] Setup: QLearningAgent vs StarterGhosts (%d games, max %d iterations, <=%ds).%n",
                GAMES, GameTestUtils.maxSteps(), GameTestUtils.maxDurationSeconds());
        System.out.printf("[QLearning#1] Pass condition: average score > 0.%n");

        double average = GameTestUtils.averageScore("[QLearning#1]", agent, new StarterGhosts(), GAMES);
        boolean pass = average > 0.0;
        System.out.printf("[QLearning#1] Result: %s (average %.2f)%n", pass ? "PASS" : "FAIL", average);
        TestProgressTracker.record("QLearning#1", pass, 2.0);
        TestProgressTracker.storeMetric("QL_BASELINE", average);
        TestProgressTracker.storeObject("QL_AGENT_BASELINE", agent);
        TestProgressTracker.singleDivider();
        assertTrue(pass, "[QLearning#1] Expected positive average score against StarterGhosts.");
    }

    @Test
    @Order(2)
    @DisplayName("QLearningAgent improved training vs StarterGhosts (10000 episodes)")
    void qLearningImprovesWithMoreTraining() {
        QLearningAgent_template agent = (QLearningAgent_template) TestProgressTracker.getObject("QL_AGENT_BASELINE");
        if (agent == null) {
            agent = new QLearningAgent_template();
        }
        double alpha = getDoubleField(agent, "alpha");
        double gamma = getDoubleField(agent, "gamma");
        double epsilon = getDoubleField(agent, "epsilon");
        int originalEpisodes = getIntField(agent, "episodes");
        int additionalEpisodes = originalEpisodes;
        int totalEpisodes = originalEpisodes + additionalEpisodes;

        System.out.printf("[QLearning#2] Worth 3/20 points in coursework 2.%n");
        System.out.printf("[QLearning#2] Parameters before extra training: episodes=%d, alpha=%.2f, gamma=%.2f, epsilon=%.2f%n",
                originalEpisodes, alpha, gamma, epsilon);
        System.out.printf("[QLearning#2] Additional training episodes: %d (total %d)%n",
                additionalEpisodes, totalEpisodes);

        double baseline = TestProgressTracker.getMetric("QL_BASELINE");
        System.out.printf("[QLearning#2] Baseline (from QLearning#1) average: %.2f%n", baseline);

        Map<Object, Double> baselineSnapshot = snapshotQTable(agent);
        double bestAverage = Double.NEGATIVE_INFINITY;
        double finalAverage = Double.NEGATIVE_INFINITY;
        int attempts = 5;
        boolean pass = false;

        for (int attempt = 1; attempt <= attempts; attempt++) {
            restoreQTable(agent, baselineSnapshot);
            System.out.printf("[QLearning#2] Attempt %d/%d: add %d episodes (total %d).%n",
                    attempt, attempts, additionalEpisodes, totalEpisodes);
            trainAdditionalEpisodes(agent, additionalEpisodes);
            System.out.printf("[QLearning#2] Setup: QLearningAgent vs StarterGhosts (%d games, max %d iterations, <=%ds).%n",
                    GAMES, GameTestUtils.maxSteps(), GameTestUtils.maxDurationSeconds());
            double attemptAverage = GameTestUtils.averageScore("[QLearning#2][Attempt " + attempt + "]",
                    agent, new StarterGhosts(), GAMES);
            if (attemptAverage > bestAverage) {
                bestAverage = attemptAverage;
            }
            if (!Double.isNaN(baseline) && attemptAverage > baseline) {
                pass = true;
                finalAverage = attemptAverage;
                System.out.printf("[QLearning#2] Improvement observed on attempt %d (%.2f > %.2f).%n",
                        attempt, attemptAverage, baseline);
                break;
            }
            System.out.printf("[QLearning#2] Attempt %d did not beat baseline %.2f.%n", attempt, baseline);
        }

        if (!pass) {
            finalAverage = bestAverage;
        }

        System.out.printf("[QLearning#2] Result: %s (best average %.2f vs baseline %.2f)%n",
                pass ? "PASS" : "FAIL", finalAverage, baseline);
        TestProgressTracker.record("QLearning#2", pass, 3.0);

        TestProgressTracker.singleDivider();
        System.out.printf("[QLearning#2] Information only: best observed average %.2f after %d total episodes (alpha=%.2f, gamma=%.2f, epsilon=%.2f).%n",
                finalAverage, totalEpisodes, alpha, gamma, epsilon);
        TestProgressTracker.doubleDivider();

        assertTrue(pass,
                String.format("Best observed average %.2f did not exceed baseline %.2f after %d total episodes.", finalAverage, baseline, totalEpisodes));
    }

    private void trainAdditionalEpisodes(QLearningAgent_template agent, int episodesPerCall) {
        int baseEpisodes = getIntField(agent, "episodes");
        int fullRuns = episodesPerCall / baseEpisodes;
        for (int i = 0; i < fullRuns; i++) {
            agent.train();
        }
        int remainder = episodesPerCall % baseEpisodes;
        if (remainder == 0) {
            return;
        }
        try {
            Field field = QLearningAgent_template.class.getDeclaredField("episodes");
            field.setAccessible(true);
            int original = field.getInt(agent);
            field.setInt(agent, remainder);
            agent.train();
            field.setInt(agent, original);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to adjust training episodes", e);
        }
    }

    private int getIntField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(target);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to read field '" + fieldName + "'", e);
        }
    }

    private double getDoubleField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getDouble(target);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to read field '" + fieldName + "'", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Object, Double> snapshotQTable(QLearningAgent_template agent) {
        try {
            Field field = QLearningAgent_template.class.getDeclaredField("qTable");
            field.setAccessible(true);
            Map<Object, Double> table = (Map<Object, Double>) field.get(agent);
            return new HashMap<>(table);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to access Q-table", e);
        }
    }

    private void restoreQTable(QLearningAgent_template agent, Map<Object, Double> snapshot) {
        try {
            Field field = QLearningAgent_template.class.getDeclaredField("qTable");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Object, Double> table = (Map<Object, Double>) field.get(agent);
            table.clear();
            table.putAll(snapshot);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to restore Q-table", e);
        }
    }
}
