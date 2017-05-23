package ksmori.hu.ait.spades.model;

import com.google.firebase.database.Exclude;


public class GameVariable {

    @Exclude public static final String STATE_KEY = "state";
    @Exclude public static final String ROUND_KEY = "roundNumber";
    @Exclude public static final String TRICK_NUMBER_KEY = "trickNumber";
    @Exclude public static final String SPADES_BROKEN_KEY = "spadesBroken";
    @Exclude public static final String CURRENT_SUIT_KEY = "currentSuit";
    @Exclude public static final String LAST_PLAYER_KEY = "lastPlayer";
    @Exclude public static final String NEXT_PLAYER_KEY = "nextPlayer";

    @Exclude public static final String KEY = "variables";

    private Game.State state; //converted from enum
    private int roundNumber;
    private int trickNumber;
    private boolean spadesBroken;
    private Card.Suit currentSuit; //converted from enum
    private String lastPlayer;
    private String nextPlayer;

    public GameVariable() {
    }

    public GameVariable(Game.State state, int roundNumber, int trickNumber,
                        boolean spadesBroken, Card.Suit currentSuit, String lastPlayer, String nextPlayer) {
        this.state = state;
        this.roundNumber = roundNumber;
        this.trickNumber = trickNumber;
        this.spadesBroken = spadesBroken;
        this.currentSuit = currentSuit;
        this.lastPlayer = lastPlayer;
        this.nextPlayer = nextPlayer;
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
            state = Game.State.valueOf(stateString);
        }
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

    @Exclude
    public Game.State getStateValue() {
        return state;
    }

    @Exclude
    public Card.Suit getCurrentSuitValue() {
        return currentSuit;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getTrickNumber() {
        return trickNumber;
    }

    public void setTrickNumber(int trickNumber) {
        this.trickNumber = trickNumber;
    }

    public boolean isSpadesBroken() {
        return spadesBroken;
    }

    public void setSpadesBroken(boolean spadesBroken) {
        this.spadesBroken = spadesBroken;
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
}

