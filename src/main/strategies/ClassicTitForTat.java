package main.strategies;
import main.Player;

public class ClassicTitForTat extends Player {
    /*
        Cooperates on the first round and imitates its opponent's previous move thereafter.
     */

    @Override
    protected boolean generateResponse() {
        if (!currentGameHistory.isEmpty()) {
            return currentGameHistory.get(currentGameHistory.size() - 1).getOpponentResponse();
        } else return true;

    }


}
