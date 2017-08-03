package com.barakyesh.cluster.discovery.framework.api;

import java.io.Closeable;

/**
 * Created by Barak Yeshoua.
 */
public interface ClusterNode extends Closeable{
    void start() throws Exception;
}
