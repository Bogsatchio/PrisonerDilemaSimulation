package main.strategies;

import main.Player;
import main.ResponsePair;

import java.util.ArrayList;

public class ReverseTitForTat extends Player {
    /*
        Defects if the opponent cooperated and cooperates if the opponent defected.
    */
    public ReverseTitForTat() {
        this.strategyType = StrategyType.NASTY;
        this.strategyTemper = StrategyTemper.VENGEFUL;
        this.description = "Defects if the opponent cooperated and cooperates if the opponent defected";
    }

    public ReverseTitForTat(ReverseTitForTat other) {
        super(other);
    }

    public ReverseTitForTat copy() {
        return new ReverseTitForTat();
    }

    @Override
    protected boolean generateResponse(ArrayList<ResponsePair> currentGameHistory) {
        if (!currentGameHistory.isEmpty()) {
            return !(currentGameHistory.get(currentGameHistory.size() - 1).getOpponentResponse());
        } else return false;
    }
}
