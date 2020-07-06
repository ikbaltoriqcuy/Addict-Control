package org.d3ifcool.addictcontrol.Schedule;

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
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;
import org.d3ifcool.addictcontrol.MainActivity;
import org.d3ifcool.addictcontrol.R;
import org.d3ifcool.addictcontrol.ScheduleActivity;

import java.util.ArrayList;


public class ScheduleAdapter extends ArrayAdapter<Schedule> {
    private Context mContext;
    private ArrayList<Schedule> mCrashSchedule = new ArrayList<Schedule>();

    /**
     *
     * @param context is a context parent
     * @param objects is a data to set in list view
     */
    public ScheduleAdapter(@NonNull Context context,  @NonNull ArrayList<Schedule> objects) {
        super(context, 0, objects);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        //when convert view is null
        if (convertView == null) {
            //set convert view with layout "list_schedule"
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_schedule,parent,false);
        }

        //get all component in layout list_schedule
        final TextView nameScheduling = (TextView) convertView.findViewById(R.id.name_scheduling);
        final TextView timeScheduling = (TextView) convertView.findViewById(R.id.time_scheduling);
        final Switch activeSwitch = (Switch) convertView.findViewById(R.id.active_switch);


        //get current item in list view
        final Schedule schedule = getItem(position);
        //create alert dialog
        AlertDialog.Builder showContentOption = new AlertDialog.Builder(mContext);
        //get layout "content_option"
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.content_option,null);

        //get all component in layout "content_option"
        Button edit = (Button) dialogView.findViewById(R.id.edit_button);
        Button delete = (Button) dialogView.findViewById(R.id.hapus_button);

        //set content alert dialog
        showContentOption.setView(dialogView);
        // this variabel use to show alert dialog
        final AlertDialog alertDialog = showContentOption.create();
        //this use to update data in for each item in list view
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when activeSwitch is false

                if (!activeSwitch.isChecked()) {
                    //go to Schedule Activity and send data
                    Intent intent = new Intent(mContext, ScheduleActivity.class);
                    intent.putExtra("id", schedule.getIdSchedule());//Schedule Id
                    intent.putExtra("name", schedule.getNameSchedule());//Schedule Name
                    intent.putExtra("day", schedule.getDay());//Schedule day
                    intent.putExtra("start", schedule.getStartTime());//Schedule start time
                    intent.putExtra("end", schedule.getEndTime());//Schedule end time
                    intent.putExtra("active",0);//Schedule active
                    mContext.startActivity(intent);
                    //
                    //cancel alert dialoog
                    alertDialog.cancel();
                    //destroy this activity
                    ((Activity) mContext).finish();

                    //when activeSwitch is true cannot update data
                }else {
                    Toast.makeText(mContext,"Mohon dimatikan terlebih dahulu",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //this use to delete data in for each item in list view
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hide alert dialog
                alertDialog.hide();
                //when activeSwitch is false
                if(!activeSwitch.isChecked()) {
                    //create alert dialog
                    final AlertDialog.Builder messageAlert = new AlertDialog.Builder(mContext);
                    //get layout from delete_message
                    View viewDeleteMessage = inflater.inflate(R.layout.delete_message, null);
                    //set content alert dialog
                    messageAlert.setView(viewDeleteMessage);
                    //set negative button in alert dialog
                    messageAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //go to parent layout
                        }
                    });
                    //set positive button in alert dialog
                    messageAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //delete data from database
                            DatabaseAdapter databaseAdapter = new DatabaseAdapter(mContext);
                            databaseAdapter.deleteSchedule(schedule);

                            //goto LockScreenActivity
                            Intent intent = new Intent(mContext, MainActivity.class);
                            mContext.startActivity(intent);
                            //destroy this activity
                            ((Activity) mContext).finish();


                        }
                    });
                    //show alert dialog
                    AlertDialog alert = messageAlert.create();
                    alert.show();
                    //cancel current alert dialog active
                    alertDialog.cancel();

                    //when activeSwitch is a true
                }else{
                    //cannot delete data
                    Toast.makeText(mContext,"Mohon dimatikan terlebih dahulu",Toast.LENGTH_SHORT).show();

                }
            }
        });

        //set onLongClickListener for each child layout parent
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //show main alert dialog
                alertDialog.show();
                return true;
            }
        });


        //set all content
        nameScheduling.setText(schedule.getNameSchedule());
        timeScheduling.setText(schedule.getStartTime() + " - " + schedule.getEndTime());
        activeSwitch.setChecked(schedule.getActive() ==1 ? true:false);
        //set activeSwitch onClickListener to auto update data in database
        activeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call database adapter
                DatabaseAdapter databaseAdapter = new DatabaseAdapter(mContext);

                //create variabel to get data from database
                mCrashSchedule = new ArrayList<>();
                //check if there is crash schedule
                boolean crash = check(schedule);
                //when activeSwitch is a false
                if(activeSwitch.isChecked()) {
                    //if crash is a false
                    if (!crash) {
                        //update data schedule in database
                        databaseAdapter.updateSchedule(new Schedule(schedule.getIdSchedule(),
                                schedule.getNameSchedule(),
                                schedule.getDay(),
                                schedule.getStartTime().toString(),
                                schedule.getEndTime().toString(),
                                1));
                    //else if crash is a true
                    } else{
                        //create alert dialog
                        AlertDialog.Builder listScheduleBuilder = new AlertDialog.Builder(mContext);
                        //getLayoutInflater
                        LayoutInflater inflater = (LayoutInflater)  mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View dialogView = inflater.inflate(R.layout.crash_layout,null);
                        //get list view from layout crash_layout and set data adapter
                        ListView listSchedule = (ListView) dialogView.findViewById(R.id.crash_list_view);
                        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(mContext,mCrashSchedule);
                        listSchedule.setAdapter(scheduleAdapter);
                        //
                        //alert dialog setPositiveButton
                        listScheduleBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //goto parent activity
                                mContext.startActivity(new Intent(mContext,MainActivity.class));
                            }
                        });

                        //set conten alert dialog
                        listScheduleBuilder.setView(dialogView);
                        //show alert dialog
                        AlertDialog alertDialog = listScheduleBuilder.create();
                        alertDialog.show();
                        //set ActiveSwitch is false
                        activeSwitch.setChecked(false);
                    }
                    //when activeSwitch is a true
                }else{
                    //update data schedule in database
                    databaseAdapter.updateSchedule(
                            new Schedule(schedule.getIdSchedule(),
                            schedule.getNameSchedule(),
                            schedule.getDay(),
                            schedule.getStartTime().toString(),
                            schedule.getEndTime().toString(),
                            0));
                }


            }
        });

        return convertView;
    }

    //this component use if there is crash schedule in database
    public boolean check(Schedule schedule) {
        //set default crash false
        boolean crash = false;
        //make variabel to get all data schedule
        ArrayList<Schedule> getAllSchedule = new ArrayList<>();

        try{
            DatabaseAdapter databaseAdapter =new DatabaseAdapter(mContext);
            getAllSchedule = databaseAdapter.getAllSchedule();
        }catch (Exception e) {

        }
        //
        //check for each component Schedule in getAllSchedule
        for (int i= 0 ; i < getAllSchedule.size(); i++ ) {
            //get current data getAllSchedule to i
            Schedule data = getAllSchedule.get(i);

            //when data schedule to update is same with data in schedule.getDay and data is ctive
            if(data.getDay().equals(schedule.getDay())
                    && data.getActive() == 1){
                //when data.getIdSchedue is same with schedule getIdSchedule then there is a crash schedule
                if (data.getIdSchedule() != schedule.getIdSchedule()) {
                    //when schedule.startTime in range data.getStartTime and data.getEndTime
                    if (convertToInt(schedule.getStartTime()) >= convertToInt(data.getStartTime())
                            && convertToInt(schedule.getStartTime()) <= convertToInt(data.getEndTime()) ||
                            convertToInt(schedule.getEndTime()) >= convertToInt(data.getStartTime())
                                    && convertToInt(schedule.getEndTime()) <= convertToInt(data.getEndTime())) {
                        //add crash schedule to mCrashSchedule
                        mCrashSchedule.add(data);
                        //set crash to true
                        crash = true;

                    }
                    //when schedule.endTime in range data.getStartTime and data.getEndTime
                    else if (convertToInt(data.getStartTime()) >= convertToInt(schedule.getStartTime() )
                            && convertToInt(data.getStartTime()) <=  convertToInt(schedule.getEndTime()) ||
                            convertToInt(data.getEndTime()) >= convertToInt(schedule.getStartTime()) &&
                                    convertToInt(data.getEndTime())<= convertToInt(schedule.getEndTime())) {
                        //add crash schedule to mCrashSchedule
                        mCrashSchedule.add(data);
                        //set crash to true
                        crash = true;

                    }
                }
            }


        }

        return crash;
    }

    //this method use to convert time to int
    public int convertToInt (String time) {
        //first split String
        String dataSplit[] = time.split(":");
        //parse to int
        int dataTimeInt  = Integer.parseInt(dataSplit[0]+dataSplit[1]);
        //return value dataTimeInt
        return dataTimeInt;
    }
}
