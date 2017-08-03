package com.barakyesh.common.utils.thread;

import com.barakyesh.common.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Barak Yeshoua.
 */
public abstract class AsyncIntervalRunnable implements Runnable,Closeable,Runner{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private ClosableExecutorService closableExecutorService;
    @Override
    public void run() {
        try {
            while (true) {
                doAction();
                Thread.sleep(getSleepInterval());
            }
        } catch (InterruptedException e) {
            log.warn("Thread got interrupted");
        } catch (Exception e) {
            log.error("Error while running",e);
        }
        log.info("Thread stop running");
    }

    protected abstract long getSleepInterval();

    protected abstract void doAction() throws Exception;

    @Override
    public void close() throws IOException {
        CloseableUtils.closeQuietly(closableExecutorService);
    }

    protected void start(String threadNamePrefix){
        closableExecutorService = new ClosableExecutorService(ThreadUtils.singleThreadExecutor(threadNamePrefix));
        closableExecutorService.execute(this);
    }
}
