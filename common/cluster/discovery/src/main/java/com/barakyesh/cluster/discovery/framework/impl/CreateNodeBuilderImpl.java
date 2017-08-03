package com.barakyesh.cluster.discovery.framework.impl;

import com.barakyesh.cluster.discovery.framework.api.ClusterChangeListener;
import com.barakyesh.cluster.discovery.framework.api.ClusterNode;
import com.barakyesh.cluster.discovery.framework.api.CreateNodeBuilder;
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
}
