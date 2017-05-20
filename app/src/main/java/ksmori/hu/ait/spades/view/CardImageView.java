package ksmori.hu.ait.spades.view;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CardImageView extends AppCompatImageView{

    private static final String DEBUG_TAG = "CardImageView";

    public CardImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
            Log.d(DEBUG_TAG,"onTouchEvent("+this.toString()+", DOWN)");
        }
        // Don't let touch events trigger for views behind this one.
        return true;
    }
}
