package com.barakyesh.cluster.discovery.impl;


import com.barakyesh.cluster.discovery.api.ClusterChangeListener;
import com.barakyesh.cluster.discovery.api.NodeDetails;
import com.barakyesh.cluster.discovery.api.NodeStatus;
import com.barakyesh.common.utils.thread.AsyncIntervalRunnable;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.barakyesh.cluster.discovery.api.ClusterEventType.*;

/**
 * Created by Barak Yeshoua.
 */
public class ClusterChangeListenerRunner extends AsyncIntervalRunnable{
    private final ServiceDiscovery<NodeDetails> serviceDiscovery;
    private ServiceInstance<NodeDetails> thisInstance;
    private final ClusterChangeListener listener;
    private Set<ServiceInstance<NodeDetails>> serviceInstances;

    ClusterChangeListenerRunner(ServiceDiscovery<NodeDetails> serviceDiscovery, ServiceInstance<NodeDetails> thisInstance, ClusterChangeListener listener) {
        this.serviceDiscovery = serviceDiscovery;
        this.thisInstance = thisInstance;
        this.listener = listener;
    }

    @Override
    protected long getSleepInterval() {
        return listener.getRunIntervalInMs();
    }

    @Override
    protected void doAction() throws Exception {
        serviceInstances = checkServiceInstances();
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
    public void start() throws Exception {
        serviceInstances = listInstances();
        start(getClass().getSimpleName()+"-"+thisInstance.getName()+"-"+thisInstance.getId());
    }
}
