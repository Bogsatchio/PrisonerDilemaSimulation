package main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Game {
    public Game(Player leftPLayer, Player rightPlayer, int rounds, int matches) {
        this.leftPLayer = leftPLayer;
        this.leftPlayerPoints = 0;
        this.leftPlayerPointsTotal = 0;
        this.rightPlayer = rightPlayer;
        this.rightPlayerPoints = 0;
        this.rightPlayerPointsTotal = 0;
        this.rounds = rounds;
        this.currentRound = 1;
        this.matches = matches;

        this.matchId = 1;
        this.gameId = globalGameId;
        globalGameId++;


        this.roundRecords = new ArrayList<>();
    }

    Player leftPLayer;
    Player rightPlayer;
    int leftPlayerPoints;
    int leftPlayerPointsTotal;
    int rightPlayerPoints;
    int rightPlayerPointsTotal;

    int rounds;
    int matches;
    int matchId;
    int gameId;
    static int globalGameId = 1;
    int currentRound;
    List<RoundRecord> roundRecords;


    // returns Outcome
    Outcome awardPoints(boolean leftR, boolean rightR) {
        Outcome outcome;

        if (leftR && !rightR)  {
            // left cooperate | right betray
            rightPlayerPoints += 5;
            outcome = Outcome.RIGHTBETRAY;
        } else if (!leftR && rightR) {
            // left betray | right cooperate
            leftPlayerPoints += 5;
            outcome = Outcome.LEFTBETRAY;
        } else if (leftR && rightR) {
            // both cooperate
            leftPlayerPoints += 3;
            rightPlayerPoints += 3;
            outcome = Outcome.COOPERATION;
        } else{
            // both betray
            leftPlayerPoints += 1;
            rightPlayerPoints += 1;
            outcome = Outcome.BOTHBETRAY;
        }
        return outcome;

    }

    void playRound() {
        boolean leftResponse = leftPLayer.generateResponse();
        boolean rightResponse = rightPlayer.generateResponse();
        Outcome outcome = awardPoints(leftResponse, rightResponse);
        //System.out.println("Left Player: " + leftResponse + " | Right Player: " + rightResponse);


        // add round to currentGameHistory
        leftPLayer.currentGameHistory.add(new ResponsePair(leftResponse, rightResponse));
        rightPlayer.currentGameHistory.add(new ResponsePair(rightResponse, leftResponse));
        RoundRecord roundRecord = new RoundRecord(this.gameId, this.matchId, this.currentRound, this.leftPLayer.id,
                this.rightPlayer.id, leftResponse, rightResponse, outcome);
        this.roundRecords.add(roundRecord);
        currentRound++;
    }
    void playTheGame() {
        // Clearing game history at the start of the game
        leftPLayer.currentGameHistory = new ArrayList<>();
        rightPlayer.currentGameHistory = new ArrayList<>();

        //Playing out the game and cashing in points
        for (int j = 0; j < this.matches; j++) {
            for (int i = 0; i <= this.rounds; i++) {
                playRound();
//            System.out.println("Current points:");
//            System.out.println("Left Player: " + leftPlayerPoints + " | Right Player: " + rightPlayerPoints);
//            System.out.println();
            }
            System.out.println("Result of: " + this + "|  executed by: " + Thread.currentThread().getName());
            System.out.println("Left Player: " + leftPlayerPoints + " | Right Player: " + rightPlayerPoints + "|  executed by: " + Thread.currentThread().getName());

            // Adding to total score and reseting points from the game to 0 and increasing matchId
            leftPlayerPointsTotal += leftPlayerPoints;
            rightPlayerPointsTotal += rightPlayerPoints;
            leftPlayerPoints = 0;
            rightPlayerPoints = 0;
            currentRound = 1;
            matchId++;

            // Saving roundRecords to SQL db and clearing the List
            roundRecordsToDb(roundRecords);
            roundRecords.clear();


            }

    }

    void roundRecordsToDb(List<RoundRecord> records) {
        // open connection, execute sql insert statement
        String sql = """
                INSERT INTO roundrecord 
                (gameId, matchId, roundId, leftPlayerId, rightPlayerId, leftPlayerResponse, rightPlayerResponse, outcome) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)""";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/simulation_dev",
                System.getenv("MYSQL_USER"),
                System.getenv("MYSQL_PASS"));
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false); // Start transaction

            for (RoundRecord rec : records) {
                pstmt.setInt(1, rec.gameId());
                pstmt.setInt(2, rec.matchId());
                pstmt.setInt(3, rec.roundId());
                pstmt.setInt(4, rec.leftPlayerId());
                pstmt.setInt(5, rec.rightPlayerId());
                pstmt.setBoolean(6, rec.leftPlayerResponse());
                pstmt.setBoolean(7, rec.rightPlayerResponse());
                pstmt.setString(8, rec.outcome().toString());
                pstmt.addBatch(); // Add to batch for batch execution
            }
            pstmt.executeBatch(); // Execute batch
            conn.commit(); // Commit transaction
            System.out.println("Records inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    static ArrayList<Game> createGamesList(ArrayList<Player> players, int matches, int rounds) {
        ArrayList<Game> games = new ArrayList<>();
        // create game objects
        for (int i = 0; i < players.size(); i++) {
            for (int j = i+1; j < players.size(); j++) {
                Player leftPlayer = players.get(i);
                Player rightPlayer = players.get(j);
                Game game = new Game(leftPlayer, rightPlayer, rounds, matches);
                games.add(game);
                //System.out.println(players.get(i).getClass().getName() + " : " + players.get(j).getClass().getName() );
            }
        }
        return games;
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

enum Outcome {
    COOPERATION,
    LEFTBETRAY,
    RIGHTBETRAY,
    BOTHBETRAY

}

record RoundRecord(int gameId, int matchId, int roundId , int leftPlayerId,  int rightPlayerId, boolean leftPlayerResponse, boolean rightPlayerResponse, Outcome outcome) {}

// record MatchRecord (int gameId, int matchId, int leftPlayerId)

