package com.devsquad.mind_melody.Model.Forum;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Post.class, Reply.class}, version = 1, exportSchema = false)
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
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

