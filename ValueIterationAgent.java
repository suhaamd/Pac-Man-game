package pacman.controllers.agents;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Value Iteration Agent for Ms. Pac-Man
 * 
 * As per specification:
 * - Estimate a value function V(s) for all GameStates
 * - Compute a greedy policy π(s) based on these values
 * - Use a fixed number of iterations (20)
 * - Use Game.copy() method to safely simulate hypothetical moves
 * - Rewards are granted on entry to a state
 */
public class ValueIterationAgent extends Controller<MOVE> {

    private final Map<GameState, Double> valueFunction = new HashMap<>(); // Stores the utility estimate V(s) for each abstract GameState
    private final Map<GameState, MOVE> policy = new HashMap<>(); // Stores the greedy policy π(s) derived from the value function
    private final double gamma = 0.9; // Discount factor
    private final int iterations = 20; // Fixed number of iterations as per spec

    public ValueIterationAgent() {
        // Generate all abstract states
        List<GameState> states = StateGenerator.getAllStates();
        
        // Initialize V(s) = 0 and policy(s) = MOVE.NEUTRAL for all states
        for (GameState state : states) {
            valueFunction.put(state, 0.0);
            policy.put(state, MOVE.NEUTRAL);
        }
        
        // Create dummy game for transition simulation (as required by spec)
        Game dummyGame = new Game(0);
        
        // Run value iteration for fixed number of iterations
        for (int iter = 0; iter < iterations; iter++) {
            // Temporary maps to store newly computed values and policies
            Map<GameState, Double> newValueFunction = new HashMap<>();
            Map<GameState, MOVE> newPolicy = new HashMap<>();
            
            // For each state, enumerate legal actions
            for (GameState state : states) {
                List<MOVE> legalMoves = state.getLegalMoves();// Retrieve actions physically legal for Pac-Man in this state
                
                if (legalMoves == null || legalMoves.isEmpty()) {
                    // No legal moves - terminal state has value 0
                    newValueFunction.put(state, 0.0);
                    newPolicy.put(state, MOVE.NEUTRAL);
                    continue;
                }
                
             // Prepare to find the best action according to Bellman optimality
                double maxValue = Double.NEGATIVE_INFINITY;
                MOVE bestAction = MOVE.NEUTRAL;
                
                // For each action, evaluate the expectation of r + γ·V(s′)
                for (MOVE action : legalMoves) {
                    // Get transitions for this state-action pair
                    List<Transition> transitions = state.getTransitions(dummyGame, action);
                    
                    // If no transitions available, skip action as invalid
                    if (transitions == null || transitions.isEmpty()) {
                        continue;
                    }
                    
                    // Compute expected value: Σ P(s'|s,a) * [R(s,a,s') + γ * V(s')]
                    double actionValue = 0.0;
                    
                    for (Transition t : transitions) {
                        double reward = t.reward;
                        double prob = t.probability;
                        GameState nextState = t.nextState;
                        double nextValue = valueFunction.getOrDefault(nextState, 0.0);
                        
                        // Bellman equation
                        actionValue += prob * (reward + gamma * nextValue);
                    }
                    
                    // Track best action
                    if (actionValue > maxValue) {
                        maxValue = actionValue;
                        bestAction = action;
                    }
                }
                
                // Handle edge case
                if (Double.isInfinite(maxValue) && maxValue < 0) {
                    maxValue = 0.0;
                    bestAction = MOVE.NEUTRAL;
                }
                
                // Store the best value and action into fresh maps
                newValueFunction.put(state, maxValue);
                newPolicy.put(state, bestAction);
            }
            
            // Replace the old ones at the end of the sweep
            valueFunction.clear();
            valueFunction.putAll(newValueFunction);
            policy.clear();
            policy.putAll(newPolicy);
        }
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        // Map the live game to a GameState
        GameState currentState = GameState.fromGame(game);
        
        // Return policy(state), falling back to MOVE.NEUTRAL when state was never seen
        return policy.getOrDefault(currentState, MOVE.NEUTRAL);
    }
}