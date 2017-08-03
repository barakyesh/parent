package com.barakyesh.cluster.framework.api;

import com.barakyesh.cluster.framework.impl.CreateNodeBuilderImpl;

import java.util.HashMap;

/**
 * Created by Barak Yeshoua.
 */
public interface CreateNodeBuilder {
    CreateNodeBuilder schema(String schema);
    CreateNodeBuilder host(String host);
    CreateNodeBuilder port(int port);
    CreateNodeBuilder properties(HashMap<String, String> properties);
    CreateNodeBuilderImpl checkIntervalInMs(long checkIntervalInMs);
    ClusterNode forName(String name) throws Exception;
    CreateNodeBuilder registerListener(ClusterChangeListener clusterChangeListener);
    CreateNodeBuilder registerStatusUpdater(NodeStatusUpdater nodeStatusUpdater);
}
