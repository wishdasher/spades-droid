package ksmori.hu.ait.spades.game;

public class GameModel {

    public static final int NUM_PLAYERS = 4;

    private Player[] players; //indexed by ID
    private Card[] trick; //also indexed by player ID
    private boolean isSpadesBroken;
    private Deck deck;

    // BEGIN Singleton Design
    private static GameModel instance = null;

    private GameModel() {
        initializePlayers();
        deck = new Deck();
    }

    public static GameModel getInstance() {
        if (instance == null) {
            instance = new GameModel();
        }

        return instance;
    }
    // END Singleton Design

    private void initializePlayers() {
        players = new Player[NUM_PLAYERS];
        for ( Player.Id i: Player.Id.values() ) {
            players[i.getValue()] = new Player(i.getValue(),i.getText());
        }
    }


}
