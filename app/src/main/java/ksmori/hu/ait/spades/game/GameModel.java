package ksmori.hu.ait.spades.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameModel {

    public static final int NUM_PLAYERS = 4;

    private Player[] players; //indexed by ID
    private Card[] trick; //also indexed by player ID
    private boolean isSpadesBroken;
    private List<Card> deck; //nice methods for shuffling, hopefully

    // BEGIN Singleton Design
    private static GameModel instance = null;

    private GameModel() {
        initializeDeck();
        initializePlayers();
    }

    public static GameModel getInstance() {
        if (instance == null) {
            instance = new GameModel();
        }

        return instance;
    }
    // END Singleton Design

    private void initializeDeck(){
        deck = new ArrayList<Card>();
        for(Card.Suit s : Card.Suit.values()) {
            for (int j = 0; j < 13; j++) {
                deck.add(new Card(j,s.getRank()));
            }
        }
    }


    private void initializePlayers() {
        players = new Player[NUM_PLAYERS];
        for ( Player.Id i: Player.Id.values() ) {
            players[i.getValue()] = new Player(i.getValue(),i.getText());
        }
    }

    private void shuffleDeck(){
        Random rng = new Random();
        // Do the Fisher-Yates Shuffle
        int indexToShift;
        Card tmpSwap;
        for (int i = deck.size(); i > 0; i++) {
            indexToShift = rng.nextInt(i);
            tmpSwap = deck.get(i);
            deck.set(i,deck.get(indexToShift));
            deck.set(indexToShift,tmpSwap);
        }
    }

    private void dealCardsToPlayers(){
        shuffleDeck();
        int numCardsPerPlayer = deck.size() / NUM_PLAYERS;
        for (int i = 0; i < NUM_PLAYERS; i++) {
            players[i].setCards(deck.subList(i*numCardsPerPlayer,(i+1)*numCardsPerPlayer));
        }
    }

}
