package org.d3ifcool.addictcontrol.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.d3ifcool.addictcontrol.Database.TimeWorkContract;
import org.d3ifcool.addictcontrol.Database.ScheduleCursorAdapter;
import org.d3ifcool.addictcontrol.R;



public class Sabtu extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ScheduleCursorAdapter mAdapter;
    private View mView;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_sabtu, container, false);
        ListView listView = (ListView) mView.findViewById(R.id.list_item_sabtu);

        mAdapter = new ScheduleCursorAdapter(getContext(), null);

        listView.setAdapter(mAdapter);

        getLoaderManager().initLoader(1,null ,this);

        // Inflate the layout for this fragment
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TimeWorkContract.ScheduleEntry._ID,
                TimeWorkContract.ScheduleEntry.KEY_NAME,
                TimeWorkContract.ScheduleEntry.KEY_DAY,
                TimeWorkContract.ScheduleEntry.KEY_START_TIME,
                TimeWorkContract.ScheduleEntry.KEY_END_TIME ,
                TimeWorkContract.ScheduleEntry.KEY_ACTIVE
        };

        return new CursorLoader(
                getContext(),
                TimeWorkContract.ScheduleEntry.CONTENT_URI,
                projection,
                TimeWorkContract.ScheduleEntry.KEY_DAY + "=?",
                new String[] {String.valueOf("Sabtu")},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        LinearLayout linearLayout = (LinearLayout) mView.findViewById( R.id.empty);
        linearLayout.setVisibility(View.VISIBLE);

        if (data.getCount() != 0) {
            mAdapter.swapCursor(data);
            linearLayout.setVisibility(View.GONE);
            return;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
