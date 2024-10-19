package com.devsquad.mind_melody.Activities;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.devsquad.mind_melody.R;
import com.google.android.material.button.MaterialButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RelativeLayout;


public class SleepQualityReportActivity extends AppCompatActivity {

    private RelativeLayout main;
    private MaterialButton btn;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sleep_quality_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
    }

    private void initView() {
        main = (RelativeLayout) findViewById(R.id.main);
        btn = (MaterialButton) findViewById(R.id.btn);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv1.setText(String.format("1.Microphone Detected Sound at %.2f db", SleepActivity.decibel));
        if (SleepActivity.shakeSum > 0) {
            tv2.setText("2.Accelerometer Detected Phone Movement ");
        } else {
            tv2.setText("2.Accelerometer Detected No Phone Movement ");
        }
        if (SleepActivity.decibel >= 60 || SleepActivity.shakeSum >= 3) {
            tv3.setText("Sleep Quality:\n Poor");
        } else if (SleepActivity.decibel >= 30 || SleepActivity.shakeSum >= 1) {
            tv3.setText("Sleep Quality:\n Moderate");
        } else {
            tv3.setText("Sleep Quality:\n Good");
        }

        // Initialize the back button
        Button returnButton = findViewById(R.id.returnButton);

        // Set the click event for the back button
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to SleepActivity
                Intent intent = new Intent(SleepQualityReportActivity.this, SleepActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}