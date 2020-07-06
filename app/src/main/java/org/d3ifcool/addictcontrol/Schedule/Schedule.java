package org.d3ifcool.addictcontrol.Schedule;


public class Schedule {
    private int mIdSchedule;
    private String mNameSchedule;
    private String mDay;
    private String mStartTime;
    private String mEndTime;
    private int mActive;

    /**
     *
     * @param idSchedule is a id scheule
     * @param nameSchedule is a name of schedule
     * @param day is a day schedule is start
     * @param startTime is a time schedule is start
     * @param endTime is a time schedule is stop
     * @param active is a schedule active
     */


    public Schedule(int idSchedule, String nameSchedule, String day, String startTime, String endTime, int active) {
        String splitStartText[] = startTime.split(":");
        String splitEndText[] = endTime.split(":");
        this.mIdSchedule = idSchedule;
        this.mNameSchedule = nameSchedule;
        this.mDay = day;

        //convert start time and time to time default format android system
        splitStartText[0] = splitStartText[0].length() ==1 ? "0"+splitStartText[0] : splitStartText[0];
        splitStartText[1] = splitStartText[1].length() ==1 ? "0"+splitStartText[1] : splitStartText[1];

        this.mStartTime = splitStartText[0] +":"+ splitStartText[1];

        splitEndText[0] = splitEndText[0].length() ==1 ? "0"+splitEndText[0] : splitEndText[0];
        splitEndText[1] = splitEndText[1].length() ==1 ? "0"+splitEndText[1] : splitEndText[1];

        this.mEndTime = splitEndText[0]+":"+splitEndText[1];
        this.mActive = active;
        //
    }

    public int getIdSchedule() {
        return mIdSchedule;
    }

    public String getNameSchedule() {
        return mNameSchedule;
    }

    public String getDay() {
        return mDay;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public int getActive() {
        return mActive;
    }


    @Override
    public String toString() {
        return "Schedule{" +
                "idSchedule='" + mIdSchedule + '\'' +
                ", nameSchedule='" + mNameSchedule + '\'' +
                ", day='" + mDay + '\'' +
                ", startTime='" + mStartTime + '\'' +
                ", endTime='" + mEndTime + '\'' +
                ", active=" + mActive +
                '}';
    }
}
