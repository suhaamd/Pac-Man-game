package pacman.controllers.agents;

import pacman.game.Constants.MOVE;

import java.util.Objects;

public class StateActionPair {
    public final GameState state;
    public final MOVE action;

    public StateActionPair(GameState state, MOVE action) {
        this.state = state;
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StateActionPair)) return false;
        StateActionPair that = (StateActionPair) o;
        return Objects.equals(state, that.state) && action == that.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, action);
    }
}
