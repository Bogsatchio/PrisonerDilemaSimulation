package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;


public class Wave {

    // Additional arguments for Game matches and rounds
    public Wave(String experimentId, ArrayList<Player> players, int cooperationPoints, int oneSideBetrayalPoints, int twoSideBetrayalPoints,
                int rounds, int matches, int numEliminated, double winnersPremium, Connection connection) {

        this.experimentId = experimentId;
        this.waveId = experimentId + globalWaveId;
        globalWaveId++;
        this.cooperationPoints = cooperationPoints;
        this.oneSideBetrayalPoints = oneSideBetrayalPoints;
        this.twoSideBetrayalPoints = twoSideBetrayalPoints;
        this.players = players;
        this.rounds = rounds;
        this.matches = matches;
        this.numEliminated = numEliminated;
        this.winnersPremium = winnersPremium;
        this.connection = connection;
        this.waveResults = new ArrayList<>();



        this.games =  Game.createGamesList(this.players, this.waveId, this.cooperationPoints, this.oneSideBetrayalPoints,
                this.twoSideBetrayalPoints, this.rounds, this.matches, this.winnersPremium, this.connection);
        this.totalPlayersScore = initializeTotalPlayersScore();
        this.orderedPlayersScore = new ArrayList<>();

        this.idPlayerMap = new HashMap<>();
        for (Player player : players) {
            idPlayerMap.put(player.id, player);
        }
    }

    String experimentId;
    String waveId;
    static int globalWaveId = 1;
    int cooperationPoints;
    int oneSideBetrayalPoints;
    int twoSideBetrayalPoints;
    int rounds;
    int matches;
    int numEliminated;
    double winnersPremium;

    ArrayList<Player> players;
    List<WaveResult> waveResults;
    HashMap<Integer, Player> idPlayerMap;
    Map<Integer, Integer> totalPlayersScore;
    List<Map.Entry<Integer, Integer>> orderedPlayersScore;
    ArrayList<Game> games;
    Connection connection;


    Map<Integer, Integer> initializeTotalPlayersScore() {
        HashMap<Integer, Integer> scoreBoard = new HashMap<>();
        for (Player p : players) {
            scoreBoard.put(p.id, 0);
        }

        return scoreBoard;
    }

