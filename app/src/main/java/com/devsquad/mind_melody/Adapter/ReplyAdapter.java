package com.devsquad.mind_melody.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Adapter.SimpleDateFormatter.DateUtils;
import com.devsquad.mind_melody.Model.Forum.Reply;
import com.devsquad.mind_melody.R;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {

    private List<Reply> replyList;
    private Context context;

    public ReplyAdapter(Context context, List<Reply> replyList) {
        this.context = context;
        this.replyList = replyList;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        Reply reply = replyList.get(position);
        holder.replyAuthor.setText(reply.getAuthor());
        holder.replyContent.setText(reply.getContent());
        String formattedDate = DateUtils.formatDateToSydneyTime(reply.getCreatedAt());
        holder.replyDate.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {

        TextView replyAuthor, replyContent, replyDate;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            replyAuthor = itemView.findViewById(R.id.replyAuthor);
            replyContent = itemView.findViewById(R.id.replyContent);
            replyDate = itemView.findViewById(R.id.replyDate);
        }
    }
}

