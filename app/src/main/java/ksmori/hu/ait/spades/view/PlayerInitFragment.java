package ksmori.hu.ait.spades.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ksmori.hu.ait.spades.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerInitFragment extends FragmentTagged {

    public static final String TAG = "PlayerInitFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player_init, container, false);
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().findViewById(R.id.sign_out_button)
                .setOnClickListener((View.OnClickListener) getActivity());

        getView().findViewById(R.id.btn_temporary)
                .setOnClickListener((View.OnClickListener) getActivity());
    }

    public String getTmpText(){
        return ((EditText) getView().findViewById(R.id.et_hostname)).getText().toString();
    }
}
