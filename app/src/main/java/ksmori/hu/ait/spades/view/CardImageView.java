package ksmori.hu.ait.spades.view;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class CardImageView extends AppCompatImageView {


    private GestureDetector touchHandler;

    public CardImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        touchHandler=new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent event) {
                // triggers first for both single tap and long press
                bringToFront();
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                resetHandPosition(event);
                return true;
            }

        });
    }

    private void resetHandPosition(MotionEvent event) {
        
    }



}
