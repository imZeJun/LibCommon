package com.lib.common.worker;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class SimpleWorker {

    private static Looper sLooper;

    private Handler mWorkerHandler;
    private Handler mMainHandler;

    private SimpleWorker() {
        synchronized (SimpleWorker.class) {
            if (sLooper == null) {
                HandlerThread thread = new HandlerThread("SimpleWorker");
                thread.start();
                sLooper = thread.getLooper();
            }
        }
        mWorkerHandler = createHandler(sLooper);
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    protected Handler createHandler(Looper looper) {
        return new Handler(looper);
    }

    public void startAsyncWork(Runnable runnable) {
        mWorkerHandler.post(runnable);
    }

    public void startSyncWork(Runnable runnable) {
        mMainHandler.post(runnable);
    }
}
