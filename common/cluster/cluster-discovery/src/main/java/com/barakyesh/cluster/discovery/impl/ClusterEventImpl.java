package com.barakyesh.cluster.discovery.impl;

import com.barakyesh.cluster.discovery.api.ClusterEvent;
import com.barakyesh.cluster.discovery.api.ClusterEventType;

/**
 * Created by Barak Yeshoua.
 */
public class ClusterEventImpl implements ClusterEvent{
    private int size;
    private ClusterEventType type;

    public ClusterEventImpl(int size, ClusterEventType type) {
        this.size = size+1;//add self to cluster size
        this.type = type;
    }

    @Override
    public int getClusterSize() {
        return size;
    }

    @Override
    public ClusterEventType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ClusterEventImpl{" +
                "size=" + size +
                ", type=" + type +
                '}';
    }
}
