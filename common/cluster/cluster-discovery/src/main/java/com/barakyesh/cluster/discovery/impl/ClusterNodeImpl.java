package com.barakyesh.cluster.discovery.impl;

import com.barakyesh.cluster.discovery.api.*;
import com.barakyesh.common.utils.CloseableUtils;
import com.google.common.base.Preconditions;
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
    private final ClusterChangeListener listener;
    private final NodeStatusUpdater updater;
    private ClusterChangeListenerRunner listenerRunner;
    private NodeStatusUpdaterRunner updaterRunner;


    ClusterNodeImpl(CreateNodeBuilderImpl createNodeBuilder) throws Exception {
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

        this.listener = createNodeBuilder.getListener();
        this.updater = createNodeBuilder.getUpdater();

        JsonInstanceSerializer<NodeDetails> serializer = new JsonInstanceSerializer<>(NodeDetails.class);

        serviceDiscovery = ServiceDiscoveryBuilder.builder(NodeDetails.class)
                .client(createNodeBuilder.getClient())
                .basePath(createNodeBuilder.getClusterPath())
                .serializer(serializer)
                .thisInstance(thisInstance)
                .build();
    }

    @Override
    public void start() throws Exception
    {
        log.info("Starting service discovery instance");
        serviceDiscovery.start();
        if(listener!=null) {
            listenerRunner = new ClusterChangeListenerRunner(serviceDiscovery,thisInstance,listener);
            log.info("Starting listener runner instance");
            listenerRunner.start();
        }
        if(updater!=null) {
            updaterRunner = new NodeStatusUpdaterRunner(serviceDiscovery,thisInstance,updater);
            log.info("Starting updater runner instance");
            updaterRunner.start();
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
        CloseableUtils.closeQuietly(listenerRunner);
        CloseableUtils.closeQuietly(updaterRunner);
        CloseableUtils.closeQuietly(serviceDiscovery);
    }
}
