package ksmori.hu.ait.spades.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Card implements Comparable, Serializable {

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

    @Exclude public static final int ACE = 14;
    @Exclude public static final int JACK = 11;
    @Exclude public static final int QUEEN = 12;
    @Exclude public static final int KING = 13;

    @Exclude public static final int MIN_VALUE = 2;
    @Exclude public static final int MAX_VALUE = 14;


    public Card() {
    }

    public Card(int value, Suit suit) {
        if (value > MAX_VALUE || value < MIN_VALUE) {
            throw new IllegalArgumentException("Card value must be between "
                    + MIN_VALUE + " and " + MAX_VALUE +", incl.");
        }
        this.suit = suit;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getSuit() {
        // convert enum to string
        if (suit == null) {
            return null;
        } else {
            return suit.name();
        }
    }

    public void setSuit(String suitString) {
        // get enum from string
        if (suitString == null) {
            suit = null;
        } else {
            suit = Suit.valueOf(suitString);
        }
    }


    @Exclude
    public Suit getSuitValue() {
        return suit;
    }

    @Exclude
    public static String determineImageName(Card card) {
        String strTitle;
        switch (card.getValue()) {
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
                strTitle = "" + card.getValue();
        }
        String strSuit = card.getSuitValue().name().toLowerCase();
        String res = "card_" + strTitle + "_of_" + strSuit + "s";
//        return getResources().getIdentifier(res, "drawable", "ksmori.hu.ait.spades");
        return res;
    }

    @Exclude
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
    @Exclude
    @Override
    public int compareTo(@NonNull Object o) {
        Card that = (Card) o; // may throw ClassCastException
        if (this.getSuitValue() == that.getSuitValue()) {
            return this.getValue() - that.getValue();
        }
        return this.getSuitValue().getRank() - that.getSuitValue().getRank();
    }

}
