package com.walkap.x_android.fragment.days;

import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WednesdayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WednesdayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WednesdayFragment extends BaseDayFragment {

    public WednesdayFragment() {
        // Required empty public constructor
    }

    @Override
    public int getDay(){
        return 2;
    }

}
