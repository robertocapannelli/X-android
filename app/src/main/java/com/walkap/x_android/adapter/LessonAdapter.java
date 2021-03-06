package com.walkap.x_android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.walkap.x_android.R;
import com.walkap.x_android.model.Scheduler;
import com.walkap.x_android.viewHolder.LessonViewHolder;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonViewHolder> {

    private final String TAG = "LessonAdapter";

    private Context context;
    private List<Scheduler> allLessons;

    public LessonAdapter(Context context, List<Scheduler> allLessons){
        this.context = context;
        this.allLessons = allLessons;
    }

    @Override
    public LessonViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LessonViewHolder holder, int position) {
        Scheduler lessons = allLessons.get(position);
        Log.d(TAG, "Lessons: " + lessons);
        holder.classroom.setText(lessons.getClassroom().substring(0, 1).toUpperCase() + lessons.getClassroom().substring(1));
        holder.schoolSubject.setText(lessons.getSchoolSubject().substring(0, 1).toUpperCase() + lessons.getSchoolSubject().substring(1));

        int hour = lessons.getTime().getHour();
        int minute = lessons.getTime().getMinute();
        int duration = lessons.getTime().getDuration();
        int endHour = (hour + (minute + duration) / 60) % 24;
        int endMinute = (minute + duration) % 60;

        String sStartHour = "" + hour;
        String sStartMinute = "" + minute;
        String sEndHour = "" + endHour;
        String sEndMinute = "" + endMinute;

        if(hour < 10){
            sStartHour = "0" + hour;
        }

        if(minute < 10){
            sStartMinute = "0" + minute;
        }

        if(endHour < 10){
            sEndHour = "0" + endHour;
        }

        if(endMinute < 10){
            sEndMinute = "0" + endMinute;
        }

        holder.start.setText(sStartHour + " : " + sStartMinute);
        holder.end.setText(sEndHour  + " : " + sEndMinute);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "allLessons.size(): " + allLessons.size());
        return allLessons.size();
    }

    public Scheduler getChild(int position){
        return allLessons.get(position);
    }

}