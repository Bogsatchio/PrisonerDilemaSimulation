package main.strategies;

import main.Player;
import main.ResponsePair;

import java.util.ArrayList;

public class ClassicPavlov extends Player {
    /*
         - Repeat your behavior from the last round, if it was successful
                (i.e. you betrayed and the opponent cooperated or you both cooperated)
         - Change your behavior if you lost in the last round
                (i.e. you cooperated and your opponent betrayed or you both betrayed).
     */
    @Override
    protected boolean generateResponse(ArrayList<ResponsePair> currentGameHistory) {
        boolean currentResponse = true;
        if (!currentGameHistory.isEmpty()) {
            ResponsePair lastRound = currentGameHistory.get(currentGameHistory.size() - 1);
            currentResponse = lastRound.getMyResponse();
            // switch response if previous round was lost else continue you last behavior
            if ((lastRound.getMyResponse() && !lastRound.getOpponentResponse()) ||  (!lastRound.getMyResponse() && !lastRound.getOpponentResponse())) {
                currentResponse = !currentResponse;
            } else {
                currentResponse = lastRound.getMyResponse();
            }
        }
        return currentResponse;


    }



}