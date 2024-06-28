package main;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Wave {

    // Additional arguments for Game matches and rounds
    public Wave(ArrayList<Player> players, Connection connection) {
        this.players = players;
        this.connection = connection;

        this.games =  Game.createGamesList(players, 10, 100, connection);
    }

    ArrayList<Player> players;
    ArrayList<Game> games;
    Connection connection;

    // TODO:Hash Mapa na sumowanie punktów dla każdego gracza podczas Fali. Inicjalizacja dla kazdego gracza po zero.
    //  Sumowanie wyników z każdej gry i otrzymane ostatecznych wyników Fali.


    void playOutWave() {
        // Multi Thread
        //ExecutorService executorService = Executors.newFixedThreadPool(games.size());
        ExecutorService executorService = Executors.newCachedThreadPool();

        for (Game game : games) {
            executorService.submit(game::playTheGame);
        }

        executorService.shutdown();     // Shut down the executor service gracefully
        try {
            // Wait for all tasks to complete
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

    }





}
