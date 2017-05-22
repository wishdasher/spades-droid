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

public class SpadesGameRootLayout extends PercentRelativeLayout{

    private static final String DEBUG_TAG = "SpadesGameRootLayout";

    private boolean mIsDragging;
    private CardImageView activeCard;
    private List<PointF> testPts = new ArrayList<>();

//    private float mTouchSlopPx;
//    private static final int mTouchSlopDp = 50; //threshold value, in dp, above which to detect drag gesture

    public SpadesGameRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);
    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        DisplayMetrics metrics = new DisplayMetrics();
//        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        mTouchSlopPx = mTouchSlopDp * metrics.density;
//    }

    /**
     * This method JUST determines whether we want to intercept the motion.
     * If we return true, onTouchEvent will be called and we do the actual
     * scrolling there.
     *
     * This interception lets us drag the Active CardImageView in the same gesture
     * as clicking on a CardImageView in the PlayerCardsFragment
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);

        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the scroll.
            mIsDragging = false;
            return false; // Do not intercept touch event, let the child handle it
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                Log.d(DEBUG_TAG,"Intercepted MOVE: "+ev.toString());
                return true;
//                if (mIsDragging) {
//                    // We're currently dragging, so yes, intercept the touch event!
//                    return true;
//                }
//                // If the user has dragged her finger horizontally more than
//                // the touch slop, start the scroll
//                // left as an exercise for the reader
//                final float diff = calculateDistance(ev);
//                Log.d(DEBUG_TAG,String.format("diff = %f, slopPx = %f",diff,mTouchSlopPx));
//
//                // Touch slop should be calculated using ViewConfiguration constants.
//                if (diff > mTouchSlopPx) {
//                    // Start scrolling!
//                    mIsDragging = true;
//                    return true;
//                }
//                break;
            default:
                return false; // in general, we want to let most events be handled by children
        }
    }

    /**
     * Here we actually handle the touch event (e.g. if the action is ACTION_MOVE,
     * scroll this container).
     * This method will only be called if the touch event was intercepted in
     * onInterceptTouchEvent
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(activeCard == null){
            activeCard = (CardImageView) this.findViewById(R.id.iv_active_card);
        }
        activeCard.dispatchTouchEvent(ev);
        return true;
    }

//    private float calculateDistance(MotionEvent ev) {
//        //Always assuming ev.getPointerCount()==1
//        //I don't want to deal with multiple fingers on the screen at once
//        final int historySize = ev.getHistorySize();
//        Log.d(DEBUG_TAG,"history size = "+historySize);
//        if(historySize > 0) {
//            float xOld = ev.getHistoricalX(historySize - 1);
//            float yOld = ev.getHistoricalX(historySize - 1);
//            return (float) Math.sqrt((xOld - ev.getX()) * (xOld - ev.getX())
//                    + (yOld - ev.getY()) * (xOld - ev.getY()));
//        }
//        return 0;
//    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        fuckingDOIT(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        fuckingDOIT(canvas);
    }

    private void fuckingDOIT(Canvas canvas) {
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
