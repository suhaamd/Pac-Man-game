package pacman.controllers.examples;

import pacman.controllers.Controller;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.EnumMap;

public class NullGhosts extends Controller<EnumMap<GHOST, MOVE>> {
    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        EnumMap<GHOST, MOVE> ghostMoves = new EnumMap<>(GHOST.class);
        for (GHOST ghost : GHOST.values()) {
            ghostMoves.put(ghost, MOVE.NEUTRAL); // Ghosts don't move
        }
        return ghostMoves;
    }
}