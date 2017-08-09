package com.barakyesh.cluster.framework.api.async;

import com.barakyesh.cluster.framework.api.ClusterEvent;
import com.barakyesh.common.utils.concurrent.IntervalRunnable;

/**
 * Created by Barak Yeshoua.
 */
public interface InstanceListener extends IntervalRunnable {
    void handleEvent(ClusterEvent event);
}
