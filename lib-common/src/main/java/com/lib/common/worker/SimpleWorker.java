package com.lib.common.worker;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleWorker {

    private static final String LOOPER_THREAD_NAME = "SimpleWorkerLooper";
    private static final String EXECUTOR_THREAD_NAME = "SimpleWorkerExecutorService";

    private static Looper sLooper;
    private static ExecutorService sExecutorService;

    private boolean useThreadPool;
    private Handler workerHandler;
    private Handler mainHandler;

    /**
     * 创建简单的异步工作类.
     * @param threadPool 为 true 表示通过线程池工作，false 则使用统一的队列，当前面出现耗时操作时会去排队。
     */
    public SimpleWorker(boolean threadPool) {
        if (threadPool) {
            createExecutor();
        } else {
            createLooper();
        }
        this.useThreadPool = threadPool;
        workerHandler = createHandler(sLooper);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 在子线程中执行任务。
     * @param runnable 任务
     */
    public void startAsyncWork(Runnable runnable) {
        if (useThreadPool) {
            sExecutorService.submit(runnable);
        } else {
            workerHandler.post(runnable);
        }
    }

    /**
     * 在主线程中执行任务。
     * @param runnable 任务。
     */
    public void startMainWork(Runnable runnable) {
        mainHandler.post(runnable);
    }

    private Handler createHandler(Looper looper) {
        return new Handler(looper);
    }

    private void createExecutor() {
        synchronized (SimpleWorker.class) {
            if (sExecutorService == null) {
                sExecutorService = Executors.newCachedThreadPool(new PriorityThreadFactory(EXECUTOR_THREAD_NAME, (android.os.Process.THREAD_PRIORITY_BACKGROUND + android.os
                        .Process.THREAD_PRIORITY_FOREGROUND) / 2));
            }
        }
    }

    private void createLooper() {
        synchronized (SimpleWorker.class) {
            if (sLooper == null) {
                HandlerThread thread = new HandlerThread(LOOPER_THREAD_NAME);
                thread.start();
                sLooper = thread.getLooper();
            }
        }
    }
}
