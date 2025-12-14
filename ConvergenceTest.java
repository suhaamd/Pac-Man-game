package test.java.pacman.controllers.agents;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;

/**
 * Template convergence test for QLearningAgent. 
 * Skips if QLearningAgent class is not on the classpath.
 *
 * Expected QLearningAgent API:
 *  - public QLearningAgent() {}
 *  - public void train(int episodes)
 *  - implements Controller<MOVE>
 */

@Disabled("Legacy template test – enable manually if you need it")
public class ConvergenceTest {

    @SuppressWarnings("unchecked")
    @Test
    public void qlearningImprovesOrIsNonDecreasing() throws Exception {
    	
    	// 1) Find the agent class in the expected package
    	Class<?> clazz = null;
    	try {
    	    clazz = Class.forName("pacman.controllers.agents.QLearningAgent");
        } catch (ClassNotFoundException e) {
            System.out.println("QLearningAgent not found in pacman.controllers.agents: skipping.");
            assumeTrue(false, "QLearningAgent class not found");
            return;
        }

        Object agent = clazz.getDeclaredConstructor().newInstance();
        assumeTrue(Controller.class.isAssignableFrom(clazz), "Agent must implement Controller<MOVE>");

        // 2) Evaluate baseline
        double s1 = evaluateMeanScore((Controller<MOVE>)agent, 5);

        // 3) If a no-arg train() exists, invoke it once more to continue training
        try {
            clazz.getMethod("train").invoke(agent);
        } catch (NoSuchMethodException nsme) {
            // no-op: either training happened in constructor or not exposed
        }

        double s2 = evaluateMeanScore((Controller<MOVE>)agent, 5);

        System.out.printf("Mean scores before/after extra train(): %.1f / %.1f%n", s1, s2);

        // Allow noise: require non-decreasing trend with small tolerance
        assertTrue(s2 + 1 >= s1,
                "Score after additional training should not be worse by a large margin");
    }

    private double evaluateMeanScore(Controller<MOVE> agent, int games) {
        Executor exec = new Executor();
        pacman.controllers.examples.StarterGhosts ghosts = new pacman.controllers.examples.StarterGhosts();
        int total = 0;
        for (int i = 0; i < games; i++) {
            total += exec.runGameTimed(agent, ghosts, false);
        }
        return total / (double)games;
    }
}
