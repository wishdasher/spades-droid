package ksmori.hu.ait.spades;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ksmori.hu.ait.spades.view.GameTableFragment;

public class SpadesGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spades_game);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupFragments();
    }

    // I have ZERO CLUE why nothing shows up
    public void setupFragments() {
        GameTableFragment mGameTableFragment = new GameTableFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_game_table_container, mGameTableFragment,GameTableFragment.TAG);
        ft.commit();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.ll_player_box_top);
        ImageView imageView = (ImageView) findViewById(R.id.iv_player_card_top);
        LinearLayout textLayout = (LinearLayout) findViewById(R.id.ll_info_box_top);

        if(textLayout.getWidth()+imageView.getWidth() != 0) {
            layout.getLayoutParams().width = textLayout.getWidth() + imageView.getWidth()
                    + (int)getResources().getDimension(R.dimen.spacing_card_info);
            layout.requestLayout();
        } else {
            Log.d("SpadesGameActivity","Failed to resize layout: could not retrieve view dimensions");
        }
    }
}
