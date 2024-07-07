package main.strategies;

import main.Player;
import main.ResponsePair;

import java.util.ArrayList;

public class ForgivingTitForTat extends Player {
    /*
        Cooperates on the first round and imitates its opponent's previous move thereafter.
        But it does not imitate first few betrayals unless they exceed numForgiveness.
        It can also be extra forgiving and start the numForgiveness again after it defects.
     */

    public ForgivingTitForTat(int numForgiveness, boolean continuousForgiving) {
        this.name = this.name + "-" + numForgiveness + (continuousForgiving ? "-Continuous" : "-NotContinuous");
        this.numForgiveness = numForgiveness;
        this.continuousForgiving = continuousForgiving;
        this.currentNumForgiveness = numForgiveness;
        this.strategyType = StrategyType.NICE;
        this.strategyTemper = StrategyTemper.FORGIVING;
        this.description = "But it does not imitate first few betrayals unless they exceed its patience level";
    }
    int numForgiveness;
    int currentNumForgiveness;
    boolean continuousForgiving;

    @Override
    protected boolean generateResponse(ArrayList<ResponsePair> currentGameHistory) {
        if (!currentGameHistory.isEmpty()) {
            boolean lastOpponentsResponse = currentGameHistory.get(currentGameHistory.size() - 1).getOpponentResponse();
            if (!lastOpponentsResponse) {
                currentNumForgiveness--;
                if (currentNumForgiveness < 0) { //Check for patience
                    if (continuousForgiving) currentNumForgiveness = numForgiveness; // extra forgiving check and set
                    return false;
                }
            } return true;
        } else return true;
    }


}
