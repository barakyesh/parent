package com.barakyesh.cluster.discovery.api;

/**
 * Created by Barak Yeshoua.
 */
public interface NodeStatusUpdater {
    NodeStatus updateStatus();
    long getRunIntervalInMs();
}
