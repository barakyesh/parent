package com.barakyesh.cluster.framework.impl;

import com.barakyesh.cluster.framework.ClusterFrameworkFactory;
import com.barakyesh.cluster.framework.api.ClusterFramework;
import com.barakyesh.cluster.framework.api.CreateNodeBuilder;
import com.barakyesh.common.utils.CloseableUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;

/**
 * Created by Barak Yeshoua.
 */
public class ClusterFrameworkImpl implements ClusterFramework {
    private static final String CLUSTER_PATH = "/cluster";

    private final CuratorFramework client;
    private final String clusterPath;

    public ClusterFrameworkImpl(ClusterFrameworkFactory.Builder builder) {
        this.clusterPath = CLUSTER_PATH +"/"+builder.getClusterName();
        client = CuratorFrameworkFactory.newClient(builder.getConnectString(), new ExponentialBackoffRetry(builder.getBaseSleepTimeMs(),builder.getMaxRetries()));
    }

    public void start() throws Exception {
        client.start();
        client.createContainers(CLUSTER_PATH);
    }

    @Override
    public CreateNodeBuilder createNode() {
        return new CreateNodeBuilderImpl(client, clusterPath);
    }


    @Override
    public void close() throws IOException {
        CloseableUtils.closeQuietly(client);
    }
}
