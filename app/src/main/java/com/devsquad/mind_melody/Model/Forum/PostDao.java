package com.devsquad.mind_melody.Model.Forum;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface PostDao {

    // 插入帖子
    @Insert
    long insertPost(Post post);

    // 获取所有帖子
    @Query("SELECT * FROM Post ORDER BY createdAt DESC")
    List<Post> getAllPosts();

    // 根据 postId 获取帖子
    @Query("SELECT * FROM Post WHERE postId = :postId")
    Post getPostById(int postId);

    // 更新点赞数
    @Query("UPDATE Post SET likesNum = likesNum + 1 WHERE postId = :postId")
    void likePost(int postId);

    // 更新点赞数
    @Query("UPDATE Post SET likesNum = likesNum - 1 WHERE postId = :postId")
    void disLikePost(int postId);


}
