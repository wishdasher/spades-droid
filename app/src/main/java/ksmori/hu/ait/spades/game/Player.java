package ksmori.hu.ait.spades.game;

import java.util.List;
import java.util.Set;

public class Player {

    private Set<Card> cards;
    private boolean isHostPlayer;
    private Player partner;

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
        public String getText() { return text; }

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
    private String name;

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public Set<Card> getCards(){
        return cards;
    }

    public void setCards(Set<Card> cards){
        this.cards = cards;
    }

}
