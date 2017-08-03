package com.barakyesh.cluster.framework.api;

import java.io.Closeable;

/**
 * Created by Barak Yeshoua.
 */
public interface ClusterFramework extends Closeable {
    void start() throws Exception;
    CreateNodeBuilder createNode();
}
