package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Adapter.MeditationVideoAdapter;
import com.devsquad.mind_melody.Model.Video.VideoItem;
import com.devsquad.mind_melody.R;

import java.util.ArrayList;
import java.util.List;

public class MeditationVideoListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton backToHomeButton;
    private MeditationVideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation_video_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        backToHomeButton = findViewById(R.id.backToHomeButton);

        backToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MeditationVideoListActivity.this, HomeActivity.class);
            startActivity(intent);  // 启动 HomeActivity
            finish();  // 关闭当前界面，防止回退到该界面
        });

        // 创建视频列表
        List<VideoItem> videoItemList = new ArrayList<>();

        videoItemList.add(new VideoItem("focus Day 1", R.drawable.focus, R.raw.day1));
        videoItemList.add(new VideoItem("focus Day 2", R.drawable.focus, R.raw.day2));
        videoItemList.add(new VideoItem("focus Day 3", R.drawable.focus, R.raw.day3));
        videoItemList.add(new VideoItem("focus Day 4", R.drawable.focus, R.raw.day4));
        videoItemList.add(new VideoItem("focus Day 5", R.drawable.focus, R.raw.day5));
        videoItemList.add(new VideoItem("focus Day 6", R.drawable.focus, R.raw.day6));
        videoItemList.add(new VideoItem("focus Day 7", R.drawable.focus, R.raw.day7));

        videoItemList.add(new VideoItem("meditation", R.drawable.meditation, R.raw.meditation));


        adapter = new MeditationVideoAdapter(videoItemList, video -> {
            Intent intent = new Intent(MeditationVideoListActivity.this, VideoPlayerActivity.class);
            intent.putExtra("videoResId", video.getVideoResId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
}

