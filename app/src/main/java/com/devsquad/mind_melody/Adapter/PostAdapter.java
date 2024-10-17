package com.devsquad.mind_melody.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Model.Forum.Post;
import com.devsquad.mind_melody.R;
import com.devsquad.mind_melody.Adapter.SimpleDateFormatter.DateUtils;
import com.devsquad.mind_melody.Model.Forum.PostDao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private Context context;
    private PostDao postDao;  // 数据库DAO
    private Set<Integer> likedPosts = new HashSet<>();  // 跟踪用户点赞的帖子ID

    // 定义接口
    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    // 接口的实例
    private OnItemClickListener onItemClickListener;

    // 设置监听器方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public PostAdapter(Context context, List<Post> postList, PostDao postDao) {
        this.context = context;
        this.postList = postList;
        this.postDao = postDao;
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

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(post);  // 通过接口传递点击事件
            }
        });

        // 设置点赞图标的初始状态
        if (likedPosts.contains(post.getPostId())) {
            holder.likeIcon.setImageResource(R.drawable.ic_thumb_up_clicked);  // 显示已点赞图标
        } else {
            holder.likeIcon.setImageResource(R.drawable.ic_thumb_up);  // 显示未点赞图标
        }

        // 设置点赞图标的点击事件
        holder.likeIcon.setOnClickListener(v -> {
            if (likedPosts.contains(post.getPostId())) {
                // 如果已经点赞，取消点赞
                CompletableFuture.runAsync(() -> {
                    postDao.disLikePost(post.getPostId());  // 调用取消点赞的DAO方法
                    post.setLikesNum(post.getLikesNum() - 1);  // 更新本地的点赞数
                }).thenRun(() -> {
                    // 更新UI
                    likedPosts.remove(post.getPostId());  // 更新跟踪点赞的集合
                    holder.likesNum.post(() -> holder.likesNum.setText(post.getLikesNum() + " Likes"));  // 更新点赞数
                    holder.likeIcon.post(() -> holder.likeIcon.setImageResource(R.drawable.ic_thumb_up));  // 切换回未点赞图标
                });
            } else {
                // 如果没有点赞，进行点赞
                CompletableFuture.runAsync(() -> {
                    postDao.likePost(post.getPostId());  // 调用点赞的DAO方法
                    post.setLikesNum(post.getLikesNum() + 1);  // 更新本地的点赞数
                }).thenRun(() -> {
                    // 更新UI
                    likedPosts.add(post.getPostId());  // 更新跟踪点赞的集合
                    holder.likesNum.post(() -> holder.likesNum.setText(post.getLikesNum() + " Likes"));  // 更新点赞数
                    holder.likeIcon.post(() -> holder.likeIcon.setImageResource(R.drawable.ic_thumb_up_clicked));  // 切换为已点赞图标
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView author, title, content, createdAt, likesNum;
        ImageView likeIcon;  // 新增点赞图标

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            createdAt = itemView.findViewById(R.id.createdAt);
            likesNum = itemView.findViewById(R.id.likesNum);
            likeIcon = itemView.findViewById(R.id.likeIcon);  // 获取点赞图标的视图
        }
    }
}