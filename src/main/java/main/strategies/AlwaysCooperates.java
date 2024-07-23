package main.strategies;

import main.Player;
import main.ResponsePair;

import java.util.ArrayList;

public class AlwaysCooperates extends Player{
    /*
            Always cooperates.
     */
    public AlwaysCooperates() {
        this.strategyType = StrategyType.NICE;
        this.strategyTemper = StrategyTemper.FORGIVING;
        this.description = "Always cooperates";
    }

    public AlwaysCooperates(AlwaysCooperates other) {
        super(other); // Call the superclass copy constructor
    }

    public AlwaysCooperates copy() {
        return new AlwaysCooperates();
    }

    @Override
    protected boolean generateResponse(ArrayList<ResponsePair> currentGameHistory) {
        return true;
    }
}
