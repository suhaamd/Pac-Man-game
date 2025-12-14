package pacman.controllers.agents;

public class Transition {
    public final GameState nextState;
    public final double probability;
    public final double reward;

    public Transition(GameState nextState, double probability, double reward) {
        this.nextState = nextState;
        this.probability = probability;
        this.reward = reward;
    }
}