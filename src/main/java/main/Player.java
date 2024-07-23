package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Player implements Cloneable{

    public Player() {
        //this.currentGameHistory = new ArrayList<>();
        this.name = getRightPart(this.getClass().getName());
        this.id = globalId;
        globalId++;
        this.strategyType = StrategyType.NASTY;
        this.strategyTemper = StrategyTemper.RANDOM;
        this.description = "Default: Totally random behavior";
    }

    public Player(Player other) {
        this.name = other.name;
        this.id = globalId;
        globalId++;
        this.strategyType = other.strategyType;
        this.strategyTemper = other.strategyTemper;
        this.description = other.description;
        this.random = new Random(); // Create a new Random instance for deep copy
    }

    public Player copy() {
        return new Player();
    }

    static int globalId = 1;
    //protected  ArrayList<ResponsePair> currentGameHistory;
    protected String name;

    protected int id;
    protected StrategyType strategyType;
    protected StrategyTemper strategyTemper;
    protected String description;

    Random random = new Random();
    protected boolean generateResponse(ArrayList<ResponsePair> currentGameHistory) {
        return random.nextBoolean();
    }

    public String getRightPart(String str) {
        String[] parts = str.split("\\.");
        return parts[parts.length - 1];
    }


    public enum StrategyType {
        NICE,
        NASTY
    }

    public enum StrategyTemper {
        FORGIVING,
        VENGEFUL,
        RANDOM
    }

    // GET LAST ResponePair

}




