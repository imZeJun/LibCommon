package com.lib.common.worker;


import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class PriorityThreadFactory implements ThreadFactory {

    private static final String BASE_NAME = "PriorityThreadFactory";

    private final int priority;
    private final AtomicInteger number = new AtomicInteger();
    private final String name;

    public PriorityThreadFactory(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {

        return new Thread(r, BASE_NAME + "-" + name + '-' + number.getAndIncrement()) {
            @Override
            public void run() {
                Process.setThreadPriority(priority);
                super.run();
            }
        };
    }
}
