package com.barakyesh.cluster.framework.api.async;

import com.barakyesh.common.utils.async.IntervalRunnable;

/**
 * Created by Barak Yeshoua.
 */
public interface LeaderAction extends IntervalRunnable{
    void doAction();
}
