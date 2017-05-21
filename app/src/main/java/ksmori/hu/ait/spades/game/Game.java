package ksmori.hu.ait.spades.game;

import com.google.firebase.database.Exclude;

public class Game {

    public enum State {
        WAITING, SETUP, READY, BIDDING, PLAY, END
    }

    // UNCHANGING
    private String hostPlayer;

    // GAME WIDE
    private State state; //converted from enum
    private int roundNumber;

    // ROUND WIDE
    private boolean spadesBroken;
    private int trickNumber;
    private String lastPlayer;
    private String nextPlayer;
    private Card.Suit currentSuit; //converted from enum

    public Game(String hostPlayer, State state, int roundNumber, boolean spadesBroken, int trickNumber, String lastPlayer, String nextPlayer, Card.Suit currentSuit) {
        this.hostPlayer = hostPlayer;
        this.state = state;
        this.roundNumber = roundNumber;
        this.spadesBroken = spadesBroken;
        this.trickNumber = trickNumber;
        this.lastPlayer = lastPlayer;
        this.nextPlayer = nextPlayer;
        this.currentSuit = currentSuit;
    }

    public String getHostPlayer() {
        return hostPlayer;
    }

    public void setHostPlayer(String hostPlayer) {
        this.hostPlayer = hostPlayer;
    }

    public String getState() {
        // convert enum to string
        if (state == null) {
            return null;
        } else {
            return state.name();
        }
    }

    public void setState(String stateString) {
        // get enum from string
        if (stateString == null) {
            state = null;
        } else {
            state = State.valueOf(stateString);
        }
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public boolean isSpadesBroken() {
        return spadesBroken;
    }

    public void setSpadesBroken(boolean spadesBroken) {
        this.spadesBroken = spadesBroken;
    }

    public int getTrickNumber() {
        return trickNumber;
    }

    public void setTrickNumber(int trickNumber) {
        this.trickNumber = trickNumber;
    }

    public String getLastPlayer() {
        return lastPlayer;
    }

    public void setLastPlayer(String lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    public String getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(String nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public Card.Suit getCurrentSuit() {
        return currentSuit;
    }

    public void setCurrentSuit(Card.Suit currentSuit) {
        this.currentSuit = currentSuit;
    }

    @Exclude
    public State getStateValue() {
        return state;
    }


}

