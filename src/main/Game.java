package main;

import java.util.ArrayList;

public class Game {
    public Game(Player leftPLayer, Player rightPlayer, int rounds, int games) {
        this.leftPLayer = leftPLayer;
        this.leftPlayerPoints = 0;
        this.rightPlayer = rightPlayer;
        this.rightPlayerPoints = 0;
        this.rounds = rounds;
        this.games = games;
    }

    Player leftPLayer;
    Player rightPlayer;
    int leftPlayerPoints;
    int rightPlayerPoints;

    int rounds;
    int games;

    // returns Outcome
    void awardPoints(boolean leftR, boolean rightR) {
        String outcome;

        if (leftR && !rightR)  {
            // left cooperate | right betray
            rightPlayerPoints += 5;

        } else if (!leftR && rightR) {
            // left betray | right cooperate
            leftPlayerPoints += 5;
        } else if (leftR && rightR) {
            // both cooperate
            leftPlayerPoints += 3;
            rightPlayerPoints += 3;
        } else if (!leftR && !rightR) {
            // both betray
            leftPlayerPoints += 1;
            rightPlayerPoints += 1;
        }

    }

    void playRound() {
        boolean leftResponse = leftPLayer.generateResponse();
        boolean rightResponse = rightPlayer.generateResponse();
        awardPoints(leftResponse, rightResponse);
        //System.out.println("Left Player: " + leftResponse + " | Right Player: " + rightResponse);

        // add round to currentGameHistory
        leftPLayer.currentGameHistory.add(new ResponsePair(leftResponse, rightResponse));
        rightPlayer.currentGameHistory.add(new ResponsePair(rightResponse, leftResponse));

    }
    void playTheGame() {
        // Clearing game history at the start of the game
        leftPLayer.currentGameHistory = new ArrayList<>();
        rightPlayer.currentGameHistory = new ArrayList<>();

        //Playing out the game and cashing in points
        for (int j = 0; j <=this.games; j++) {
            for (int i = 0; i <= this.rounds; i++) {
                playRound();
//            System.out.println("Current points:");
//            System.out.println("Left Player: " + leftPlayerPoints + " | Right Player: " + rightPlayerPoints);
//            System.out.println();
            }
            System.out.println("Result of: " + this + "|  executed by: " + Thread.currentThread().getName());
            System.out.println("Left Player: " + leftPlayerPoints + " | Right Player: " + rightPlayerPoints + "|  executed by: " + Thread.currentThread().getName());
            // Saving points from the round

            // Reseting points from the game to 0
            leftPlayerPoints = 0;
            rightPlayerPoints = 0;
            }

    }

    @Override
    public String toString() {

        return getRightPart(leftPLayer.getClass().getName()) + " : " + getRightPart(rightPlayer.getClass().getName());
    }

    public String getRightPart(String str) {
        String[] parts = str.split("\\.");
        return parts[parts.length - 1];
    }

}

