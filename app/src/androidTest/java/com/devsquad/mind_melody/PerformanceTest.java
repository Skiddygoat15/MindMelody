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

        // 创建测试用的 Post
        Post testPost = new Post("Test Post Title", "Test Post Content", "Author", new Date(), 0, 1);

        int iterations = 1000;  // 迭代次数设置为 1000 次

        // 测试使用 New Thread 插入和查询
        long newThreadInsertTime = testInsertPostWithNewThread(testPost, iterations);
        long newThreadQueryTime = testQueryPostWithNewThread(1, iterations);

        // 测试使用 Room 线程池插入和查询
        long threadPoolInsertTime = testInsertPostWithThreadPool(testPost, iterations);
        long threadPoolQueryTime = testQueryPostWithThreadPool(1, iterations);

        // 输出结果
        Log.d(TAG, "New Thread Insert Average Time: " + newThreadInsertTime + " ms");
        Log.d(TAG, "Thread Pool Insert Average Time: " + threadPoolInsertTime + " ms");
        Log.d(TAG, "New Thread Query Average Time: " + newThreadQueryTime + " ms");
        Log.d(TAG, "Thread Pool Query Average Time: " + threadPoolQueryTime + " ms");

        assertTrue("Thread Pool insert should be faster", threadPoolInsertTime < newThreadInsertTime);
        assertTrue("Thread Pool query should be faster", threadPoolQueryTime < newThreadQueryTime);
    }

    // 使用 New Thread 插入 Post 的测试
    private long testInsertPostWithNewThread(Post post, int iterations) throws InterruptedException {
        AtomicLong totalDuration = new AtomicLong();
        CountDownLatch latch = new CountDownLatch(iterations);  // 使用 CountDownLatch 控制并发

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            new Thread(() -> {
                postDao.insertPost(post);
                long endTime = System.nanoTime();
                totalDuration.addAndGet((endTime - startTime) / 1_000_000);  // 转换为毫秒
                latch.countDown();
            }).start();
        }

        latch.await(60, TimeUnit.SECONDS); // 最长等待 60 秒
        return totalDuration.get() / iterations;  // 计算平均时间
    }

    // 使用 New Thread 查询 Post 的测试
    private long testQueryPostWithNewThread(int postId, int iterations) throws InterruptedException {
        AtomicLong totalDuration = new AtomicLong();
        CountDownLatch latch = new CountDownLatch(iterations);  // 使用 CountDownLatch 控制并发

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            new Thread(() -> {
                postDao.getPostById(postId);
                long endTime = System.nanoTime();
                totalDuration.addAndGet((endTime - startTime) / 1_000_000);  // 转换为毫秒
                latch.countDown();
            }).start();
        }

        latch.await(60, TimeUnit.SECONDS); // 最长等待 60 秒
        return totalDuration.get() / iterations;  // 计算平均时间
    }

    // 使用 Room 线程池插入 Post 的测试
    private long testInsertPostWithThreadPool(Post post, int iterations) throws InterruptedException {
        AtomicLong totalDuration = new AtomicLong();
        CountDownLatch latch = new CountDownLatch(iterations);  // 使用 CountDownLatch 控制并发

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            forumDB.getQueryExecutor().execute(() -> {
                postDao.insertPost(post);
                long endTime = System.nanoTime();
                totalDuration.addAndGet((endTime - startTime) / 1_000_000);  // 转换为毫秒
                latch.countDown();
            });
        }

        latch.await(60, TimeUnit.SECONDS); // 最长等待 60 秒
        return totalDuration.get() / iterations;  // 计算平均时间
    }

    // 使用 Room 线程池查询 Post 的测试
    private long testQueryPostWithThreadPool(int postId, int iterations) throws InterruptedException {
        AtomicLong totalDuration = new AtomicLong();
        CountDownLatch latch = new CountDownLatch(iterations);  // 使用 CountDownLatch 控制并发

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            forumDB.getQueryExecutor().execute(() -> {
                postDao.getPostById(postId);
                long endTime = System.nanoTime();
                totalDuration.addAndGet((endTime - startTime) / 1_000_000);  // 转换为毫秒
                latch.countDown();
            });
        }

        latch.await(60, TimeUnit.SECONDS); // 最长等待 60 秒
        return totalDuration.get() / iterations;  // 计算平均时间
    }
}
