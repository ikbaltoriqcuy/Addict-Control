/*
* Copyright 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.d3ifcool.addictcontrol.AppInfo;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.d3ifcool.addictcontrol.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragment that demonstrates how to use App Usage Statistics API.
 */
public class AppUsageStatisticsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = AppUsageStatisticsFragment.class.getSimpleName();

    //VisibleForTesting for variables below
    UsageStatsManager mUsageStatsManager;
    UsageListAdapter mUsageListAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    Button mOpenUsageSettingButton;
    Spinner mSpinner;
    List<UsageStats> usageStatsList;
    List<UsageStats> tempUsageStatsList= new ArrayList<>();
    SearchView searchView;
    String currentType;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link AppUsageStatisticsFragment}.
     */
    public static AppUsageStatisticsFragment newInstance() {
        AppUsageStatisticsFragment fragment = new AppUsageStatisticsFragment();
        return fragment;
    }

    public AppUsageStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getSystemService("usagestats"); //Context.USAGE_STATS_SERVICE
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_app_usage_statistics, container, false);

        searchView = (SearchView) view.findViewById(R.id.search);
        searchView.setOnQueryTextListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        mUsageListAdapter = new UsageListAdapter(getContext());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_app_usage);
        mLayoutManager = mRecyclerView.getLayoutManager();
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mUsageListAdapter);
        mOpenUsageSettingButton = (Button) rootView.findViewById(R.id.button_open_usage_setting);
        mSpinner = (Spinner) rootView.findViewById(R.id.spinner_time_span);
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            String[] strings = getResources().getStringArray(R.array.action_list);

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ConvertTimeDelay.typeMillis = position;

                StatsUsageInterval statsUsageInterval = StatsUsageInterval
                        .getValue(strings[position]);
                currentType = strings[position];
                if (statsUsageInterval != null) {
                    usageStatsList =
                            getUsageStatistics(statsUsageInterval.mInterval);
                    Collections.sort(usageStatsList, new LastTimeLaunchedComparatorDesc());
                    updateAppsList(usageStatsList);
                }

                searchView.setQuery("", false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Returns the {@link #mRecyclerView} including the time span specified by the
     * intervalType argument.
     *
     * @param intervalType The time interval by which the stats are aggregated.
     *                     Corresponding to the value of {@link UsageStatsManager}.
     *                     E.g. {@link UsageStatsManager#INTERVAL_DAILY}, {@link
     *                     UsageStatsManager#INTERVAL_WEEKLY},
     *
     * @return A list of {@link UsageStats}.
     */
    public List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(intervalType, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        if (queryUsageStats.size() == 0) {
            Log.i(TAG, "The user may not allow the access to apps usage. ");
            Toast.makeText(getActivity(),
                    "Aktifkan Fitur",
                    Toast.LENGTH_LONG).show();
            mOpenUsageSettingButton.setVisibility(View.VISIBLE);
            mOpenUsageSettingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            });
        }else{
            mOpenUsageSettingButton.setVisibility(View.GONE);

        }
        return queryUsageStats;
    }

    @Override
    public void onResume() {
        super.onResume();
        StatsUsageInterval statsUsageInterval = StatsUsageInterval
                .getValue(currentType);
        if (statsUsageInterval != null) {
            usageStatsList =
                    getUsageStatistics(statsUsageInterval.mInterval);
            Collections.sort(usageStatsList, new LastTimeLaunchedComparatorDesc());
            updateAppsList(usageStatsList);
        }

    }

    /**
     * Updates the {@link #mRecyclerView} with the list of {@link UsageStats} passed as an argument.
     *
     * @param usageStatsList A list of {@link UsageStats} from which update the
     *                       {@link #mRecyclerView}.
     */
    //VisibleForTesting
    void updateAppsList(List<UsageStats> usageStatsList) {
        List<CustomUsageStats> customUsageStatsList = new ArrayList<>();
        for (int i = 0; i < usageStatsList.size(); i++) {
            CustomUsageStats customUsageStats = new CustomUsageStats();
            customUsageStats.usageStats = usageStatsList.get(i);
            try {
                Drawable appIcon = getActivity().getPackageManager()
                        .getApplicationIcon(customUsageStats.usageStats.getPackageName());
                customUsageStats.appIcon = appIcon;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, String.format("App Icon is not found for %s",
                        customUsageStats.usageStats.getPackageName()));
                customUsageStats.appIcon = getActivity()
                        .getDrawable(R.drawable.ic_launcher_background);
            }
            customUsageStatsList.add(customUsageStats);
        }
        mUsageListAdapter.setCustomUsageStatsList(customUsageStatsList);
        mUsageListAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }

    private void search(String text) {

        if(usageStatsList !=null && !text.equals("")) {
            tempUsageStatsList.clear();
            for(int n=0; n<usageStatsList.size() ; n++) {
                final PackageManager pm = getActivity().getPackageManager();
                ApplicationInfo ai;
                try {
                    ai = pm.getApplicationInfo(usageStatsList.get(n).getPackageName(), 0);
                } catch (final PackageManager.NameNotFoundException e) {
                    ai = null;
                }
                 String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
                try{
                    if(text.toLowerCase().equals(applicationName.toLowerCase().substring(0,text.length()))) {
                        tempUsageStatsList.add(usageStatsList.get(n));
                    }

                }catch (IndexOutOfBoundsException e) {
                    updateAppsList(tempUsageStatsList);
                    return;
                }

            }

                updateAppsList(tempUsageStatsList);

        }


        if(usageStatsList !=null && text.equals("")) {
            updateAppsList(usageStatsList);
        }
    }

    List<CustomUsageStats> sort( List<CustomUsageStats> customUsageStatsList ) {
        List<CustomUsageStats> data = new ArrayList<>();
        long temp=0;
        long index=0 ;
        CustomUsageStats customUsageStats = null ;
        while(customUsageStatsList.size() !=0) {
            for (int n = 0; n < customUsageStatsList.size(); n++) {
                if (customUsageStatsList.get(n).usageStats.getTotalTimeInForeground() > temp) {
                    customUsageStats =customUsageStatsList.get(n);
                    temp = customUsageStatsList.get(n).usageStats.getTotalTimeInForeground();
                    index = n;
                }
            }

            data.add(customUsageStats);
            customUsageStatsList.remove(index);
        }

        return data;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        search(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        Log.d("text:",newText);
        return true;
    }

    /**
     * The {@link Comparator} to sort a collection of {@link UsageStats} sorted by the timestamp
     * last time the app was used in the descendant order.
     */
    private static class LastTimeLaunchedComparatorDesc implements Comparator<UsageStats> {

        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getLastTimeUsed(), left.getLastTimeUsed());
        }
    }

    /**
     * Enum represents the intervals for {@link UsageStatsManager} so that
     * values for intervals can be found by a String representation.
     *
     */
    //VisibleForTesting
    static enum StatsUsageInterval {
        DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
        WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
        MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY),
        YEARLY("Yearly", UsageStatsManager.INTERVAL_YEARLY);

        private int mInterval;
        private String mStringRepresentation;

        StatsUsageInterval(String stringRepresentation, int interval) {
            mStringRepresentation = stringRepresentation;
            mInterval = interval;
        }

        static StatsUsageInterval getValue(String stringRepresentation) {
            for (StatsUsageInterval statsUsageInterval : values()) {
                if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
                    return statsUsageInterval;
                }
            }
            return null;
        }
    }
}
