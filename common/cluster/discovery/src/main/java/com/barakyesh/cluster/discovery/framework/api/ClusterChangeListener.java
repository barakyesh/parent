package com.barakyesh.cluster.discovery.framework.api;

/**
 * Created by Barak Yeshoua.
 */
public interface ClusterChangeListener {
    void nodeAdded();
    void nodeRemoved();
    void clusterSizeChanged(int newClusterSize);
}
