package main.strategies;

import main.Player;

public class AlwaysDefect extends Player {
    /*
        Always defects.
     */
    @Override
    protected boolean generateResponse() {
        return false;
    }

}
