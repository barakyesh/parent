package com.barakyesh.cluster.discovery;

import com.barakyesh.cluster.discovery.framework.ClusterFrameworkFactory;
import com.barakyesh.cluster.discovery.framework.api.ClusterChangeListener;
import com.barakyesh.cluster.discovery.framework.api.ClusterFramework;
import com.barakyesh.cluster.discovery.framework.api.ClusterNode;
import com.barakyesh.cluster.discovery.framework.status.NodeStatus;
import com.barakyesh.cluster.discovery.framework.utils.CloseableUtils;
import com.barakyesh.cluster.discovery.framework.utils.ThreadExecutorsService;
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
public class Main {

    public static void main(String[] args) throws Exception {
        ClusterFramework myCluster = null;
        List<ClusterNode> nodes = new ArrayList<>();
        try
        {
            myCluster = ClusterFrameworkFactory.newCluster("myCluster", "192.168.33.12:2181",1000,3);
            myCluster.start();
            for(int i =0;i<10;i++) {
                ClusterNode clusterNode = myCluster.createNode()
                        .schema("http")
                        .host("127.0.0.1")
                        .port(8080)
                        .checkIntervalInMs(1000)
                        .properties(new HashMap<>())
                        .registerListener(new ClusterChangeListener() {
                            private final Logger log = LoggerFactory.getLogger(getClass());
                            @Override
                            public void nodeAdded() {
                                log.info("node added");
                            }

                            @Override
                            public void nodeRemoved() {
                                log.info("node removed");
                            }

                            @Override
                            public void clusterSizeChanged(int newClusterSize) {
                                log.info("clusterSizeChanged = " + newClusterSize);
                            }

                            @Override
                            public NodeStatus updateStatus() {
                                return NodeStatus.values()[new Random().nextInt(NodeStatus.values().length)];
                            }
                        })
                        .forName("node-"+i);
                clusterNode.start();
                nodes.add(clusterNode);
            }
            System.out.println("Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        }
        finally
        {
            nodes.forEach(CloseableUtils::closeQuietly);
            CloseableUtils.closeQuietly(myCluster);
            ThreadExecutorsService.close();
        }
    }
}
