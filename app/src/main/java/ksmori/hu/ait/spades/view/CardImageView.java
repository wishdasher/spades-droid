package ksmori.hu.ait.spades.view;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import ksmori.hu.ait.spades.model.Card;
import ksmori.hu.ait.spades.util.SpadesDebug;

public class CardImageView extends AppCompatImageView{

    private static final String DEBUG_TAG = "CardImageView";

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    private Card card;

    public CardImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String actionStr = SpadesDebug.getActionString(event);
        Log.d(DEBUG_TAG,String.format("onTouchEvent(%s)",actionStr));
        return true; // Don't let touch events trigger for views behind this one.
    }

}
