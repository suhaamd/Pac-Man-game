package test.java.pacman.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import pacman.controllers.Controller;
import pacman.controllers.agents.ValueIterationAgent_template;
import pacman.controllers.examples.NullGhosts;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;


import java.lang.reflect.Field;
import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(2)
@TestMethodOrder(OrderAnnotation.class)
class ValueIterationAgentTest {

    private static final int GAMES = 3;

    private final Controller<EnumMap<GHOST, MOVE>> ghosts = new NullGhosts();

    @Test
    @Order(1)
    @DisplayName("ValueIterationAgent smoke check vs NullGhosts (>10)")
    void valueIterationBasicSanity() {
        Controller<MOVE> trained = new ValueIterationAgent_template();
        int iterations = getIntField(trained, "iterations");

        System.out.printf("[ValueIteration#1] Worth 3/20 points in coursework 2.%n");
        System.out.printf("[ValueIteration#1] Setup: ValueIterationAgent vs NullGhosts (%d games, max %d iterations, <=%ds).%n",
                GAMES, GameTestUtils.maxSteps(), GameTestUtils.maxDurationSeconds());
        System.out.printf("[ValueIteration#1] Pass condition: average score > 10.%n");
        System.out.printf("[ValueIteration#1] Planning iterations used: %d%n", iterations);

        double average = GameTestUtils.averageScore("[ValueIteration#1]", trained, ghosts, GAMES);
        boolean pass = average > 10.0;
        System.out.printf("[ValueIteration#1] Result: %s (average %.2f)%n", pass ? "PASS" : "FAIL", average);
        TestProgressTracker.record("ValueIteration#1", pass, 3.0);
        TestProgressTracker.singleDivider();
        assertTrue(pass, "[ValueIteration#1] Expected average score > 10 against NullGhosts.");
    }

    @Test
    @Order(2)
    @DisplayName("ValueIterationAgent achieves >=100 vs NullGhosts")
    void valueIterationScoresAgainstNullGhosts() {
        Controller<MOVE> trained = new ValueIterationAgent_template();

        int iterations = getIntField(trained, "iterations");
        System.out.printf("[ValueIteration#2] Worth 3/20 points in coursework 2.%n");
        System.out.printf("[ValueIteration#2] Training iterations: %d%n", iterations);
        System.out.printf("[ValueIteration#2] Setup: ValueIterationAgent vs NullGhosts (%d games, max %d iterations, <=%ds).%n",
                GAMES, GameTestUtils.maxSteps(), GameTestUtils.maxDurationSeconds());
        System.out.printf("[ValueIteration#2] Pass condition: average score >= 100.%n");

        double trainedAverage = GameTestUtils.averageScore("[ValueIteration#2]", trained, ghosts, GAMES);

        boolean pass = trainedAverage >= 100.0;
        System.out.printf("[ValueIteration#2] Result: %s (average %.2f)%n", pass ? "PASS" : "FAIL", trainedAverage);
        TestProgressTracker.record("ValueIteration#2", pass, 3.0);

        TestProgressTracker.singleDivider();
        System.out.println("[ValueIteration#2] Information only: ValueIterationAgent vs StarterGhosts (3 games, max "
                + GameTestUtils.maxSteps() + " iterations, <=" + GameTestUtils.maxDurationSeconds() + "s).");
        GameTestUtils.averageScore("[ValueIteration#2][Info]", trained, new StarterGhosts(), 3);
        TestProgressTracker.doubleDivider();

        assertTrue(pass,
                String.format("Expected ValueIterationAgent average to be at least 100 but was %.2f", trainedAverage));
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
}
