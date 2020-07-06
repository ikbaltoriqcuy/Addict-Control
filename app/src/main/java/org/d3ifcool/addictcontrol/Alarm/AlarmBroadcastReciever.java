package org.d3ifcool.addictcontrol.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.d3ifcool.addictcontrol.MainActivity;


public class AlarmBroadcastReciever extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!TempDataAlarm.isServiceRun){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, AlarmService.class));
            } else {
                context.startService(new Intent(context, AlarmService.class));
            }
        }
    }
}
