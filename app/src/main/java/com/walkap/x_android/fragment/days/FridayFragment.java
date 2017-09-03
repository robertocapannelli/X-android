package com.walkap.x_android.fragment.days;

import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FridayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FridayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FridayFragment extends BaseDayFragment {

    public FridayFragment() {
        // Required empty public constructor
    }

    @Override
    public int getDay(){
        return 4;
    }

}
