package ksmori.hu.ait.spades.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ksmori.hu.ait.spades.R;

public class GameTableFragment extends Fragment {

    public static final String TAG = "GameTableFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game_table, container, false);

        return rootView;
    }
}
