package org.d3ifcool.addictcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;
import org.d3ifcool.addictcontrol.Schedule.Schedule;
import org.d3ifcool.addictcontrol.Schedule.ScheduleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cool on 9/27/2018.
 */

public class KidsControlAdapter extends ArrayAdapter<Kid> {
    private Context mContext;

    public KidsControlAdapter(@NonNull Context context, @NonNull ArrayList<Kid> objects) {
        super(context, 0, objects);
        this.mContext = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        //when convert view is null
        if (convertView == null) {
            //set convert view with layout "list_schedule"
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_kids,parent,false);
        }

        //get all component in layout list_schedule
        final TextView name = (TextView) convertView.findViewById(R.id.username);
        final TextView smartphoneOn = (TextView) convertView.findViewById(R.id.smartphoneOn);
        final ImageView emoji = (ImageView) convertView.findViewById(R.id.emot);


        //get current item in list view
        final Kid kid = getItem(position);

        name.setText(kid.getNama());
        smartphoneOn.setText((kid.getTime()));
        emoji.setImageResource(kid.getEmoteImage());


        return convertView;
    }

}
