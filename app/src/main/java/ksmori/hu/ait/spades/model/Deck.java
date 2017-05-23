package ksmori.hu.ait.spades.model;

/*
 * A deck with the 52 standard cards
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private final List<Card> deck;

    public Deck() {
        deck = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (int value = Card.MIN_VALUE; value <= Card.MAX_VALUE; value++) {
                deck.add(new Card(value, suit));
            }
        }
        shuffle();
        checkRep();
    }

    private void checkRep() {
    }

    void shuffle() {
        Collections.shuffle(deck);
        checkRep();
    }

    public List<ArrayList<Card>> deal(int numPlayers) {
        List<ArrayList<Card>> hands = new ArrayList<>();
        int cardsEach = deck.size() / numPlayers;
        for (int p = 0; p < numPlayers; p++) {
            hands.add(new ArrayList<Card>(deck.subList(cardsEach * p, cardsEach * (p+1))));
        }
        return hands;
    }


}
