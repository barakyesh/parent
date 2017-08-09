package com.barakyesh.cluster.framework.impl.async;


import com.barakyesh.cluster.framework.api.NodeDetails;
import com.barakyesh.cluster.framework.api.NodeStatus;
import com.barakyesh.cluster.framework.api.async.InstanceListener;
import com.barakyesh.cluster.framework.impl.ClusterEventImpl;
import com.barakyesh.common.utils.concurrent.AsyncIntervalRunnable;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.barakyesh.cluster.framework.api.ClusterEventType.CLUSTER_SIZE_CHANGED;
import static com.barakyesh.cluster.framework.api.ClusterEventType.NODE_ADDED;
import static com.barakyesh.cluster.framework.api.ClusterEventType.NODE_REMOVED;


/**
 * Created by Barak Yeshoua.
 */
public class ClusterInstanceListenerRunner extends AsyncIntervalRunnable{
    private final ServiceDiscovery<NodeDetails> serviceDiscovery;
    private ServiceInstance<NodeDetails> thisInstance;
    private final InstanceListener listener;
    private Set<ServiceInstance<NodeDetails>> serviceInstances;

    public ClusterInstanceListenerRunner(ServiceDiscovery<NodeDetails> serviceDiscovery, ServiceInstance<NodeDetails> thisInstance, InstanceListener listener) {
        super(listener.getRunIntervalInMs());
        this.serviceDiscovery = serviceDiscovery;
        this.thisInstance = thisInstance;
        this.listener = listener;
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


    public void start() throws Exception {
        serviceInstances = listInstances();
        start(getClass().getSimpleName()+"-"+thisInstance.getName()+"-"+thisInstance.getId());
    }
}
