package com.walkap.x_android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.walkap.x_android.R;
import com.walkap.x_android.model.Scheduler;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Scheduler> {

    public CustomAdapter(Context context, int textViewResourceId, List<Scheduler> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewOptimize(position, convertView, parent);
    }

    public View getViewOptimize(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row, null);
            viewHolder = new ViewHolder();
            viewHolder.classroom = (TextView)convertView.findViewById(R.id.optionsClassroomTextView);
            viewHolder.schoolSubject = (TextView)convertView.findViewById(R.id.optionsSchoolSubjectTextView);
            viewHolder.hourBeginning = (TextView)convertView.findViewById(R.id.optionsHoursBeginningTextView);
            viewHolder.duration = (TextView)convertView.findViewById(R.id.optionsHoursEndTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Scheduler scheduler = getItem(position);
        viewHolder.classroom.setText("class room " + scheduler.getClassroom());
        viewHolder.classroom.setTextColor(Color.BLACK);
        viewHolder.schoolSubject.setText("school Subject " + scheduler.getSchoolSubject());
        viewHolder.schoolSubject.setTextColor(Color.BLACK);
        if(scheduler.getTime().getMinute() == 0){
            viewHolder.hourBeginning.setText("to the hour " + scheduler.getTime().getHour() + ":0" + scheduler.getTime().getMinute());
        }
        else {
            viewHolder.hourBeginning.setText("to the hour " + scheduler.getTime().getHour() + ":" + scheduler.getTime().getMinute());
        }
        viewHolder.hourBeginning.setTextColor(Color.BLACK);
        viewHolder.duration.setText("for " + scheduler.getTime().getDuration() + " minutes");
        viewHolder.duration.setTextColor(Color.BLACK);
        return convertView;
    }

    private class ViewHolder {
        public TextView classroom;
        public TextView schoolSubject;
        public TextView hourBeginning;
        public TextView duration;

        private String schedulerId;
    }


}