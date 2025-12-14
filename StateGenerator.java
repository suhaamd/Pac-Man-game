package pacman.controllers.agents;

import java.util.ArrayList;
import java.util.List;

public class StateGenerator {
    public static List<GameState> getAllStates() {
        List<GameState> states = new ArrayList<>();
        for (int node = 0; node < 100; node += 5) {
            for (int pillDist = 0; pillDist < 10; pillDist++) {
                for (int ghostDist = 0; ghostDist < 10; ghostDist++) {
                    for (boolean edible : new boolean[]{true, false}) {
                        states.add(new GameState(node, pillDist * 10, ghostDist * 10, edible));
                    }
                }
            }
        }
        return states;
    }
}