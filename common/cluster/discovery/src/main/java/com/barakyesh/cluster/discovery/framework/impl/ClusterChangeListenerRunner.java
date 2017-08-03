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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Barak Yeshoua.
 */
public class ClusterChangeListenerRunner implements ListenerRunner,Runnable,Closeable{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ServiceDiscovery<NodeDetails> serviceDiscovery;
    private final ClusterChangeListener listener;
    private final String clusterPath;
    private volatile boolean isRunning;
    private Set<ServiceInstance<NodeDetails>> serviceInstances;

    ClusterChangeListenerRunner(ServiceDiscovery<NodeDetails> serviceDiscovery, ClusterChangeListener listener,String clusterPath) {
        this.serviceDiscovery = serviceDiscovery;
        this.listener = listener;
        this.clusterPath = clusterPath;
    }

    @Override
    public void run() {
        try {
            serviceInstances = serviceDiscovery.queryForInstances(clusterPath).stream().filter(nodeDetailsServiceInstance -> {
                NodeStatus status = nodeDetailsServiceInstance.getPayload().getStatus();
                return status.ordinal() > NodeStatus.RED.ordinal();
            }).collect(Collectors.toSet());
            isRunning = true;
            while (isRunning) {
                Set<ServiceInstance<NodeDetails>> nodeDetailsSet = serviceDiscovery.queryForInstances(clusterPath).stream()
                        .filter(nodeDetailsServiceInstance -> {
                            NodeStatus status = nodeDetailsServiceInstance.getPayload().getStatus();
                            return status.ordinal() > NodeStatus.RED.ordinal();
                        }).collect(Collectors.toSet());
                if (nodeDetailsSet.size() != serviceInstances.size()) {
                    listener.clusterSizeChanged(nodeDetailsSet.size());
                }
                serviceInstances.stream().filter(nodeDetails -> !nodeDetailsSet.contains(nodeDetails)).forEach(nodeDetails -> listener.nodeRemoved());
                nodeDetailsSet.stream().filter(nodeDetails -> !serviceInstances.contains(nodeDetails)).forEach(nodeDetails -> listener.nodeAdded());
                serviceInstances = nodeDetailsSet;
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            log.warn("Thread {} got interrupted",Thread.currentThread().getName(),e);
        } catch (Exception e) {
            log.error("Error on {} while listening for cluster changes",Thread.currentThread().getName(),e);
        }
    }

    @Override
    public void close() throws IOException {
        isRunning = false;
        ThreadExecutorsService.closeAndWait(getClass().getName(),30000);
    }

    @Override
    public void start() {
        ThreadExecutorsService.runAsync(this,getClass().getName());
    }
}
