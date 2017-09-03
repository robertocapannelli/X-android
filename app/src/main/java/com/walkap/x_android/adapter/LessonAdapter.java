package com.walkap.x_android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.walkap.x_android.R;
import com.walkap.x_android.ViewHolder.LessonViewHolder;
import com.walkap.x_android.model.Scheduler;

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
        Log.d(TAG, "Lssons: " + lessons);
        holder.classroom.setText(lessons.getClassroom());
        holder.schoolSubject.setText(lessons.getSchoolSubject());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "allLessons.size(): " + allLessons.size());
        return allLessons.size();
    }

}