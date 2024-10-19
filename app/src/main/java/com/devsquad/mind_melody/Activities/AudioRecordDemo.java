package com.devsquad.mind_melody.Activities;
 
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.Manifest;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class AudioRecordDemo {

    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private static final String TAG = "AudioRecord";
    static final int SAMPLE_RATE_IN_HZ = 8000;
    static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                    AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    AudioRecord mAudioRecord;
    boolean isGetVoiceRun;
    Object mLock;

    public void setGetVoiceRun(boolean getVoiceRun) {
        isGetVoiceRun = getVoiceRun;
    }

    public AudioRecordDemo() {
        mLock = new Object();
    }

    // Check and request recording permissions
    public boolean checkAndRequestPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_CODE);
            return false;
        } else {
            // Permission has been granted
            return true;
        }
    }

    public void getNoiseLevel(Context context) {
        // Check permissions first
        if (!checkAndRequestPermissions(context)) {
            Log.e(TAG, "No recording privileges.");
            return;
        }

        if (isGetVoiceRun) {
            Log.e(TAG, "It's still recording.");
            return;
        }
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        if (mAudioRecord == null) {
            Log.e("sound", "mAudioRecord initialization failure");
        }
        isGetVoiceRun = true;
 
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioRecord.startRecording();
                short[] buffer = new short[BUFFER_SIZE];
                while (isGetVoiceRun) {
                    //r is the length of the actual data read, generally r will be less than the buffersize
                    int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                    long v = 0;
                    // Remove the contents of the buffer and perform the sum-of-squares operation.
                    for (int i = 0; i < buffer.length; i++) {
                        v += buffer[i] * buffer[i];
                    }
                    // The sum of the squares is divided by the total length of the data to get the volume size.
                    double mean = v / (double) r;
                    double volume = 10 * Math.log10(mean);
                    Log.d(TAG, "db:" + volume);
                    SleepActivity.decibel = Math.max(volume, SleepActivity.decibel);
                    // About ten times a second.
                    synchronized (mLock) {
                        try {
                            mLock.wait(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }
        }).start();
    }
}