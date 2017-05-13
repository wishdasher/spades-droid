package ksmori.hu.ait.spades.game;

import android.content.res.Resources;
import android.support.annotation.NonNull;

public class Card implements Comparable {



    public enum Suit {
        DIAMOND(0), CLUB(1), HEART(2), SPADE(3);

        private int rank;

        Suit(int r) {
            rank = r;
        }

        public int getRank() {
            return rank;
        }
    }

    private Suit suit;
    private int value; //2 through 14 because Ace is the highest value
    private int imageResource;

    public static final int ACE = 14;
    public static final int JACK = 11;
    public static final int QUEEN = 12;
    public static final int KING = 13;

    public static final int MIN_VALUE = 2;
    public static final int MAX_VALUE = 14;

    public Suit getSuit() {
        return suit;
    }

    public int getValue() {
        return value;
    }


    public Card(int value, Suit suit) {
        this.suit = suit;
        this.value = value;
        String res = determineImageName();
        Resources.getSystem().getIdentifier(res, "drawable", "ksmori.hu.ait.spades");
    }

    private String determineImageName() {
        String strTitle;
        switch (value) {
            case ACE:
                strTitle = "ace";
                break;
            case JACK:
                strTitle = "jack";
                break;
            case QUEEN:
                strTitle = "queen";
                break;
            case KING:
                strTitle = "king";
                break;
            default:
                strTitle = "" + value;
        }
        String strSuit = suit.name().toLowerCase();
        return "card_" + strTitle + "_of_" + strSuit + ".png";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Card)) {
            return false;
        }
        Card thatCard = (Card) obj;
        return this.suit == thatCard.suit && this.value == thatCard.value;
    }

    /**
     * Comparison for card display only
     * Implement separate comparator if card values needed to be compared for gameplay
     * @param o
     * @return
     */
    @Override
    public int compareTo(@NonNull Object o) {
        Card that = (Card) o; // may throw ClassCastException
        if (this.getSuit() == that.getSuit()) {
            return this.getValue() - that.getValue();
        }
        return this.getSuit().getRank() - that.getSuit().getRank();
    }

}
