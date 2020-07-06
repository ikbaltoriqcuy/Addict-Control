package org.d3ifcool.addictcontrol;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.d3ifcool.addictcontrol.SlidingTab.SlidingTabLayout;
import org.d3ifcool.addictcontrol.SlidingTab.ViewPagerAdapter;


public class ViewScheduleActivitty extends AppCompatActivity{

    private ViewPager mPager;
    private ViewPagerAdapter mAdapter;
    private SlidingTabLayout mTabs;
    private CharSequence[] mTitles ;
    private int mNumboftabs =7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_schedule_activitty);

        //get all day in one week
        mTitles = getResources().getTextArray(R.array.days) ;

        //hide actioBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        mAdapter =  new ViewPagerAdapter(getSupportFragmentManager(),mTitles,mNumboftabs);

        // Initial ViewPager View and setting the adapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        // Initial the Sliding Tab Layout View
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.colorAccent);
            }

            @Override
            public int getDividerColor(int position) {
                return getResources().getColor(R.color.colorTextTab);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        mTabs.setViewPager(mPager);

    }

    public void addSchedule(View view) {
        //go to ScheduleActivity
        Intent taskActivity = new Intent(this,ScheduleActivity.class);
        startActivity(taskActivity);
        //destroy this activity
        this.finish();
    }
}
