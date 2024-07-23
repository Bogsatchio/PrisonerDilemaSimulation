package main.strategies;

import main.Player;
import main.ResponsePair;

import java.util.ArrayList;

public class AlwaysDefect extends Player {
    /*
        Always defects.
     */

    public AlwaysDefect() {
        this.strategyType = StrategyType.NASTY;
        this.strategyTemper = StrategyTemper.VENGEFUL;
        this.description = "Always defects";
    }

    public AlwaysDefect(AlwaysDefect other) {
        super(other); // Call the superclass copy constructor
    }

    public AlwaysDefect copy() {
        return new AlwaysDefect();
    }

    @Override
    protected boolean generateResponse(ArrayList<ResponsePair> currentGameHistory) {
        return false;
    }

}
