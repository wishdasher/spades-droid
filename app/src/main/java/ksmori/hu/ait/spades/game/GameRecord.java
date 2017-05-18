package ksmori.hu.ait.spades.game;


import java.util.ArrayList;
import java.util.List;

/**
 * A way to display previous rounds.
 * Should have no references to game objects themselves.
 */

public class GameRecord {

    private Team teamA;
    private Team teamB;

    private List<Integer> scoresA;
    private List<Integer> scoresB;

    private GameRecord(Team a, Team b) {
        teamA = a;
        teamB = b;
        scoresA = new ArrayList<>();
        scoresB = new ArrayList<>();
    }

    public void update() {
        scoresA.add(teamA.getTotalScore());
        scoresB.add(teamB.getTotalScore());
    }
}
