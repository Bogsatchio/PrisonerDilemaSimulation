package main.strategies;

import main.Player;
import main.ResponsePair;

import java.util.ArrayList;

public class TitForTwoTats extends Player {

    /*
        Cooperates unless betrayed twice in a row.
     */

    public TitForTwoTats() {
        this.strategyType = StrategyType.NICE;
        this.strategyTemper = StrategyTemper.FORGIVING;
        this.description = "Cooperates unless betrayed twice in a row";
    }

    @Override
    protected boolean generateResponse(ArrayList<ResponsePair> currentGameHistory) {

        if (currentGameHistory.size() >= 2) {
            if (!currentGameHistory.get(currentGameHistory.size() - 1).getOpponentResponse() && !currentGameHistory.get(currentGameHistory.size() - 2).getOpponentResponse()) {
                return false;
            } else return true;

        } else return true;

    }

}
