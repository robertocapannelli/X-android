package com.walkap.x_android.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class TimeSchoolSubject {

    private int day;
    private int hour;
    private int minute;
    private int duration;

    public TimeSchoolSubject(){

    }

    public TimeSchoolSubject(int day, int hour, int minute, int duration){
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.duration = duration;
    }

    public void setDay(int day){
        this.day = day;
    }

    public int getDay(){
        return day;
    }
    public void setHour(int hour){
        this.hour = hour;
    }

    public int getHour(){
        return hour;
    }

    public void setMinute(int minute){
        this.minute = minute;
    }

    public int getMinute(){
        return minute;
    }

    public void setDuration(int duration){
        this.duration = duration;
    }

    public int getDuration(){
        return duration;
    }

}
