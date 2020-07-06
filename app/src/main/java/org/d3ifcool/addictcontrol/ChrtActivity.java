package org.d3ifcool.addictcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

//import com.github.mikephil.charting.charts.BarChart;

public class ChrtActivity extends AppCompatActivity {

    private Spinner spinner;
    //private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chrt);

        //barChart = findViewById(R.id.bar_chart);
        spinner = (Spinner) findViewById(R.id.type_spinner);

        ArrayAdapter<CharSequence> arrayAdapter  = ArrayAdapter.createFromResource(
                this,R.array.type_chart, android.R.layout.simple_spinner_dropdown_item
        );

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }


    private void barChar() {


    }

}
