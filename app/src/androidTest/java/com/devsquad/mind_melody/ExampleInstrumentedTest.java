package com.devsquad.mind_melody;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.Model.User.UserDB;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "PerformanceTest";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.devsquad.mind_melody", appContext.getPackageName());
    }

    // 测试使用 new Thread() 的方法
    @Test
    public void testPerformanceWithNewThread() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UserDB db = UserDB.getDatabase(appContext);

        long startTime = System.nanoTime();

        // 使用 new Thread 进行数据库查询
        new Thread(() -> {
            User user = db.userDao().getUserByEmail("test@example.com");

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;  // 转换为毫秒
            Log.d(TAG, "Time taken with new Thread: " + duration + " ms");

            assertNotNull(user);  // 这里根据需要判断 user 是否不为 null
            assertTrue("New Thread method took too long", duration < 500);  // 假设期望时间为 500ms 内完成
        }).start();
    }

    // 测试使用 Room Executor 的方法
    @Test
    public void testPerformanceWithRoomExecutor() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UserDB db = UserDB.getDatabase(appContext);

        long startTime = System.nanoTime();

        // 使用 Room 提供的查询线程池
        db.getQueryExecutor().execute(() -> {
            User user = db.userDao().getUserByEmail("test@example.com");

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;  // 转换为毫秒
            Log.d(TAG, "Time taken with Room Executor: " + duration + " ms");

            assertNotNull(user);  // 这里根据需要判断 user 是否不为 null
            assertTrue("Room Executor method took too long", duration < 500);  // 假设期望时间为 500ms 内完成
        });
    }
}