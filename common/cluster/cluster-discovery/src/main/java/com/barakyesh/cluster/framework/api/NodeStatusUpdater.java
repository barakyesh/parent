package com.barakyesh.cluster.framework.api;

/**
 * Created by Barak Yeshoua.
 */
public interface NodeStatusUpdater {
    NodeStatus updateStatus();
    long getRunIntervalInMs();
}
