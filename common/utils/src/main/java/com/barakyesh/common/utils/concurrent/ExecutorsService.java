package com.barakyesh.common.utils.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Barak Yeshoua.
 */
public class ExecutorsService {

    private static final Logger log = LoggerFactory.getLogger(ExecutorsService.class);

    public static ClosableExecutorService singleThreadExecutor(String threadNamePrefix) {
        return new ClosableExecutorService(Executors.newSingleThreadExecutor(newExecutorsThreadFactory(threadNamePrefix)));
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

    public static ThreadFactory newExecutorsThreadFactory(String processName)
    {
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> {
            log.error("Unexpected exception in thread: " + t, e);
            throw new RuntimeException(e);
        };
        return new ThreadFactoryBuilder()
                .setNameFormat(processName + "-%d")
                .setDaemon(true)
                .setUncaughtExceptionHandler(uncaughtExceptionHandler)
                .build();
    }

}
