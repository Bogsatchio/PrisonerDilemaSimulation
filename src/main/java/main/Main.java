package main;

import main.strategies.*;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // String experimentId, String, desc,
        // int cooperationPoints, int oneSideBetrayalPoints, int twoSideBetrayalPoints, int rounds, int matches, int waves, int numEliminated,
        // double winnersPremium,

        long startTime = System.nanoTime();

        ArrayList<Player> players = instantiatePlayers();
        ArrayList<String> stringArgs = parseStringArgs(args);
        ArrayList<Integer> intArgs = parseIntArgs(args);
        double doubleArg = parseDoubleArg(args);

        System.out.println(stringArgs);
        System.out.println(intArgs);
        System.out.println(doubleArg);


        //EXECUTION (fire up connection to db)
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/simulation_dev",
                System.getenv("MYSQL_USER"),
                System.getenv("MYSQL_PASS"));) {
            conn.setAutoCommit(false);

//            String desc = "A classic game with standard scoring and all possible players";
//            Experiment experiment = new Experiment("Classic", desc, players,3,7,1,
//                    100, 3, 2, 3, 1, conn);
            Experiment experiment = new Experiment(stringArgs.get(0), stringArgs.get(1), players,
                    intArgs.get(0),intArgs.get(1),intArgs.get(2), intArgs.get(3), intArgs.get(4), intArgs.get(5), intArgs.get(6),
                    doubleArg, conn);
            experiment.playOutExperiment();





        } catch (SQLException e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        long executionTime
                = (endTime - startTime) / 1000000;

        System.out.println("The program takes "
                + executionTime + "ms");


    }

    static ArrayList<String> parseStringArgs(String[] args) {
        return new ArrayList<>(List.of(args[0], args[1]));
    }

    static ArrayList<Integer> parseIntArgs(String[] args) {
        ArrayList<Integer> intArgs = new ArrayList<>();
        for (int i = 2; i <= 8; i++) {
            intArgs.add(Integer.parseInt(args[i]));
        }
        return  intArgs;
    }
    static double parseDoubleArg(String[] args) {
        return Double.parseDouble(args[9]);
    }

    static ArrayList<Player> instantiatePlayers() {
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
        Player p17 = new ReverseTitForTat();

        ArrayList<Player> players = new ArrayList<>(List.of(p1, p2, p3, p4, p5, p6, p7, p8,
                p9, p12, p15, p16, p17));

        return players;
    }

}
