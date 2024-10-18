package com.devsquad.mind_melody.Model.User;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.devsquad.mind_melody.Model.DateConverter.DateConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class}, version = 2, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class UserDB extends RoomDatabase {

    private static final String DATABASE_NAME = "user_db";
    private static volatile UserDB DBINSTANCE;

    // 创建一个固定大小的线程池，这里创建了 4 个线程用于处理查询任务
    private static final ExecutorService queryExecutor = Executors.newFixedThreadPool(4);

    // Getting Dao
    public abstract UserDao userDao();

    // Getting database instance
    public static UserDB getDatabase(final Context context) {
        if (DBINSTANCE == null) {
            synchronized (UserDB.class) {
                if (DBINSTANCE == null) {
                    DBINSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    UserDB.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()  // 如果迁移策略不存在，则销毁旧数据
                            .setQueryExecutor(queryExecutor)
                            .build();
                }
            }
        }
        return DBINSTANCE;
    }

    // Destroy database instance
    public static void destroyInstance() {
        DBINSTANCE = null;
    }

}
