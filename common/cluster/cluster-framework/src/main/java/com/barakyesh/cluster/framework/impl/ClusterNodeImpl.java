package com.barakyesh.cluster.framework.impl;

import com.barakyesh.cluster.framework.api.ClusterNode;
import com.barakyesh.cluster.framework.api.NodeDetails;
import com.barakyesh.cluster.framework.api.NodeStatus;
import com.barakyesh.cluster.framework.api.async.InstanceListener;
import com.barakyesh.cluster.framework.api.async.LeaderAction;
import com.barakyesh.cluster.framework.api.async.NodeStatusUpdater;
import com.barakyesh.cluster.framework.impl.async.ClusterInstanceListenerRunner;
import com.barakyesh.cluster.framework.impl.async.ClusterLeaderActionRunner;
import com.barakyesh.cluster.framework.impl.async.ClusterNodeStatusUpdaterRunner;
import com.barakyesh.common.utils.CloseableUtils;
import com.google.common.base.Preconditions;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;


/**
 * Created by Barak Yeshoua.
 */
public class ClusterNodeImpl implements ClusterNode {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ServiceDiscovery<NodeDetails> serviceDiscovery;
    private final ServiceInstance<NodeDetails> thisInstance;
    private final InstanceListener instanceListener;
    private final NodeStatusUpdater nodeStatusUpdater;
    private final LeaderAction leaderAction;
    private final CuratorFramework client;
    private final String clusterPath;
    private ClusterLeaderActionRunner clusterLeaderActionRunner;
    private ClusterInstanceListenerRunner clusterInstanceListenerRunner;
    private ClusterNodeStatusUpdaterRunner clusterNodeStatusUpdaterRunner;


    ClusterNodeImpl(CreateNodeBuilderImpl createNodeBuilder) throws Exception {
        this.instanceListener = createNodeBuilder.getInstanceListener();
        this.nodeStatusUpdater = createNodeBuilder.getNodeStatusUpdater();
        this.leaderAction = createNodeBuilder.getLeaderAction();
        this.client = createNodeBuilder.getClient();
        this.clusterPath = createNodeBuilder.getClusterPath();

        UriSpec uriSpec = new UriSpec(createNodeBuilder.getSchema() + "://{address}:{port}"+createNodeBuilder.getContext()+"/"+createNodeBuilder.getName());
        NodeDetails payload = new NodeDetails();
        payload.setNodeProperties(createNodeBuilder.getProperties());
        thisInstance = ServiceInstance.<NodeDetails>builder()
                .name(Preconditions.checkNotNull(createNodeBuilder.getName(),"name can not be null"))
                .address(Preconditions.checkNotNull(createNodeBuilder.getHost(),"host can not be null"))
                .payload(payload)
                .port(Preconditions.checkNotNull(createNodeBuilder.getPort(),"port can not be null"))
                .uriSpec(uriSpec)
                .build();

        JsonInstanceSerializer<NodeDetails> serializer = new JsonInstanceSerializer<>(NodeDetails.class);

        serviceDiscovery = ServiceDiscoveryBuilder.builder(NodeDetails.class)
                .client(client)
                .basePath(clusterPath)
                .serializer(serializer)
                .thisInstance(thisInstance)
                .build();
    }

    @Override
    public void start() throws Exception
    {
        log.info("Starting service discovery instance");
        serviceDiscovery.start();
        if(instanceListener !=null) {
            clusterInstanceListenerRunner = new ClusterInstanceListenerRunner(serviceDiscovery,thisInstance, instanceListener);
            log.info("Starting clusterInstanceListenerRunner instance");
            clusterInstanceListenerRunner.start();
        }
        if(nodeStatusUpdater !=null) {
            clusterNodeStatusUpdaterRunner = new ClusterNodeStatusUpdaterRunner(serviceDiscovery,thisInstance, nodeStatusUpdater);
            log.info("Starting clusterNodeStatusUpdaterRunner instance");
            clusterNodeStatusUpdaterRunner.start();
        }
        if(leaderAction !=null) {
            clusterLeaderActionRunner = new ClusterLeaderActionRunner(leaderAction,client, clusterPath, thisInstance.getName() + "-" + thisInstance.getId());
            log.info("Starting clusterLeaderActionRunner instance");
            clusterLeaderActionRunner.start();
        }
    }

    @Override
    public String getName() {
        return thisInstance.getName();
    }

    @Override
    public String getId() {
        return thisInstance.getId();
    }

    @Override
    public String getHost() {
        return thisInstance.getAddress();
    }

    @Override
    public Integer getPort() {
        return thisInstance.getPort();
    }

    @Override
    public Map<String, String> getServicePropeties() {
        return thisInstance.getPayload().getNodeProperties();
    }

    @Override
    public long getRegistrationTimeUTC() {
        return thisInstance.getRegistrationTimeUTC();
    }

    @Override
    public NodeStatus getServiceStatus() {
        return thisInstance.getPayload().getStatus();
    }

    @Override
    public String getServiceUrl() {
        return thisInstance.buildUriSpec();
    }

    @Override
    public void close() throws IOException
    {
        CloseableUtils.closeQuietly(clusterInstanceListenerRunner);
        CloseableUtils.closeQuietly(clusterNodeStatusUpdaterRunner);
        CloseableUtils.closeQuietly(clusterLeaderActionRunner);
        CloseableUtils.closeQuietly(serviceDiscovery);
    }
}
