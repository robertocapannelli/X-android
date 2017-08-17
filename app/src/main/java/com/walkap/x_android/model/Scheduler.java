package com.walkap.x_android.model;

public class Scheduler {

    private String classroom;
    private String schoolSubject;
    private String schoolSubjectId;
    private String schedulerId;
    private TimeSchoolSubject time;

    public Scheduler(){

    }

    public Scheduler(String classroom, String schoolSubject, TimeSchoolSubject time){
        this.classroom = classroom;
        this.schoolSubject = schoolSubject;
        this.time = time;
    }

    public void setClassroom(String classroom){
        this.classroom = classroom;
    }

    public String getClassroom(){
        return classroom;
    }

    public void setSchedulerId(String schedulerId){
        this.schedulerId = schedulerId;
    }

    public String getSchedulerId(){
        return schedulerId;
    }

    public void setSchoolSubject(String schoolSubject){
        this.schoolSubject = schoolSubject;
    }

    public String getSchoolSubject(){
        return schoolSubject;
    }

    public void setSchoolSubjectId(String schoolSubjectId){
        this.schoolSubjectId = schoolSubjectId;
    }

    public String getSchoolSubjectId(){
        return schoolSubjectId;
    }

    public void setTime(TimeSchoolSubject time){
        this.time = time;
    }

    public TimeSchoolSubject getTime(){
        return time;
    }

}
