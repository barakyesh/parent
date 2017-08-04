package com.barakyesh.cluster.discovery.api;

import com.barakyesh.common.utils.thread.IntervalRunnable;

/**
 * Created by Barak Yeshoua.
 */
public interface NodeStatusUpdater extends IntervalRunnable{
    NodeStatus updateStatus();
}
