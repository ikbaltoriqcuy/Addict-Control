package org.d3ifcool.addictcontrol;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.Alarm.LockService;
import org.d3ifcool.addictcontrol.Alarm.TempDataAlarm;
import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LockActivity extends AppCompatActivity {

    private String mEndTime ; // recieve data end time from intent put extra
    private String mNameSchedule = "Fokus"; // recieve data name schedule from intent put extra
    private int mCurrentDelay;// recieve data current delay from intent put extra
    private int mNextDelay;// data to set next delay

    private TextView mMessage;// message alert when lock activity active
    private TextView mSchedule;// is a info schedule active

    private MediaPlayer mMediaPlayer ; // to run file music

    private Thread mThread;//to get thread and run it

    private TimeTick mTimeTick;

    private boolean mIsClick = false;
    private boolean isClose = false;
    private  HomeKeyLogger homeKeyLogger;
    private boolean isHomeButtonPress;
    private boolean isMinimize;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ImageButton closeButton= (ImageButton) findViewById(R.id.close_btn);
        if(TempDataAlarm.isLock) {
           closeButton.setVisibility(View.GONE);
        }

        //hide action bar when opened
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //

        //get component from activity lock
        mMessage = (TextView) findViewById(R.id.message_text_view);
        mSchedule = (TextView) findViewById(R.id.schedule_text_view);
        //

        //get all data from intent put ectra and set it
        recieveData();
        //

        //run thread
        mTimeTick = new TimeTick(this,mEndTime);
        mThread = mTimeTick;
        mThread.start();
        //

        //show content
        showData(mCurrentDelay);

        //run music alarm
        releaseMediaPlayer();
        mMediaPlayer = MediaPlayer.create(this,R.raw.sound_alarm);
        mMediaPlayer.start();


        //homeKeyLogger = new HomeKeyLogger();
       // homeKeyLogger.lock(this);
    }

    /*
    //when destroy stop thread
    @Override
    protected void onDestroy() {
        mTimeTick.cancel();
        //homeKeyLogger.unlock();
       // homeKeyLogger = null;
        super.onDestroy();

    }
    */

    //to release media player
    private void releaseMediaPlayer(){
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer =null;
        }
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Log.i("TAG", "Press Home");
            System.exit(0);
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }



    //start AlarmService when click Home button
    @Override
    protected void onUserLeaveHint() {


        super.onUserLeaveHint();
    }

    @Override
    protected void onStop() {
        isHomeButtonPress = true;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //disable wifi
    private void disableWifi(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }

    //get Account from database
    private Account getAccount(){
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        return databaseAdapter.getAccount();
    }

    //recieve data from intent put extra
    private void recieveData(){
        Bundle extra = getIntent().getExtras();
        if(extra== null) {
            //do nothing
        }else{
            mNameSchedule = extra.getString("name_scheduling");
            mCurrentDelay = extra.getInt("current_delay");

            Toast.makeText(this,mEndTime,Toast.LENGTH_SHORT).show();
        }
    }




    //set data mMessage ,mNextDelay ,and mSchedule
    private void showData(int delay) {
        switch (delay) {
            case 0:
                mMessage.setText(  mNameSchedule +
                        "\nmohon untuk tidak bermain smartphone");
                mNextDelay = 5;
                break;
            case 5:
                disableWifi();
                mMessage.setText("Mohon untuk tidak bermain smartphone pada kegiatan\n '"+ mNameSchedule +"'" +
                        " ini");
                mNextDelay = 10;
                break;
            case 10:
                disableWifi();
                mMessage.setText("Hallo," + getAccount().getmUsername() + " sekarang anda \n" +"" +
                        " memasuki kegiatan '" + mNameSchedule +"'\n");
                mSchedule.setText("Quote Anda : \n" +
                        getAccount().getQuote());
                mNextDelay =-1;
                break;
            case -1 :
                disableWifi();
                mMessage.setText("Hallo," + getAccount().getmUsername() + " sekarang anda \n" +"" +
                        " memasuki kegiatan '" + mNameSchedule +"'\n");
                mSchedule.setText("Quote Anda : \n" +
                        getAccount().getQuote());
                mNextDelay =-1;
                break;
        }
    }

    public void Close(View view) {
        //isClose = true;
        TempDataAlarm.isManualLock = false;
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    private void closeActivity(){
        finish();
    }

    private void restart(){
        startActivity(getIntent());
    }


    //this class to check end time and close it if system time android same with end time
    class TimeTick extends Thread{
        private String mEndTime;
        private Context mContext;
        private Boolean mLoop= true;

        /**
         *
         * @param context is a perent coontext
         * @param endTime is a current endTime from schedule
         */
        public TimeTick(Context context,String endTime){
            this.mContext = context;
            this.mEndTime = endTime;
        }

        @Override
        public void run() {
            while (mLoop){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



                Log.d("now","nj");


                if (!TempDataAlarm.isLock && !TempDataAlarm.isManualLock){
                    Log.d("now","closed");
                    closeActivity();
                    break;
                }

                Intent openMainActivity= getIntent();
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openMainActivity, 0);



            }
        }

        public void cancel(){
            mLoop = false;
        }


    }

}
