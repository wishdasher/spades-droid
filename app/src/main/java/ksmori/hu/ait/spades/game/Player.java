package ksmori.hu.ait.spades.game;

import java.util.ArrayList;

public class Player {
    private ArrayList<Card> cards;
    public enum Id{
        NORTH(0,"North"), EAST(1,"East"), SOUTH(2,"South"), WEST(3,"West");
        private int value;
        private String text;

        private Id(int value, String text){
            this.value = value;
            this.text = text;
        }
        public int getValue() {
            return value;
        }

        // How Prof avoids storing Enum in Realm
        public static Id fromInt(int value) throws IllegalArgumentException {
            for (Id i : Id.values()) {
                if (i.value == value) {
                    return i;
                }
            }
            throw new IllegalArgumentException("Only integers 0-3 correspond to Player IDs");
        }

        public Id next(){
            return fromInt((this.value + 1)%4);
        }
    }
    private int id;
    private int bid;
    private int score;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
