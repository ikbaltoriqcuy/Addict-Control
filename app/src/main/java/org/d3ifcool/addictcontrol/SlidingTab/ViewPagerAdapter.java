package org.d3ifcool.addictcontrol.SlidingTab;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.d3ifcool.addictcontrol.Fragment.Jumat;
import org.d3ifcool.addictcontrol.Fragment.Kamis;
import org.d3ifcool.addictcontrol.Fragment.Minggu;
import org.d3ifcool.addictcontrol.Fragment.Rabu;
import org.d3ifcool.addictcontrol.Fragment.Sabtu;
import org.d3ifcool.addictcontrol.Fragment.Selasa;
import org.d3ifcool.addictcontrol.Fragment.Senin;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence[] mTitles, int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0 :
                Senin senin = new Senin();
                return senin;

            case 1 :
                Selasa selasa = new Selasa();
                return selasa;

            case 2 :
                Rabu rabu = new Rabu();
                return rabu;

            case 3 :
                Kamis kamis = new Kamis();
                return kamis;

            case 4 :
                Jumat jumat = new Jumat();
                return jumat;

            case 5 :
                Sabtu sabtu = new Sabtu();
                return sabtu;

            case 6 :
                Minggu minggu = new Minggu();
                return minggu;


        }
        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}