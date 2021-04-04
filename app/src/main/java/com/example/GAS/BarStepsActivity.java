
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
        import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
        import com.github.mikephil.charting.charts.HorizontalBarChart;
        import android.content.Context;
        import android.preference.PreferenceManager;

        import java.util.ArrayList;

public class BarStepsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedpreferences;
        String MyPREFERENCES = "MyPrefs" ;
        setContentView(R.layout.activity_bar_steps);
        BarChart chart = (BarChart) findViewById(R.id.barchart);

        ArrayList<BarEntry> NoOfEmp = new ArrayList<BarEntry>();
        // accessing the shared data from the memory
        sharedpreferences = getSharedPreferences("MyPrefs", 0);
        int steps = Integer.parseInt(sharedpreferences.getString("stepCount", "1500"));


        NoOfEmp.add(new BarEntry(800, 0));
        NoOfEmp.add(new BarEntry(900, 1));
        NoOfEmp.add(new BarEntry(1000, 2));
        NoOfEmp.add(new BarEntry(1100, 3));
        NoOfEmp.add(new BarEntry(1200, 4));
        NoOfEmp.add(new BarEntry(steps, 5));


        ArrayList<String> year = new ArrayList<String>();

        year.add("Jan");
        year.add("Feb");
        year.add("Mar");
        year.add("Apr");
        year.add("May");
        year.add("Jun");

        BarDataSet bardataset = new BarDataSet(NoOfEmp, "Number Steps/Session");

        BarData data = new BarData(year, bardataset);

        chart.setData(data);

        chart.setDescription("The most right bar is the last session");
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.animateY(5000);
    }

//    public static int getDefaults(String key, Context context) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        return preferences.getString(key, null);
//    }
}

