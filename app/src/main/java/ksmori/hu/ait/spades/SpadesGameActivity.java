package ksmori.hu.ait.spades;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.List;

import ksmori.hu.ait.spades.model.Card;
import ksmori.hu.ait.spades.model.Player;
import ksmori.hu.ait.spades.presenter.SpadesPresenter;
import ksmori.hu.ait.spades.view.GameTableFragment;
import ksmori.hu.ait.spades.view.PlayerCardsFragment;

public class SpadesGameActivity extends AppCompatActivity implements SpadesGameScreen{

    private SpadesPresenter mSpadesPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spades_game);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpadesPresenter = new SpadesPresenter();
        mSpadesPresenter.startNewGame();
        // Tell all the Card ImageViews to have onTouch & onSwipe Listeners
    }

    public void setupGameTableFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_game_table_container, new GameTableFragment(),GameTableFragment.TAG);
        ft.commit();
    }


    @Override
    public void displayBidding() {
        Player player = mSpadesPresenter.getBiddingPlayer();
        List<Card> playerCards = mSpadesPresenter.getCards(player);
        setupPlayerCardsFragment(playerCards);

    }

    private void setupPlayerCardsFragment(List<Card> playerCards) {
    }

    @Override
    public void displayGame() {

    }
}
