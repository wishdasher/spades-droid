package ksmori.hu.ait.spades.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameModel {

    public static final int NUM_PLAYERS = 4;

    private Player north; //also the host
    private Player east;
    private Player south;
    private Player west;

    private boolean biddingPhase;
    private boolean spadesBroken;

    private List<Player> players;
    private Player startingPlayer;
    private Deck deck;

    private static GameModel instance = null;

    private GameModel() {
        initializePlayers();
        setPlayersPositions();
        deck = new Deck();
    }

    public static GameModel getInstance() {
        if (instance == null) {
            instance = new GameModel();
        }
        return instance;
    }

    private void initializePlayers() {
        //do something linky with players and devices, host player should be north

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
        for (Player player : players) {
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

}

