package com.barakyesh.cluster.framework.api;

import com.barakyesh.cluster.discovery.api.CreateNodeBuilder;

import java.io.Closeable;

/**
 * Created by Barak Yeshoua.
 */
public interface ClusterFramework extends Closeable {
    void start() throws Exception;
    CreateNodeBuilder createNode();
}
