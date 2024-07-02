package main;

import java.sql.Connection;
import java.util.ArrayList;

public class Experiment {
    public Experiment(ArrayList<Player> players, int cooperationPoints, int oneSideBetrayalPoints,
                      int twoSideBetrayalPoints, int rounds, int matches, int waves, int numEliminated, double winnersPremium, Connection connection) {
        this.cooperationPoints = cooperationPoints;
        this.oneSideBetrayalPoints = oneSideBetrayalPoints;
        this.twoSideBetrayalPoints = twoSideBetrayalPoints;
        this.rounds = rounds;
        this.matches = matches;
        this.waves = waves;
        this.numEliminated = numEliminated;
        this.winnersPremium = winnersPremium;
        this.players = players;
        this.connection = connection;
    }

    int cooperationPoints;
    int oneSideBetrayalPoints;
    int twoSideBetrayalPoints;
    int rounds;
    int matches;
    int waves;
    int numEliminated;
    double winnersPremium;
    ArrayList<Player> players;
    Connection connection;

    void playOutExperiment() {
        for (int x = 0; x < waves; x++) {
            Wave wave = new Wave(players, cooperationPoints, oneSideBetrayalPoints, twoSideBetrayalPoints,
                    rounds, matches, numEliminated, winnersPremium, connection);
            players = wave.playOutWave();
        }
    }


}
