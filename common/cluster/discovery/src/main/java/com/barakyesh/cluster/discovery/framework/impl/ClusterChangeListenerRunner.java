package com.barakyesh.cluster.discovery.framework.impl;

import com.barakyesh.cluster.discovery.framework.api.ClusterChangeListener;
import com.barakyesh.cluster.discovery.framework.api.ListenerRunner;
import com.barakyesh.cluster.discovery.framework.api.NodeDetails;
import com.barakyesh.cluster.discovery.framework.status.NodeStatus;
import com.barakyesh.cluster.discovery.framework.utils.ThreadExecutorsService;
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

/**
 * Created by Barak Yeshoua.
 */
public class ClusterChangeListenerRunner implements ListenerRunner,Runnable,Closeable{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ServiceDiscovery<NodeDetails> serviceDiscovery;
    private ServiceInstance<NodeDetails> thisInstance;
    private final ClusterChangeListener listener;
    private long checkIntervalInMs;
    private volatile boolean isRunning;
    private Set<ServiceInstance<NodeDetails>> serviceInstances;

    ClusterChangeListenerRunner(ServiceDiscovery<NodeDetails> serviceDiscovery,ServiceInstance<NodeDetails> thisInstance, ClusterChangeListener listener,long checkIntervalInMs) {
        this.serviceDiscovery = serviceDiscovery;
        this.thisInstance = thisInstance;
        this.listener = listener;
        this.checkIntervalInMs = checkIntervalInMs;
    }

    @Override
    public void run() {
        try {
            serviceInstances = listInstances();
            isRunning = true;
            while (isRunning) {
                updateNodeStatus();
                serviceInstances = getServiceInstances();
                Thread.sleep(checkIntervalInMs);
            }
        } catch (InterruptedException e) {
            log.warn("Thread {} got interrupted",Thread.currentThread().getName(),e);
        } catch (Exception e) {
            log.error("Error on {} while listening for cluster changes",Thread.currentThread().getName(),e);
        }
        log.info("{} stop running",Thread.currentThread().getName());
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
                                return status.ordinal() > NodeStatus.RED.ordinal();
                            })
                            .collect(Collectors.toSet())
            );
        }
        return instances;
    }

    private void updateNodeStatus() throws Exception {
        NodeStatus status = listener.updateStatus();
        if(thisInstance.getPayload().getStatus() != status) {
            log.info("Changing {} status into {}", thisInstance.getName(), status);
            thisInstance.getPayload().setStatus(status);
            serviceDiscovery.updateService(thisInstance);
        }
    }

    private Set<ServiceInstance<NodeDetails>> getServiceInstances() throws Exception {
        Set<ServiceInstance<NodeDetails>> nodeDetailsSet = listInstances();
        if (nodeDetailsSet.size() != serviceInstances.size()) {
            listener.clusterSizeChanged(nodeDetailsSet.size());
        }
        serviceInstances.stream().filter(nodeDetails -> !nodeDetailsSet.contains(nodeDetails)).forEach(nodeDetails -> listener.nodeRemoved());
        nodeDetailsSet.stream().filter(nodeDetails -> !serviceInstances.contains(nodeDetails)).forEach(nodeDetails -> listener.nodeAdded());
        return nodeDetailsSet;
    }

    @Override
    public void close() throws IOException {
        isRunning = false;
        ThreadExecutorsService.close(getClass().getName()+"-"+thisInstance.getName());
    }

    @Override
    public void start() {
        ThreadExecutorsService.runAsync(this,getClass().getName()+"-"+thisInstance.getName());
    }
}
