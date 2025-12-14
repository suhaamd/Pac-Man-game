package pacman.controllers.agents;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * QLearningAgent — minimal summary:
 * Init: start with an empty Q-table. Use α = 0.1, γ = 0.9, ε = 0.1, and run for 5 training episodes.
 * Training loop per step:
 * 1. Map the Game to your GameState and gather legal actions.
 * 2. ε-greedy: with probability ε choose a random action, else take argmax Q(s,a).
 * 3. Clone the game, advance one tick using the chosen MOVE against StarterGhosts.
 * 4. Reward r = score(s′) − score(s).
 * 5. Update Q(s,a) ← Q(s,a) + α · [r + γ · max_{a′} Q(s′,a′) − Q(s,a)].
 * 6. Continue from the next state until the game ends.
 * Acting: convert the live game into a GameState and return the MOVE with the highest learned Q-value
 * (fallback MOVE.NEUTRAL if ties or unseen).
 */
public class QLearningAgent_template extends Controller<MOVE> {

    private final Map<StateActionPair, Double> qTable = new HashMap<>();
    private final double alpha = 0.1;
    private final double gamma = 0.9;
    private final double epsilon = 0.1;
    private final int episodes = 5;

    public QLearningAgent_template() {
        // Run the template training routine immediately upon construction.
        train();
    }

    public void train() {
        // Implement the episodic training loop described in the class comment.
        System.out.println("Training Q-Learning Agent...");
        
        for (int episode = 0; episode < episodes; episode++) {
            // Create a new game instance
            Game game = new Game(0);
            StarterGhosts ghosts = new StarterGhosts();
            
            while (!game.gameOver()) {
                // 1. Map the Game to your GameState and gather legal actions.
                GameState currentState = GameState.fromGame(game);
                MOVE[] legalMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
                
                // 2. ε-greedy: with probability ε choose a random action, else take argmax Q(s,a).
                MOVE action;
                if (Math.random() < epsilon) {
                    // Random action
                    action = legalMoves[new Random().nextInt(legalMoves.length)];
                } else {
                    // Best action
                    action = getBestAction(currentState);
                }
                
                // Get score before action
                int scoreBefore = game.getScore();
                
                // 3. Clone the game, advance one tick using the chosen MOVE against StarterGhosts.
                game.advanceGame(action, ghosts.getMove(game.copy(), 0));
                
                // Get score after action
                int scoreAfter = game.getScore();
                
                // Get next state
                GameState nextState = GameState.fromGame(game);
                
                // 4. Reward r = score(s′) − score(s).
                double reward = scoreAfter - scoreBefore;
                
                // 5. Update Q(s,a) ← Q(s,a) + α · [r + γ · max_{a′} Q(s′,a′) − Q(s,a)].
                StateActionPair pair = new StateActionPair(currentState, action);
                double currentQ = qTable.getOrDefault(pair, 0.0);
                double maxNextQ = game.gameOver() ? 0.0 : getMaxQ(nextState);
                double newQ = currentQ + alpha * (reward + gamma * maxNextQ - currentQ);
                qTable.put(pair, newQ);
            }
            
            System.out.println("Episode " + episode + " Score: " + game.getScore());
        }
        
        System.out.println("Training complete.");
    }

    public MOVE getBestAction(GameState state) {
        // Return the action with the highest Q-value for the provided state.
        MOVE bestMove = MOVE.NEUTRAL;
        double bestValue = Double.NEGATIVE_INFINITY;
        
        for (MOVE move : MOVE.values()) {
            StateActionPair pair = new StateActionPair(state, move);
            double qValue = qTable.getOrDefault(pair, 0.0);
            if (qValue > bestValue) {
                bestValue = qValue;
                bestMove = move;
            }
        }
        
        return bestMove;
    }

    public double getMaxQ(GameState state) {
        // Helper for max_{a} Q(s,a) used inside the TD update.
        double maxQ = 0.0;
        
        for (MOVE action : MOVE.values()) {
            StateActionPair pair = new StateActionPair(state, action);
            double q = qTable.getOrDefault(pair, 0.0);
            if (q > maxQ) {
                maxQ = q;
            }
        }
        
        return maxQ;
    }

    
    @Override
    public MOVE getMove(Game game, long timeDue) {
        // Convert the runtime Game into a GameState and exploit the learned policy.
        GameState state = GameState.fromGame(game);
        return getBestAction(state);
    }
}