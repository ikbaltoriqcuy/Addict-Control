package org.d3ifcool.addictcontrol.Alarm;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
import org.d3ifcool.addictcontrol.HomeActivity;
import org.d3ifcool.addictcontrol.Schedule.CurrentSchedule;
import org.d3ifcool.addictcontrol.LockActivity;
import org.d3ifcool.addictcontrol.Schedule.Schedule;
import org.d3ifcool.addictcontrol.SmartphoneActive.CountTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;


public  class  LockService extends Service {


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
            mDelay = intent.getExtras().getInt("delay");// get current delay
            mNameSchedule = intent.getExtras().getString("nameSchedule");

        }catch (Exception e){

        }

        //start Thread
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
    public void onDestroy() {
        super.onDestroy();
    }


    public class CheckTime extends Thread {
        private Context mContext;//context from parent
        private boolean mIsLoop;//variabel to while  condition
        private String mCurrentActive;//Schedule Active
        private String mCurrEndTime;

        private boolean mIsScheduleEnd=false;

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


                if (!detectDeviceIsLock()) {

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
                        Log.d("Second", String.valueOf(CountTime.mSecond));



                    DatabaseAdapter databaseAdapter = new DatabaseAdapter(mContext);
                    final Account account = databaseAdapter.getAccount();


                    DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("users").child("usr_" + account.getmUsername());
                    mData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                            Map<String,Object> map =  dataSnapshot.getValue(genericTypeIndicator);

                            boolean isLock = (boolean) map.get("isLock");

                            if(isLock) {
                                Intent intent = new Intent(mContext,LockActivity.class);
                                startActivity(intent);
                                TempDataAlarm.isLock = true;
                            }else{
                                if(account !=null) {
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

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
                    Log.i("Delay", String.valueOf(mCountDown));
                //sleepapp for 1 second
                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //

                    //this condition is use to check if time now in range start time and end time
                    if (mCountDown == 0 ) {
                        //count down is stop
                        mCountDown = 0;
                        //the lock activity was launch
                        mIsOpen = true;

                        //start Lock activity

                        Intent lockActivity = new Intent(mContext, LockActivity.class);
                        lockActivity.putExtra("name_scheduling", "Fokus");
                        lockActivity.putExtra("current_delay", mDelay);
                        lockActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(lockActivity);


                        //thread is stop
                        loop = mIsLoop;

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



