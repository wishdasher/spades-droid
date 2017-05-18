package ksmori.hu.ait.spades.view;

import android.support.v4.app.Fragment;

public abstract class FragmentTagged extends Fragment {

    public abstract String getTAG(); // getTag is reserved but only works for static XML tags
}
