package ksmori.hu.ait.spades.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ksmori.hu.ait.spades.R;

public class GameTableFragment extends FragmentTagged {

    public static final String TAG = "GameTableFragment";
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game_table, container, false);
        return rootView;
    }


    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void onResume() {
        super.onResume();

        // called when view becomes visible and has its final size
        rootView.post(new Runnable() {
            @Override
            public void run() {
                final String[] positions = {"top","left","bottom","right"};
                for(String str : positions) {
                    LinearLayout layout = (LinearLayout) getView().findViewById(getResources().getIdentifier(
                            "player_box_" + str, "id", "ksmori.hu.ait.spades")
                    );
                    ImageView imageView = (ImageView) layout.findViewById(getResources().getIdentifier(
                            "iv_player_card_" + str, "id", "ksmori.hu.ait.spades")
                    );
                    LinearLayout textLayout = (LinearLayout) layout.findViewById(getResources().getIdentifier(
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
        });
    }

}
