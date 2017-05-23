package ksmori.hu.ait.spades.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ksmori.hu.ait.spades.R;
import ksmori.hu.ait.spades.SpadesGameActivity;
import ksmori.hu.ait.spades.SpadesGameScreen;

public class SpadesGameRootLayout extends PercentRelativeLayout{

    private static final String DEBUG_TAG = "SpadesGameRootLayout";

    private CardImageView activeCard;
    private List<PointF> testPts = new ArrayList<>();
    private SpadesGameScreen spadesGameScreen;
    private static final float SLOP_RADIUS_PERCENT = SpadesGameActivity.SLOP_RADIUS_PERCENT;


    public SpadesGameRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        spadesGameScreen = (SpadesGameScreen) context;
//        this.setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
        if(activeCard == null){
            activeCard = (CardImageView) this.findViewById(R.id.iv_active_card);
        }
        
    }

    /**
     * This method JUST determines whether we want to intercept the motion.
     * If we return true, onTouchEvent will be called and we do the actual
     * scrolling there.
     *
     * This interception lets us drag the Active CardImageView in the same gesture
     * as clicking on a CardImageView in the PlayerCardsFragment
     */
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        final int action = MotionEventCompat.getActionMasked(ev);
//        // Always handle the case of the touch gesture being complete.
//        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
//            return false; // Do not intercept touch event, let the child handle it
//        }
//        switch (action) {
//            case MotionEvent.ACTION_MOVE:
//                Log.d(DEBUG_TAG,"Intercepted MOVE: "+ev.toString());
//                return true/*spadesGameScreen.getIsTouchable()*/;
//            default:
//                return false; // in general, we want to let most events be handled by children
//        }
//        Log.d(DEBUG_TAG,String.format("onInterceptTouchEvent(%s)",ev.toString()));
//        if(ev.getActionMasked() == MotionEvent.ACTION_CANCEL){
//            Log.d(DEBUG_TAG,String.format("onInterceptTouchEvent(%s) is CANCEL",ev.toString()));
//            return false;
//        } if (!spadesGameScreen.getIsTouchable()) {
//            Log.d(DEBUG_TAG, String.format("onInterceptTouchEvent(%s) ignored b/c !isTouchable", ev.toString()));
//            return false;
//        }
//        spadesGameScreen.setIsTouchable(false);
//        final int action = MotionEventCompat.getActionMasked(ev);
////        int[] loc = new int[2];
////        activeCard.getLocationOnScreen(loc);
//        switch (action) {
//            case MotionEvent.ACTION_MOVE: {
//                activeCard.getHeight();
//                activeCard.getWidth();
////                Log.d(DEBUG_TAG, String.format("ActiveCard location: %d <= X <= %d\n\t%d <= Y <= %d",
////                        loc[0], loc[0] + activeCard.getWidth(), loc[1], loc[1] + activeCard.getHeight()));
//                if (spadesGameScreen.isForActiveCard(ev)) {
//                    Log.d(DEBUG_TAG, "MOVE is in Card SLOP: " + ev.toString());
//                    spadesGameScreen.setIsTouchable(true);
//                    return true; //intercept; handle any MOVE near ActiveCard in Root.OnTouchEvent
//                }
//            }
//            case MotionEvent.ACTION_UP: {
////                Log.d(DEBUG_TAG, String.format("ActiveCard location: %d <= X <= %d\n\t%d <= Y <= %d",
////                        loc[0], loc[0] + activeCard.getWidth(), loc[1], loc[1] + activeCard.getHeight()));
//                if (spadesGameScreen.isForActiveCard(ev)) {
//                    Log.d(DEBUG_TAG, "UP is in Card SLOP: " + ev.toString());
//                    spadesGameScreen.setIsTouchable(true);
//                    return true; //intercepted, sent to Root.OnTouchEvent
//                }
//            }
//        }
//        spadesGameScreen.setIsTouchable(true);
//        return false; // in general, we want to let most events be handled by children
//    }
//
//    /**
//     * Here we actually handle the touch event (e.g. if the action is ACTION_MOVE, drag ActiveCard
//     * This method will be called if the touch event was intercepted in onInterceptTouchEvent
//     * OR if no children (nor this class's onTouchListener) handle the event
//     *
//     * injects an ACTION_CANCEL when returning true
//     */
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if(/*spadesGameScreen.getIsTouchable() && */ev.getActionMasked()==MotionEvent.ACTION_MOVE) {
//            Log.d(DEBUG_TAG,String.format("onTouchEvent(%s) for ActiveCard",ev.toString()));
//            spadesGameScreen.setLastAction(ev.getActionMasked());
//            activeCard.dispatchTouchEvent(ev);
//            return true;
//        }
////        spadesGameScreen.setIsTouchable(true);
//        return true; // everything not sent by onIntercept has this as its "last resort"
//    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        doIt(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        doIt(canvas);
    }

    private void doIt(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        float radius = 10;
        for(PointF p : testPts){
            canvas.drawCircle(p.x,p.y,radius,paint);
        }
    }

    public void addTestPt(PointF p){
        testPts.add(p);
        this.invalidate();
    }

    public void clearTestPts(){
        testPts.clear();
    }
}
