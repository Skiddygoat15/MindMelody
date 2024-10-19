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
            startActivity(intent);  
            finish();  
        });

        // create video list
        List<VideoItem> videoItemList = new ArrayList<>();

        videoItemList.add(new VideoItem("Meditation", R.drawable.meditation, R.raw.meditation));

        videoItemList.add(new VideoItem("Focus Day 1", R.drawable.focus, R.raw.day1));
        videoItemList.add(new VideoItem("Focus Day 2", R.drawable.focus, R.raw.day2));
        videoItemList.add(new VideoItem("Focus Day 3", R.drawable.focus, R.raw.day3));
        videoItemList.add(new VideoItem("Focus Day 4", R.drawable.focus, R.raw.day4));
        videoItemList.add(new VideoItem("Focus Day 5", R.drawable.focus, R.raw.day5));
        videoItemList.add(new VideoItem("Focus Day 6", R.drawable.focus, R.raw.day6));
        videoItemList.add(new VideoItem("Focus Day 7", R.drawable.focus, R.raw.day7));

        adapter = new MeditationVideoAdapter(videoItemList, video -> {
            Intent intent = new Intent(MeditationVideoListActivity.this, VideoPlayerActivity.class);
            intent.putExtra("videoResId", video.getVideoResId());
            startActivity(intent);
        });
        recyclerView.setAdapter(new MeditationVideoAdapter(videoItemList, videoItem -> {
            Intent intent = new Intent(MeditationVideoListActivity.this, VideoPlayerActivity.class);
            
            intent.putExtra("videoTitle", videoItem.getTitle());
            intent.putExtra("videoResId", videoItem.getVideoResId());
            startActivity(intent);
        }));

    }
}

