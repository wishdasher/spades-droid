package ksmori.hu.ait.spades.game;

import java.util.ArrayList;

public class GameModel {
    private Player[] players; //indexed by ID
    private Card[] trick; //also indexed by player ID
    private boolean isSpadesBroken;
    private ArrayList<Card> deck; //nice methods for shuffling, hopefully

}
