package com.walkap.x_android.fragment;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;
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


public class ChoiceSchoolSubjectFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ChoiceSchoolSubjectFrag";

    private OnFragmentInteractionListener mListener;

    private EditText classroomEditText;
    private EditText schoolSubjectEditText;

    private ToggleButton toggleButton1;
    private ToggleButton toggleButton2;
    private ToggleButton toggleButton3;
    private ToggleButton toggleButton4;
    private ToggleButton toggleButton5;
    private ToggleButton toggleButton6;

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

        toggleButton1 = (ToggleButton) rootView.findViewById(R.id.toggleButton1);
        toggleButton2 = (ToggleButton) rootView.findViewById(R.id.toggleButton2);
        toggleButton3 = (ToggleButton) rootView.findViewById(R.id.toggleButton3);
        toggleButton4 = (ToggleButton) rootView.findViewById(R.id.toggleButton4);
        toggleButton5 = (ToggleButton) rootView.findViewById(R.id.toggleButton5);
        toggleButton6 = (ToggleButton) rootView.findViewById(R.id.toggleButton6);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        switch (day){
            case 0: toggleButton1.setChecked(true); break;
            case 1: toggleButton2.setChecked(true); break;
            case 2: toggleButton3.setChecked(true); break;
            case 3: toggleButton4.setChecked(true); break;
            case 4: toggleButton5.setChecked(true); break;
            case 5: toggleButton6.setChecked(true); break;
            default:                                break;
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (!validateForm()) {
            return ;
        }else{
            addScheduler();
        }

    }

    @Override
    public  void onStart(){
        super.onStart();

        toggleButton1.setOnClickListener(toggleButtonListener);
        toggleButton2.setOnClickListener(toggleButtonListener);
        toggleButton3.setOnClickListener(toggleButtonListener);
        toggleButton4.setOnClickListener(toggleButtonListener);
        toggleButton5.setOnClickListener(toggleButtonListener);
        toggleButton6.setOnClickListener(toggleButtonListener);
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
        myIntent.putExtra("day", actualDaySelected());
        getActivity().startActivity(myIntent);
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(classroomEditText.getText().toString())) {
            classroomEditText.setError(getResources().getString(R.string.required));
            result = false;
        } else {
            classroomEditText.setError(null);
        }

        if (TextUtils.isEmpty(schoolSubjectEditText.getText().toString())) {
            schoolSubjectEditText.setError(getResources().getString(R.string.required));
            result = false;
        } else {
            schoolSubjectEditText.setError(null);
        }

        return result;
    }

    final View.OnClickListener toggleButtonListener = new View.OnClickListener() {
        public void onClick(final View v) {
            switch(v.getId()) {
                case R.id.toggleButton1:
                    setAllLessOne(0);
                    break;
                case R.id.toggleButton2:
                    setAllLessOne(1);
                    break;
                case R.id.toggleButton3:
                    setAllLessOne(2);
                    break;
                case R.id.toggleButton4:
                    setAllLessOne(3);
                    break;
                case R.id.toggleButton5:
                    setAllLessOne(4);
                    break;
                case R.id.toggleButton6:
                    setAllLessOne(5);
                    break;
            }
        }
    };

    public void setAllLessOne(int one) {
        toggleButton1.setChecked(false);
        toggleButton2.setChecked(false);
        toggleButton3.setChecked(false);
        toggleButton4.setChecked(false);
        toggleButton5.setChecked(false);
        toggleButton6.setChecked(false);

        switch (one){
            case 0: toggleButton1.setChecked(true); break;
            case 1: toggleButton2.setChecked(true); break;
            case 2: toggleButton3.setChecked(true); break;
            case 3: toggleButton4.setChecked(true); break;
            case 4: toggleButton5.setChecked(true); break;
            case 5: toggleButton6.setChecked(true); break;
            default:                                break;
        }
    }

    public int actualDaySelected(){
        if(toggleButton1.isChecked()){
            return 0;
        }
        if(toggleButton2.isChecked()){
            return 1;
        }
        if(toggleButton3.isChecked()){
            return 2;
        }
        if(toggleButton4.isChecked()){
            return 3;
        }
        if(toggleButton5.isChecked()){
            return 4;
        }
        if(toggleButton6.isChecked()){
            return 5;
        }

        return -1;
    }

}
