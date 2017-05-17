package ksmori.hu.ait.spades.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ksmori.hu.ait.spades.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends FragmentTagged {

    public static final String TAG = "SignInFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().findViewById(R.id.sign_in_button)
                .setOnClickListener((View.OnClickListener) getActivity());
    }


}
