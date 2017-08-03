package com.barakyesh.cluster.framework.impl;


import com.barakyesh.cluster.framework.api.ClusterChangeListener;
import com.barakyesh.cluster.framework.api.NodeDetails;
import com.barakyesh.cluster.framework.api.NodeStatus;
import com.barakyesh.cluster.framework.api.Runner;
import com.barakyesh.common.utils.thread.AsyncIntervalRunnable;
import com.barakyesh.common.utils.thread.ClosableExecutorService;
import com.barakyesh.common.utils.thread.ThreadUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.barakyesh.cluster.framework.api.ClusterEventType.*;

/**
 * Created by Barak Yeshoua.
 */
public class ClusterChangeListenerRunner extends AsyncIntervalRunnable implements Runner{
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
