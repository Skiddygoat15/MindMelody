package com.devsquad.mind_melody.Model.Forum;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface PostDao {

    // insert post
    @Insert
    long insertPost(Post post);

    // Get all posts
    @Query("SELECT * FROM Post ORDER BY createdAt DESC")
    List<Post> getAllPosts();

    // Get post by postId
    @Query("SELECT * FROM Post WHERE postId = :postId")
    Post getPostById(int postId);

    // Updating the number of likes
    @Query("UPDATE Post SET likesNum = likesNum + 1 WHERE postId = :postId")
    void likePost(int postId);

    // Updating the number of likes
    @Query("UPDATE Post SET likesNum = likesNum - 1 WHERE postId = :postId")
    void disLikePost(int postId);

    @Query("UPDATE Post SET author = :author WHERE UserIdO = :userId")
    void updatePostAuthor(String author, int userId);


}
