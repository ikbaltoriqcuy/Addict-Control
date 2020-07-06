package org.d3ifcool.addictcontrol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.d3ifcool.addictcontrol.Alarm.AlarmService;
import org.d3ifcool.addictcontrol.Alarm.TempDataAlarm;
import org.d3ifcool.addictcontrol.SmartphoneActive.CountTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StartProgram extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_service);

        SharedPreferences sharedPref = getSharedPreferences("date", MODE_PRIVATE); ;
        if(sharedPref ==null) {
            Calendar cal = Calendar.getInstance(); //is variabel to get calender
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MM:yyyy");
            String time = simpleDateFormat.format(cal.getTime());
            SharedPreferences shared = getSharedPreferences("date", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("now", time);
            editor.commit();
        }

        //start LockScreenActivity
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);

        if(!TempDataAlarm.isServiceRun) {
            startService(new Intent(this, AlarmService.class).putExtra("isLoop", true));
        }



        finish();

    }



}