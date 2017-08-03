package com.barakyesh.cluster.discovery.framework.impl;

import com.barakyesh.cluster.discovery.framework.ClusterFrameworkFactory;
import com.barakyesh.cluster.discovery.framework.api.ClusterFramework;
import com.barakyesh.cluster.discovery.framework.api.CreateNodeBuilder;
import com.barakyesh.cluster.discovery.framework.api.NodeDetails;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;

/**
 * Created by Barak Yeshoua.
 */
public class ClusterFrameworkImpl implements ClusterFramework {
    private static final String CLUSTER_PATH = "/cluster";

    private final CuratorFramework client;
    private final String clusterPath;
    private ServiceDiscovery<NodeDetails> serviceDiscovery;

    public ClusterFrameworkImpl(ClusterFrameworkFactory.Builder builder) {
        this.clusterPath = CLUSTER_PATH +"/"+builder.getClusterName();
        client = CuratorFrameworkFactory.newClient(builder.getConnectString(), builder.getRetryPolicy());
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
