package com.barakyesh.common.utils.concurrent;

/**
 * Created by Barak Yeshoua.
 */
public interface IntervalRunnable{
    /**
     * Get the time to wait between two runs
     *
     * @return the run interval in ms
     */
    long getRunInterval();
}
