package com.example.GAS;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;

public class ViewingData extends AppCompatActivity {
    Button TotalSessionsTime,TotalLeftArmsSwings,TotalRightArmsSwings, TotalNumberSteps,AverageFeetWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewing_data);
        // BarChart barChart = (BarChart) findViewById(R.id.barchart);




        TotalSessionsTime = findViewById(R.id.TotalSessionsTime);
        TotalNumberSteps = findViewById(R.id.TotalNumberSteps);
        TotalLeftArmsSwings = findViewById(R.id.TotalLeftArmsSwings);
        TotalRightArmsSwings = findViewById(R.id.TotalRightArmsSwings);
        AverageFeetWidth = findViewById(R.id.AverageFeetWidth);
        //AverageSwingsDirection = findViewById(R.id.AverageSwingsDirection);

        //btnPieChart = findViewById(R.id.btnPieChart);
        TotalSessionsTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ViewingData.this, BarTotaltimeActivity.class);
                startActivity(I);
            }
        });

        TotalNumberSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ViewingData.this, BarStepsActivity.class);
                startActivity(I);
            }
        });

        TotalLeftArmsSwings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ViewingData.this, BarTotalLeftWingsActivity.class);
                startActivity(I);
            }
        });

        TotalRightArmsSwings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ViewingData.this, BarTotalRightWingsActivity.class);
                startActivity(I);
            }
        });




        AverageFeetWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ViewingData.this, AverageWidtheachActivity.class);
                startActivity(I);
            }
        });

//        AverageSwingsDirection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent I = new Intent(ViewingData.this, AverageDirectionActivity.class);
//                startActivity(I);
//            }
//        });



    }


}