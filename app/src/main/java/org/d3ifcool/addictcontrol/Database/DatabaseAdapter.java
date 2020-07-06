package org.d3ifcool.addictcontrol.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.Quotes.Quotes;
import org.d3ifcool.addictcontrol.Schedule.Schedule;

import java.util.ArrayList;


public class DatabaseAdapter {

    private Context mContext;

    public DatabaseAdapter(Context context) {
        mContext = context ;
    }

    public long addQuote (String quotes){
        ContentValues values = new ContentValues();

        values.put(TimeWorkContract.QuotesEntry.KEY_NAME_QUOTE ,quotes);//data quote
        // Inserting Row
        mContext.getContentResolver().insert(TimeWorkContract.QuotesEntry.CONTENT_URI, values);

        Log.i("data",quotes);

        return 1;
    }


    public long addSchedule(Schedule schedule) {

        ContentValues values = new ContentValues();
        //values.put(TimeWorkContract.ScheduleEntry._ID, String.valueOf(schedule.getIdSchedule()) ); //schedule id
        values.put(TimeWorkContract.ScheduleEntry.KEY_NAME, String.valueOf(schedule.getNameSchedule())); //schedule nam
        values.put(TimeWorkContract.ScheduleEntry.KEY_DAY,String.valueOf(schedule.getDay()));//schedule day
        values.put(TimeWorkContract.ScheduleEntry.KEY_START_TIME, String.valueOf(schedule.getStartTime()));//schedule start time
        values.put(TimeWorkContract.ScheduleEntry.KEY_END_TIME, String.valueOf(schedule.getEndTime()));//schedule end time
        values.put(TimeWorkContract.ScheduleEntry.KEY_ACTIVE,schedule.getActive());//schedule active

        Toast.makeText(mContext,TimeWorkContract.ScheduleEntry.KEY_ACTIVE,Toast.LENGTH_SHORT).show();

        // Inserting Row
        mContext.getContentResolver().insert(TimeWorkContract.ScheduleEntry.CONTENT_URI, values);
        long i =1;
        return i;
    }

    public long addAccount (Account account){
        ContentValues values = new ContentValues();
        values.put(TimeWorkContract.AccountEntry.KEY_USERNAME ,String.valueOf(account.getmUsername()));//account username
        values.put(TimeWorkContract.AccountEntry.KEY_IMAGE ,account.getmImage());//account image
        values.put(TimeWorkContract.AccountEntry.KEY_TYPE_ACCOUNT ,account.getmTypeAccount());//account is login
        values.put(TimeWorkContract.AccountEntry.KEY_MY_PASSWORD,String.valueOf(account.getQuote()));//account my quote
        // Inserting Row
        mContext.getContentResolver().insert(TimeWorkContract.AccountEntry.CONTENT_URI, values);
        long i =1;

        return i;
    }



    public ArrayList<Schedule> getAllSchedule() {

        ArrayList<Schedule> schedulestList = new ArrayList<Schedule>();
        String[] projection = {
                TimeWorkContract.ScheduleEntry._ID,
                TimeWorkContract.ScheduleEntry.KEY_NAME,
                TimeWorkContract.ScheduleEntry.KEY_DAY,
                TimeWorkContract.ScheduleEntry.KEY_START_TIME,
                TimeWorkContract.ScheduleEntry.KEY_END_TIME ,
                TimeWorkContract.ScheduleEntry.KEY_ACTIVE
        };

        Cursor cursor =mContext.getContentResolver().query(TimeWorkContract.ScheduleEntry.CONTENT_URI, projection,
                null,null,null);

        int indexColumnId = cursor.getColumnIndex(TimeWorkContract.ScheduleEntry._ID);
        int indexColumnName = cursor.getColumnIndex(TimeWorkContract.ScheduleEntry.KEY_NAME);
        int indexColumnDay = cursor.getColumnIndex(TimeWorkContract.ScheduleEntry.KEY_DAY);
        int indexColumnStartTime = cursor.getColumnIndex(TimeWorkContract.ScheduleEntry.KEY_START_TIME);
        int indexColumnEndTime = cursor.getColumnIndex(TimeWorkContract.ScheduleEntry.KEY_END_TIME);
        int indexColumnActive = cursor.getColumnIndex(TimeWorkContract.ScheduleEntry.KEY_ACTIVE);

        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = new Schedule(
                        cursor.getInt(indexColumnId),
                        cursor.getString(indexColumnName),
                        cursor.getString(indexColumnDay),
                        cursor.getString(indexColumnStartTime),
                        cursor.getString(indexColumnEndTime),
                        cursor.getInt(indexColumnActive));
                // Adding quote to list
                schedulestList.add(schedule);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return schedulestList;

    }



