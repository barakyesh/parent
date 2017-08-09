package com.barakyesh.cluster.framework.api;

import com.barakyesh.cluster.framework.api.async.InstanceListener;
import com.barakyesh.cluster.framework.api.async.LeaderAction;
import com.barakyesh.cluster.framework.api.async.NodeStatusUpdater;

import java.util.HashMap;

/**
 * Created by Barak Yeshoua.
 */
public interface CreateNodeBuilder {
    CreateNodeBuilder schema(String schema);
    CreateNodeBuilder context(String context);
    CreateNodeBuilder host(String host);
    CreateNodeBuilder port(int port);
    CreateNodeBuilder properties(HashMap<String, String> properties);
    ClusterNode forName(String name) throws Exception;
    CreateNodeBuilder registerInstanceListener(InstanceListener instanceListener);
    CreateNodeBuilder registerNodeStatusUpdater(NodeStatusUpdater nodeStatusUpdater);
    CreateNodeBuilder registerLeaderAction(LeaderAction leaderAction);
}
