package org.d3ifcool.addictcontrol.Alarm;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;
import org.d3ifcool.addictcontrol.Database.DatabaseFirebase;
import org.d3ifcool.addictcontrol.Database.User;
import org.d3ifcool.addictcontrol.DetectInternetConnectiom;
import org.d3ifcool.addictcontrol.MainActivity;
import org.d3ifcool.addictcontrol.R;
import org.d3ifcool.addictcontrol.Schedule.CurrentSchedule;
import org.d3ifcool.addictcontrol.LockActivity;
import org.d3ifcool.addictcontrol.Schedule.Schedule;
import org.d3ifcool.addictcontrol.ScheduleActivity;
import org.d3ifcool.addictcontrol.SmartphoneActive.CountTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;


public  class  AlarmService extends Service {

    private boolean mIsOpen = false;
    private ArrayList<Schedule> mCurrSchedule;
    private int mDelay =0;
    private int mCountDown;

    private String mNameSchedule = "" ;

    //convert time to int
    private int convertToInt (String time) {
        String dataSplit[] = time.split(":");
        int dataTimeInt  = Integer.parseInt(dataSplit[0]+dataSplit[1]);

        return dataTimeInt;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isLoop = false;
        try{
            //recieve data from intent put extra
            isLoop = intent.getExtras().getBoolean("isLoop"); //when service want to run real time
            mDelay = intent.getExtras().getInt("delay");// get current delay
            mNameSchedule = intent.getExtras().getString("nameSchedule");//get name schedule
        }catch (Exception e){

        }

        //start Thread
        SharedPreferences sharedPref = getSharedPreferences("time", MODE_PRIVATE); ;
        CountTime.mSecond = sharedPref.getInt("second", 0);
        CountTime.mMinute= sharedPref.getInt("minute", 0);
        CountTime.mHour = sharedPref.getInt("hour", 0);

        TempDataAlarm.isServiceRun = true;
        CheckTime checkTime = new CheckTime(this,isLoop,mNameSchedule);
        Thread thread = checkTime;
        thread.start();
        //
        //when thread is finish
        boolean isClose = stopService(intent);

        Log.d("Is close " , String.valueOf(isClose ? 1 : 0));

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public class CheckTime extends Thread {
        private Context mContext;//context from parent
        private boolean mIsLoop;//variabel to while  condition
        private String mCurrentActive;//Schedule Active
        private String mCurrEndTime;

        private boolean mIsScheduleEnd=false;
        private DetectInternetConnectiom internetConnectiom;

        /**
         *
         * @param context is context from parent
         * @param loop is variabel to while  condition
         * @param mNameShedule is Schedule Active
         */
        public CheckTime(Context context,Boolean loop,String mNameShedule) {
            this.mContext = context;
            this.mIsLoop = loop;
            this.mCurrentActive = mNameShedule;
            internetConnectiom = new DetectInternetConnectiom(mContext);

            Log.i(" Make It" , "true");

        }


        @Override
        public void run() {
            //set while condition
            boolean loop = true;
            //mCountDown when Thread is not run in real time
            mCountDown = mDelay * 60;

            CurrentSchedule currentSchedule;
            //get current active data schedule from database with same day in system android
            currentSchedule = new CurrentSchedule(mContext);
            //

            while (loop) {

                currentSchedule.setCurrentSchedule();
                mCurrSchedule = currentSchedule.getmCurrentSchedule();

                //if mCountDown not zero then count down
                if (mCountDown != 0) {
                    mCountDown--;
                }

                Log.i("Delay", String.valueOf(mCountDown));
                //sleepapp for 1 second
                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Calendar cal = Calendar.getInstance(); //is variabel to get calender
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MM:yyyy");
                String time = simpleDateFormat.format(cal.getTime());

                SharedPreferences sharedPref = getSharedPreferences("date", MODE_PRIVATE); ;
                String currentTime = sharedPref.getString("now", "");

                if(!time.equals(currentTime)) {
                    SharedPreferences shared = getSharedPreferences("date", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("now", time);
                    editor.commit();

                    CountTime.mHour = 0 ;
                    CountTime.mMinute = 0 ;
                    CountTime.mSecond = 0 ;

                    SharedPreferences sharedPref1 = getSharedPreferences("time",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedPref1.edit();
                    editor1.putInt("second" , CountTime.mSecond);
                    editor1.putInt("minute" , CountTime.mMinute);
                    editor1.putInt("hour" , CountTime.mHour);
                }

                if (!detectDeviceIsLock()) {

                    Log.d("Second", String.valueOf(CountTime.mSecond));

                    DatabaseAdapter databaseAdapter = new DatabaseAdapter(mContext);
                    final Account account = databaseAdapter.getAccount();

                    if(account !=null) {
                        CountTime.mSecond++;
                        if (CountTime.mSecond == 60) {
                            CountTime.mMinute++;
                            CountTime.mSecond = 0;
                        }

                        if (CountTime.mMinute == 60) {
                            CountTime.mHour++;
                            CountTime.mSecond = 0;
                            CountTime.mMinute = 0;
                        }
                    }

                    if(account !=null && internetConnectiom.checkInternetConnection()) {

                        if (TempDataAlarm.isHomeTrigger) {
                            Intent intent = new Intent(mContext, LockActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            TempDataAlarm.isLock = true;
                        }

                        DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("users").child("usr_" + account.getmUsername());
                        mData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {
                                };
                                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);

                                try{
                                    boolean isLock = (boolean) map.get("isLock");

                                    if (isLock) {
                                        if (!TempDataAlarm.isLock) {
                                            Intent intent = new Intent(mContext, LockActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            TempDataAlarm.isLock = true;
                                        }
                                    } else {

                                            DatabaseFirebase databaseFirebase = new DatabaseFirebase(mContext);
                                            databaseFirebase.updateUser(
                                                    account.getmUsername(),
                                                    new User(null,
                                                            null,
                                                            null,
                                                            null,
                                                            CountTime.mHour + ":" + CountTime.mMinute + ":" + CountTime.mSecond,
                                                            false)
                                            );

                                        TempDataAlarm.isLock = false;
                                    }

                                }catch (Exception e) {

                                }
                                                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    if(account !=null && !internetConnectiom.checkInternetConnection()){

                    }



                }

            }
        }


        private boolean detectDeviceIsLock() {
            KeyguardManager myKM = (KeyguardManager) mContext.getSystemService(mContext.KEYGUARD_SERVICE);
            if( myKM.inKeyguardRestrictedInputMode() ) {
                return  true;
            } else {
                return false;
            }
        }

    }

}