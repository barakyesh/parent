package com.barakyesh.cluster.discovery.api;

/**
 * Created by Barak Yeshoua.
 */
public interface ClusterChangeListener {
    void handleEvent(ClusterEvent event);
    long getRunIntervalInMs();
}
