package com.barakyesh.cluster.framework.api;

/**
 * Created by Barak Yeshoua.
 */
public interface ClusterChangeListener {
    void handleEvent(ClusterEvent event);
    long getRunIntervalInMs();
}
