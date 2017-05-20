package ksmori.hu.ait.spades.view;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ksmori.hu.ait.spades.R;
import ksmori.hu.ait.spades.model.Card;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerCardsFragment extends FragmentTagged implements View.OnTouchListener{

    public static final String TAG = "PlayerCardsFragment";
    public static final String CARDS_KEY = "CARDS_KEY";

    private static final int ROW_LENGTH = 7;
    private static final String[] rows = {"top", "bottom"};
    private static final String DEBUG_TAG = TAG;
    private List<Card> playerCards;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_player_cards, container, false);
        final Bundle argBundle = getArguments();
        if(argBundle != null) {
            playerCards = (List<Card>) argBundle.getSerializable(CARDS_KEY);
        } else {
            playerCards = new ArrayList<>(0);
        }
        return rootView;
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void onResume() {
        super.onResume();

        rootView.post(new Runnable() {
            @Override
            public void run() {
                loadContents();
                correctLayout();
                attachListeners();
            }
        });
    }

    private void attachListeners() {
        for (String row : rows) {
            for (int i = 1; i <= 7; i++) {
                ImageView iv = (ImageView) getView().findViewById(getResources()
                        .getIdentifier("iv_row_"+row+"_card"+i,"id","ksmori.hu.ait.spades"));
                if(iv.getWidth()!=0){
                    iv.setOnTouchListener(this);
//                    iv.setOnTouchListener((View.OnTouchListener)getActivity());
                }
            }
        }
    }

    private void correctLayout() {
        for (String row : rows) {
            int widthInPixels = 0;
            ImageView prev = null;
            for (int i = 1; i <= 7; i++) {
                ImageView iv = (ImageView) getView().findViewById(getResources()
                        .getIdentifier("iv_row_"+row+"_card"+i,"id","ksmori.hu.ait.spades"));
                if(iv.getWidth()!=0 && prev!=null) {
                    ((ViewGroup.MarginLayoutParams) prev.getLayoutParams())
                            .setMargins(0, 0, -iv.getWidth() / 2, 0);
                    prev.requestLayout();
                    widthInPixels -= prev.getWidth() / 2;
                }
                widthInPixels += iv.getWidth();
                prev = iv;
                Log.d(TAG,"cards" + i + " width = "+widthInPixels);
            }
            LinearLayout layout = (LinearLayout) getView().findViewById(getResources()
                    .getIdentifier("ll_card_row_" + row, "id", "ksmori.hu.ait.spades"));
            layout.getLayoutParams().width = widthInPixels;
            layout.requestLayout();
        }
    }

    private void loadContents(){
        for (int i = 0; i < playerCards.size(); i++) {
            String ivName = "iv_row_"+ rows[i/ROW_LENGTH] +"_card"+(i%ROW_LENGTH+1);
            ImageView iv = (ImageView) getView().findViewById(getResources().getIdentifier(
                    ivName,"id", "ksmori.hu.ait.spades"));
            iv.setImageResource(getResources().getIdentifier(
                    playerCards.get(i).getImageResourceName(),"drawable","ksmori.hu.ait.spades"));
        }
        for (int i = playerCards.size(); i < rows.length * ROW_LENGTH; i++) {
            String ivName = "iv_row_"+ rows[i/ROW_LENGTH] +"_card"+(i%ROW_LENGTH+1);
            ImageView iv = (ImageView) getView().findViewById(getResources().getIdentifier(
                    ivName,"id", "ksmori.hu.ait.spades"));
            iv.setImageDrawable(null);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // give the activity a chance to handle the touch
        if(((View.OnTouchListener) getActivity()).onTouch(v, event)){
            return true;
        }
        Log.d(DEBUG_TAG,"onTouch("+v.toString()+", ??)");
        return false;
    }
}
