package com.devsquad.mind_melody.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Model.Forum.Post;
import com.devsquad.mind_melody.R;
import com.devsquad.mind_melody.Adapter.SimpleDateFormatter.DateUtils;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private Context context;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.author.setText(post.getAuthor());
        holder.title.setText(post.getTitle());
        holder.content.setText(post.getContent());
        // 使用 DateUtils 来格式化 createdAt
        String formattedDate = DateUtils.formatDateToSydneyTime(post.getCreatedAt());
        holder.createdAt.setText(formattedDate);  // 格式化后的时间显示
        holder.likesNum.setText(post.getLikesNum() + " Likes");
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView author, title, content, createdAt, likesNum;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            createdAt = itemView.findViewById(R.id.createdAt);
            likesNum = itemView.findViewById(R.id.likesNum);
        }
    }
}

