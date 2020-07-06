package org.d3ifcool.addictcontrol;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.Alarm.AlarmBroadcastReciever;
import org.d3ifcool.addictcontrol.Alarm.DoJobScheduler;
import org.d3ifcool.addictcontrol.Alarm.TempDataAlarm;
import org.d3ifcool.addictcontrol.Database.Connect;
import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;
import org.d3ifcool.addictcontrol.Database.DatabaseFirebase;
import org.d3ifcool.addictcontrol.Database.User;
import org.d3ifcool.addictcontrol.SmartphoneActive.CountTime;

import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    private static boolean isStart;

    private TextView mSmartphoneOn,textLock, user;

    private ImageButton imageButton;

    private Handler handler;

    public static boolean isRunnable = false;
    private static boolean isServiceRun = false ;
    private DatabaseAdapter mDatabase;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDatabase = new DatabaseAdapter(this);
        account = mDatabase.getAccount();

        //hide actioBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mSmartphoneOn = (TextView) findViewById(R.id.smartphoneOn);
        user = (TextView) findViewById(R.id.user);

        if(account != null)
            user.setText(account.getmUsername());


        if(!isRunnable) {
            handler = new Handler();
            handler.postDelayed(runnable,1000);
        }

        imageButton = (ImageButton) findViewById(R.id.image_button);
        textLock = (TextView) findViewById(R.id.textLock);

        Intent intent = new Intent(this, AlarmBroadcastReciever.class);
        boolean alarmRunnig = (PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_NO_CREATE) !=null);
        if(!alarmRunnig) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0) ;
            AlarmManager alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),1000, pendingIntent);

        }


    }
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {

            SharedPreferences sharedPref = getSharedPreferences("time",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("second" , CountTime.mSecond);
            editor.putInt("minute" , CountTime.mMinute);
            editor.putInt("hour" , CountTime.mHour);

            editor.commit();
            mSmartphoneOn.setText(CountTime.mHour + ":" + CountTime.mMinute + ":" + CountTime.mSecond);
            handler.postDelayed(this,1000);
        }
    };

    private boolean detectDeviceIsLock() {
        KeyguardManager myKM = (KeyguardManager) this.getSystemService(this.KEYGUARD_SERVICE);
        if( myKM.inKeyguardRestrictedInputMode() ) {
            return  true;
        } else {
            return false;
        }
    }
    @Override
    public void finish() {
        super.finish();
        handler.removeCallbacks(runnable);

    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);
        super.onBackPressed();
    }



    public void StartLock(View view) {
        isStart = true;
        Intent intent = new Intent(this,LockActivity.class);
        startActivity(intent);
        TempDataAlarm.isManualLock = true;
        finish();
    }
}

