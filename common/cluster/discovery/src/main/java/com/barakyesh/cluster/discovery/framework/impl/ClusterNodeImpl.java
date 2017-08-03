package com.barakyesh.cluster.discovery.framework.impl;

import com.barakyesh.cluster.discovery.framework.api.ClusterChangeListener;
import com.barakyesh.cluster.discovery.framework.api.ClusterNode;
import com.barakyesh.cluster.discovery.framework.api.NodeDetails;
import com.google.common.base.Preconditions;
import org.apache.curator.utils.CloseableUtils;
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
    private ClusterChangeListenerRunner listenerRunner;


    ClusterNodeImpl(CreateNodeBuilderImpl createNodeBuilder) throws Exception {
        UriSpec uriSpec = new UriSpec("{scheme}://{host}:{port}");
        thisInstance = ServiceInstance.<NodeDetails>builder()
                .name(Preconditions.checkNotNull(createNodeBuilder.getName(),"name can not be null"))
                .address(Preconditions.checkNotNull(createNodeBuilder.getHost(),"host can not be null"))
                .payload(new NodeDetails(createNodeBuilder.getProperties()))
                .port(Preconditions.checkNotNull(createNodeBuilder.getPort(),"port can not be null")) // in a real application, you'd use a common port
                .uriSpec(uriSpec)
                .build();

        this.listener = createNodeBuilder.getListener();

        // if you mark your payload class with @JsonRootName the provided JsonInstanceSerializer will work
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
        serviceDiscovery.start();
        if(listener!=null) {
            listenerRunner = new ClusterChangeListenerRunner(serviceDiscovery, listener, clusterPath);
            listenerRunner.start();
        }
    }

    @Override
    public void close() throws IOException
    {
        CloseableUtils.closeQuietly(serviceDiscovery);
        CloseableUtils.closeQuietly(listenerRunner);
    }
}
