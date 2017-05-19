package ksmori.hu.ait.spades;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import ksmori.hu.ait.spades.game.GameModel;
import ksmori.hu.ait.spades.view.GameTableFragment;
import ksmori.hu.ait.spades.view.PlayerCardsFragment;

public class SpadesGameActivity extends AppCompatActivity {

    private GameModel gameModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spades_game);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupFragments();
        GameModel.getInstance().start("N", "E", "S", "W");
    }

    public void setupFragments() {
        GameTableFragment mGameTableFragment = new GameTableFragment();
        PlayerCardsFragment mPlayerCardsFragment = new PlayerCardsFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_game_table_container, mGameTableFragment,GameTableFragment.TAG);
        ft.add(R.id.fl_player_cards_container,mPlayerCardsFragment,PlayerCardsFragment.TAG);
        ft.commit();
    }



}
