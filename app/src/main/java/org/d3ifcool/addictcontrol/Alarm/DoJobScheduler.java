package org.d3ifcool.addictcontrol.Alarm;

import android.app.KeyguardManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import org.d3ifcool.addictcontrol.SmartphoneActive.CountTime;

/**
 * Created by cool on 10/17/2018.
 */

public class DoJobScheduler extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters parameters) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {
                    if (!detectDeviceIsLock()) {
                        try {
                            Thread.sleep(1000);

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
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        jobFinished(parameters, false);
                    }
                }

            }
        }).start();

    }

    private boolean detectDeviceIsLock() {
        KeyguardManager myKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if( myKM.inKeyguardRestrictedInputMode() ) {
            return  true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
