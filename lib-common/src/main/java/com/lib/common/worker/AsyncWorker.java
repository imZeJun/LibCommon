package com.lib.common.worker;

import android.content.AsyncQueryHandler;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public abstract class AsyncWorker<ResultType, CookieType> extends Handler {

    private Handler mWorkerThreadHandler;

    private static Looper sLooper = null;

    protected static final class WorkerArgs<ResultType, CookieType> {
        Handler handler;
        int token;
        ResultType result;
        CookieType cookie;
    }

    protected class WorkerHandler extends Handler {

        WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WorkerArgs<ResultType, CookieType> args = (WorkerArgs<ResultType, CookieType>) msg.obj;
            int token = args.token;
            CookieType cookie = args.cookie;
            ResultType result = onAsyncWorkBegin(token, cookie);
            Message reply = args.handler.obtainMessage(token);
            args.result = result;
            reply.obj = args;
            reply.sendToTarget();
        }
    }

    public AsyncWorker() {
        super();
        synchronized (AsyncQueryHandler.class) {
            if (sLooper == null) {
                HandlerThread thread = new HandlerThread("AsyncWorker");
                thread.start();
                sLooper = thread.getLooper();
            }
        }
        mWorkerThreadHandler = createHandler(sLooper);
    }

    protected Handler createHandler(Looper looper) {
        return new WorkerHandler(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        WorkerArgs<ResultType, CookieType> args = (WorkerArgs<ResultType, CookieType>) msg.obj;
        onAsyncWorkFinished(args.token, args.cookie, args.result);
    }

    public void startAsyncWork(int token, CookieType cookie) {
        Message message = mWorkerThreadHandler.obtainMessage(token);
        WorkerArgs workerArgs = new WorkerArgs();
        workerArgs.handler = this;
        workerArgs.token = token;
        workerArgs.cookie = cookie;
        mWorkerThreadHandler.sendMessage(message);
    }

    /**
     * 在子线程中回调，用于处理耗时的任务，并返回处理的结果。
     *
     * @param token 用于标识任务的 ID。
     * @param cookie 用于传入任务所需的参数。
     * @return 任务处理的结果。
     */
    public abstract ResultType onAsyncWorkBegin(int token, CookieType cookie);

    /**
     * 在主线程中回调，更新 UI。
     *
     * @param token 用于标识任务的 ID。
     * @param cookie 用于传入任务所需的参数。
     * @param result 任务处理的结果。
     */
    public abstract void onAsyncWorkFinished(int token, CookieType cookie, ResultType result);


}
