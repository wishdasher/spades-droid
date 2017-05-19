package ksmori.hu.ait.spades.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameModel {

    public static final int NUM_PLAYERS = 4;

    private Player north; //also the host
    private Player east;
    private Player south;
    private Player west;

    private Team teamNS;
    private Team teamEW;

    private boolean biddingPhase;
    private boolean spadesBroken;

    private List<Player> players;

    private Player startingPlayer;
    private Player nextPlayer;

    private int roundCount;
    private int trickCount;

    private Deck deck = new Deck();

    private static GameModel instance = null;

    private GameModel() {
        //needs to be started
    }

    public static GameModel getInstance() {
        if (instance == null) {
            instance = new GameModel();
        }
        return instance;
    }

    public void start(String p1, String p2, String p3, String p4) {
        initializePlayers(p1, p2, p3, p4);
        setPlayersPositions();
        roundCount = 0;
        resetGame();
    }

    private void initializePlayers(String p1, String p2, String p3, String p4) {
        north = new Player(p1, true);
        east = new Player(p2, false);
        south = new Player(p3, false);
        west = new Player(p4, false);

        teamNS = new Team(north, south);
        teamEW = new Team(east, west);

        players = new ArrayList<>(Arrays.asList(north, east, south, west));
    }

    private void setPlayersPositions() {
        north.left = east;
        north.partner = south;
        north.right = west;
        east.left = south;
        east.partner = west;
        east.right = north;
        south.left = west;
        south.partner = north;
        south.right = east;
        west.left = north;
        west.partner = east;
        west.right = south;
    }

    public void resetGame() {
        roundCount++;
        trickCount = 0;
        if (startingPlayer == null) {
            startingPlayer = north;
        } else {
            startingPlayer = startingPlayer.left;
        }
        nextPlayer = startingPlayer;
        for (Player player : players) {
            //clears hand and sets bid to none
            player.reset();
        }
        biddingPhase = true;
        spadesBroken = false;
        deck.shuffle();
        List<List<Card>> hands = deck.deal(NUM_PLAYERS);
        for (int p = 0; p < players.size(); p++) {
            players.get(p).setHand(hands.get(p));
        }
    }

    public Player getNorth() {
        return north;
    }

    public Player getEast() {
        return east;
    }

    public Player getSouth() {
        return south;
    }

    public Player getWest() {
        return west;
    }

    public Team getTeamNS() {
        return teamNS;
    }

    public Team getTeamEW() {
        return teamEW;
    }

    public boolean isBiddingPhase() {
        return biddingPhase;
    }

    public boolean isSpadesBroken() {
        return spadesBroken;
    }

}

