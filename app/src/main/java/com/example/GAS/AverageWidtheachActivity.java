

package com.example.GAS;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import androidx.appcompat.app.AppCompatActivity;

        import com.github.mikephil.charting.charts.PieChart;
        import com.github.mikephil.charting.data.BarEntry;
        import com.github.mikephil.charting.data.Entry;
        import com.github.mikephil.charting.data.PieData;
        import com.github.mikephil.charting.data.PieDataSet;
        import com.github.mikephil.charting.formatter.PercentFormatter;
        import com.github.mikephil.charting.utils.ColorTemplate;

        import java.util.ArrayList;

public class AverageWidtheachActivity extends AppCompatActivity {
    PieChart pieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_average_widtheach);
        PieChart pieChart = findViewById(R.id.piechart);
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("MyPrefs", 0);
        int stepWidth = Integer.parseInt(sharedpreferences.getString("stepWidth", "1500"));

        ArrayList<Entry> NoOfEmp = new ArrayList<Entry>();
        NoOfEmp.add(new Entry(3.4f, 0));
        NoOfEmp.add(new Entry(4, 1));
        NoOfEmp.add(new Entry(5, 2));
        NoOfEmp.add(new Entry(3.5f, 3));
        NoOfEmp.add(new Entry(3.6f, 4));
        NoOfEmp.add(new Entry(stepWidth, 5));

        PieDataSet dataSet = new PieDataSet(NoOfEmp, "Average width in cm");

        ArrayList<String> year = new ArrayList<String>();

        year.add("Jan");
        year.add("Feb");
        year.add("Mar");
        year.add("Apr");
        year.add("May");
        year.add("Jun");

        PieData data = new PieData(year, dataSet);
        pieChart.setData(data);

        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(5000, 5000);


    }


}

