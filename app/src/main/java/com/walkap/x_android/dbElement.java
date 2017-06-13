/**
 * Created by Morcrat on 13/06/17.
 */
package com.walkap.x_android;

public class dbElement {

    private int id;
    private String classRoom;
    private String hour;
    private String matter;

    public dbElement() {

    }

    public dbElement(int id,String classRoom, String hour, String matter)
    {
        this.id = id;
        this.classRoom = classRoom;
        this.hour = hour;
        this.matter = matter;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getHour() {
        return hour;
    }

    public void setMatter(String matter) {
        this.matter = matter;
    }

    public String getMatter() {
        return matter;
    }

}
