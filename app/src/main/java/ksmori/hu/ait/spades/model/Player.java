package ksmori.hu.ait.spades.model;

import com.google.firebase.database.Exclude;

import java.util.Arrays;
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

    @Exclude public static final String NORTH_KEY = "north";
    @Exclude public static final String EAST_KEY = "east";
    @Exclude public static final String SOUTH_KEY = "south";
    @Exclude public static final String WEST_KEY = "west";
    @Exclude public static final String LEFT_KEY = "left";
    @Exclude public static final String TRICKS_KEY = "tricks";

    @Exclude public static final String HAND_KEY = "hand";
    @Exclude public static final String CARD_KEY = "played";

    @Exclude private static final List<String> dirOrder = Arrays.asList(NORTH_KEY, EAST_KEY, SOUTH_KEY, WEST_KEY);
    private String name;
    private Bid bid;
    private List<Card> hand;
    private int tricks;
    private String left;
    private String partner;
    private String right;

    private Card played;

    public Player() {
    }

    public Player(String name, Bid bid, List<Card> hand, int tricks, String left, String partner, String right) {
        this.name = name;
        this.bid = bid;
        this.hand = hand;
        this.tricks = tricks;
        this.left = left;
        this.partner = partner;
        this.right = right;
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

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    @Exclude
    public static String getLeftDir(String dir) {
        int index = dirOrder.indexOf(dir);
        int leftIndex = index + 1;
        return dirOrder.get(leftIndex % Game.NUM_PLAYERS);
    }

    @Exclude
    public static String getPartnerDir(String dir) {
        int index = dirOrder.indexOf(dir);
        int leftIndex = index + 2;
        return dirOrder.get(leftIndex % Game.NUM_PLAYERS);
    }

    @Exclude
    public static String getRightDir(String dir) {
        int index = dirOrder.indexOf(dir);
        int leftIndex = index + 3;
        return dirOrder.get(leftIndex % Game.NUM_PLAYERS);
    }
}
