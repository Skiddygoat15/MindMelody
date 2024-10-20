package com.devsquad.mind_melody;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.devsquad.mind_melody.Model.Forum.ForumDB;
import com.devsquad.mind_melody.Model.Forum.Post;
import com.devsquad.mind_melody.Model.Forum.PostDao;
import com.devsquad.mind_melody.Model.Forum.Reply;
import com.devsquad.mind_melody.Model.Forum.ReplyDao;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PerformanceTest {

    private static final String TAG = "PerformanceTest";
    private ForumDB forumDB;
    private PostDao postDao;
    private ReplyDao replyDao;

    @Test
    public void comparePerformance() throws InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        forumDB = ForumDB.getDatabase(appContext);
        postDao = forumDB.postDao();
        replyDao = forumDB.replyDao();


        Post testPost = new Post("Test Post Title", "Test Post Content", "Author", new Date(), 0, 1);

        int iterations = 3000;


        long newThreadInsertTime = testInsertPostWithNewThread(testPost, iterations);
        long newThreadQueryTime = testQueryPostWithNewThread(1, iterations);


        long threadPoolInsertTime = testInsertPostWithThreadPool(testPost, iterations);
        long threadPoolQueryTime = testQueryPostWithThreadPool(1, iterations);


        Log.d(TAG, "New Thread Insert Average Time: " + newThreadInsertTime + " ms");
        Log.d(TAG, "Thread Pool Insert Average Time: " + threadPoolInsertTime + " ms");
        Log.d(TAG, "New Thread Query Average Time: " + newThreadQueryTime + " ms");
        Log.d(TAG, "Thread Pool Query Average Time: " + threadPoolQueryTime + " ms");

        assertTrue("Thread Pool insert should be faster", threadPoolInsertTime < newThreadInsertTime);
        assertTrue("Thread Pool query should be faster", threadPoolQueryTime < newThreadQueryTime);
    }


    private long testInsertPostWithNewThread(Post post, int iterations) throws InterruptedException {
        AtomicLong totalDuration = new AtomicLong();
        CountDownLatch latch = new CountDownLatch(iterations);

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            new Thread(() -> {
                postDao.insertPost(post);
                long endTime = System.nanoTime();
                totalDuration.addAndGet((endTime - startTime) / 1_000_000);
                latch.countDown();
            }).start();
        }

        latch.await(60, TimeUnit.SECONDS);
        return totalDuration.get() / iterations;
    }

    private long testQueryPostWithNewThread(int postId, int iterations) throws InterruptedException {
        AtomicLong totalDuration = new AtomicLong();
        CountDownLatch latch = new CountDownLatch(iterations);

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            new Thread(() -> {
                postDao.getPostById(postId);
                long endTime = System.nanoTime();
                totalDuration.addAndGet((endTime - startTime) / 1_000_000);  // 转换为毫秒
                latch.countDown();
            }).start();
        }

        latch.await(60, TimeUnit.SECONDS);
        return totalDuration.get() / iterations;
    }

    private long testInsertPostWithThreadPool(Post post, int iterations) throws InterruptedException {
        AtomicLong totalDuration = new AtomicLong();
        CountDownLatch latch = new CountDownLatch(iterations);

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            forumDB.getQueryExecutor().execute(() -> {
                postDao.insertPost(post);
                long endTime = System.nanoTime();
                totalDuration.addAndGet((endTime - startTime) / 1_000_000);
                latch.countDown();
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        return totalDuration.get() / iterations;
    }

    private long testQueryPostWithThreadPool(int postId, int iterations) throws InterruptedException {
        AtomicLong totalDuration = new AtomicLong();
        CountDownLatch latch = new CountDownLatch(iterations);

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            forumDB.getQueryExecutor().execute(() -> {
                postDao.getPostById(postId);
                long endTime = System.nanoTime();
                totalDuration.addAndGet((endTime - startTime) / 1_000_000);
                latch.countDown();
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        return totalDuration.get() / iterations;
    }
}
