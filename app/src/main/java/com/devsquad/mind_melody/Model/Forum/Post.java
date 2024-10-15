package com.devsquad.mind_melody.Model.Forum;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "Post")
public class Post implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "postId")
    private int postId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "author")
    private String author;

    @ColumnInfo(name = "createdAt")
    private Date createdAt;

    @ColumnInfo(name = "likesNum")
    private int likesNum;  // 新增字段：记录点赞数

    @ColumnInfo(name = "UserIdO")
    private int UserIdO;

    // 构造函数，getter 和 setter
    public Post(String title, String content, String author, Date createdAt, int likesNum, int UserIdO) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.likesNum = likesNum;
        this.UserIdO = UserIdO;
    }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public int getLikesNum() { return likesNum; }
    public void setLikesNum(int likesNum) { this.likesNum = likesNum; }

    public int getUserIdO() {
        return UserIdO;
    }

    public void setUserIdO(int oUserId) {
        this.UserIdO = oUserId;
    }
}

