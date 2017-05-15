package ksmori.hu.ait.spades;

import android.content.res.Resources;
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

    public void setupFragments() {
        GameTableFragment mGameTableFragment = new GameTableFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_game_table_container, mGameTableFragment,GameTableFragment.TAG);
        ft.commit();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        final String[] positions = {"top","left","bottom","right"};
        for(String str : positions) {
            LinearLayout layout = (LinearLayout) findViewById(getResources().getIdentifier(
                    "ll_player_box_" + str, "id", "ksmori.hu.ait.spades")
            );
            ImageView imageView = (ImageView) findViewById(getResources().getIdentifier(
                    "iv_player_card_" + str, "id", "ksmori.hu.ait.spades")
            );
            LinearLayout textLayout = (LinearLayout) findViewById(getResources().getIdentifier(
                    "ll_info_box_" + str, "id", "ksmori.hu.ait.spades")
            );

            if (textLayout.getWidth() + imageView.getWidth() != 0) {
                layout.getLayoutParams().width = textLayout.getWidth() + imageView.getWidth()
                        + (int) getResources().getDimension(R.dimen.spacing_card_info);
                layout.requestLayout();
            } else {
                Log.d("SpadesGameActivity", "Failed to resize layout: could not retrieve view dimensions");
            }
        }
    }
}
