package ksmori.hu.ait.spades.game;

import java.util.Collections;
import java.util.List;

public class Player {

    private final String name;
    private final boolean isHostPlayer;
    private List<Card> hand;
    protected Player left;
    protected Player partner;
    protected Player right;
    private Bid bid;

    public Player(String name, boolean isHostPlayer) {
        this.name = name;
        this.isHostPlayer = isHostPlayer;
    }

    public void reset() {
        hand.clear();
        bid = Bid.NONE;
    }


    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
        Collections.sort(hand);
    }

    public boolean isHostPlayer() {
        return isHostPlayer;
    }

    public Bid getBid() {
        return bid;
    }

    public void setBid(Bid bid) {
        this.bid = bid;
    }
}