    ArrayList<Player>  playOutWave() {
        //ExecutorService executorService = Executors.newFixedThreadPool(games.size());
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<HashMap<Integer, Integer>>> futures = new ArrayList<>();

        for (Game game : games) {
            Future<HashMap<Integer, Integer>> future = executorService.submit(() -> {
                // This is the task that gets executed in parallel
                HashMap<Integer, Integer> pointResult = game.playTheGame();
                return pointResult;
            });
            futures.add(future);

            //executorService.submit(game::playTheGame);
        }
        System.out.println("Number of Futures received: " + futures.size());

        executorService.shutdown();     // Shut down the executor service gracefully
        try {
            // Wait for all tasks to complete
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        List<HashMap<Integer, Integer>> processedResults = new ArrayList<>();

        for (Future<HashMap<Integer, Integer>> future : futures) {
            try {
                // Retrieve the result and ddd them to the processed results list
                HashMap<Integer, Integer> result = future.get();
                processedResults.add(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        // Sum the result up for each player and increment the value in totalPlayersScore
        System.out.println("Number of Results from futures received: " + processedResults.size());
        sumUpPlayersScore(processedResults);
        printWaveResults();
        // Eliminate Players and save the result of the wave to db
        ArrayList<Player> newPlayersList = eliminateWorstPlayers(numEliminated);
        for (Player p : players) {
            WaveResult waveRes = new WaveResult(this.waveId, p.id, getSpot(p.id), totalPlayersScore.get(p.id), wasEliminated(p.id));
            this.waveResults.add(waveRes);
        }
        waveResultToDb();
        // duplicate number of the n best players
       //ArrayList<Player> updatedPlayerList = duplicateBestPlayer(numEliminated, newPlayersList);

        //return newPlayersList;
        return duplicateBestPlayer(numEliminated, newPlayersList);
    }

    void sumUpPlayersScore(List<HashMap<Integer, Integer>> processedResults) {
        for (HashMap<Integer, Integer> result : processedResults) {
            for (Integer key : result.keySet()) {
                if (totalPlayersScore.containsKey(key)) {
                    // value of the totalPlayersScore will be incremented by score form every game.
                    int newVal = totalPlayersScore.get(key) + result.get(key);
                    totalPlayersScore.put(key, newVal);
                } else {
                    System.out.println("Can't increment player id: " + key);
                }
            }
        }
        orderedPlayersScore = new ArrayList<>(totalPlayersScore.entrySet());
        orderedPlayersScore.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

    }

    int getSpot(int pId) {
        for (int i = 1; i <= orderedPlayersScore.size(); i++) {
            if (pId == orderedPlayersScore.get(i-1).getKey()) return i;
        }
        return 0;
    }

    boolean wasEliminated(int pId) {
        int sz = orderedPlayersScore.size();
        for (int i = sz - 1; i > sz - 1 - numEliminated; i--) {
            if (pId == orderedPlayersScore.get(i).getKey()) return true;
        }
        return false;
    }

    void printWaveResults() {
        System.out.println("Result of the Wave:");
        for (int i = 1; i <= orderedPlayersScore.size(); i++) {
            var pId = orderedPlayersScore.get(i-1).getKey();
            var pScore = orderedPlayersScore.get(i-1).getValue();
            var pName = idPlayerMap.get(pId).name;
            System.out.println(i + ".  " + pName + " id:" + pId + " with " + pScore + " points");
        }
    }
    ArrayList<Player> eliminateWorstPlayers(int numPlayersToEliminate) {
        ArrayList<Player> newPlayersList = new ArrayList<>();
        for (int i = 0; i <= orderedPlayersScore.size() - 1 - numPlayersToEliminate; i++) {
            var pId = orderedPlayersScore.get(i).getKey();
            newPlayersList.add(idPlayerMap.get(pId));
        }
        return newPlayersList;
    }

    ArrayList<Player> duplicateBestPlayer(int numPlayersToDuplicate, ArrayList<Player> currentPlayers) {
        List<Experiment.PlayerRecord> newPlayersRecords = new ArrayList<>();
        // Get n best Players
        for (int i = 0; i < numPlayersToDuplicate; i++) {
            var pId = orderedPlayersScore.get(i).getKey();
            // create a new Player and add it to idPlayerMap and to playersList
            //var newP = new Player(idPlayerMap.get(pId)); // TODO CHANGE IT TO A ROBUST IF STATEMENT BASED ON newP getClass
            var newP = idPlayerMap.get(pId).copy();
            idPlayerMap.put(newP.id, newP);
            currentPlayers.add(newP);
            // Insert new player record to player table in database
            newPlayersRecords.add(new Experiment.PlayerRecord(experimentId, newP.id, newP.name, newP.strategyType.toString(),
                    newP.strategyTemper.toString(), newP.description));
        }
        playersToDb(newPlayersRecords);
        return currentPlayers;

    }


    void waveResultToDb() {
        String sql = """
                INSERT INTO waveresult 
                (waveId, playerId, spot, totalScore, wasEliminated) 
                VALUES (?, ?, ?, ?, ?)""";

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            this.connection.setAutoCommit(false); // Start transaction

            for (WaveResult rec : this.waveResults) {
                pstmt.setString(1, rec.waveId());
                pstmt.setInt(2, rec.playerId());
                pstmt.setInt(3, rec.spot());
                pstmt.setInt(4, rec.totalScore());
                pstmt.setBoolean(5, rec.wasEliminated());
                pstmt.addBatch(); // Add to batch for batch execution
            }
            pstmt.executeBatch(); // Execute batch
            this.connection.commit(); // Commit transaction
            //System.out.println("Records inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    void playersToDb(List<Experiment.PlayerRecord> playerRecords) {

        String sql = """
                INSERT INTO players
                (experimentId, playerId, name, waveJoined, strategyType, strategyTemper, description)
                VALUES (?, ?, ?, ?, ?, ?, ?)""";

        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            this.connection.setAutoCommit(false); // Start transaction

            for (Experiment.PlayerRecord rec : playerRecords) {
                pstmt.setString(1, rec.experimentId());
                pstmt.setInt(2, rec.playerId());
                pstmt.setString(3, rec.name());
                pstmt.setInt(4, globalWaveId);
                pstmt.setString(5, rec.strategyType());
                pstmt.setString(6, rec.strategyTemper());
                pstmt.setString(7, rec.description());
                pstmt.addBatch(); // Add to batch for batch execution
            }
            pstmt.executeBatch(); // Execute batch
            this.connection.commit(); // Commit transaction
            //System.out.println("Records inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}

record WaveResult (String waveId, int playerId, int spot, int totalScore, boolean wasEliminated) {}
