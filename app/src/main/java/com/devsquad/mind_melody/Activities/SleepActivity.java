package com.devsquad.mind_melody.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.devsquad.mind_melody.R;
import com.google.android.material.button.MaterialButton;

public class SleepActivity extends AppCompatActivity {

    private MaterialButton stop;
    private MaterialButton start;
    private SensorManager mSensorManager = null;
    private Sensor mSensor = null;
    /*摇晃检测阈值，决定了对摇晃的敏感程度，越小越敏感。*/
    private static final double SHAKE_SHRESHOLD = 600;
    /*检测的时间间隔100ms*/
    private static final int UPDATE_INTERVAL = 200;
    /*上次检测的时间*/
    private long lastTime;
    /*上次检测时左右、前后、垂直方向加速度*/
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
        /*获取系统服务（SENSOR_SERVICE）返回一个SensorManager对象*/
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        /*通过SensorManager获取相应的（加速度感应器）Sensor类型对象*/
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
        initView();
    }


    private void initView() {
        stop = (MaterialButton) findViewById(R.id.stop);
        start = (MaterialButton) findViewById(R.id.start);
        audioRecordDemo = new AudioRecordDemo();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSensorManager.registerListener(mSensorEventListener, mSensor
                        , SensorManager.SENSOR_DELAY_NORMAL);
                audioRecordDemo.getNoiseLevel();
                start.setBackgroundColor(Color.BLUE);
                start.setText("Sleep Monitoring in Progress...");
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSensorManager.unregisterListener(mSensorEventListener, mSensor);
                audioRecordDemo.setGetVoiceRun(false);
                startActivity(new Intent(SleepActivity.this, SleepQualityReportActivity.class));
            }
        });
    }

    /*声明一个SensorEventListener对象用于侦听Sensor事件，并重载onSensorChanged方法*/
    private final SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                /*显示左右、前后、垂直方向加速度*/
                /*手机晃动检测*/
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
                            Log.d("zzz", "onSensorChanged: 手机在晃动");
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

}