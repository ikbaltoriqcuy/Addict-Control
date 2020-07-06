package org.d3ifcool.addictcontrol;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;
import org.d3ifcool.addictcontrol.Schedule.Schedule;
import org.d3ifcool.addictcontrol.Schedule.ScheduleAdapter;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {

    //variabel to recieved Intent.putExtra from ViewScheduleActivity
    private int mIdSchedule; //schedule id
    private String mNameSchedule;//schedule name
    private String mDay;// schedule day
    private String mStartTime;// schedule start time
    private String mEndTime;//schedule end time
    private int mActive;// schedule active
    //end

    //schedule crash
    private ArrayList<Schedule> mCrashSchedule = new ArrayList<Schedule>();

    //array of day in one week
    private  String[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        //set maDays in one week
        mDays = this.getResources().getStringArray(R.array.days);

        //if function add schedule , set text databaseButton "Tambah"
        Button databaseButton = (Button) findViewById(R.id.database_button);
        databaseButton.setText("Tambah");
        //get data from intent put extra
        recievedData();
    }

    //if backpress button is click then go to LockScreenActivity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("current_tab",0);
            startActivity(intent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    //method to recieved data from Intent.putExtra
    private void recievedData(){
        Bundle extra = getIntent().getExtras();
        if(extra== null) {
            //do nothing

        }else{
            mIdSchedule = extra.getInt("id"); //Schedule id
            mNameSchedule = extra.getString("name") ;// Schedule name
            mDay = extra.getString("day");// Schedule day
            mStartTime = extra.getString("start");//Schedule start time
            mEndTime = extra.getString("end");//Schedule end time
            mActive = extra.getInt("active");// schedule active
            //show data for each component in layout
            showData();
        }


    }

    //show data from each component in this layout
    private void showData() {
        //initial all component in layoout
        EditText add =(EditText) findViewById(R.id.add_edit_text) ;
        TextView daysTextView = (TextView) findViewById(R.id.days_textview);
        TextView startTextView = (TextView) findViewById(R.id.start_time_textview);
        TextView endTextView = (TextView) findViewById(R.id.end_time_textview);
        Switch activeSwitch = (Switch) findViewById(R.id.active);

        //set text databaseButton to "Edit"
        Button databaseButton = (Button) findViewById(R.id.database_button);
        databaseButton.setText("Edit");

        //set data for each component in layout
        add.setText(mNameSchedule);
        daysTextView.setText(mDay);
        startTextView.setText(mStartTime);
        endTextView.setText(mEndTime);
        activeSwitch.setChecked(mActive==1 ? true:false );
    }



    //when event onClick startTime
    public void startTime(View view) {
        //create alert dialog
        AlertDialog.Builder showTimePicker = new AlertDialog.Builder(this);
        //get content from date_time
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.date_time,null);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker) ;
        //set content alert dialog
        showTimePicker.setView(dialogView);
        //set title alert dialog
        showTimePicker.setTitle("Set Time");
        //set negative button alert dialog
        showTimePicker.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        //set postive button alert dialog
        showTimePicker.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //get time from timePicker
                int hour, minute ;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = timePicker.getHour() ;
                    minute = timePicker.getMinute();
                }else {
                    hour = timePicker.getCurrentHour() ;
                    minute = timePicker.getCurrentMinute();
                }
                //

                //set text to startTime
                TextView startTime = (TextView) findViewById(R.id.start_time_textview) ;
                if (String.valueOf(minute).length() == 1){
                    startTime.setText(hour  + ":" + "0" + minute );
                }else{
                    startTime.setText(hour  + ":" + minute );
                }

            }
        });
        //show alert dialog
        AlertDialog alertDialog = showTimePicker.create();
        alertDialog.show();
    }


    public void endTime(View view) {
        //create alert dialog
        AlertDialog.Builder showTimePicker = new AlertDialog.Builder(this);
        //get content from date_time
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.date_time,null);
        //set content alert dialog
        showTimePicker.setView(dialogView);
        //set title alert dialog
        showTimePicker.setTitle("Set Time");

        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker) ;
        //set negative button alert dialog
        showTimePicker.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        //set postive button alert dialog
        showTimePicker.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //get time from timePicker
                int hour=0 , minute=0 ;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = timePicker.getHour() ;
                    minute = timePicker.getMinute();
                }else {
                    hour = timePicker.getCurrentHour() ;
                    minute = timePicker.getCurrentMinute();
                }
                //

                //set text to endTime
                TextView endTime = (TextView) findViewById(R.id.end_time_textview) ;
                if (String.valueOf(minute).length() == 1){
                    endTime.setText(hour  + ":" + "0" + minute );
                }else{
                    endTime.setText(hour  + ":" + minute );
                }
            }
        });
        //show alert dialog
        AlertDialog alertDialog = showTimePicker.create();
        alertDialog.show();
    }

    public void showListDays(View view) {
        //create alert builder
        AlertDialog.Builder listDaysBuilder = new AlertDialog.Builder(this);
        //get layout list_days
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.list_days,null);
        final ListView listDays = (ListView) dialogView.findViewById(R.id.days_listview) ;
        //
        //set arrayData with mDays
        final ArrayList<String> arrayData = new ArrayList<String>();
        for (String data:mDays) {
            //set for each item in arraydata
            arrayData.add(data);
        }
        //

        //set content alert builder
        listDaysBuilder.setView(dialogView);
        //set title in alert builder
        listDaysBuilder.setTitle("Pilih Hari");
        //set arraya adapter in ListView 'listDays'
        listDays.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayData));
        //show alert alert dialog
        final AlertDialog alertDialog = listDaysBuilder.create();
        alertDialog.show();
        //
        //set on item click listener in listDays
        listDays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //set text in daysTextView
                TextView daysTextView = (TextView) findViewById(R.id.days_textview);
                daysTextView.setText(arrayData.get(position));

                //cancel alert dialog
                alertDialog.cancel();
            }
        });


    }

    //this method is use to save data Schedule to database
    public void addToDatabase(View view) {
        //initial all components in this layout
        EditText add =(EditText) findViewById(R.id.add_edit_text) ;
        TextView daysTextView = (TextView) findViewById(R.id.days_textview);
        TextView startTextView = (TextView) findViewById(R.id.start_time_textview);
        TextView endTextView = (TextView) findViewById(R.id.end_time_textview);
        Switch active = (Switch) findViewById(R.id.active);
        Button databaseButton = (Button) findViewById(R.id.database_button);
        //
        //make variabel to save data from component layout
        String nameSchedule = add.getText().toString();
        String daySchedule = daysTextView.getText().toString();
        String startSchedule = startTextView.getText().toString();
        String endSchedule = endTextView.getText().toString();
        //

        //refresh mCrashSchedule to prevent error when check crash schedule
        mCrashSchedule.clear();
        //get boolean crash schedule from method check
        boolean crash = check(startSchedule,endSchedule);

        //if dataabaseButton get Text "Tambah"
        if(databaseButton.getText().equals("Tambah")){
            //when result crash is false then can add schedule in the system
            if(!crash) {
                //check if startSchedule and endSchedule is not out of daySchedule
                if (convertToInt(startSchedule) < convertToInt(endSchedule)) {
                    //save data schedule to database
                    try {
                        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
                        databaseAdapter.addSchedule(
                                new Schedule(
                                        0,
                                        nameSchedule,
                                        daySchedule,
                                        startSchedule,
                                        endSchedule, active.isChecked() ? 1 : 0));

                        Toast.makeText(this, "sukses di buat", Toast.LENGTH_LONG).show();

                        //
                        // go to LockScreenActivity
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        //Destroy this Activity
                        this.finish();
                    }catch (IllegalArgumentException e) {
                        Toast.makeText(this,
                                "Maaf nama kegiatan tidak boleh kosong",
                                Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(this,"jadwal yang anda buat melampui hari " + daySchedule ,Toast.LENGTH_SHORT).show();
                }

            //when result crash is true then show alert because there is a schedule is crash and active
            }else if(crash){
                //Create alert dialog
                AlertDialog.Builder listScheduleBuilder = new AlertDialog.Builder(this);
                //get layout "crash_layout"
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.crash_layout,null);
                //
                //get list view in crash_layout and set adapter
                ListView listSchedule = (ListView) dialogView.findViewById(R.id.crash_list_view);
                ScheduleAdapter scheduleAdapter = new ScheduleAdapter(this,mCrashSchedule);
                listSchedule.setAdapter(scheduleAdapter);
                //
                //set title alert dialog
                listScheduleBuilder.setTitle("Jadwal Bentrok");
                //set positive button alert dialog
                listScheduleBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //back to activity parent
                    }
                });
                //set content alert dialog
                listScheduleBuilder.setView(dialogView);
                //show alert dialog
                AlertDialog alertDialog = listScheduleBuilder.create();
                alertDialog.show();
            }
        //when databaseButton get text "Edit"
        } else if (databaseButton.getText().equals("Edit")){
            //when result crash is false then can update schedule in the system
            if(!crash) {
                //update schedule in database
                try {
                    Schedule schedule = new Schedule(
                            mIdSchedule,
                            nameSchedule,
                            daySchedule,
                            startSchedule,
                            endSchedule,
                            active.isChecked() ? 1 : 0);
                    DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
                    databaseAdapter.updateSchedule(schedule);

                    Toast.makeText(this, "sukses di buat", Toast.LENGTH_LONG).show();

                    //

                    // go to LockScreenActivity
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    //destroy this activity
                    this.finish();
                }catch (IllegalArgumentException e) {
                    Toast.makeText(this,
                            "Maaf nama kegiatan tidak boleh kosong",
                            Toast.LENGTH_SHORT).show();
                }

                //when result crash is true then cannot update schedule in the system
            }else{
                //Create alert dialog
                AlertDialog.Builder listScheduleBuilder = new AlertDialog.Builder(this);
                //get layout "crash _layout"
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.crash_layout,null);
                //
                //get list view in crash_layout and set adapter
                ListView listSchedule = (ListView) dialogView.findViewById(R.id.crash_list_view) ;
                ScheduleAdapter scheduleAdapter = new ScheduleAdapter(this,mCrashSchedule);
                listSchedule.setAdapter(scheduleAdapter);
                ///
                //set positive button to alert dialog
                listScheduleBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //go to parent activity
                    }
                });

                //set content alert dialog
                listScheduleBuilder.setView(dialogView);

                //show alert dialog
                AlertDialog alertDialog = listScheduleBuilder.create();
                alertDialog.show();

            }

        }

    }
    //this method use to reset all component in this layout
    public void reset(View view){
        //initial all component in layout
        EditText add =(EditText) findViewById(R.id.add_edit_text) ;
        TextView daysTextView = (TextView) findViewById(R.id.days_textview);
        TextView startTextView = (TextView) findViewById(R.id.start_time_textview);
        TextView endTextView = (TextView) findViewById(R.id.end_time_textview);
        Switch active = (Switch) findViewById(R.id.active);
        //
        //set all component to default
        add.setText("");
        daysTextView.setText("Senin");
        startTextView.setText("06:00");
        endTextView.setText("07:00");
        active.setChecked(false);
        //
    }
    //this component use if there is crash schedule in database
    public boolean check(String startTime ,String endTime) {
        //set default crash false
        boolean crash = false;
        //make variabel to get all data schedule
        ArrayList<Schedule> getAllSchedule = new ArrayList<>();

        try{
            DatabaseAdapter databaseAdapter =new DatabaseAdapter(this);
            getAllSchedule = databaseAdapter.getAllSchedule();
        }catch (Exception e) {

        }
        //
        //check for each component Schedule in getAllSchedule
        for (int i= 0 ; i < getAllSchedule.size(); i++ ) {

            //get current data getAllSchedule to i
            Schedule data = getAllSchedule.get(i);

            //get component daysTextView and activeSwitch in this layout
            TextView daysTextView = (TextView) findViewById(R.id.days_textview);
            Switch activeSwitch = (Switch) findViewById(R.id.active);
            //check all component to get crash schedule or not
            if(data.getDay().equals(daysTextView.getText().toString())
                    && data.getActive() == 1 && activeSwitch.isChecked()&&
                    data.getIdSchedule() != (mIdSchedule)){
                //when startTime in range data.getStartTime and data.getEndTime
                if(convertToInt(startTime) >=convertToInt(data.getStartTime())
                        && convertToInt(startTime) <=convertToInt(data.getEndTime()) ||
                        convertToInt(endTime) >=convertToInt(data.getStartTime())
                                && convertToInt(endTime) <=convertToInt(data.getEndTime())) {
                    //add crash schedule to mCrashSchedule
                    mCrashSchedule.add(data);
                    //set crash to true
                    crash = true;
                //when endTime in range data.getStartTime and data.getEndTime
                }else if(convertToInt(data.getStartTime()) >= convertToInt(startTime)
                        && convertToInt(data.getStartTime()) <= convertToInt(endTime) ||
                        convertToInt(data.getEndTime()) >= convertToInt(startTime)
                                && convertToInt(data.getEndTime()) <= convertToInt(endTime)){
                    //add crash schedule to mCrashSchedule
                    mCrashSchedule.add(data);
                    //set crash to true
                    crash = true;

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

    public boolean checkSanity(String nameSchedule) {
        if (TextUtils.isEmpty(nameSchedule)) {
            Toast.makeText(this,
                    "Maaf nama kegiatan tidak boleh kosong",
                    Toast.LENGTH_SHORT).show();

            return true;
        }

        return false;
    }

}
