package com.walkap.x_android;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
        Log.d("hey", scheduler.getClassroom());
        viewHolder.classroom.setText(scheduler.getClassroom());
        viewHolder.schoolSubject.setText(scheduler.getSchoolSubject());
        viewHolder.hourBeginning.setText(scheduler.getTime().getHour() + ":" + scheduler.getTime().getMinute());
        viewHolder.duration.setText(scheduler.getTime().getDuration() + "");
        return convertView;
    }

    private class ViewHolder {
        public TextView classroom;
        public TextView schoolSubject;
        public TextView hourBeginning;
        public TextView duration;
    }


}