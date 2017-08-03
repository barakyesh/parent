package com.barakyesh.cluster.discovery.framework.impl;

import com.barakyesh.cluster.discovery.framework.api.ClusterChangeListener;
import com.barakyesh.cluster.discovery.framework.api.ClusterNode;
import com.barakyesh.cluster.discovery.framework.api.NodeDetails;
import com.barakyesh.cluster.discovery.framework.utils.CloseableUtils;
import com.google.common.base.Preconditions;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * Created by Barak Yeshoua.
 */
public class ClusterNodeImpl implements ClusterNode{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ServiceDiscovery<NodeDetails> serviceDiscovery;
    private final ServiceInstance<NodeDetails> thisInstance;
    private final ClusterChangeListener listener;
    private final String clusterPath;
    private final long checkIntervalInMs;
    private ClusterChangeListenerRunner listenerRunner;


    ClusterNodeImpl(CreateNodeBuilderImpl createNodeBuilder) throws Exception {
        UriSpec uriSpec = new UriSpec(createNodeBuilder.getSchema() + "://{host}:{port}");
        NodeDetails payload = new NodeDetails();
        payload.setNodeProperties(createNodeBuilder.getProperties());
        this.checkIntervalInMs = createNodeBuilder.getCheckIntervalInMs();
        thisInstance = ServiceInstance.<NodeDetails>builder()
                .name(Preconditions.checkNotNull(createNodeBuilder.getName(),"name can not be null"))
                .address(Preconditions.checkNotNull(createNodeBuilder.getHost(),"host can not be null"))
                .payload(payload)
                .port(Preconditions.checkNotNull(createNodeBuilder.getPort(),"port can not be null"))
                .uriSpec(uriSpec)
                .build();

        this.listener = createNodeBuilder.getListener();

        JsonInstanceSerializer<NodeDetails> serializer = new JsonInstanceSerializer<>(NodeDetails.class);

        this.clusterPath = createNodeBuilder.getClusterPath();
        serviceDiscovery = ServiceDiscoveryBuilder.builder(NodeDetails.class)
                .client(createNodeBuilder.getClient())
                .basePath(clusterPath)
                .serializer(serializer)
                .thisInstance(thisInstance)
                .build();
    }

    public ServiceInstance<NodeDetails> getThisInstance()
    {
        return thisInstance;
    }

    @Override
    public void start() throws Exception
    {
        log.info("Starting service discovery instance");
        serviceDiscovery.start();
        if(listener!=null) {
            listenerRunner = new ClusterChangeListenerRunner(serviceDiscovery,thisInstance, listener,checkIntervalInMs);
            log.info("Starting listener runner instance");
            listenerRunner.start();
        }
    }

    @Override
    public void close() throws IOException
    {
        CloseableUtils.closeQuietly(listenerRunner);
        CloseableUtils.closeQuietly(serviceDiscovery);
    }
}
