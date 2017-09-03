package com.walkap.x_android.fragment.days;

import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MondayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MondayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MondayFragment extends BaseDayFragment {

    private final String TAG = "MondayFragment";

    @Override
    public int getDay(){
        return 0;
    }

}