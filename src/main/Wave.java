package main;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.*;

public class Wave {

    // Additional arguments for Game matches and rounds
    public Wave(ArrayList<Player> players, Connection connection) {
        this.players = players;
        this.connection = connection;

        this.games =  Game.createGamesList(players, 10, 100, connection);
        this.totalPlayersScore = initializeTotalPlayersScore();
        this.orderedPlayersScore = new ArrayList<>();
    }

    ArrayList<Player> players;
    Map<Integer, Integer> totalPlayersScore;
    List<Map.Entry<Integer, Integer>> orderedPlayersScore;
    ArrayList<Game> games;
    Connection connection;

    // TODO: Print out the result of the Wave. Eliminate the weakest player


    Map<Integer, Integer> initializeTotalPlayersScore() {
        HashMap<Integer, Integer> scoreBoard = new HashMap<>();
        for (Player p : players) {
            scoreBoard.put(p.id, 0);
        }

        return scoreBoard;
    }

    List<HashMap<Integer, Integer>> playOutWave() {
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
        System.out.println(processedResults);
        sumUpPlayersScore(processedResults);



        return processedResults;
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

}
