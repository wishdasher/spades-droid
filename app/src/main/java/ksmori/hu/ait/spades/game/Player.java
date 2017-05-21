package ksmori.hu.ait.spades.game;

import com.google.firebase.database.Exclude;

import java.util.List;

public class Player {

    public enum Bid {
        ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8),
        NINE(9), TEN(10), ELEVEN(11), TWELVE(12), THIRTEEN(13), NIL(10);

        private int multiplier;

        Bid(int value) {
            this.multiplier = value;
        }

        public int getMultiplier() {
            return multiplier;
        }
    }

    @Exclude
    public static final String NORTH_KEY = "north";
    @Exclude
    public static final String EAST_KEY = "east";
    @Exclude
    public static final String SOUTH_KEY = "south";
    @Exclude
    public static final String WEST_KEY = "west";

    private String name;
    private Bid bid;
    private List<Card> hand;
    private int tricks;
    private String leftKey;
    private String partnerKey;
    private String rightKey;

    public Player() {
    }

    public Player(String name, Bid bid, List<Card> hand, int tricks, String leftKey, String partnerKey, String rightKey) {
        this.name = name;
        this.bid = bid;
        this.hand = hand;
        this.tricks = tricks;
        this.leftKey = leftKey;
        this.partnerKey = partnerKey;
        this.rightKey = rightKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String id) {
        this.name = name;
    }

    public String getBid() {
        // convert enum to string
        if (bid == null) {
            return null;
        } else {
            return bid.name();
        }
    }

    public void setBid(String bidString) {
        // get enum from string
        if (bidString == null) {
            bid = null;
        } else {
            bid = Bid.valueOf(bidString);
        }
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public int getTricks() {
        return tricks;
    }

    public void setTricks(int tricks) {
        this.tricks = tricks;
    }

    public String getLeftKey() {
        return leftKey;
    }

    public void setLeftKey(String leftKey) {
        this.leftKey = leftKey;
    }

    public String getPartnerKey() {
        return partnerKey;
    }

    public void setPartnerKey(String partnerKey) {
        this.partnerKey = partnerKey;
    }

    public String getRightKey() {
        return rightKey;
    }

    public void setRightKey(String rightKey) {
        this.rightKey = rightKey;
    }
}
