package ksmori.hu.ait.spades;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ksmori.hu.ait.spades.model.Card;
import ksmori.hu.ait.spades.model.Player;
import ksmori.hu.ait.spades.presenter.SpadesPresenter;
import ksmori.hu.ait.spades.view.GameTableFragment;
import ksmori.hu.ait.spades.view.PlayerCardsFragment;

public class SpadesGameActivity extends AppCompatActivity implements SpadesGameScreen,
        View.OnTouchListener{

    private static final String DEBUG_TAG = "SpadesGameActivity";
    private SpadesPresenter mSpadesPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spades_game);

        setupGameTableFragment();
        List<Card> playerCards = new ArrayList<>();
        for (int i = Card.MIN_VALUE; i <= Card.MAX_VALUE; i++) {
            playerCards.add(new Card(i, Card.Suit.SPADE));
        }
        setupPlayerCardsFragment(playerCards);
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
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(DEBUG_TAG,"onTouch("+v.toString()+", ??)");
        return false;
    }
}
