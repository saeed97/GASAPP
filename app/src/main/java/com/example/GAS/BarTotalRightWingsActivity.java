package com.example.GAS;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class BarTotalRightWingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_total_right_swings);
        BarChart chart = findViewById(R.id.barchart);

        ArrayList<BarEntry> NoOfEmp = new ArrayList<BarEntry>();

        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("MyPrefs", 0);
        int rightArmCount = Integer.parseInt(sharedpreferences.getString("rightArmCount", "1500"));


        NoOfEmp.add(new BarEntry(800, 0));
        NoOfEmp.add(new BarEntry(900, 1));
        NoOfEmp.add(new BarEntry(1000, 2));
        NoOfEmp.add(new BarEntry(1100, 3));
        NoOfEmp.add(new BarEntry(1200, 4));
        NoOfEmp.add(new BarEntry(rightArmCount, 5));


        ArrayList<String> year = new ArrayList<String>();

        year.add("Jan");
        year.add("Feb");
        year.add("Mar");
        year.add("Apr");
        year.add("May");
        year.add("Jun");

        BarDataSet bardataset = new BarDataSet(NoOfEmp, "Total Right Arm Swings");
        BarData data = new BarData(year, bardataset);

        chart.setData(data);

        chart.setDescription("The most right bar is the last session");
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.animateY(5000);


    }
}

