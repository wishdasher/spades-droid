package ksmori.hu.ait.spades.game;


public enum Bid {
    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8),
    NINE(9), TEN(10), ELEVEN(11), TWELVE(12), THIRTEEN(13), NIL(10), NONE(0);

    private int value;

    Bid(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
