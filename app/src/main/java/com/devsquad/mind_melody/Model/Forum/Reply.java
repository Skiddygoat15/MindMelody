package com.devsquad.mind_melody.Model.Forum;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "Reply",
        foreignKeys = @ForeignKey(entity = Post.class,
                parentColumns = "postId",
                childColumns = "postId",
                onDelete = ForeignKey.CASCADE))
public class Reply implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "replyId")
    private int replyId;

    @ColumnInfo(name = "postId")
    private int postId;  // 外键，指向 Post 表

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "author")
    private String author;

    @ColumnInfo(name = "createdAt")
    private Date createdAt;

    // 构造函数，getter 和 setter
    public Reply(int postId, String content, String author, Date createdAt) {
        this.postId = postId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    public int getReplyId() { return replyId; }
    public void setReplyId(int replyId) { this.replyId = replyId; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
