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
        Player p7 = new RandomDefector(0.9);
        Player p8 = new RandomDefector(0.7);
        Player p9 = new TitForTatBetrayalCounter(10);
        Player p10 = new TitForTatBetrayalCounter(50);
        Player p11 = new ForgivingTitForTat(5, true);
        Player p12 = new ForgivingTitForTat(5, false);
        Player p13 = new ForgivingTitForTat(15, true);
        Player p14 = new ForgivingTitForTat(15, false);
        Player p15 = new GrimTrigger();
        Player p16 = new AlwaysCooperates();

        ArrayList<Player> players = new ArrayList<>(List.of(p1, p2, p3, p4, p5, p6, p7, p8,
                p9, p10, p11, p12, p13, p14, p15, p16));

        //EXECUTION (fire up connection to db)
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/simulation_dev",
                System.getenv("MYSQL_USER"),
                System.getenv("MYSQL_PASS"));) {
            conn.setAutoCommit(false);

            Experiment experiment = new Experiment(players,3,5,1,
                    300, 5, 5, 1, 1.1, conn);
            experiment.playOutExperiment();

            // PLay out 3 Waves
//            for (int x = 0; x < 3; x++) {
//                Wave wave = new Wave(players, 300, 5, 1, 1.1, conn);
//                players = wave.playOutWave();
//            }


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
