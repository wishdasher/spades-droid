package ksmori.hu.ait.spades.game;

/*
 * A deck with the 52 standard cards
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Deck {

    private List<Card> deck;

    public Deck() {
        deck = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (int value = Card.MIN_VALUE; value < Card.MAX_VALUE; value++) {
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

    List<Set<Card>> deal(int numPlayers) {
        List<Set<Card>> hands = new ArrayList<>();
        int cardsEach = deck.size() / numPlayers;
        for (int p = 0; p < numPlayers; p++) {
            hands.add(new HashSet<Card>(deck.subList(cardsEach * p, cardsEach * (p+1))));
        }
        return hands;
    }


}
