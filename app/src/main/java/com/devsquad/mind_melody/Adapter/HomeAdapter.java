package com.devsquad.mind_melody.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private String username;
    private Context context;

    public HomeAdapter(Context context, String username) {
        this.context = context;
        this.username = username;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_content, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        // Set the welcome message with the username
        holder.welcomeText.setText("Welcome to Mind Melody, " + username + "!");

        // Set the slogan message
        holder.sloganText.setText("You are now at the Mind Melody home page. Feel free to explore other features of the app through the button below!");

        // Set the time-based message
        String currentTimeMessage = getTimeBasedMessage();
        holder.timeMessage.setText(currentTimeMessage);
    }

    @Override
    public int getItemCount() {
        // Only one item for now
        return 1;
    }

    private String getTimeBasedMessage() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
        // 设置时区为悉尼
        sdf.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));

        int hour = Integer.parseInt(sdf.format(new Date()));

        if (hour >= 6 && hour < 18) {
            return "Daytime is the best time for meditation!";
        } else if (hour >= 18 && hour < 23) {
            return "It's evening, have a great night's sleep!";
        } else {
            return "It's late night, don't forget to rest early!";
        }
    }


    public static class HomeViewHolder extends RecyclerView.ViewHolder {

        TextView welcomeText, sloganText, timeMessage;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            welcomeText = itemView.findViewById(R.id.welcomeText);
            sloganText = itemView.findViewById(R.id.sloganText);
            timeMessage = itemView.findViewById(R.id.timeMessage);
        }
    }
}
