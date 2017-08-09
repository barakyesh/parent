package com.barakyesh.common.utils.async;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

/**
 * Created by Barak Yeshoua.
 */
public class ClosableExecutorService implements Closeable{

    private ExecutorService executorService;
    private List<FutureTask> futureTasks = new ArrayList<>();

    ClosableExecutorService(ExecutorService executorService){
        this.executorService = executorService;
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
