package main;

import main.strategies.*;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        long startTime = System.nanoTime();


        Player p1 = new Player();
        Player p2 = new AlwaysDefect();
        Player p3 = new ClassicTitForTat();
        Player p4 = new SuspiciousTitForTat();
        Player p5 = new TitForTwoTats();
        Player p6 = new ClassicPavlov();
        Player p7 = new RandomDefector(0.2);
        Player p8 = new RandomDefector(0.75);

        ArrayList<Player> players = new ArrayList<>(List.of(p1, p2, p3, p4, p5, p6, p7, p8));



        //EXECUTION (fire up connection to db)
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/simulation_dev",
                System.getenv("MYSQL_USER"),
                System.getenv("MYSQL_PASS"));) {
            conn.setAutoCommit(false);

            Wave wave = new Wave(players, conn);
            var waveRes = wave.playOutWave();
            System.out.println(wave.orderedPlayersScore);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        long executionTime
                = (endTime - startTime) / 1000000;

        System.out.println("The program takes "
                + executionTime + "ms");




//        games.forEach(System.out::println);
//        System.out.println(games.size());



//        Game game = new Game(p3, p8, 20, 2);
//        game.playTheGame();
//        System.out.println(game.leftPlayerPointsTotal + " | " + game.rightPlayerPointsTotal);




    }
}
