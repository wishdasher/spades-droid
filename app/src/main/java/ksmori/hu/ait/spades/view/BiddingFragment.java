package ksmori.hu.ait.spades.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ksmori.hu.ait.spades.R;

/**
 * A {@link Fragment} in which users enter their.
 */
public class BiddingFragment extends Fragment {


    public BiddingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bidding, container, false);
    }

}
