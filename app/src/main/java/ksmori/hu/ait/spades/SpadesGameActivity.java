package ksmori.hu.ait.spades;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ksmori.hu.ait.spades.model.Card;
import ksmori.hu.ait.spades.model.Player;
import ksmori.hu.ait.spades.presenter.SpadesPresenter;
import ksmori.hu.ait.spades.util.SpadesDebug;
import ksmori.hu.ait.spades.view.CardImageView;
import ksmori.hu.ait.spades.view.GameTableFragment;
import ksmori.hu.ait.spades.view.PlayerCardsFragment;
import ksmori.hu.ait.spades.view.SpadesGameRootLayout;

public class SpadesGameActivity extends AppCompatActivity implements SpadesGameScreen,
        View.OnTouchListener{

    private static final String DEBUG_TAG = "SpadesGameActivity";
    private SpadesPresenter mSpadesPresenter;
    private SpadesGameRootLayout rootLayout;
    private List<Card> playerCards;

    private CardImageView activeCard;
    private int[] activeCardOriginalLocation;
    private float dX;
    private float dY;
    private int lastAction;

    private boolean isHost;
    private String position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spades_game);

        setupGameTableFragment();
        playerCards = new ArrayList<>();
        for (int i = Card.MIN_VALUE; i <= Card.MAX_VALUE; i++) {
            playerCards.add(new Card(i, Card.Suit.SPADE));
        }
        setupPlayerCardsFragment(playerCards);



        activeCard = (CardImageView) findViewById(R.id.iv_active_card);
        activeCard.setOnTouchListener(this);
        rootLayout = (SpadesGameRootLayout) findViewById(R.id.layout_root_game_activity);
        activeCard.bringToFront();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(activeCardOriginalLocation == null) {
            activeCardOriginalLocation = new int[2];
            activeCard.getLocationOnScreen(activeCardOriginalLocation); // mutator method
        }
    }

    public void setupGameTableFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_game_table_container, new GameTableFragment(),GameTableFragment.TAG);
        ft.commit();
    }

    private void setupPlayerCardsFragment(List<Card> playerCards) {
        PlayerCardsFragment pcf = new PlayerCardsFragment();
        Bundle argBundle = new Bundle();
        argBundle.putSerializable(PlayerCardsFragment.CARDS_KEY,(Serializable) playerCards);
        pcf.setArguments(argBundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_player_cards_container, pcf,PlayerCardsFragment.TAG);
        ft.commit();
    }


    @Override
    public void displayBidding() {
        Player player = mSpadesPresenter.getBiddingPlayer();
        List<Card> playerCards = mSpadesPresenter.getCards(player);
        setupPlayerCardsFragment(playerCards);

    }

    @Override
    public void displayGame() {

    }

    @Override
    public void setActiveCard(CardImageView civ, MotionEvent touchEvent) {
        if(civ == null){
            activeCard.setImageResource(0);// invalid/none
            SpadesGameRootLayout.LayoutParams p =
                    (SpadesGameRootLayout.LayoutParams) activeCard.getLayoutParams();
            p.width = 0;
            p.height = 0;
            activeCard.setLayoutParams(p);
        } else {
            activeCard.setLayoutParams(new SpadesGameRootLayout.LayoutParams(
                    civ.getWidth(),civ.getHeight()));
            activeCard.setImageResource(/*(Integer) civ.getTag()*/R.drawable.card_2_of_diamonds);
            activeCard.setTag(civ.getTag());

//            Log.d(DEBUG_TAG,String.format("Active Card: x.locOG=%d, y.locOG=%d",
//                    activeCardOriginalLocation[0],activeCardOriginalLocation[1]));
            int[] loc = new int[2];
            civ.getLocationOnScreen(loc);
            activeCard.setX(loc[0] - activeCardOriginalLocation[0]);
            activeCard.setY(loc[1] - activeCardOriginalLocation[1]);

            activeCard.bringToFront();
            activeCard.requestLayout();

            // Attempt to steal subsequent ACTION_MOVEs from other cardimageviews
            MotionEvent subscribe = MotionEvent.obtain(touchEvent);
            subscribe.setAction(MotionEvent.ACTION_DOWN);
            activeCard.dispatchTouchEvent(subscribe);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        String actionStr = SpadesDebug.getActionString(event);
        Log.d(DEBUG_TAG, String.format("onTouch(%s,%s)", v.toString(), actionStr));
        if(event.getActionMasked() == MotionEvent.ACTION_CANCEL){
            return true;
        } else if(v.getId() == R.id.iv_active_card){
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(DEBUG_TAG, "ActiveCard DOWN!");
                    dX = v.getX() - event.getRawX();
                    dY = v.getY() - event.getRawY();
                    lastAction = MotionEvent.ACTION_DOWN;
                    break;

                case MotionEvent.ACTION_MOVE:
                    v.setY(event.getRawY() + dY);
                    v.setX(event.getRawX() + dX);
                    lastAction = MotionEvent.ACTION_MOVE;
                    break;

                case MotionEvent.ACTION_UP:
                    if (lastAction == MotionEvent.ACTION_DOWN) {
                        Log.d(DEBUG_TAG, "ActiveCard clickUp!");
                    } else if (lastAction == MotionEvent.ACTION_MOVE){
                        Log.d(DEBUG_TAG, "ActiveCard dragUp!");
                    }
                    break;

                default:
                    return false;
            }
            return true;
        } else if(v instanceof CardImageView && v.getId() != R.id.iv_active_card
                && event.getActionMasked() != MotionEvent.ACTION_DOWN){
            return true;
        }
        return false;
    }
}
