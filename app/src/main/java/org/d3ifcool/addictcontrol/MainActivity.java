package org.d3ifcool.addictcontrol;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TabHost;

import org.d3ifcool.addictcontrol.Account.Account;
import org.d3ifcool.addictcontrol.AppInfo.AppUsageStatisticsActivity;
import org.d3ifcool.addictcontrol.Database.DatabaseAdapter;

public class MainActivity extends ActivityGroup {

    private int mCurrentTab = 0; //curent tab from tab host
    private TabHost mTabHost; //tabhost

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
            Account account = databaseAdapter.getAccount();
            if(account == null) {
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
            }

        }catch (Exception e) {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }



        Bundle extra = getIntent().getExtras();
        if(extra== null) {
            mCurrentTab = 0;
        }else{
            mCurrentTab = extra.getInt("current_tab");
        }

        Intent intentActivity1,intentActivity2,intentActivity3;
        mTabHost = (TabHost) findViewById(R.id.tabHost);
        TabHost.TabSpec tabSpec1,tabSpec2,tabSpec3,tabSpec4;
        mTabHost.setup(getLocalActivityManager());


        //create TabSpec "Home" using tabHost

        tabSpec4 = mTabHost.newTabSpec("Home");
        tabSpec4.setIndicator("Beranda",getResources().getDrawable(R.drawable.ic_home_black_24dp) );
        intentActivity3 = new Intent(this,HomeActivity.class);
        tabSpec4.setContent(intentActivity3);
        mTabHost.addTab(tabSpec4);

        //end

        //create TabSpec "Home" using tabHost

        tabSpec1 = mTabHost.newTabSpec("Kegiatan");
        tabSpec1.setIndicator("Penggunaan Aplikasi",getResources().getDrawable(R.drawable.ic_access_time_black_24dp));
        intentActivity1= new Intent(this,AppUsageStatisticsActivity.class);
        tabSpec1.setContent(intentActivity1);
        mTabHost.addTab(tabSpec1);

        //end

        //create TabSpec "Quotes" using tabHost


        tabSpec2 = mTabHost.newTabSpec("Parent");
        tabSpec2.setIndicator("Kontrol");
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        Account account = databaseAdapter.getAccount();

        if(account !=null) {
            if (account.getmTypeAccount().equals("Orang Tua"))
                intentActivity2 = new Intent(this, KidsControl.class);
            else
                intentActivity2 = new Intent(this, ControlActivity.class);

            tabSpec2.setContent(intentActivity2);

            mTabHost.addTab(tabSpec2);
        }

        //end

        //create TabSpec "MyAccount" using tabHost

        /*
        tabSpec3 = mTabHost.newTabSpec(getString(R.string.tab_account));
        tabSpec3.setIndicator("",getResources().getDrawable(R.drawable.ic_person_black_24dp));
        intentActivity3 = new Intent(this,AccountAcivity.class);
        tabSpec3.setContent(intentActivity3);
        mTabHost.addTab(tabSpec3);
        */

        //end




        //set tab which one you want to open first time 0 or 1 or 2
        mTabHost.setCurrentTab(mCurrentTab);


    }

    private  void setTabDesign() {
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if(s.equals("Penggunaan Aplikasi")){

                }
            }
        });
    }

    public void OpenSettings(View view) {
        Intent intent = new Intent(this,AccountAcivity.class);
        startActivity(intent);
    }


    //create profile for first time
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Intent intent = new Intent(this,CreateProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    /*
    @Override
    public void finish() {
        //super.finish();
    }
    */
}
