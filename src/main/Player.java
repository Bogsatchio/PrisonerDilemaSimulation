package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Player {

    public Player() {
        this.currentGameHistory = new ArrayList<>();
    }

    protected  ArrayList<ResponsePair> currentGameHistory;
    //protected String name = "Base Player";

    Random random = new Random();
    protected boolean generateResponse() {
        return random.nextBoolean();
    }


}



