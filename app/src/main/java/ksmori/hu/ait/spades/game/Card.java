package ksmori.hu.ait.spades.game;

import android.support.annotation.NonNull;

public class Card implements Comparable{

    public enum Suit {
        DIAMOND(0), CLUB(1), HEART(2), SPADE(3);

        private int rank;

        private Suit(int rank) {
            this.rank = rank;
        }

        public int getRank() {
            return rank;
        }

        // How Prof avoids storing Enum in Realm
        public static Suit fromInt(int rank) throws IllegalArgumentException {
            for (Suit s : Suit.values()) {
                if (s.rank == rank) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Only integers 0-3 correspond to suits");
        }
    }

    private int suit;
    private int value;

    public int getSuit() {
        return suit;
    }

    public int getValue() {
        return value;
    }


    public Card(int value, int suit) {
        this.suit = suit;
        this.value = value;
    }

    @Override

    public boolean equals(Object obj) {
        return compareTo(obj) == 0;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Card that = (Card) o; // may throw ClassCastException
        if(this.getValue() == that.getValue()){
            return this.getSuit() - that.getSuit();
        }
        return this.getValue() - that.getValue();
    }
}
