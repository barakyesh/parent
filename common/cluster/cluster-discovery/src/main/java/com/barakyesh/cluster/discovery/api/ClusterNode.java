package com.barakyesh.cluster.discovery.api;

import java.io.Closeable;

/**
 * Created by Barak Yeshoua.
 */
public interface ClusterNode extends Closeable{
    void start() throws Exception;
}