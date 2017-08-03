package com.barakyesh.common.utils.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Barak Yeshoua.
 */
public class ThreadUtils {

    public static ThreadFactory threadFactory(String threadNamePrefix){
        return new ExecutorsThreadFactory(threadNamePrefix);
    }

    public static ExecutorService singleThreadExecutor(String threadNamePrefix) {
        return Executors.newSingleThreadExecutor(threadFactory(threadNamePrefix));
    }

    private static class ExecutorsThreadFactory implements ThreadFactory {
        private String threadNamePrefix;

        ExecutorsThreadFactory(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        public Thread newThread(Runnable r) {
            final Thread newThread = Executors.defaultThreadFactory().newThread(r);
            newThread.setName(threadNamePrefix + "-" + newThread.getName());
            return newThread;
        }
    }
}
