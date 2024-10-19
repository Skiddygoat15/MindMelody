package com.devsquad.mind_melody.Model.Forum;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ReplyDao {

    // Insert reply
    @Insert
    long insertReply(Reply reply);

    // Get all replies to a post by postId.
    @Query("SELECT * FROM Reply WHERE postId = :postId ORDER BY createdAt ASC")
    List<Reply> getRepliesByPostId(int postId);

    @Query("UPDATE Reply SET author = :newAuthor WHERE author = :oldAuthor")
    void updateReplyAuthor(String newAuthor, String oldAuthor);
}

