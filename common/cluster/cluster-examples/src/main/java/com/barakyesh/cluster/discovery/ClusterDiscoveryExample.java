package com.barakyesh.cluster.discovery;

import com.barakyesh.cluster.framework.ClusterFrameworkFactory;
import com.barakyesh.cluster.framework.api.ClusterEvent;
import com.barakyesh.cluster.framework.api.ClusterFramework;
import com.barakyesh.cluster.framework.api.ClusterNode;
import com.barakyesh.cluster.framework.api.NodeStatus;
import com.barakyesh.cluster.framework.api.async.InstanceListener;
import com.barakyesh.cluster.framework.api.async.LeaderAction;
import com.barakyesh.cluster.framework.api.async.NodeStatusUpdater;
import com.barakyesh.common.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Barak Yeshoua.
 */
public class ClusterDiscoveryExample {
    public static void main(String[] args) throws Exception {
        ClusterFramework myCluster = null;
        List<ClusterNode> nodes = new ArrayList<>();
        try {
            myCluster = ClusterFrameworkFactory.newCluster("myCluster", "192.168.33.12:2181", 1000, 3);
            myCluster.start();
            for (int i = 0; i < 3; i++) {
                ClusterNode clusterNode = myCluster.createNode()
                        .schema("http")
                        .host("127.0.0.1")
                        .port(8080)
                        .context("/rest")
                        .properties(new HashMap<>())
                        .registerInstanceListener(new InstanceListener() {

                            private final Logger log = LoggerFactory.getLogger(getClass());

                            @Override
                            public void handleEvent(ClusterEvent event) {
                                log.info("received event {}", event.toString());
                            }

                            @Override
                            public long getRunIntervalInMs() {
                                return 10000;
                            }

                        })
                        .registerNodeStatusUpdater(new NodeStatusUpdater() {

                            @Override
                            public NodeStatus updateStatus() {
                                return NodeStatus.values()[new Random().nextInt(NodeStatus.values().length)];
                            }

                            @Override
                            public long getRunIntervalInMs() {
                                return 10000;
                            }


                        })
                        .registerLeaderAction(new LeaderAction() {
                            private final Logger log = LoggerFactory.getLogger(getClass());
                            @Override
                            public void doAction() {
                                log.info("{} is now the leader.",Thread.currentThread().getName());
                            }

                            @Override
                            public long getRunIntervalInMs() {
                                return 10000;
                            }
                        })
                        .forName("myService");
                clusterNode.start();
                nodes.add(clusterNode);
            }
            System.out.println("Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } finally {
            nodes.forEach(CloseableUtils::closeQuietly);
            CloseableUtils.closeQuietly(myCluster);
        }
    }
}
