package pacman.controllers.agents;

import pacman.Executor;

import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.examples.NullGhosts;

public class TestQLearning {
    public static void main(String[] args) {
        Executor exec = new Executor();
        //QLearningAgent agent = new QLearningAgent();
        //PolicyIterationAgent agent = new  PolicyIterationAgent();
        ValueIterationAgent_template agent = new  ValueIterationAgent_template();
        System.out.println("[QL] Running evaluation...");
        exec.runExperiment(agent, new StarterGhosts(), 50);
        //exec.runExperiment(agent, new NullGhosts(), 50);
    }
}
