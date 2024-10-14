package com.devsquad.mind_melody.Model.Forum;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.devsquad.mind_melody.Model.DateConverter.DateConverter;
import java.lang.Class;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PostDao_Impl implements PostDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Post> __insertionAdapterOfPost;

  private final SharedSQLiteStatement __preparedStmtOfLikePost;

  public PostDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPost = new EntityInsertionAdapter<Post>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `Post` (`postId`,`title`,`content`,`author`,`createdAt`,`likesNum`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Post value) {
        stmt.bindLong(1, value.getPostId());
        if (value.getTitle() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getTitle());
        }
        if (value.getContent() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getContent());
        }
        if (value.getAuthor() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getAuthor());
        }
        final Long _tmp = DateConverter.dateToTimestamp(value.getCreatedAt());
        if (_tmp == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, _tmp);
        }
        stmt.bindLong(6, value.getLikesNum());
      }
    };
    this.__preparedStmtOfLikePost = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE Post SET likesNum = likesNum + 1 WHERE postId = ?";
        return _query;
      }
    };
  }

  @Override
  public long insertPost(final Post post) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfPost.insertAndReturnId(post);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void likePost(final int postId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfLikePost.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, postId);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfLikePost.release(_stmt);
    }
  }

  @Override
  public List<Post> getAllPosts() {
    final String _sql = "SELECT * FROM Post ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfPostId = CursorUtil.getColumnIndexOrThrow(_cursor, "postId");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
      final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final int _cursorIndexOfLikesNum = CursorUtil.getColumnIndexOrThrow(_cursor, "likesNum");
      final List<Post> _result = new ArrayList<Post>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Post _item;
        final String _tmpTitle;
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _tmpTitle = null;
        } else {
          _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        }
        final String _tmpContent;
        if (_cursor.isNull(_cursorIndexOfContent)) {
          _tmpContent = null;
        } else {
          _tmpContent = _cursor.getString(_cursorIndexOfContent);
        }
        final String _tmpAuthor;
        if (_cursor.isNull(_cursorIndexOfAuthor)) {
          _tmpAuthor = null;
        } else {
          _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
        }
        final Date _tmpCreatedAt;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfCreatedAt);
        }
        _tmpCreatedAt = DateConverter.fromTimestamp(_tmp);
        final int _tmpLikesNum;
        _tmpLikesNum = _cursor.getInt(_cursorIndexOfLikesNum);
        _item = new Post(_tmpTitle,_tmpContent,_tmpAuthor,_tmpCreatedAt,_tmpLikesNum);
        final int _tmpPostId;
        _tmpPostId = _cursor.getInt(_cursorIndexOfPostId);
        _item.setPostId(_tmpPostId);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Post getPostById(final int postId) {
    final String _sql = "SELECT * FROM Post WHERE postId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, postId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfPostId = CursorUtil.getColumnIndexOrThrow(_cursor, "postId");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
      final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final int _cursorIndexOfLikesNum = CursorUtil.getColumnIndexOrThrow(_cursor, "likesNum");
      final Post _result;
      if(_cursor.moveToFirst()) {
        final String _tmpTitle;
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _tmpTitle = null;
        } else {
          _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        }
        final String _tmpContent;
        if (_cursor.isNull(_cursorIndexOfContent)) {
          _tmpContent = null;
        } else {
          _tmpContent = _cursor.getString(_cursorIndexOfContent);
        }
        final String _tmpAuthor;
        if (_cursor.isNull(_cursorIndexOfAuthor)) {
          _tmpAuthor = null;
        } else {
          _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
        }
        final Date _tmpCreatedAt;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfCreatedAt);
        }
        _tmpCreatedAt = DateConverter.fromTimestamp(_tmp);
        final int _tmpLikesNum;
        _tmpLikesNum = _cursor.getInt(_cursorIndexOfLikesNum);
        _result = new Post(_tmpTitle,_tmpContent,_tmpAuthor,_tmpCreatedAt,_tmpLikesNum);
        final int _tmpPostId;
        _tmpPostId = _cursor.getInt(_cursorIndexOfPostId);
        _result.setPostId(_tmpPostId);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
