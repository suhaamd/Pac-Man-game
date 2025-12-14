package pacman.controllers.agents;

import pacman.controllers.examples.NullGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.*;

public class GameState {
    private final int pacmanNode;
    private final int distToClosestPill;
    private final int distToClosestGhost;
    private final boolean ghostIsEdible;

    public GameState(int pacmanNode, int distToClosestPill, int distToClosestGhost, boolean ghostIsEdible) {
        this.pacmanNode = pacmanNode;
        this.distToClosestPill = distToClosestPill / 10;
        this.distToClosestGhost = distToClosestGhost / 10;
        this.ghostIsEdible = ghostIsEdible;
    }

    public static GameState fromGame(Game game) {
        int pacNode = game.getPacmanCurrentNodeIndex();

        int[] pills = game.getActivePillsIndices();
        int closestPillDist = Integer.MAX_VALUE;
        for (int pill : pills) {
            closestPillDist = Math.min(closestPillDist, game.getShortestPathDistance(pacNode, pill));
        }

        int closestGhostDist = Integer.MAX_VALUE;
        boolean edible = false;
        for (GHOST ghost : GHOST.values()) {
            int gNode = game.getGhostCurrentNodeIndex(ghost);
            int dist = game.getShortestPathDistance(pacNode, gNode);
            if (dist < closestGhostDist) {
                closestGhostDist = dist;
                edible = game.getGhostEdibleTime(ghost) > 0;
            }
        }

        return new GameState(pacNode, closestPillDist, closestGhostDist, edible);
    }

    public List<MOVE> getLegalMoves() {
        return Arrays.asList(MOVE.values());
    }

    public List<Transition> getTransitions(Game game, MOVE action) {
        Game sim = game.copy();
        sim.advanceGame(action, new NullGhosts().getMove(sim, -1));
        GameState nextState = GameState.fromGame(sim);
        double reward = sim.getScore() - game.getScore();  // Delta score as reward
        return Collections.singletonList(new Transition(nextState, 1.0, reward));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameState)) return false;
        GameState state = (GameState) o;
        return pacmanNode == state.pacmanNode &&
               distToClosestPill == state.distToClosestPill &&
               distToClosestGhost == state.distToClosestGhost &&
               ghostIsEdible == state.ghostIsEdible;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pacmanNode, distToClosestPill, distToClosestGhost, ghostIsEdible);
    }
}