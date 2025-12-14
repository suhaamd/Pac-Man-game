package test.java.pacman.controllers.agents;
import pacman.controllers.agents.ValueIterationAgent_template;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import pacman.Executor;
import pacman.controllers.examples.StarterGhosts;

/**
 * Simple smoke test: run one timed game and check it completes with a non-negative score.
 */
@Disabled("Legacy template test – enable manually if you need it")
public class LegacyGameSmokeTest {

    @Test
    public void playsOneGameAndFinishes() {
    	double score=0;
    	Executor exec = new Executor();
        ValueIterationAgent_template agent = new  ValueIterationAgent_template();
        score = exec.runGameTimed(agent, new StarterGhosts(), false);
        assertTrue(score >= 0, "Score should be >= 0");
    }
}
