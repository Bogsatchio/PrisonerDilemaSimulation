package main.strategies;

import main.Player;
import main.ResponsePair;

import java.util.ArrayList;

public class TitForTatBetrayalCounter extends Player {
    /*
        Cooperates on the first round and imitates its opponent's previous move thereafter.
        But once every COUNTER round it defects.
     */

    public TitForTatBetrayalCounter(int betrayalCounter) {
        this.name = this.name + "-" + betrayalCounter;
        this.betrayalCounter = betrayalCounter;
        this.currentCounter = 0;
    }

    int betrayalCounter;
    int currentCounter;

    @Override
    protected boolean generateResponse(ArrayList<ResponsePair> currentGameHistory) {
        currentCounter++;
        if (!currentGameHistory.isEmpty()) {
            if (currentCounter > betrayalCounter){ //Betrayal from Counter
                currentCounter = 0;
                return false;
            } else { // Classic TitForTat
                return currentGameHistory.get(currentGameHistory.size() - 1).getOpponentResponse();
            }
        } else return true;
    }

}
