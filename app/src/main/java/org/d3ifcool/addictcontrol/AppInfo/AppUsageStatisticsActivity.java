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

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import org.d3ifcool.addictcontrol.R;

/**
 * Launcher Activity for the App Usage Statistics sample app.
 */
public class AppUsageStatisticsActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage_statistics);
        relativeLayout = (RelativeLayout) findViewById(R.id.message);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if(android.os.Build.VERSION.SDK_INT <21) {
            relativeLayout.setVisibility(View.VISIBLE);
        }else{
            if (savedInstanceState == null) {

                relativeLayout.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, AppUsageStatisticsFragment.newInstance())
                        .commit();
            }
        }
    }
}
