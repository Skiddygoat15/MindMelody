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

    // Using new Thread() method
    @Test
    public void testPerformanceWithNewThread() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UserDB db = UserDB.getDatabase(appContext);

        long startTime = System.nanoTime();

        new Thread(() -> {
            User user = db.userDao().getUserByEmail("test@example.com");

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            Log.d(TAG, "Time taken with new Thread: " + duration + " ms");

            assertNotNull(user);
            assertTrue("New Thread method took too long", duration < 500);
        }).start();
    }

    // Using room thread pool (room executor)
    @Test
    public void testPerformanceWithRoomExecutor() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UserDB db = UserDB.getDatabase(appContext);

        long startTime = System.nanoTime();

        db.getQueryExecutor().execute(() -> {
            User user = db.userDao().getUserByEmail("test@example.com");

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            Log.d(TAG, "Time taken with Room Executor: " + duration + " ms");

            assertNotNull(user);
            assertTrue("Room Executor method took too long", duration < 500);
        });
    }
}