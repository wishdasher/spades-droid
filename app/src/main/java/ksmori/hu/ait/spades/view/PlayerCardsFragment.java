package ksmori.hu.ait.spades.view;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ksmori.hu.ait.spades.R;
import ksmori.hu.ait.spades.SpadesGameScreen;
import ksmori.hu.ait.spades.model.Card;
import ksmori.hu.ait.spades.util.SpadesDebug;
import android.support.annotation.Nullable;

/**
 * A {@link Fragment} attached to a SpadesGameScreen
 * Displays a player's hand of cards; supports playing
 * cards by drag-and-drop into a sibling GameTable
 */
public class PlayerCardsFragment extends FragmentTagged
        implements View.OnTouchListener, CardsDisplay{

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

    public void attachListeners() {
        attachListeners(playerCards);
    }

    public void attachListeners(List<Card> playableCards) {
        for (String row : rows) {
            for (int i = 1; i <= ROW_LENGTH; i++) {
                CardImageView civ = (CardImageView) getView().findViewById(getResources()
                        .getIdentifier("iv_row_"+row+"_card"+i,"id","ksmori.hu.ait.spades"));
                if(civ.getCard()!=null && playableCards.contains(civ.getCard())){
                    civ.setOnTouchListener(this);
                } else {
                    civ.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                }
            }
        }
        getView().setOnTouchListener((View.OnTouchListener) mSpadesGameScreen);
    }

    public void detachListeners(){
        for (String row : rows) {
            for (int i = 1; i <= ROW_LENGTH; i++) {
                CardImageView civ = (CardImageView) getView().findViewById(getResources()
                        .getIdentifier("iv_row_"+row+"_card"+i,"id","ksmori.hu.ait.spades"));
                civ.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
            }
        }
        getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    private void correctLayout() {
        for (String row : rows) {
            int widthInPixels = 0;
            CardImageView prev = null;
            for (int i = 1; i <= 7; i++) {
                CardImageView civ = (CardImageView) getView().findViewById(getResources()
                        .getIdentifier("iv_row_"+row+"_card"+i,"id","ksmori.hu.ait.spades"));
                if(civ.getCard()!=null && prev!=null) {
                    civ.getLayoutParams().width=prev.getWidth();
                    ((ViewGroup.MarginLayoutParams) prev.getLayoutParams())
                            .setMargins(0, 0, -prev.getWidth() / 2, 0);
                    prev.requestLayout();
                    widthInPixels -= prev.getWidth() / 2;
                } else if(civ.getCard()==null && prev!=null && prev.getCard()!=null){
                    civ.getLayoutParams().width=0;
                    ((ViewGroup.MarginLayoutParams) prev.getLayoutParams())
                            .setMargins(0, 0, 0, 0);
                    prev.requestLayout();
                }
                widthInPixels += civ.getWidth();
                prev = civ;
//                Log.d(TAG,"cards" + i + " cumulative width = "+widthInPixels);
            }
            LinearLayout layout = (LinearLayout) getView().findViewById(getResources()
                    .getIdentifier("ll_card_row_" + row, "id", "ksmori.hu.ait.spades"));
            layout.getLayoutParams().width = widthInPixels;
            layout.requestLayout();
        }
    }

    private void loadContents(){
        Collections.sort(playerCards);
        for (int i = 0; i < playerCards.size(); i++) {
            String cName = "iv_row_"+ rows[i/ROW_LENGTH] +"_card"+(i%ROW_LENGTH+1);
            CardImageView c = (CardImageView) getView().findViewById(getResources().getIdentifier(
                    cName,"id", "ksmori.hu.ait.spades"));
            int resID = getResources().getIdentifier(
                    Card.determineImageName(playerCards.get(i)),"drawable","ksmori.hu.ait.spades");
            c.setImageResource(resID);
            c.setCard(playerCards.get(i));
        }
        for (int i = playerCards.size(); i < rows.length * ROW_LENGTH; i++) {
            String cName = "iv_row_"+ rows[i/ROW_LENGTH] +"_card"+(i%ROW_LENGTH+1);
            CardImageView c = (CardImageView) getView().findViewById(getResources().getIdentifier(
                    cName,"id", "ksmori.hu.ait.spades"));
            c.setImageDrawable(null);
            c.setCard(null);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_CANCEL){
            return true;
        }
        String actionStr = SpadesDebug.getActionString(event);
        Log.d(DEBUG_TAG,String.format("onTouch(%s,%s)",v.toString(),actionStr));
        if(mSpadesGameScreen.getIsTouchable() && v instanceof CardImageView
                && event.getActionMasked() == MotionEvent.ACTION_DOWN ){
            Log.d(DEBUG_TAG,"setting Screen Active Card");
            mSpadesGameScreen.setActiveCard((CardImageView) v);
            removeCardFromHand(((CardImageView) v));
            return true;
        } else {
            MotionEvent cancelEvt = MotionEvent.obtain(event);
            cancelEvt.setAction(MotionEvent.ACTION_CANCEL);
            v.dispatchTouchEvent(cancelEvt);
        }
        return true;
    }

    private void removeCardFromHand(CardImageView c) {
        int i = playerCards.indexOf(c.getCard());
        if(i >= 0) {
            cardToPlay = playerCards.remove(i);
            c.setImageDrawable(null);
            c.setCard(null);
            loadContents();
            correctLayout();
        } else {
            throw new IllegalArgumentException("Attempting to remove unheld card");
        }
    }

    @Override
    public void cancelCardSelection() {
        if(playerCards.indexOf(cardToPlay) < 0 && cardToPlay !=null){ // false if a card was played in sandbox
            playerCards.add(cardToPlay);
            Collections.sort(playerCards);
            loadContents();
            correctLayout();
        } else {
            Log.d(DEBUG_TAG,String.format("Invalid card cancellation: %s not held",cardToPlay));
//            CardImageView civ = ((SpadesGameScreen) getActivity()).getActiveCard();
//            Log.d(DEBUG_TAG,String.format("activeCard: width=%f,image=%d",civ.getWidth(),civ.getCard()));
//            throw new IllegalArgumentException("cancelCardSelection operating on faulty info");
        }
    }

    @Override
    public void removeSelectedCard() {
        cardToPlay = null;
    }
}
