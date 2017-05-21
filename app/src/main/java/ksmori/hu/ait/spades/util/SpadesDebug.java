package ksmori.hu.ait.spades.util;

import android.view.MotionEvent;

public abstract class SpadesDebug {
    public static final String getActionString(MotionEvent event){
        // Record method, action, and view in the debug log
        String actionStr = "";
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                actionStr+="DOWN";
                break;
            case MotionEvent.ACTION_UP:
                actionStr+="UP";
                break;
            case MotionEvent.ACTION_MOVE:
                actionStr+="MOVE";
                break;
            case MotionEvent.ACTION_OUTSIDE:
                actionStr+="OUTSIDE";
                break;
            case MotionEvent.ACTION_CANCEL:
                actionStr+="CANCEL";
                break;
            case MotionEvent.ACTION_HOVER_MOVE:
                actionStr+="HOVER_MOVE";
                break;
            case MotionEvent.ACTION_HOVER_ENTER:
                actionStr+="HOVER_ENTER";
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                actionStr+="HOVER_EXIT";
                break;
            case MotionEvent.ACTION_SCROLL:
                actionStr+="SCROLL";
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                actionStr+="POINTER_DOWN";
                break;
            case MotionEvent.ACTION_POINTER_UP:
                actionStr+="POINTER_UP";
                break;
            default:
                actionStr+="??";
                break;
        }
        return actionStr;
    }
}
