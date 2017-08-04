package com.barakyesh.common.utils.thread;

/**
 * Created by Barak Yeshoua.
 */
public interface IntervalRunnable {
    default long getRunIntervalInMs(){
        return 10000;
    }
}