    public Quotes getAllQuote(){
        ArrayList<Quotes> schedulestQuote = new ArrayList<Quotes>();
        String[] projection = {
                TimeWorkContract.QuotesEntry.KEY_NAME_QUOTE
        };

        Cursor cursor =mContext.getContentResolver().query(TimeWorkContract.QuotesEntry.CONTENT_URI, projection,
                null,null,null);

        int indexColumnId = cursor.getColumnIndex(TimeWorkContract.QuotesEntry.KEY_NAME_QUOTE);

        Quotes quotes = new Quotes();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(indexColumnId);
                quotes.setQuote(data);

            } while (cursor.moveToNext());
        }

        // return quote list
        return  quotes;
    }


    public Account getAccount() {
        Account account = null;

        String[] projection = {
                TimeWorkContract.AccountEntry.KEY_USERNAME,
                TimeWorkContract.AccountEntry.KEY_IMAGE,
                TimeWorkContract.AccountEntry.KEY_TYPE_ACCOUNT,
                TimeWorkContract.AccountEntry.KEY_MY_PASSWORD
        };

        Cursor cursor = mContext.getContentResolver().query(TimeWorkContract.AccountEntry.CONTENT_URI, projection,
                null,null,null);

        int indexColumnUsername = cursor.getColumnIndex(TimeWorkContract.AccountEntry.KEY_USERNAME);
        int indexColumnImage = cursor.getColumnIndex(TimeWorkContract.AccountEntry.KEY_IMAGE);
        int indexColumnTypeAccount = cursor.getColumnIndex(TimeWorkContract.AccountEntry.KEY_TYPE_ACCOUNT);
        int indexColumnMyqUOTE = cursor.getColumnIndex(TimeWorkContract.AccountEntry.KEY_MY_PASSWORD);

        if (cursor.moveToFirst()) {
            do {
                        account = new Account(
                        cursor.getString(indexColumnUsername),
                        cursor.getString(indexColumnImage),
                        cursor.getString(indexColumnTypeAccount),
                        cursor.getString(indexColumnMyqUOTE));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return account;

    }



    // Updating single scheduling
    public int updateSchedule(Schedule schedule) {
        ContentValues values = new ContentValues();
        values.put(TimeWorkContract.ScheduleEntry.KEY_NAME, String.valueOf(schedule.getNameSchedule())); //schedule name
        values.put(TimeWorkContract.ScheduleEntry.KEY_DAY,String.valueOf(schedule.getDay()));//schedule daya
        values.put(TimeWorkContract.ScheduleEntry.KEY_START_TIME, String.valueOf(schedule.getStartTime())); //schedule start time
        values.put(TimeWorkContract.ScheduleEntry.KEY_END_TIME, String.valueOf(schedule.getEndTime()));//schedule end time
        values.put(TimeWorkContract.ScheduleEntry.KEY_ACTIVE, schedule.getActive());//schedule active
        // updating row
        return mContext.getContentResolver().update(
                TimeWorkContract.ScheduleEntry.CONTENT_URI,
                values,
                TimeWorkContract.ScheduleEntry._ID + " = ?",
                new String[] { String.valueOf(schedule.getIdSchedule()) });
    }

    // Updating single quote
    public int updateQuote(String newQuote , String currentQuote ) {

        ContentValues values = new ContentValues();
        values.put(TimeWorkContract.QuotesEntry.KEY_NAME_QUOTE , newQuote);//data quote

        // updating row
        return mContext.getContentResolver().update(TimeWorkContract.QuotesEntry.CONTENT_URI,
                values, TimeWorkContract.QuotesEntry.KEY_NAME_QUOTE + " = ?",
                new String[] { currentQuote });
    }

    public int updateAccount(Account account, String curremtUsername) {
         ContentValues values = new ContentValues();
        values.put(TimeWorkContract.AccountEntry.KEY_USERNAME ,account.getmUsername());//account username
        values.put(TimeWorkContract.AccountEntry.KEY_IMAGE ,account.getmImage());//account image
        values.put(TimeWorkContract.AccountEntry.KEY_TYPE_ACCOUNT ,account.getmTypeAccount());//account is login
        values.put(TimeWorkContract.AccountEntry.KEY_MY_PASSWORD,account.getQuote());//account my quote
        // updating row
        return mContext.getContentResolver().update(TimeWorkContract.AccountEntry.CONTENT_URI,
                values, TimeWorkContract.AccountEntry.KEY_USERNAME + " = ?",
                new String[] { String.valueOf(curremtUsername) });
    }

    public void deleteSchedule(Schedule schedule) {
        mContext.getContentResolver().delete(
                TimeWorkContract.ScheduleEntry.CONTENT_URI,
                TimeWorkContract.ScheduleEntry.KEY_NAME + "=?",
                new String[] {
                String.valueOf(schedule.getNameSchedule())

        });

    }


    public void deleteQuote(String quote) {
        mContext.getContentResolver().delete(
                TimeWorkContract.QuotesEntry.CONTENT_URI,
                TimeWorkContract.QuotesEntry.KEY_NAME_QUOTE + " =?",
                new String[] { String.valueOf(quote) });
    }


    public void deleteAccount(String username) {
        mContext.getContentResolver().delete(
                TimeWorkContract.AccountEntry.CONTENT_URI,
                TimeWorkContract.AccountEntry.KEY_USERNAME + " =?",
                new String[] { String.valueOf(username) });
    }

}

