package com.barakyesh.cluster.framework.impl;


import com.barakyesh.cluster.framework.api.NodeDetails;
import com.barakyesh.cluster.framework.api.NodeStatus;
import com.barakyesh.cluster.framework.api.NodeStatusUpdater;
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

/**
 * Created by Barak Yeshoua.
 */
public class NodeStatusUpdaterRunner implements Runner,Runnable,Closeable{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ServiceDiscovery<NodeDetails> serviceDiscovery;
    private ServiceInstance<NodeDetails> thisInstance;
    private final NodeStatusUpdater updater;
    private ClosableExecutorService closableExecutorService;

    NodeStatusUpdaterRunner(ServiceDiscovery<NodeDetails> serviceDiscovery, ServiceInstance<NodeDetails> thisInstance, NodeStatusUpdater updater) {
        this.serviceDiscovery = serviceDiscovery;
        this.thisInstance = thisInstance;
        this.updater = updater;
    }

    @Override
    public void run() {
        try {
            while (true) {
                updateNodeStatus();
                Thread.sleep(updater.getRunIntervalInMs());
            }
        } catch (InterruptedException e) {
            log.warn("Thread got interrupted");
        } catch (Exception e) {
            log.error("Error while updating node status",e);
        }
        log.info("NodeStatusUpdaterRunner stop running");
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
