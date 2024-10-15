package com.devsquad.mind_melody.Model.Forum;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
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
public final class ReplyDao_Impl implements ReplyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Reply> __insertionAdapterOfReply;

  public ReplyDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReply = new EntityInsertionAdapter<Reply>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `Reply` (`replyId`,`postId`,`content`,`author`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Reply value) {
        stmt.bindLong(1, value.getReplyId());
        stmt.bindLong(2, value.getPostId());
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
      }
    };
  }

  @Override
  public long insertReply(final Reply reply) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfReply.insertAndReturnId(reply);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<Reply> getRepliesByPostId(final int postId) {
    final String _sql = "SELECT * FROM Reply WHERE postId = ? ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, postId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfReplyId = CursorUtil.getColumnIndexOrThrow(_cursor, "replyId");
      final int _cursorIndexOfPostId = CursorUtil.getColumnIndexOrThrow(_cursor, "postId");
      final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
      final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final List<Reply> _result = new ArrayList<Reply>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Reply _item;
        final int _tmpPostId;
        _tmpPostId = _cursor.getInt(_cursorIndexOfPostId);
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
        _item = new Reply(_tmpPostId,_tmpContent,_tmpAuthor,_tmpCreatedAt);
        final int _tmpReplyId;
        _tmpReplyId = _cursor.getInt(_cursorIndexOfReplyId);
        _item.setReplyId(_tmpReplyId);
        _result.add(_item);
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
