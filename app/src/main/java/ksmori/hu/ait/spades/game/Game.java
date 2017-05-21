package ksmori.hu.ait.spades.game;

import com.google.firebase.database.Exclude;

import java.util.List;

public class Game {

    public enum State {
        WAITING, SETUP, READY, BIDDING, PLAY, END
    }

    // UNCHANGING
    private String hostPlayer;
    private Player north;
    private Player east;
    private Player south;
    private Player west;
    private Team teamNS;
    private Team teamEW;

    // GAME WIDE
    private State state; //converted from enum
    private int roundNumber;
    private GameRecord gameRecord;

    // ROUND WIDE
    private boolean spadesBroken;
    private int trickNumber;
    private String lastPlayer;
    private String nextPlayer;
    private Card.Suit currentSuit; //converted from enum
    private List<Play> plays;

    public Game() {

    }

    public Game(String hostPlayer, State state, int roundNumber, GameRecord gameRecord, boolean spadesBroken, int trickNumber, String lastPlayer, String nextPlayer, Card.Suit currentSuit, List<Play> plays) {
        this.hostPlayer = hostPlayer;
        this.state = state;
        this.roundNumber = roundNumber;
        this.gameRecord = gameRecord;
        this.spadesBroken = spadesBroken;
        this.trickNumber = trickNumber;
        this.lastPlayer = lastPlayer;
        this.nextPlayer = nextPlayer;
        this.currentSuit = currentSuit;
        this.plays = plays;
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

    public String getCurrentSuit() {
        // convert enum to string
        if (currentSuit == null) {
            return null;
        } else {
            return currentSuit.name();
        }
    }

    public void setCurrentSuit(String currentSuitString) {
        // get enum from string
        if (currentSuitString == null) {
            currentSuit = null;
        } else {
            currentSuit = Card.Suit.valueOf(currentSuitString);
        }
    }

    public Team getTeamNS() {
        return teamNS;
    }

    public void setTeamNS(Team teamNS) {
        this.teamNS = teamNS;
    }

    public Team getTeamEW() {
        return teamEW;
    }

    public void setTeamEW(Team teamEW) {
        this.teamEW = teamEW;
    }

    public GameRecord getGameRecord() {
        return gameRecord;
    }

    public void setGameRecord(GameRecord gameRecord) {
        this.gameRecord = gameRecord;
    }

    public List<Play> getPlays() {
        return plays;
    }

    public void setPlays(List<Play> plays) {
        this.plays = plays;
    }

    @Exclude
    public State getStateValue() {
        return state;
    }

    @Exclude
    public Card.Suit getCurrentSuitValue() {
        return currentSuit;
    }

}

