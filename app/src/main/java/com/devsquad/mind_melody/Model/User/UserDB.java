package com.devsquad.mind_melody.Model.User;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.devsquad.mind_melody.Model.DateConverter.DateConverter;

@Database(entities = {User.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class UserDB extends RoomDatabase {

    private static final String DATABASE_NAME = "user_db";
    private static volatile UserDB DBINSTANCE;

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
