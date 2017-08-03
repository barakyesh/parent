package com.barakyesh.cluster.framework.api;

/**
 * Created by Barak Yeshoua.
 */
public interface ClusterEvent {

    int getClusterSize();

    ClusterEventType getType();
}
