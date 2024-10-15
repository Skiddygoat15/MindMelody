package com.devsquad.mind_melody.Model.Forum;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;
import com.devsquad.mind_melody.Model.DateConverter.DateConverter;
import com.devsquad.mind_melody.Model.User.User;

@Database(entities = {Post.class, Reply.class}, version = 2, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class ForumDB extends RoomDatabase {

    private static volatile ForumDB INSTANCE;

    public abstract PostDao postDao();
    public abstract ReplyDao replyDao();

    public static ForumDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ForumDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ForumDB.class, "forum_database")
                            .fallbackToDestructiveMigration() // 删除旧数据并重建数据库
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    // 定义从版本 1 升级到版本 2 的迁移策略
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
//            // 执行SQL语句，添加新列 `likesNum` 到 `Post` 表
//            database.execSQL("ALTER TABLE Post ADD COLUMN likesNum INTEGER DEFAULT 0 NOT NULL");
        }
    };
}

