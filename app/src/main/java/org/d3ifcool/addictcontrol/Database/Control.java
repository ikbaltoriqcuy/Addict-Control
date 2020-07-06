package org.d3ifcool.addictcontrol.Database;

/**
 * Created by cool on 10/26/2018.
 */

public class Control {
    private String username;
    private int second;
    private int minute;
    private int hour;

    public Control(String username, int second, int minute, int hour) {
        this.username = username;
        this.second = second;
        this.minute = minute;
        this.hour = hour;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
}
