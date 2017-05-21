package ksmori.hu.ait.spades.view;


import android.app.Activity;
import android.content.Context;
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
import java.util.Collections;
import java.util.List;

import ksmori.hu.ait.spades.R;
import ksmori.hu.ait.spades.SpadesGameScreen;
import ksmori.hu.ait.spades.model.Card;
import ksmori.hu.ait.spades.presenter.CardPresenter;
import ksmori.hu.ait.spades.util.SpadesDebug;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerCardsFragment extends FragmentTagged
        implements View.OnTouchListener, CardPresenter{

    public static final String TAG = "PlayerCardsFragment";
    public static final String CARDS_KEY = "CARDS_KEY";

    private static final int ROW_LENGTH = 7;
    private static final String[] rows = {"top", "bottom"};
    private static final String DEBUG_TAG = TAG;

    private List<Card> playerCards;
    private Card cardToPlay;
    private View rootView;
    private SpadesGameScreen mSpadesGameScreen;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        mSpadesGameScreen = (SpadesGameScreen) context;
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
            String cName = "iv_row_"+ rows[i/ROW_LENGTH] +"_card"+(i%ROW_LENGTH+1);
            CardImageView c = (CardImageView) getView().findViewById(getResources().getIdentifier(
                    cName,"id", "ksmori.hu.ait.spades"));
            int resID = getResources().getIdentifier(
                    Card.determineImageName(playerCards.get(i)),"drawable","ksmori.hu.ait.spades");
            c.setImageResource(resID);
            c.setTag(resID);
            c.setCard(playerCards.get(i));
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
        // give the parent activity a chance to handle the touch first
        if(((View.OnTouchListener) getActivity()).onTouch(v, event)){
            return true;
        }
        String actionStr = SpadesDebug.getActionString(event);
        Log.d(DEBUG_TAG,String.format("onTouch(%s,%s)",v.toString(),actionStr));

        if(v instanceof CardImageView){
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(DEBUG_TAG,"setting Screen Active Card");
                    mSpadesGameScreen.setActiveCard((CardImageView) v, event);
                    removeCardFromHand(((CardImageView) v).getCard());
                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    private void removeCardFromHand(Card c) {
        int i = playerCards.indexOf(c);
        if(i >= 0) {
            cardToPlay = playerCards.remove(i);
            loadContents();
        } else {
            throw new IllegalArgumentException("Attempting to remove unheld card");
        }
    }

    @Override
    public void cancelCardSelection() {
        playerCards.add(cardToPlay);
        Collections.sort(playerCards);
        loadContents();
    }
}
