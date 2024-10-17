package com.devsquad.mind_melody.Model.Forum;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ReplyDao {

    // 插入回复
    @Insert
    long insertReply(Reply reply);

    // 根据 postId 获取某个帖子的所有回复
    @Query("SELECT * FROM Reply WHERE postId = :postId ORDER BY createdAt ASC")
    List<Reply> getRepliesByPostId(int postId);
}

