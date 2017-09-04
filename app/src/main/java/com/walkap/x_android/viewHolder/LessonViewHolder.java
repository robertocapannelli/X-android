package com.walkap.x_android.viewHolder;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.walkap.x_android.R;

public class LessonViewHolder extends RecyclerView.ViewHolder {

    private final String TAG = "LessonViewHolder";

    public TextView classroom, schoolSubject, start, end;

    public LessonViewHolder(View itemView, TextView classroom, TextView schoolSubject) {
        super(itemView);
        this.classroom = classroom;
        this.schoolSubject = schoolSubject;
    }

    public LessonViewHolder(View itemView){
        super(itemView);

        classroom = (TextView) itemView.findViewById(R.id.optionsClassroomTextView);
        schoolSubject = (TextView) itemView.findViewById(R.id.optionsSchoolSubjectTextView);
        start = (TextView) itemView.findViewById(R.id.optionsHoursBeginningTextView);
        end = (TextView) itemView.findViewById(R.id.optionsHoursEndTextView);

        Log.d(TAG, "Set views: " + classroom + schoolSubject + start + end);
    }

    /*public void bindToPost(Scheduler schedule){
        classroom.setText(schedule.classroom);
        Log.d(TAG, "classroom:" + schedule.classroom);

        schoolSubject.setText(schedule.schoolSubject);
        Log.d(TAG, "schoolSubject: " + schedule.schoolSubject);
        start.setText("start");
    }*/
}
