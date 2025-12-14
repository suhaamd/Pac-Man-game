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


public class PolicyIterationAgent_template extends Controller<MOVE> {

    private final Map<GameState, MOVE> policy = new HashMap<>();
    private final Map<GameState, Double> valueFunction = new HashMap<>();
    private final double gamma = 0.9;
    private final int maxIterations = 20;

    public PolicyIterationAgent_template() {
        // Generate all states, initialize the policy randomly, and alternate evaluation and improvement until convergence or maxIterations.
    	 List<GameState> states = StateGenerator.getAllStates();
         Random random = new Random();
         
      // Initialize V(s) = 0 and policy(s) = random legal move
         for (GameState state : states) {
             valueFunction.put(state, 0.0);
             List<MOVE> legalMoves = state.getLegalMoves();
             MOVE randomMove = legalMoves.get(random.nextInt(legalMoves.size()));
             policy.put(state, randomMove);
         }
         
         // Run policy iteration
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

    private void policyEvaluation(List<GameState> states) {
        // Run several sweeps updating V(s) with expectations under the current policy choice.
    	Game dummyGame = new Game(0);
        int evaluationSweeps = 20;
        
        for (int sweep = 0; sweep < evaluationSweeps; sweep++) {
            Map<GameState, Double> newValueFunction = new HashMap<>();
            
            for (GameState state : states) {
                MOVE action = policy.get(state);
                double value = 0.0;
                
                List<Transition> transitions = state.getTransitions(dummyGame, action);
                
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

    private boolean policyImprovement(List<GameState> states) {
        // For each state, pick the action that maximizes expected return and report whether any action changed.
    	Game dummyGame = new Game(0);
        boolean policyStable = true;
        
        for (GameState state : states) {
            MOVE oldAction = policy.get(state);
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
            
            policy.put(state, bestAction);
            
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
