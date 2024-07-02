package main.strategies;

import main.Player;
import main.ResponsePair;

import java.util.ArrayList;

public class AlwaysCooperates extends Player {
    /*
        Always cooperates.
     */
    @Override
    protected boolean generateResponse(ArrayList<ResponsePair> currentGameHistory) {
        return true;
    }
}
