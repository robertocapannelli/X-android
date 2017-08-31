package com.walkap.x_android.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.walkap.x_android.activity.addScheduleActivity;

import com.walkap.x_android.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChoiceSchoolSubjectFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChoiceSchoolSubjectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChoiceSchoolSubjectFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ChoiceSchoolSubjectFragment";

    private OnFragmentInteractionListener mListener;

    private EditText classroomEditText;
    private EditText schoolSubjectEditText;

    public ChoiceSchoolSubjectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_choice_school_subject, container, false);

        classroomEditText = (EditText) rootView.findViewById(R.id.classroomEditText);
        schoolSubjectEditText = (EditText) rootView.findViewById(R.id.schoolSubjectEditText);

        //Button listener
        Button btn = (Button) rootView.findViewById(R.id.saveButton);
        btn.setOnClickListener(this);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (!validateForm()) {
            return;
        }else{
            addScheduler();
        }

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void addScheduler() {

        String classroomString = classroomEditText.getText().toString();
        String schoolSubjectString = schoolSubjectEditText.getText().toString();

        Intent myIntent = new Intent(getActivity(), addScheduleActivity.class);
        myIntent.putExtra("classroom", classroomString); //Optional parameters
        myIntent.putExtra("schoolSubject", schoolSubjectString);
        getActivity().startActivity(myIntent);



    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(classroomEditText.getText().toString())) {
            classroomEditText.setError("Required");
            result = false;
        } else {
            classroomEditText.setError(null);
        }

        if (TextUtils.isEmpty(schoolSubjectEditText.getText().toString())) {
            schoolSubjectEditText.setError("Required");
            result = false;
        } else {
            schoolSubjectEditText.setError(null);
        }

        return result;
    }

}
