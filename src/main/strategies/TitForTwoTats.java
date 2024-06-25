package main.strategies;

import main.Player;

public class TitForTwoTats extends Player {

    /*
        Cooperates unless defected against twice in a row.
     */
    @Override
    protected boolean generateResponse() {
        if (currentGameHistory.size() >= 2) {
            if (!currentGameHistory.get(currentGameHistory.size() - 1).getOpponentResponse() && !currentGameHistory.get(currentGameHistory.size() - 2).getOpponentResponse()) {
                return false;
            } else return true;

        } else return true;

    }

}
