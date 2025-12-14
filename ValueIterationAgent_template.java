package pacman.controllers.agents;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.controllers.examples.NullGhosts;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ValueIterationAgent — template summary:
 * Initialize: generate your abstract state space, set V(s)=0, and set policy(s)=MOVE.NEUTRAL.
 * Repeat for a fixed number of sweeps:
 * 1. For each state, enumerate legal actions.
 * 2. For each action, evaluate the expectation of r + γ·V(s′) over all transitions returned by the model.
 * 3. Store the best value and action into fresh maps and replace the old ones at the end of the sweep.
 * Acting: map the live game to a GameState and return policy(state), falling back to MOVE.NEUTRAL when the state was never seen.
 */
public class ValueIterationAgent_template extends Controller<MOVE> {

    private final Map<GameState, Double> valueFunction = new HashMap<>();
    private final Map<GameState, MOVE> policy = new HashMap<>();
    private final double gamma = 0.9;
    private final int iterations = 20;
    private final List<GameState> states;

    public ValueIterationAgent_template() {
        // Use StateGenerator.getAllStates() to build the abstract state list, then initialize V(s) and run the iterative backups described above.
        this.states = StateGenerator.getAllStates();
        
     // Initialize V(s) = 0 and policy(s) = MOVE.NEUTRAL for all states
        for (GameState state : states) {
            valueFunction.put(state, 0.0);
            policy.put(state, MOVE.NEUTRAL);
        }
        
     // Run value iteration for fixed number of iterations
        Game dummyGame = new Game(0);
        
        for (int iter = 0; iter < iterations; iter++) {
        	Map<GameState, Double> newValueFunction = new HashMap<>();
        	Map<GameState, MOVE> newPolicy = new HashMap<>();
        	
        	for (GameState state : states) {
                List<MOVE> legalMoves = state.getLegalMoves();
                double maxValue = Double.NEGATIVE_INFINITY;
                MOVE bestAction = MOVE.NEUTRAL;
                
                for (MOVE action : legalMoves) {
                    double actionValue = 0.0;
                    
                    List<Transition> transitions = state.getTransitions(dummyGame, action);
                    
                    for (Transition transition : transitions) {
                        double reward = transition.reward;
                        double probability = transition.probability;
                        GameState nextState = transition.nextState;
                        
                        double nextValue = valueFunction.getOrDefault(nextState, 0.0);
                        actionValue += probability * (reward + gamma * nextValue);
                    }
                    
                    if (actionValue > maxValue) {
                        maxValue = actionValue;
                        bestAction = action;
                    }
                }
                
                newValueFunction.put(state, maxValue);
                newPolicy.put(state, bestAction);
            }
        	
        	// Replace old maps with new ones
            valueFunction.clear();
            valueFunction.putAll(newValueFunction);
            policy.clear();
            policy.putAll(newPolicy);
        }
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        // Map the current Game into a GameState and read the action from the policy map.
        //return MOVE.NEUTRAL;
    	
    	// Map the current Game into a GameState and read the action from the policy map.
        GameState currentState = GameState.fromGame(game);
        return policy.getOrDefault(currentState, MOVE.NEUTRAL);
    }
}
