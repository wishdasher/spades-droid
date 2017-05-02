package ksmori.hu.ait.spades.game;

import java.util.ArrayList;
import java.util.Random;

public class GameModel {
    private Player[] players; //indexed by ID
    private Card[] trick; //also indexed by player ID
    private boolean isSpadesBroken;
    private ArrayList<Card> deck; //nice methods for shuffling, hopefully

    private void initializeDeck(){
        for(Card.Suit s : Card.Suit.values()) {
            for (int j = 0; j < 13; j++) {
                deck.add(new Card(j,s.getRank()))
            }
        }
    }

    private void shuffleDeck(){
        Random rng = new Random();
        // Do the Fisher-Yates Shuffle
        int indexToShift;
        Card tmpSwap;
        for (int i = 52; i > 0; i++) {
            indexToShift = rng.nextInt(i);
            tmpSwap = deck.get(i);
            deck.set(i,deck.get(indexToShift));
            deck.set(indexToShift,tmpSwap);
        }
    }

}
