package main;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    public Game(Player leftPLayer, Player rightPlayer, int cooperationPoints, int oneSideBetrayalPoints,
                int twoSideBetrayalPoints, int rounds, int matches, double winnersPremium, Connection connection) {
        this.cooperationPoints = cooperationPoints;
        this.oneSideBetrayalPoints = oneSideBetrayalPoints;
        this.twoSideBetrayalPoints = twoSideBetrayalPoints;
        this.leftPLayer = leftPLayer;
        this.leftPlayerPoints = 0;
        this.leftPlayerPointsTotal = 0;
        this.rightPlayer = rightPlayer;
        this.rightPlayerPoints = 0;
        this.rightPlayerPointsTotal = 0;
        this.rounds = rounds;
        this.currentRound = 1;
        this.matches = matches;
        this.winnersPremium = winnersPremium;
        this.connection = connection;

        this.matchId = 1;
        this.gameId = globalGameId;
        globalGameId++;


        this.roundRecords = new ArrayList<>();
        this.matchRecords = new ArrayList<>();

        this.rightPlayerGameHistory = new ArrayList<>();
        this.leftPlayerGameHistory = new ArrayList<>();
    }

    int cooperationPoints;
    int oneSideBetrayalPoints;
    int twoSideBetrayalPoints;
    Player leftPLayer;
    Player rightPlayer;
    Connection connection;
    int leftPlayerPoints;
    int leftPlayerPointsTotal;
    int rightPlayerPoints;
    int rightPlayerPointsTotal;

    int rounds;
    int matches;
    double winnersPremium;
    int matchId;
    int gameId;
    static int globalGameId = 1;
    int currentRound;
    ArrayList<ResponsePair> leftPlayerGameHistory;
    ArrayList<ResponsePair> rightPlayerGameHistory;
    List<RoundRecord> roundRecords;
    List<MatchRecord> matchRecords;


    // returns Outcome
    Outcome awardPoints(boolean leftR, boolean rightR) {
        Outcome outcome;

        if (leftR && !rightR)  {
            // left cooperate | right betray
            rightPlayerPoints += oneSideBetrayalPoints;
            outcome = Outcome.RIGHTBETRAY;
        } else if (!leftR && rightR) {
            // left betray | right cooperate
            leftPlayerPoints += oneSideBetrayalPoints;
            outcome = Outcome.LEFTBETRAY;
        } else if (leftR && rightR) {
            // both cooperate
            leftPlayerPoints += cooperationPoints;
            rightPlayerPoints += cooperationPoints;
            outcome = Outcome.COOPERATION;
        } else{
            // both betray
            leftPlayerPoints += twoSideBetrayalPoints;
            rightPlayerPoints += twoSideBetrayalPoints;
            outcome = Outcome.BOTHBETRAY;
        }
        return outcome;

    }

    void playRound() {
        boolean leftResponse = leftPLayer.generateResponse(leftPlayerGameHistory);
        boolean rightResponse = rightPlayer.generateResponse(rightPlayerGameHistory);
        Outcome outcome = awardPoints(leftResponse, rightResponse);
        //System.out.println("Left Player: " + leftResponse + " | Right Player: " + rightResponse);


        // add round to currentGameHistory
        leftPlayerGameHistory.add(new ResponsePair(leftResponse, rightResponse));
        rightPlayerGameHistory.add(new ResponsePair(rightResponse, leftResponse));
        RoundRecord roundRecord = new RoundRecord(this.gameId, this.matchId, this.currentRound,
                leftResponse, rightResponse, outcome, this.leftPlayerPoints, this.rightPlayerPoints);
        this.roundRecords.add(roundRecord);
        currentRound++;
    }

    HashMap<Integer, Integer> playTheGame() { // This can return a HashMap of leftPTotalPoints and rightPTotalPoints HashMap<Integer, Integer> pointResult;
        // Clearing game history at the start of the game
        leftPlayerGameHistory = new ArrayList<>();
        rightPlayerGameHistory = new ArrayList<>();

        //Playing out the game and cashing in points
        for (int j = 0; j < this.matches; j++) {
            for (int i = 0; i <= this.rounds; i++) {
                playRound();
//            System.out.println("Current points:");
//            System.out.println("Left Player: " + leftPlayerPoints + " | Right Player: " + rightPlayerPoints);
//            System.out.println();
            }


//            System.out.println("Result of: " + this + "|  executed by: " + Thread.currentThread().getName());
//            System.out.println("Left Player: " + leftPlayerPoints + " | Right Player: " + rightPlayerPoints + "|  executed by: " + Thread.currentThread().getName());

            int winnerID = determineTheWinner(winnersPremium);
            MatchRecord matchRecord = new MatchRecord(this.gameId, this.matchId, this.leftPLayer.id,
                    this.rightPlayer.id, this.leftPlayerPoints, this.rightPlayerPoints, winnerID);
            this.matchRecords.add(matchRecord);

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
        // Saving matchRecords to SQL db and clearing the List
        matchRecordsToDb();
        matchRecords.clear();
        HashMap<Integer, Integer> pointResult = new HashMap<>();
        pointResult.put(leftPLayer.id, leftPlayerPointsTotal);
        pointResult.put(rightPlayer.id, rightPlayerPointsTotal);
        //System.out.println(pointResult);

        return pointResult;
    }

    void roundRecordsToDb(List<RoundRecord> records) { // Can get rid of an argument
        String sql = """
                INSERT INTO roundrecord 
                (gameId, matchId, roundId, leftPlayerResponse, rightPlayerResponse, outcome, leftPlayerCurrentPoints, rightPlayerCurrentPoints) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)""";

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            this.connection.setAutoCommit(false); // Start transaction

            for (RoundRecord rec : records) {
                pstmt.setInt(1, rec.gameId());
                pstmt.setInt(2, rec.matchId());
                pstmt.setInt(3, rec.roundId());
                pstmt.setBoolean(4, rec.leftPlayerResponse());
                pstmt.setBoolean(5, rec.rightPlayerResponse());
                pstmt.setString(6, rec.outcome().toString());
                pstmt.setInt(7, rec.leftPlayerCurrentPoints());
                pstmt.setInt(8, rec.rightPlayerCurrentPoints());
                pstmt.addBatch(); // Add to batch for batch execution
            }
            pstmt.executeBatch(); // Execute batch
            this.connection.commit(); // Commit transaction
            //System.out.println("Records inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    void matchRecordsToDb() {
        String sql = """
                INSERT INTO matchrecord 
                (gameId, matchId, leftPlayerId, rightPlayerId, leftPlayerFinalScore, rightPlayerFinalScore, winnerId) 
                VALUES (?, ?, ?, ?, ?, ?, ?)""";

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            this.connection.setAutoCommit(false); // Start transaction

            for (MatchRecord rec : this.matchRecords) {
                pstmt.setInt(1, rec.gameId());
                pstmt.setInt(2, rec.matchId());
                pstmt.setInt(3, rec.leftPlayerId());
                pstmt.setInt(4, rec.rightPlayerId());
                pstmt.setInt(5, rec.leftPLayerFinalScore());
                pstmt.setInt(6, rec.rightPLayerFinalScore());
                pstmt.setInt(7, rec.winnerId());
                pstmt.addBatch(); // Add to batch for batch execution
            }
            pstmt.executeBatch(); // Execute batch
            this.connection.commit(); // Commit transaction

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    int determineTheWinner(double winnersPremium) {
        // outputs winner ID and increases the winner points by 120%
        if (this.leftPlayerPoints > this.rightPlayerPoints) {
            leftPlayerPoints = (int) (winnersPremium * leftPlayerPoints);
            return leftPLayer.id;
        }
        else if (this.leftPlayerPoints < this.rightPlayerPoints) {
            rightPlayerPoints = (int) (winnersPremium * rightPlayerPoints);
            return rightPlayer.id;
        }
        else return 0; // 0 means DRAW
    }

    static ArrayList<Game> createGamesList(ArrayList<Player> players, int cooperationPoints, int oneSideBetrayalPoints,
                                           int twoSideBetrayalPoints, int rounds, int matches, double winnersPremium, Connection connection) {
        ArrayList<Game> games = new ArrayList<>();
        // create game objects
        for (int i = 0; i < players.size(); i++) {
            for (int j = i+1; j < players.size(); j++) {
                Player leftPlayer = players.get(i);
                Player rightPlayer = players.get(j);
                Game game = new Game(leftPlayer, rightPlayer, cooperationPoints,oneSideBetrayalPoints, twoSideBetrayalPoints, rounds, matches, winnersPremium, connection);
                games.add(game);
                //System.out.println(players.get(i).getClass().getName() + " : " + players.get(j).getClass().getName() );
            }
        }
        System.out.println("Number of Games created: " + games.size());
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

record RoundRecord(int gameId, int matchId, int roundId , boolean leftPlayerResponse,
                   boolean rightPlayerResponse, Outcome outcome, int leftPlayerCurrentPoints, int rightPlayerCurrentPoints) {}

record MatchRecord (int gameId, int matchId, int leftPlayerId, int rightPlayerId,
                    int leftPLayerFinalScore, int rightPLayerFinalScore, int winnerId) {}

