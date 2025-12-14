package test.java.pacman.tests;

import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Helper methods used across integration-style gameplay tests.
 */
public final class GameTestUtils {
    private static final Random RANDOM = new Random(0L);
    private static final int MAX_STEPS = 2000;
    private static final int MAX_DURATION_SECONDS = 10;

    private GameTestUtils() {
    }

    /**
     * Executes a single timed game with the provided controllers.
     *
     * @param pacmanController controller for Pac-Man
     * @param ghostController  controller for the ghosts
     * @param visual           whether to show the game view (should be false in automated tests)
     * @return final game score
     */
    public static double playTimedGame(Controller<MOVE> pacmanController,
                                       Controller<EnumMap<GHOST, MOVE>> ghostController,
                                       boolean visual) {
        Game game = new Game(0);

        int steps = 0;
        while (!game.gameOver() && steps < MAX_STEPS) {
            MOVE pacMove = pacmanController.getMove(game.copy(), System.currentTimeMillis());
            EnumMap<GHOST, MOVE> ghostMoves = ghostController.getMove(game.copy(), System.currentTimeMillis());
            game.advanceGame(pacMove, ghostMoves);
            steps++;
        }

        pacmanController.terminate();
        ghostController.terminate();
        return game.getScore();
    }

    /**
     * Runs multiple timed games and returns the average score.
     *
     * @param pacmanController controller under evaluation
     * @param ghostController  opponent ghosts
     * @param games            number of games to play
     * @return average score across the games
     */
    public static double averageScore(Controller<MOVE> pacmanController,
                                      Controller<EnumMap<GHOST, MOVE>> ghostController,
                                      int games) {
        return averageScore("", pacmanController, ghostController, games);
    }

    public static double averageScore(String prefix,
                                      Controller<MOVE> pacmanController,
                                      Controller<EnumMap<GHOST, MOVE>> ghostController,
                                      int games) {
        double total = 0.0;
        for (int i = 0; i < games; i++) {
            double score = playTimedGame(pacmanController, ghostController, false);
            total += score;
            System.out.printf("%s  - Game %d/%d (max %d iterations, <=%ds): %.2f%n",
                    prefix, i + 1, games, MAX_STEPS, MAX_DURATION_SECONDS, score);
        }
        double average = total / games;
        System.out.printf("%s  > Average over %d game(s): %.2f%n", prefix, games, average);
        return average;
    }

    /**
     * Computes the largest absolute difference between consecutive elements of the provided scores.
     *
     * @param scores list of score samples
     * @return maximum absolute difference between consecutive entries, or 0 when fewer than two samples exist
     */
    public static double maxConsecutiveDifference(List<Double> scores) {
        Objects.requireNonNull(scores, "scores");
        if (scores.size() < 2) {
            return 0.0;
        }
        double maxDiff = 0.0;
        for (int i = 1; i < scores.size(); i++) {
            maxDiff = Math.max(maxDiff, Math.abs(scores.get(i) - scores.get(i - 1)));
        }
        return maxDiff;
    }

    /**
     * Returns a deterministic random seed for situations where explicit randomness is needed.
     *
     * @return next pseudo random long
     */
    public static long nextSeed() {
        return RANDOM.nextLong();
    }

    /**
     * Creates a copy of the provided game allowing further simulation in tests.
     *
     * @param game original game state
     * @return deep copy of the game
     */
    public static Game copyGame(Game game) {
        return game.copy();
    }

    public static int maxSteps() {
        return MAX_STEPS;
    }

    public static int maxDurationSeconds() {
        return MAX_DURATION_SECONDS;
    }
}
