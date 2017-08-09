package com.barakyesh.cluster.framework.impl.async;


import com.barakyesh.cluster.framework.api.NodeDetails;
import com.barakyesh.cluster.framework.api.NodeStatus;
import com.barakyesh.cluster.framework.api.async.NodeStatusUpdater;
import com.barakyesh.common.utils.concurrent.AsyncIntervalRunnable;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Barak Yeshoua.
 */
public class ClusterNodeStatusUpdaterRunner extends AsyncIntervalRunnable{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ServiceDiscovery<NodeDetails> serviceDiscovery;
    private ServiceInstance<NodeDetails> thisInstance;
    private final NodeStatusUpdater updater;


    public ClusterNodeStatusUpdaterRunner(ServiceDiscovery<NodeDetails> serviceDiscovery, ServiceInstance<NodeDetails> thisInstance, NodeStatusUpdater updater) {
        super(updater.getRunIntervalInMs());
        this.serviceDiscovery = serviceDiscovery;
        this.thisInstance = thisInstance;
        this.updater = updater;
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

    public void start() {
        start(getClass().getSimpleName()+"-"+thisInstance.getName()+"-"+thisInstance.getId());
    }
}
