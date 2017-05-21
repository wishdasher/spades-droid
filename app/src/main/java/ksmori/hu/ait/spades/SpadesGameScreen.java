package ksmori.hu.ait.spades;

import android.view.MotionEvent;

import ksmori.hu.ait.spades.view.CardImageView;

public interface SpadesGameScreen {
    public void displayBidding();

    public void displayGame();

    public void setActiveCard(CardImageView civ, MotionEvent touchEvent);
}
