package main.strategies;

import main.Player;
import main.ResponsePair;

import java.util.ArrayList;
import java.util.Random;

public class RandomDefector extends Player {
    /*
        Defects randomly according to probability input
     */

    public RandomDefector(double defectionProbability) {
        this.name = this.name + "-" + defectionProbability;
        this.defectionProbability = defectionProbability;
    }

    Random random = new Random();
    double defectionProbability;

    @Override
    protected boolean generateResponse(ArrayList<ResponsePair> currentGameHistory) {
        float randVal = random.nextFloat(0, 1);
        if (defectionProbability >= randVal) {
            return false;
        } else {
            return true;
        }
    }


}
