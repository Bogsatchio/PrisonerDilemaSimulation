package main.strategies;

import main.Player;

import java.util.Random;

public class RandomDefector extends Player {
    /*
        Defects randomly according to probability input
     */

    public RandomDefector(double defectionProbability) {
        this.defectionProbability = defectionProbability;
    }

    Random random = new Random();
    double defectionProbability;

    @Override
    protected boolean generateResponse() {
        float randVal = random.nextFloat(0, 1);
        if (defectionProbability >= randVal) {
            return false;
        } else {
            return true;
        }

    }
}
