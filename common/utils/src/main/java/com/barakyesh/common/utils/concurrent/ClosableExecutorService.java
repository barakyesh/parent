package com.barakyesh.common.utils.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Barak Yeshoua.
 */
public class ClosableExecutorService implements Closeable{
    private static final Logger log = LoggerFactory.getLogger(ClosableExecutorService.class);

    private ExecutorService executorService;
    private List<FutureTask> futureTasks = new ArrayList<>();

    ClosableExecutorService(ExecutorService executorService){
        this.executorService = executorService;
    }

    public static ClosableExecutorService singleThreadExecutor(String threadNamePrefix) {
        return new ClosableExecutorService(Executors.newSingleThreadExecutor(newExecutorsThreadFactory(threadNamePrefix)));
    }

    private static ThreadFactory newExecutorsThreadFactory(String threadNamePrefix)
    {
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> {
            log.error("Unexpected exception in thread: " + t, e);
            throw new RuntimeException(e);
        };
        return new ThreadFactoryBuilder()
                .setNameFormat(threadNamePrefix + "-%d")
                .setDaemon(true)
                .setUncaughtExceptionHandler(uncaughtExceptionHandler)
                .build();
    }


    @Override
    public void close() throws IOException {
        futureTasks.stream().
                filter(futureTask -> !(futureTask.isCancelled() || futureTask.isDone())).
                forEach(futureTask -> futureTask.cancel(true));
        executorService.shutdown();
    }


    public void execute(Runnable runnable) {
        FutureTask<Void> futureTask = new FutureTask<>(runnable, null);
        futureTasks.add(futureTask);
        executorService.execute(futureTask);
    }
}
