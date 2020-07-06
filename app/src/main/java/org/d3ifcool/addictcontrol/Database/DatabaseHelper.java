package org.d3ifcool.addictcontrol.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static org.d3ifcool.addictcontrol.Database.TimeWorkContract.AccountEntry;
import static org.d3ifcool.addictcontrol.Database.TimeWorkContract.QuotesEntry;
import static org.d3ifcool.addictcontrol.Database.TimeWorkContract.ScheduleEntry;

/**
 * Created by cool on 4/14/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "timeWorks.db";

    private static final String CREATE_QUOTES_TABLE =
            "CREATE TABLE " +
                    QuotesEntry.TABLE_QUOTES + "("
                    + QuotesEntry.KEY_NAME_QUOTE + " TEXT  )";

    private static final String CREATE_SCHEDULING_TABLE =
            "CREATE TABLE " +
                    ScheduleEntry.TABLE_SCHEDULE + "("
                    + ScheduleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ScheduleEntry.KEY_NAME + " TEXT,"
                    + ScheduleEntry.KEY_DAY + " TEXT,"
                    + ScheduleEntry.KEY_START_TIME + " TEXT,"
                    + ScheduleEntry.KEY_END_TIME  + " TEXT,"
                    + ScheduleEntry.KEY_ACTIVE + " INTEGER)";

    private static final String CREATE_ACCOUNT_TABLE =
            "CREATE TABLE " +
                    AccountEntry.TABLE_ACCOUNT + "("
                    + AccountEntry.KEY_USERNAME + " TEXT PRIMARY KEY,"
                    + AccountEntry.KEY_IMAGE + " TEXT,"
                    + AccountEntry.KEY_TYPE_ACCOUNT + " TEXT, "
                    + AccountEntry.KEY_MY_PASSWORD +" TEXT)";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(CREATE_SCHEDULING_TABLE);
            db.execSQL(CREATE_QUOTES_TABLE);
            db.execSQL(CREATE_ACCOUNT_TABLE);
        }catch(Exception e) {
            Log.e("Create Database", "Error to Create");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
