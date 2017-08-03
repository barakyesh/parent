package com.barakyesh.cluster.discovery.api;

/**
 * Created by Barak Yeshoua.
 */
public interface ClusterEvent {
    int getClusterSize();
    ClusterEventType getType();
}
