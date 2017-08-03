package com.barakyesh.cluster.discovery.impl;


import com.barakyesh.cluster.discovery.api.NodeDetails;
import com.barakyesh.cluster.discovery.api.NodeStatus;
import com.barakyesh.cluster.discovery.api.NodeStatusUpdater;
import com.barakyesh.common.utils.thread.AsyncIntervalRunnable;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Barak Yeshoua.
 */
public class NodeStatusUpdaterRunner extends AsyncIntervalRunnable{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ServiceDiscovery<NodeDetails> serviceDiscovery;
    private ServiceInstance<NodeDetails> thisInstance;
    private final NodeStatusUpdater updater;


    NodeStatusUpdaterRunner(ServiceDiscovery<NodeDetails> serviceDiscovery, ServiceInstance<NodeDetails> thisInstance, NodeStatusUpdater updater) {
        this.serviceDiscovery = serviceDiscovery;
        this.thisInstance = thisInstance;
        this.updater = updater;
    }

    @Override
    protected long getSleepInterval() {
        return updater.getRunIntervalInMs();
    }

    @Override
    protected void doAction() throws Exception {
        updateNodeStatus();
    }

    private void updateNodeStatus() throws Exception {
        NodeStatus status = updater.updateStatus();
        if(thisInstance.getPayload().getStatus() != status) {
            log.info("Changing {} node {} status into {}",thisInstance.getName(),thisInstance.getId(), status);
            thisInstance.getPayload().setStatus(status);
            serviceDiscovery.updateService(thisInstance);
        }
    }


    @Override
    public void start() {
        start(getClass().getSimpleName()+"-"+thisInstance.getName()+"-"+thisInstance.getId());
    }
}
