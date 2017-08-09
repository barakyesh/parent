package com.barakyesh.common.utils.concurrent;

import com.barakyesh.common.utils.CloseableUtils;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Barak Yeshoua.
 */
public abstract class AsyncIntervalRunnable implements Runnable,IntervalRunnable,Closeable{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private ClosableExecutorService closableExecutorService;
    private boolean isRunning = true;
    private final long sleepInterval;

    protected AsyncIntervalRunnable(long sleepInterval) {
        Preconditions.checkArgument(sleepInterval > 0,"sleepInterval must be greater than zero, current value = %s",sleepInterval);
        this.sleepInterval = sleepInterval;
    }

    @Override
    public void run() {
        try {
            while(isRunning) {
                doAction();
                Thread.sleep(getRunInterval());
            }
        } catch (InterruptedException e) {
            log.warn("Thread got interrupted");
        } catch (Exception e) {
            log.error("Error while running",e);
        }
        log.info("Thread stop running");
    }

    public long getRunInterval(){
        return sleepInterval;
    }

    protected abstract void doAction() throws Exception;

    @Override
    public void close() throws IOException {
        isRunning = false;
        CloseableUtils.closeQuietly(closableExecutorService);
    }

    protected void start(String threadNamePrefix){
        closableExecutorService = ClosableExecutorService.singleThreadExecutor(threadNamePrefix);
        closableExecutorService.execute(this);
    }
}
