package com.barakyesh.cluster.discovery.api;

import java.util.HashMap;

/**
 * Created by Barak Yeshoua.
 */
public interface CreateNodeBuilder {
    CreateNodeBuilder schema(String schema);
    CreateNodeBuilder host(String host);
    CreateNodeBuilder port(int port);
    CreateNodeBuilder properties(HashMap<String, String> properties);
    ClusterNode forName(String name) throws Exception;
    CreateNodeBuilder registerListener(ClusterChangeListener clusterChangeListener);
    CreateNodeBuilder registerStatusUpdater(NodeStatusUpdater nodeStatusUpdater);
}
