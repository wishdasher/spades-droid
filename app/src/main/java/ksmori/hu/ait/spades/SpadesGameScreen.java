package ksmori.hu.ait.spades;

import android.view.MotionEvent;
import android.view.View;

import ksmori.hu.ait.spades.model.Play;
import ksmori.hu.ait.spades.view.CardImageView;

public interface SpadesGameScreen {
    public void displayBidding();

    public void displayGame();

    public void setActiveCard(CardImageView civ);
    public CardImageView getActiveCard();

    public boolean getIsTouchable();
    public void setIsTouchable(boolean isTouchable);

    void setLastAction(int motionEventAction);
}
