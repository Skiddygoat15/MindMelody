package com.devsquad.mind_melody.Model.Forum;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ForumDB_Impl extends ForumDB {
  private volatile PostDao _postDao;

  private volatile ReplyDao _replyDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `Post` (`postId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT, `content` TEXT, `author` TEXT, `createdAt` INTEGER, `likesNum` INTEGER NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `Reply` (`replyId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `postId` INTEGER NOT NULL, `content` TEXT, `author` TEXT, `createdAt` INTEGER, FOREIGN KEY(`postId`) REFERENCES `Post`(`postId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0e227d42bc281b1ac417c764deccd206')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `Post`");
        _db.execSQL("DROP TABLE IF EXISTS `Reply`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        _db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsPost = new HashMap<String, TableInfo.Column>(6);
        _columnsPost.put("postId", new TableInfo.Column("postId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPost.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPost.put("content", new TableInfo.Column("content", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPost.put("author", new TableInfo.Column("author", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPost.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPost.put("likesNum", new TableInfo.Column("likesNum", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPost = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPost = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPost = new TableInfo("Post", _columnsPost, _foreignKeysPost, _indicesPost);
        final TableInfo _existingPost = TableInfo.read(_db, "Post");
        if (! _infoPost.equals(_existingPost)) {
          return new RoomOpenHelper.ValidationResult(false, "Post(com.devsquad.mind_melody.Model.Forum.Post).\n"
                  + " Expected:\n" + _infoPost + "\n"
                  + " Found:\n" + _existingPost);
        }
        final HashMap<String, TableInfo.Column> _columnsReply = new HashMap<String, TableInfo.Column>(5);
        _columnsReply.put("replyId", new TableInfo.Column("replyId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReply.put("postId", new TableInfo.Column("postId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReply.put("content", new TableInfo.Column("content", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReply.put("author", new TableInfo.Column("author", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReply.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReply = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysReply.add(new TableInfo.ForeignKey("Post", "CASCADE", "NO ACTION",Arrays.asList("postId"), Arrays.asList("postId")));
        final HashSet<TableInfo.Index> _indicesReply = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReply = new TableInfo("Reply", _columnsReply, _foreignKeysReply, _indicesReply);
        final TableInfo _existingReply = TableInfo.read(_db, "Reply");
        if (! _infoReply.equals(_existingReply)) {
          return new RoomOpenHelper.ValidationResult(false, "Reply(com.devsquad.mind_melody.Model.Forum.Reply).\n"
                  + " Expected:\n" + _infoReply + "\n"
                  + " Found:\n" + _existingReply);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "0e227d42bc281b1ac417c764deccd206", "c66f1abd5a9db5854ce33ab5b5771b70");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "Post","Reply");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `Post`");
      _db.execSQL("DELETE FROM `Reply`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(PostDao.class, PostDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ReplyDao.class, ReplyDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  public List<Migration> getAutoMigrations(
      @NonNull Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecsMap) {
    return Arrays.asList();
  }

  @Override
  public PostDao postDao() {
    if (_postDao != null) {
      return _postDao;
    } else {
      synchronized(this) {
        if(_postDao == null) {
          _postDao = new PostDao_Impl(this);
        }
        return _postDao;
      }
    }
  }

  @Override
  public ReplyDao replyDao() {
    if (_replyDao != null) {
      return _replyDao;
    } else {
      synchronized(this) {
        if(_replyDao == null) {
          _replyDao = new ReplyDao_Impl(this);
        }
        return _replyDao;
      }
    }
  }
}
