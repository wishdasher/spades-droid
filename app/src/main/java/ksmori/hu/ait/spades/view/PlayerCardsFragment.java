package ksmori.hu.ait.spades.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ksmori.hu.ait.spades.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerCardsFragment extends FragmentTagged {


    public static final String TAG = "PlayerCardsFragment";
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_player_cards, container, false);
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
                final String[] rows = {"top", "bottom"};
                for (String str : rows) {
                    HorizontalScrollView hsv = (HorizontalScrollView) getView()
                            .findViewById(getResources().getIdentifier(
                                    "hsv_card_row_" + str, "id", "ksmori.hu.ait.spades")
                            );
                    LinearLayout layout = (LinearLayout) hsv.
                            findViewById(getResources().getIdentifier(
                                "ll_card_row_" + str, "id", "ksmori.hu.ait.spades")
                    );
                    int widthInPixels = 0;
                    ImageView prev = null;
                    for (int i = 1; i <= 7; i++) {
                        ImageView iv = (ImageView) layout.findViewById(getResources().getIdentifier(
                                "iv_row_" + str + "_card" + i, "id", "ksmori.hu.ait.spades"));
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
                    layout.getLayoutParams().width = widthInPixels;
                    layout.requestLayout();
                }
            }
        });
    }

}
