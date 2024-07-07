package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Experiment {
    public Experiment(String experimentId, String description, ArrayList<Player> players, int cooperationPoints, int oneSideBetrayalPoints,
                      int twoSideBetrayalPoints, int rounds, int matches, int waves, int numEliminated, double winnersPremium, Connection connection) {

        this.experimentId = experimentId;
        this.description = description;
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

    String experimentId;
    String description;
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
        playersToDb();
        experimentToDb();
        for (int x = 0; x < waves; x++) {
            Wave wave = new Wave(experimentId, players ,cooperationPoints, oneSideBetrayalPoints, twoSideBetrayalPoints,
                    rounds, matches, numEliminated, winnersPremium, connection);
            players = wave.playOutWave();
        }
    }

    void experimentToDb() {
        String sql = """
                INSERT INTO experimentrecord
                (experimentId, cooperationPoints, oneSideBetrayalPoints, twoSideBetrayalPoints, waves, matches,
                 rounds, numEliminatedPerWave, winnersPremium, description)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            this.connection.setAutoCommit(false); // Start transaction

            pstmt.setString(1, experimentId);
            pstmt.setInt(2, cooperationPoints);
            pstmt.setInt(3, oneSideBetrayalPoints);
            pstmt.setInt(4, twoSideBetrayalPoints);
            pstmt.setInt(5, waves);
            pstmt.setInt(6, matches);
            pstmt.setInt(7, rounds);
            pstmt.setInt(8, numEliminated);
            pstmt.setDouble(9, winnersPremium);
            pstmt.setString(10, description);
            pstmt.executeUpdate(); // Execute insert statement

            this.connection.commit(); // Commit transaction
            // System.out.println("Record inserted successfully!");
        } catch (SQLException e) {
            //this.connection.rollback(); // Rollback transaction on error
            e.printStackTrace();
        }


    }

    void playersToDb() {
        // Create Player Records list
        List<PlayerRecord> playerRecords = new ArrayList<>();
        for (Player p : players) {
            PlayerRecord newPRec = new PlayerRecord(experimentId, p.id, p.name, p.strategyType.toString(),
                    p.strategyTemper.toString(), p.description);
            playerRecords.add(newPRec);
        }

        String sql = """
                INSERT INTO players
                (experimentId, playerId, name, strategyType, strategyTemper, description) 
                VALUES (?, ?, ?, ?, ?, ?)""";

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            this.connection.setAutoCommit(false); // Start transaction

            for (PlayerRecord rec : playerRecords) {
                pstmt.setString(1, rec.experimentId());
                pstmt.setInt(2, rec.playerId());
                pstmt.setString(3, rec.name());
                pstmt.setString(4, rec.strategyType());
                pstmt.setString(5, rec.strategyTemper());
                pstmt.setString(6, rec.description());
                pstmt.addBatch(); // Add to batch for batch execution
            }
            pstmt.executeBatch(); // Execute batch
            this.connection.commit(); // Commit transaction
            //System.out.println("Records inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    record PlayerRecord (String experimentId, int playerId, String name, String strategyType, String strategyTemper, String description) {}


}
