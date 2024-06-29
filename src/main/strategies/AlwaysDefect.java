package main.strategies;

import main.Player;
import main.ResponsePair;

import java.util.ArrayList;

public class AlwaysDefect extends Player {
    /*
        Always defects.
     */
    @Override
    protected boolean generateResponse(ArrayList<ResponsePair> currentGameHistory) {
        return false;
    }

}
