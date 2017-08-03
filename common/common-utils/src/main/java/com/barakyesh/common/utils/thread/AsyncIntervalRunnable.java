package com.barakyesh.common.utils.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Barak Yeshoua.
 */
public abstract class AsyncIntervalRunnable implements Runnable{
    private final Logger log = LoggerFactory.getLogger(getClass());
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
            log.error("Error while listening for cluster changes",e);
        }
        log.info("ClusterChangeListenerRunner stop running");
    }

    protected abstract long getSleepInterval();

    protected abstract void doAction();


}
