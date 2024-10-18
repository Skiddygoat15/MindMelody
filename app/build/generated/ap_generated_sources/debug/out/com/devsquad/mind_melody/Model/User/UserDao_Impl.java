package com.devsquad.mind_melody.Model.User;

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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<User> __insertionAdapterOfUser;

  private final SharedSQLiteStatement __preparedStmtOfUpdateUser;

  public UserDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUser = new EntityInsertionAdapter<User>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `User` (`userId`,`userEmail`,`userPassword`,`firstName`,`lastName`,`registerDate`,`lastMeditDate`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, User value) {
        stmt.bindLong(1, value.getUserId());
        if (value.getUserEmail() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getUserEmail());
        }
        if (value.getUserPassword() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getUserPassword());
        }
        if (value.getFirstName() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getFirstName());
        }
        if (value.getLastName() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getLastName());
        }
        final Long _tmp = DateConverter.dateToTimestamp(value.getRegisterDate());
        if (_tmp == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindLong(6, _tmp);
        }
        final Long _tmp_1 = DateConverter.dateToTimestamp(value.getLastMeditDate());
        if (_tmp_1 == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindLong(7, _tmp_1);
        }
      }
    };
    this.__preparedStmtOfUpdateUser = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE User SET userEmail = ?, userPassword = ?, firstName = ?, lastName = ? WHERE userId = ?";
        return _query;
      }
    };
  }

  @Override
  public long registerUser(final User user) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfUser.insertAndReturnId(user);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateUser(final int userId, final String email, final String password,
      final String firstName, final String lastName) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateUser.acquire();
    int _argIndex = 1;
    if (email == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, email);
    }
    _argIndex = 2;
    if (password == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, password);
    }
    _argIndex = 3;
    if (firstName == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, firstName);
    }
    _argIndex = 4;
    if (lastName == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, lastName);
    }
    _argIndex = 5;
    _stmt.bindLong(_argIndex, userId);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdateUser.release(_stmt);
    }
  }

  @Override
  public User getUser(final int userId) {
    final String _sql = "SELECT * FROM User WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfUserEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "userEmail");
      final int _cursorIndexOfUserPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "userPassword");
      final int _cursorIndexOfFirstName = CursorUtil.getColumnIndexOrThrow(_cursor, "firstName");
      final int _cursorIndexOfLastName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastName");
      final int _cursorIndexOfRegisterDate = CursorUtil.getColumnIndexOrThrow(_cursor, "registerDate");
      final int _cursorIndexOfLastMeditDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMeditDate");
      final User _result;
      if(_cursor.moveToFirst()) {
        final int _tmpUserId;
        _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
        final String _tmpUserEmail;
        if (_cursor.isNull(_cursorIndexOfUserEmail)) {
          _tmpUserEmail = null;
        } else {
          _tmpUserEmail = _cursor.getString(_cursorIndexOfUserEmail);
        }
        final String _tmpUserPassword;
        if (_cursor.isNull(_cursorIndexOfUserPassword)) {
          _tmpUserPassword = null;
        } else {
          _tmpUserPassword = _cursor.getString(_cursorIndexOfUserPassword);
        }
        final String _tmpFirstName;
        if (_cursor.isNull(_cursorIndexOfFirstName)) {
          _tmpFirstName = null;
        } else {
          _tmpFirstName = _cursor.getString(_cursorIndexOfFirstName);
        }
        final String _tmpLastName;
        if (_cursor.isNull(_cursorIndexOfLastName)) {
          _tmpLastName = null;
        } else {
          _tmpLastName = _cursor.getString(_cursorIndexOfLastName);
        }
        final Date _tmpRegisterDate;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfRegisterDate)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfRegisterDate);
        }
        _tmpRegisterDate = DateConverter.fromTimestamp(_tmp);
        final Date _tmpLastMeditDate;
        final Long _tmp_1;
        if (_cursor.isNull(_cursorIndexOfLastMeditDate)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getLong(_cursorIndexOfLastMeditDate);
        }
        _tmpLastMeditDate = DateConverter.fromTimestamp(_tmp_1);
        _result = new User(_tmpUserId,_tmpUserEmail,_tmpUserPassword,_tmpFirstName,_tmpLastName,_tmpRegisterDate,_tmpLastMeditDate);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int checkEmailExists(final String email) {
    final String _sql = "SELECT COUNT(*) FROM User WHERE userEmail = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (email == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, email);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public User loginUser(final String email, final String password) {
    final String _sql = "SELECT * FROM User WHERE userEmail = ? AND userPassword = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (email == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, email);
    }
    _argIndex = 2;
    if (password == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, password);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfUserEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "userEmail");
      final int _cursorIndexOfUserPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "userPassword");
      final int _cursorIndexOfFirstName = CursorUtil.getColumnIndexOrThrow(_cursor, "firstName");
      final int _cursorIndexOfLastName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastName");
      final int _cursorIndexOfRegisterDate = CursorUtil.getColumnIndexOrThrow(_cursor, "registerDate");
      final int _cursorIndexOfLastMeditDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMeditDate");
      final User _result;
      if(_cursor.moveToFirst()) {
        final int _tmpUserId;
        _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
        final String _tmpUserEmail;
        if (_cursor.isNull(_cursorIndexOfUserEmail)) {
          _tmpUserEmail = null;
        } else {
          _tmpUserEmail = _cursor.getString(_cursorIndexOfUserEmail);
        }
        final String _tmpUserPassword;
        if (_cursor.isNull(_cursorIndexOfUserPassword)) {
          _tmpUserPassword = null;
        } else {
          _tmpUserPassword = _cursor.getString(_cursorIndexOfUserPassword);
        }
        final String _tmpFirstName;
        if (_cursor.isNull(_cursorIndexOfFirstName)) {
          _tmpFirstName = null;
        } else {
          _tmpFirstName = _cursor.getString(_cursorIndexOfFirstName);
        }
        final String _tmpLastName;
        if (_cursor.isNull(_cursorIndexOfLastName)) {
          _tmpLastName = null;
        } else {
          _tmpLastName = _cursor.getString(_cursorIndexOfLastName);
        }
        final Date _tmpRegisterDate;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfRegisterDate)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfRegisterDate);
        }
        _tmpRegisterDate = DateConverter.fromTimestamp(_tmp);
        final Date _tmpLastMeditDate;
        final Long _tmp_1;
        if (_cursor.isNull(_cursorIndexOfLastMeditDate)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getLong(_cursorIndexOfLastMeditDate);
        }
        _tmpLastMeditDate = DateConverter.fromTimestamp(_tmp_1);
        _result = new User(_tmpUserId,_tmpUserEmail,_tmpUserPassword,_tmpFirstName,_tmpLastName,_tmpRegisterDate,_tmpLastMeditDate);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public User getUserByEmail(final String email) {
    final String _sql = "SELECT * FROM User WHERE userEmail = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (email == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, email);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfUserEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "userEmail");
      final int _cursorIndexOfUserPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "userPassword");
      final int _cursorIndexOfFirstName = CursorUtil.getColumnIndexOrThrow(_cursor, "firstName");
      final int _cursorIndexOfLastName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastName");
      final int _cursorIndexOfRegisterDate = CursorUtil.getColumnIndexOrThrow(_cursor, "registerDate");
      final int _cursorIndexOfLastMeditDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMeditDate");
      final User _result;
      if(_cursor.moveToFirst()) {
        final int _tmpUserId;
        _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
        final String _tmpUserEmail;
        if (_cursor.isNull(_cursorIndexOfUserEmail)) {
          _tmpUserEmail = null;
        } else {
          _tmpUserEmail = _cursor.getString(_cursorIndexOfUserEmail);
        }
        final String _tmpUserPassword;
        if (_cursor.isNull(_cursorIndexOfUserPassword)) {
          _tmpUserPassword = null;
        } else {
          _tmpUserPassword = _cursor.getString(_cursorIndexOfUserPassword);
        }
        final String _tmpFirstName;
        if (_cursor.isNull(_cursorIndexOfFirstName)) {
          _tmpFirstName = null;
        } else {
          _tmpFirstName = _cursor.getString(_cursorIndexOfFirstName);
        }
        final String _tmpLastName;
        if (_cursor.isNull(_cursorIndexOfLastName)) {
          _tmpLastName = null;
        } else {
          _tmpLastName = _cursor.getString(_cursorIndexOfLastName);
        }
        final Date _tmpRegisterDate;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfRegisterDate)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfRegisterDate);
        }
        _tmpRegisterDate = DateConverter.fromTimestamp(_tmp);
        final Date _tmpLastMeditDate;
        final Long _tmp_1;
        if (_cursor.isNull(_cursorIndexOfLastMeditDate)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getLong(_cursorIndexOfLastMeditDate);
        }
        _tmpLastMeditDate = DateConverter.fromTimestamp(_tmp_1);
        _result = new User(_tmpUserId,_tmpUserEmail,_tmpUserPassword,_tmpFirstName,_tmpLastName,_tmpRegisterDate,_tmpLastMeditDate);
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
