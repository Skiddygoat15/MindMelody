package com.devsquad.mind_melody.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.devsquad.mind_melody.R;
import com.google.android.material.button.MaterialButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SleepActivity extends AppCompatActivity {

    private MaterialButton stop;
    private MaterialButton start;
    private SensorManager mSensorManager = null;
    private Sensor mSensor = null;
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    /* Shake detection threshold, which determines the degree of sensitivity to shaking, the smaller the more sensitive.*/
    private static final double SHAKE_SHRESHOLD = 600;
    /* Detection interval 100ms */
    private static final int UPDATE_INTERVAL = 200;
    /* Time of last test */
    private long lastTime;
    /* Acceleration in left-right, front-back, and vertical directions at the time of the last detection */
    private float last_X, last_y, last_Z;
    public static int shakeSum = 0;
    public static double decibel = 0;
    private AudioRecordDemo audioRecordDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sleep);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        /* Get system service (SENSOR_SERVICE) to return a SensorManager object */
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        /* Get the corresponding (accelerometer) Sensor type object via SensorManager */
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
        initView();
    }


    private void initView() {
        stop = findViewById(R.id.stop);
        start = findViewById(R.id.start);
        audioRecordDemo = new AudioRecordDemo();

        // Initialize the back button
        Button returnButton = findViewById(R.id.returnButton);

        // Set the click event for the back button
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to HomeActivity
                Intent intent = new Intent(SleepActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Optional, ends the current activity to prevent pressing the back button after returning to the SleepActivity.
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check to see if you have recording privileges first
                if (ContextCompat.checkSelfPermission(SleepActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    // No permissions, request permissions
                    ActivityCompat.requestPermissions(SleepActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_CODE);
                } else {
                    // Permission granted. Start recording.
                    startMonitoring();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMonitoring();
            }
        });
    }


    private void startMonitoring() {
        // Register the accelerometer listener
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        // Start recording
        audioRecordDemo.getNoiseLevel(this);
        start.setBackgroundColor(Color.BLUE);
        start.setText("Sleep Monitoring in Progress...");
    }

    private void stopMonitoring() {
        // Log off the accelerometer listener
        mSensorManager.unregisterListener(mSensorEventListener, mSensor);
        // Stop recording
        audioRecordDemo.setGetVoiceRun(false);
        // Skip to Sleep Quality Report Activity
        startActivity(new Intent(SleepActivity.this, SleepQualityReportActivity.class));
    }

    /* Declare a SensorEventListener object to listen for Sensor events and overload the onSensorChanged method */
    private final SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                /*Displays left-right, front-back, and vertical acceleration */
                /*Mobile phone shake detection */
                long currentTime = System.currentTimeMillis();
                if (lastTime != 0) {
                    long diffTime = currentTime - lastTime;
                    if (diffTime > UPDATE_INTERVAL) {

                        float diff_X = x - last_X;
                        float diff_Y = y - last_y;
                        float diff_Z = z - last_Z;
                        double diff = Math.sqrt(diff_X * diff_X + diff_Y * diff_Y + diff_Z * diff_Z) / diffTime * 10000;
                        if (diff > SHAKE_SHRESHOLD) {
                            shakeSum++;
                            Log.d("zzz", "onSensorChanged: The phone is shaking.");
                        }
                    }
                }
                lastTime = currentTime;
                last_X = x;
                last_y = y;
                last_Z = z;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted. Start recording.
                startMonitoring();
            } else {
                // Permission denied
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    // The user checked the box “Don't ask again.”
                    Log.e("SleepActivity", "Permission is permanently denied");
                    showSettingsAlert();
                } else {
                    // User refused but did not check “Don't ask again”.
                    start.setText("Permission Denied");
                    start.setBackgroundColor(Color.RED);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showSettingsAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Require Permissions")
                .setMessage("The app requires recording permissions, please go to Settings to manually grant the permissions.")
                .setPositiveButton("Go To Settings", (dialog, which) -> {
                    // 跳转到应用设置页面
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // 用户取消操作
                    Log.e("SleepActivity", "The user chooses not to go to Setup");
                })
                .show();
    }

}