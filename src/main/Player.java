package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Player {

    public Player() {
        this.currentGameHistory = new ArrayList<>();
        this.name = getRightPart(this.getClass().getName());
        this.id = globalId;
        globalId++;


    }

    static int globalId = 1;
    protected  ArrayList<ResponsePair> currentGameHistory;
    protected String name;

    protected int id;

    Random random = new Random();
    protected boolean generateResponse() {
        return random.nextBoolean();
    }

    public String getRightPart(String str) {
        String[] parts = str.split("\\.");
        return parts[parts.length - 1];
    }


}



