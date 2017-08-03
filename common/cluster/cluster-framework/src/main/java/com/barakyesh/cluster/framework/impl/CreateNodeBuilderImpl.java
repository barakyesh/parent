package com.barakyesh.cluster.framework.impl;


import com.barakyesh.cluster.framework.api.ClusterChangeListener;
import com.barakyesh.cluster.framework.api.ClusterNode;
import com.barakyesh.cluster.framework.api.CreateNodeBuilder;
import com.barakyesh.cluster.framework.api.NodeStatusUpdater;
import org.apache.curator.framework.CuratorFramework;

import java.util.HashMap;

/**
 * Created by Barak Yeshoua.
 */
public class CreateNodeBuilderImpl implements CreateNodeBuilder {
    private final CuratorFramework client;
    private final String clusterPath;
    private String schema = "http";
    private String host;
    private int port;
    private HashMap<String, String> properties = new HashMap<>();
    private String name;
    private ClusterChangeListener listener;
    private long checkIntervalInMs = 10000;
    private NodeStatusUpdater updater;

    CreateNodeBuilderImpl(CuratorFramework client, String clusterPath) {
        this.client = client;
        this.clusterPath = clusterPath;
    }

    @Override
    public CreateNodeBuilder schema(String schema) {
        this.schema = schema;
        return this;
    }

    @Override
    public CreateNodeBuilder host(String host) {
        this.host = host;
        return this;
    }

    @Override
    public CreateNodeBuilder port(int port) {
        this.port = port;
        return this;
    }

    @Override
    public CreateNodeBuilder properties(HashMap<String, String> properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public ClusterNode forName(String name) throws Exception {
        this.name = name;
        return new ClusterNodeImpl(this);
    }

    @Override
    public CreateNodeBuilder registerListener(ClusterChangeListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public CreateNodeBuilder registerStatusUpdater(NodeStatusUpdater updater) {
        this.updater = updater;
        return this;
    }

    public CreateNodeBuilderImpl checkIntervalInMs(long checkIntervalInMs) {
        this.checkIntervalInMs = checkIntervalInMs;
        return this;
    }

    String getSchema() {
        return schema;
    }

    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }

    HashMap<String, String> getProperties() {
        return properties;
    }

    String getName() {
        return name;
    }

    CuratorFramework getClient() {
        return client;
    }

    String getClusterPath() {
        return clusterPath;
    }

    ClusterChangeListener getListener() {
        return listener;
    }

    long getCheckIntervalInMs() {
        return checkIntervalInMs;
    }

    public NodeStatusUpdater getUpdater() {
        return updater;
    }
}
