package com.devsquad.mind_melody.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Adapter.SimpleDateFormatter.DateUtils;
import com.devsquad.mind_melody.Model.Forum.Post;
import com.devsquad.mind_melody.R;

import java.util.List;

public class PostDetailAdapter extends RecyclerView.Adapter<PostDetailAdapter.PostDetailViewHolder> {

    private List<Post> postDetails;
    private Context context;

    // 构造函数，接收帖子数据
    public PostDetailAdapter(Context context, List<Post> postDetails) {
        this.context = context;
        this.postDetails = postDetails;
    }

    @NonNull
    @Override
    public PostDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用自定义布局文件 item_post_reply
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_reply, parent, false);
        return new PostDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostDetailViewHolder holder, int position) {
        Post post = postDetails.get(position);

        // 设置帖子数据
        holder.postTitle.setText(post.getTitle());
        holder.postAuthor.setText(post.getAuthor());
        holder.postContent.setText(post.getContent());
        // 使用 DateUtils 来格式化 createdAt
        String formattedDate = DateUtils.formatDateToSydneyTime(post.getCreatedAt());
        holder.postDate.setText(formattedDate);  // 格式化后的时间显示
    }

    @Override
    public int getItemCount() {
        return postDetails.size();
    }

    public static class PostDetailViewHolder extends RecyclerView.ViewHolder {
        // 帖子的各个部分
        TextView postTitle, postAuthor, postContent, postDate;

        public PostDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            // 初始化View
            postTitle = itemView.findViewById(R.id.postTitle);
            postAuthor = itemView.findViewById(R.id.postAuthor);
            postContent = itemView.findViewById(R.id.postContent);
            postDate = itemView.findViewById(R.id.postDate);
        }
    }
}
