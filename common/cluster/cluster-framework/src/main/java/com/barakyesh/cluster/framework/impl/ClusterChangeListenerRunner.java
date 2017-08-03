package com.barakyesh.cluster.framework.impl;


import com.barakyesh.cluster.framework.api.ClusterChangeListener;
import com.barakyesh.cluster.framework.api.NodeDetails;
import com.barakyesh.cluster.framework.api.NodeStatus;
import com.barakyesh.cluster.framework.api.Runner;
import com.barakyesh.common.utils.CloseableUtils;
import com.barakyesh.common.utils.thread.ClosableExecutorService;
import com.barakyesh.common.utils.thread.ThreadUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.barakyesh.cluster.framework.api.ClusterEventType.*;

/**
 * Created by Barak Yeshoua.
 */
public class ClusterChangeListenerRunner implements Runner,Runnable,Closeable{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ServiceDiscovery<NodeDetails> serviceDiscovery;
    private ServiceInstance<NodeDetails> thisInstance;
    private final ClusterChangeListener listener;
    private Set<ServiceInstance<NodeDetails>> serviceInstances;
    private ClosableExecutorService closableExecutorService;

    ClusterChangeListenerRunner(ServiceDiscovery<NodeDetails> serviceDiscovery, ServiceInstance<NodeDetails> thisInstance, ClusterChangeListener listener) {
        this.serviceDiscovery = serviceDiscovery;
        this.thisInstance = thisInstance;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            serviceInstances = listInstances();
            while (true) {
                serviceInstances = checkServiceInstances();
                Thread.sleep(listener.getRunIntervalInMs());
            }
        } catch (InterruptedException e) {
            log.warn("Thread got interrupted");
        } catch (Exception e) {
            log.error("Error while listening for cluster changes",e);
        }
        log.info("ClusterChangeListenerRunner stop running");
    }

    private Set<ServiceInstance<NodeDetails>> listInstances() throws Exception
    {
        Set<ServiceInstance<NodeDetails>> instances = new HashSet<>();
        Collection<String> serviceNames = serviceDiscovery.queryForNames();
        for ( String serviceName : serviceNames )
        {
            instances.addAll(
                    serviceDiscovery.queryForInstances(serviceName)
                            .stream()
                            .filter(nodeDetailsServiceInstance -> {
                                NodeStatus status = nodeDetailsServiceInstance.getPayload().getStatus();
                                return status.ordinal() > NodeStatus.RED.ordinal() && !nodeDetailsServiceInstance.equals(thisInstance);
                            })
                            .collect(Collectors.toSet())
            );
        }
        return instances;
    }


    private Set<ServiceInstance<NodeDetails>> checkServiceInstances() throws Exception {
        Set<ServiceInstance<NodeDetails>> nodeDetailsSet = listInstances();
        if (nodeDetailsSet.size() != serviceInstances.size()) {
            listener.handleEvent(new ClusterEventImpl(nodeDetailsSet.size(),CLUSTER_SIZE_CHANGED));
        }
        serviceInstances.stream().filter(nodeDetails -> !nodeDetailsSet.contains(nodeDetails)).forEach(nodeDetails -> listener.handleEvent(new ClusterEventImpl(nodeDetailsSet.size(),NODE_REMOVED)));
        nodeDetailsSet.stream().filter(nodeDetails -> !serviceInstances.contains(nodeDetails)).forEach(nodeDetails -> listener.handleEvent(new ClusterEventImpl(nodeDetailsSet.size(),NODE_ADDED)));
        return nodeDetailsSet;
    }

    @Override
    public void close() throws IOException {
        CloseableUtils.closeQuietly(closableExecutorService);
    }

    @Override
    public void start() {
        String threadNamePrefix = getClass().getSimpleName()+"-"+thisInstance.getName()+"-"+thisInstance.getId();
        closableExecutorService = new ClosableExecutorService(ThreadUtils.singleThreadExecutor(threadNamePrefix));
        closableExecutorService.execute(this);
    }
}
