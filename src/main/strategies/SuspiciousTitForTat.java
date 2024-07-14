package main.strategies;

import main.Player;
import main.ResponsePair;

import java.util.ArrayList;

public class SuspiciousTitForTat extends Player {
    /*
        Defects on the first round and imitates its opponent's previous move thereafter.
     */

    public SuspiciousTitForTat() {
        this.strategyType = StrategyType.NASTY;
        this.strategyTemper = StrategyTemper.FORGIVING;
        this.description = "Defects on the first round and imitates its opponent's previous move thereafter";
    }

    public SuspiciousTitForTat(SuspiciousTitForTat other) {
        super(other);
    }

    public SuspiciousTitForTat copy() {
        return new SuspiciousTitForTat();
    }

    @Override
    protected boolean generateResponse(ArrayList<ResponsePair> currentGameHistory) {
        if (!currentGameHistory.isEmpty()) {
            return currentGameHistory.get(currentGameHistory.size() - 1).getOpponentResponse();
        } else return false;
    }
}


