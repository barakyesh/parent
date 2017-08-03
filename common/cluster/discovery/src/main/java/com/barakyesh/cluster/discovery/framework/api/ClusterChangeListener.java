package com.barakyesh.cluster.discovery.framework.api;

import com.barakyesh.cluster.discovery.framework.status.NodeStatus;

/**
 * Created by Barak Yeshoua.
 */
public interface ClusterChangeListener {
    void nodeAdded();
    void nodeRemoved();
    void clusterSizeChanged(int newClusterSize);
    NodeStatus updateStatus();
}
