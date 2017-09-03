package com.walkap.x_android.fragment.days;

import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SaturdayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SaturdayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaturdayFragment extends BaseDayFragment {

    public SaturdayFragment() {
        // Required empty public constructor
    }

    @Override
    public int getDay(){
        return 5;
    }

}
