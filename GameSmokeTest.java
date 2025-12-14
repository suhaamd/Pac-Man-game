package test.java.pacman.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pacman.controllers.examples.NullGhosts;
import pacman.controllers.examples.RandomPacMan;
import pacman.controllers.examples.StarterGhosts;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Minimal smoke test to ensure the game can run to completion without errors.
 */
@Order(1)
class GameSmokeTest {

    @Test
    @DisplayName("RandomPacMan completes one timed game against StarterGhosts")
    void randomPacManCompletesTimedGame() {
        int maxSteps = GameTestUtils.maxSteps();
        int maxSeconds = GameTestUtils.maxDurationSeconds();

        System.out.println("[SMOKE] Ms Pac-Man framework readiness check (no marks associated to this test).");
        GameTestUtils.playTimedGame(new RandomPacMan(), new NullGhosts(), false);
        double starterScore = GameTestUtils.playTimedGame(new RandomPacMan(), new StarterGhosts(), false);
        boolean pass = starterScore >= 0.0;
        System.out.printf("[SMOKE] build %s%n", pass ? "success" : "failure");
        TestProgressTracker.doubleDivider();
        System.out.println("The following results worth 17/20 points. Submit your reports to canvas to gain the rest 3/20 points of coursework 2.");
        assertTrue(pass, "[SMOKE] Game loop should finish without a negative score");
    }
}
