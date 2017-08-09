package com.barakyesh.cluster.framework.api.async;

import com.barakyesh.cluster.framework.api.NodeStatus;
import com.barakyesh.common.utils.concurrent.IntervalRunnable;

/**
 * Created by Barak Yeshoua.
 */
public interface NodeStatusUpdater extends IntervalRunnable{
    NodeStatus updateStatus();
}
