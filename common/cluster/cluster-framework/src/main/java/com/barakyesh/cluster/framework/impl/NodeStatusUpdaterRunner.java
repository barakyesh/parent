package com.barakyesh.cluster.framework.impl;


import com.barakyesh.cluster.framework.api.NodeDetails;
import com.barakyesh.cluster.framework.api.NodeStatus;
import com.barakyesh.cluster.framework.api.NodeStatusUpdater;
import com.barakyesh.cluster.framework.api.Runner;
import com.barakyesh.common.utils.ThreadExecutorsService;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Barak Yeshoua.
 */
public class NodeStatusUpdaterRunner implements Runner,Runnable,Closeable{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ServiceDiscovery<NodeDetails> serviceDiscovery;
    private ServiceInstance<NodeDetails> thisInstance;
    private final NodeStatusUpdater updater;
    private volatile boolean isRunning;

    NodeStatusUpdaterRunner(ServiceDiscovery<NodeDetails> serviceDiscovery, ServiceInstance<NodeDetails> thisInstance, NodeStatusUpdater updater) {
        this.serviceDiscovery = serviceDiscovery;
        this.thisInstance = thisInstance;
        this.updater = updater;
    }

    @Override
    public void run() {
        try {
            isRunning = true;
            while (isRunning) {
                updateNodeStatus();
                Thread.sleep(updater.getRunIntervalInMs());
            }
        } catch (InterruptedException e) {
            log.warn("Thread got interrupted",e);
        } catch (Exception e) {
            log.error("Error while updating node status",e);
        }
        log.info("NodeStatusUpdaterRunner stop running");
    }

    private void updateNodeStatus() throws Exception {
        NodeStatus status = updater.updateStatus();
        if(thisInstance.getPayload().getStatus() != status) {
            log.info("Changing {} status into {}", thisInstance.getName(), status);
            thisInstance.getPayload().setStatus(status);
            serviceDiscovery.updateService(thisInstance);
        }
    }


    @Override
    public void close() throws IOException {
        isRunning = false;
        ThreadExecutorsService.close(getClass().getName()+"-"+thisInstance.getName());
    }

    @Override
    public void start() {
        ThreadExecutorsService.runAsync(this,getClass().getSimpleName()+"-"+thisInstance.getName());
    }
}
