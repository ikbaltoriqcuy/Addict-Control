package org.d3ifcool.addictcontrol.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.Quotes.Quotes;
import org.d3ifcool.addictcontrol.Schedule.Schedule;

import java.util.ArrayList;


/**
 * Created by cool on 4/14/2018.
 */

public class TimeWorkProvider extends ContentProvider {
    private static  final int SCHEDULE = 1 ;
    private static  final int SCHEDULE_ID = 2 ;
    private static  final int ACCOUNT = 10 ;
    private static  final int QUOTE = 20 ;


    private static final UriMatcher sUriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(TimeWorkContract.CONTENT_AUTHORITY,
                TimeWorkContract.PATH_SCHEDULE,SCHEDULE);
        sUriMatcher.addURI(TimeWorkContract.CONTENT_AUTHORITY,
                TimeWorkContract.PATH_SCHEDULE + " /#" , SCHEDULE_ID);
        sUriMatcher.addURI(TimeWorkContract.CONTENT_AUTHORITY,
                TimeWorkContract.PATH_ACCOUNT,ACCOUNT);
        sUriMatcher.addURI(TimeWorkContract.CONTENT_AUTHORITY,
                TimeWorkContract.PATH_QUOTES,QUOTE);
    }

    private DatabaseHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase db  = mDatabaseHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case SCHEDULE :
                cursor = db.query(TimeWorkContract.ScheduleEntry.TABLE_SCHEDULE, projection,
                        selection,selectionArgs,null,null,sortOrder);
                break;
            case SCHEDULE_ID :
                selection = TimeWorkContract.ScheduleEntry.KEY_DAY + "=?";
                selectionArgs = new String[] { String.valueOf(
                        ContentUris.parseId(uri)) };

                cursor = db.query(TimeWorkContract.ScheduleEntry.TABLE_SCHEDULE, projection,
                        selection,selectionArgs,null,null,sortOrder);
                break;
            case ACCOUNT :
                cursor = db.query(TimeWorkContract.AccountEntry.TABLE_ACCOUNT, projection,
                        selection,selectionArgs,null,null,sortOrder);
                break;
            case QUOTE :
                cursor = db.query(TimeWorkContract.QuotesEntry.TABLE_QUOTES, projection,
                        selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException(
                        "Cannot query unknow uri" + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }




    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      @Nullable ContentValues contentValues) {
        SQLiteDatabase db  = mDatabaseHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long id=0;

        switch (match) {
            case SCHEDULE :
                String kegiatan = contentValues.getAsString(TimeWorkContract.ScheduleEntry.KEY_NAME);
                if (TextUtils.isEmpty(kegiatan)) {
                    throw new IllegalArgumentException(" kegiatan require a name");
                }
                id = db.insert(TimeWorkContract.ScheduleEntry.TABLE_SCHEDULE, null, contentValues);
                break;
            case ACCOUNT  :
                String account = contentValues.getAsString(TimeWorkContract.AccountEntry.KEY_USERNAME);
                if (TextUtils.isEmpty(account)) {
                    throw new IllegalArgumentException(" username require a name");
                }
                id = db.insert(TimeWorkContract.AccountEntry.TABLE_ACCOUNT, null, contentValues);
                break;
            case QUOTE  :
                String quote = contentValues.getAsString(TimeWorkContract.QuotesEntry.KEY_NAME_QUOTE);
                if (TextUtils.isEmpty(quote)) {
                    throw new IllegalArgumentException(" quote require a name");
                }
                id = db.insert(TimeWorkContract.QuotesEntry.TABLE_QUOTES, null, contentValues);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);


        return ContentUris.withAppendedId(uri,id);

    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int id=0;

        switch (match) {
            case SCHEDULE:
                id = db.delete(
                        TimeWorkContract.ScheduleEntry.TABLE_SCHEDULE,
                        selection,
                        selectionArgs
                        );
                break;
            case QUOTE :
                id = db.delete(
                        TimeWorkContract.QuotesEntry.TABLE_QUOTES,
                        selection,
                        selectionArgs
                );
                break;
            case ACCOUNT :
                id = db.delete(
                        TimeWorkContract.AccountEntry.TABLE_ACCOUNT,
                        selection,
                        selectionArgs
                );
        }
        db.close();
        return id;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        SQLiteDatabase db  = mDatabaseHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long id=0;

        switch (match) {
            case SCHEDULE :
                String kegiatan = contentValues.getAsString(TimeWorkContract.ScheduleEntry.KEY_NAME);
                if (TextUtils.isEmpty(kegiatan)) {
                    throw new IllegalArgumentException("kegiatan require a name");
                }
                id = db.update(TimeWorkContract.ScheduleEntry.TABLE_SCHEDULE, contentValues, selection,selectionArgs);
                break;
            case ACCOUNT  :
                String account = contentValues.getAsString(TimeWorkContract.AccountEntry.KEY_USERNAME);
                if (TextUtils.isEmpty(account)) {
                    throw new IllegalArgumentException("username require a name");
                }
                id = db.update(TimeWorkContract.AccountEntry.TABLE_ACCOUNT, contentValues, selection,selectionArgs);
                break;
            case QUOTE  :
                String quote = contentValues.getAsString(TimeWorkContract.QuotesEntry.KEY_NAME_QUOTE);
                if (TextUtils.isEmpty(quote)) {
                    throw new IllegalArgumentException("quote require a name");
                }
                id = db.update(TimeWorkContract.QuotesEntry.TABLE_QUOTES, contentValues, selection,selectionArgs);
                break;
        }

        return (int) id;
    }


}
