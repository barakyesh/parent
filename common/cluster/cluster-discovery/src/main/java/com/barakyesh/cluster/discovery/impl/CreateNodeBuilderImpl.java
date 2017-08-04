package com.barakyesh.cluster.discovery.impl;


import com.barakyesh.cluster.discovery.api.ClusterChangeListener;
import com.barakyesh.cluster.discovery.api.ClusterNode;
import com.barakyesh.cluster.discovery.api.CreateNodeBuilder;
import com.barakyesh.cluster.discovery.api.NodeStatusUpdater;
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
    private NodeStatusUpdater updater;
    private String context = "";

    public CreateNodeBuilderImpl(CuratorFramework client, String clusterPath) {
        this.client = client;
        this.clusterPath = clusterPath;
    }

    @Override
    public CreateNodeBuilder schema(String schema) {
        this.schema = schema;
        return this;
    }

    @Override
    public CreateNodeBuilder context(String context) {
        this.context = context;
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

    NodeStatusUpdater getUpdater() {
        return updater;
    }

    String getContext() {
        return context;
    }
}
