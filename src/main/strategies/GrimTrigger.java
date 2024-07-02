package main.strategies;

import main.Player;
import main.ResponsePair;

import java.util.ArrayList;

public class GrimTrigger extends Player {
    /*
        Cooperates until opponent betrays. When that happens it triggers and only betrays.
     */
    boolean isTriggered = false;
    @Override
    protected boolean generateResponse(ArrayList<ResponsePair> currentGameHistory) {
        if (isTriggered) return false;
        else {
            if (!currentGameHistory.isEmpty()) {
                boolean lastOpponentsResponse = currentGameHistory.get(currentGameHistory.size() - 1).getOpponentResponse();
                if (!lastOpponentsResponse) {
                    isTriggered = true;
                    return false;
                }
            }
            return true;
        }

    }

}
