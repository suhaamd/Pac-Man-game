package pacman.controllers.agents;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * PolicyIterationAgent — template summary:
 * Init: enumerate states, set V(s)=0, and assign a random legal MOVE to policy(s).
 * Repeat until policy is stable or the iteration cap is reached:
 * 1. Policy Evaluation: for each state, update V(s)=E[r + γ·V(s′)] using the current action and run a few sweeps.
 * 2. Policy Improvement: for each state, set policy(s)=argmax Σ p·(r + γ·V(s′)) and mark stability only if no action changes.
 * Acting: convert the live game into a GameState and return the stored action (fallback MOVE.NEUTRAL when the state is unknown).
 */
public class PolicyIterationAgent extends Controller<MOVE> {

	// Selected action for each abstract state
    private final Map<GameState, MOVE> policy = new HashMap<>();
    
    // V(s): estimated state values under current policy
    private final Map<GameState, Double> valueFunction = new HashMap<>();
    private final double gamma = 0.9; // Discount factor
    private final int maxIterations = 20; // Max - policy improvement rounds

    // Enumerating the abstract MDP state space produced by the environment.
    // Abstract states compress the game into a finite model suitable for planning.
    public PolicyIterationAgent() {
        
    	// Generating all states, initialize the policy randomly, and alternate evaluation and improvement until convergence or maxIterations.
    	 List<GameState> states = StateGenerator.getAllStates();
         Random random = new Random();
         
         // Initialize V(s) = 0 and policy(s) = random legal move
         for (GameState state : states) {
             valueFunction.put(state, 0.0);
             List<MOVE> legalMoves = state.getLegalMoves(); // Starting with a random legal action
             MOVE randomMove = legalMoves.get(random.nextInt(legalMoves.size()));
             policy.put(state, randomMove);
         }
         
         // Run policy iteration
         // Repeatedly alternating between evaluating the policy and improving it.
         // Terminating early once π stabilises (no action changes).
         for (int iter = 0; iter < maxIterations; iter++) {
             // Policy Evaluation
             policyEvaluation(states);
             
             // Policy Improvement
             boolean policyStable = policyImprovement(states);
             
             // If policy is stable, stop early
             if (policyStable) {
                 break;
             }
         }
    }

    //Runs a fixed number of Bellman expectation sweeps to approximate
    private void policyEvaluation(List<GameState> states) {
        // Run several sweeps updating V(s) with expectations under the current policy choice.
    	Game dummyGame = new Game(0); // Used to obtain transition models
        int evaluationSweeps = 20; // Fixed number of sweeps
        
        for (int sweep = 0; sweep < evaluationSweeps; sweep++) {
            Map<GameState, Double> newValueFunction = new HashMap<>();
            
            for (GameState state : states) {
            	// Evaluating value of state under the action dictated by π(s)
                MOVE action = policy.get(state);
                double value = 0.0;
                
                // Get all (p, r, s′) transitions under π(s)	
                List<Transition> transitions = state.getTransitions(dummyGame, action);
                
                // Bellman expectation backup
                for (Transition transition : transitions) {
                    double reward = transition.reward;
                    double probability = transition.probability;
                    GameState nextState = transition.nextState;
                    
                    double nextValue = valueFunction.getOrDefault(nextState, 0.0);
                    value += probability * (reward + gamma * nextValue);
                }
                
                newValueFunction.put(state, value);
            }
            
            // Update value function
            valueFunction.clear();
            valueFunction.putAll(newValueFunction);
        }
    
    }

    //For each state, compute the action-value and choose the action that maximises it
    private boolean policyImprovement(List<GameState> states) {
        // For each state, pick the action that maximizes expected return and report whether any action changed.
    	Game dummyGame = new Game(0);
        boolean policyStable = true;
        
        for (GameState state : states) {
            MOVE oldAction = policy.get(state);
            // Computing Q(s,a) for all legal actions
            List<MOVE> legalMoves = state.getLegalMoves();
            
            double maxValue = Double.NEGATIVE_INFINITY;
            MOVE bestAction = MOVE.NEUTRAL;
            
            for (MOVE action : legalMoves) {
                double actionValue = 0.0;
                
                // Evaluating expected return for this action
                List<Transition> transitions = state.getTransitions(dummyGame, action);
                
                for (Transition transition : transitions) {
                    double reward = transition.reward;
                    double probability = transition.probability;
                    GameState nextState = transition.nextState;
                    
                    double nextValue = valueFunction.getOrDefault(nextState, 0.0);
                    actionValue += probability * (reward + gamma * nextValue);
                }
                
                // Keeping the best Q(s,a)
                if (actionValue > maxValue) {
                    maxValue = actionValue;
                    bestAction = action;
                }
            }
            
            policy.put(state, bestAction);// Update π(s)
            
         // Check if policy changed
            if (oldAction != bestAction) {
                policyStable = false;
            }
        }
        
        return policyStable;
    }

    
    @Override
    public MOVE getMove(Game game, long timeDue) {
        // Map Game to GameState and return policy.getOrDefault(..., MOVE.NEUTRAL).
        //return MOVE.NEUTRAL;
    	
    	GameState currentState = GameState.fromGame(game);
        return policy.getOrDefault(currentState, MOVE.NEUTRAL);
    }
}
