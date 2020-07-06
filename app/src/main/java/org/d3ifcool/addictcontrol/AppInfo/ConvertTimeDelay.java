package org.d3ifcool.addictcontrol.AppInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by cool on 10/12/2018.
 */

public class ConvertTimeDelay {

    public static  int typeMillis;
    public static  long second;
    public static  long minute;
    public static  long hour;

    public static String convert(long timeMillis){
        int millis=0;
        switch (typeMillis) {
            case 0  : millis = 1000; break ;
            case 1  : millis = 10000; break ;
            case 2  : millis = 100000;break ;
            case 3  : millis = 1000000;break ;

        }
        minute = (int) ((timeMillis / (1000*60)) % 60);

        second = (int) (timeMillis / 1000) % 60 ;

        hour   = (int) ((timeMillis / (1000*60*60)) % 24);

        return  hour + " jam "+ minute + " minute " + second + " detik ";

    }

    private static Long convertToSecond(long millis){
        return millis/1000;
    }


    private static Long convertToMinute(long seconds){
        return seconds/60;
    }


    private  static Long convertToHour(long minutes){
        return minutes/60;
    }


}
