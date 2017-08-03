package com.barakyesh.cluster.discovery.framework;

import com.barakyesh.cluster.discovery.framework.api.ClusterChangeListener;
import com.barakyesh.cluster.discovery.framework.api.ClusterFramework;
import com.barakyesh.cluster.discovery.framework.api.ClusterNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Barak Yeshoua.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        ClusterFramework myCluster = null;
        List<ClusterNode> nodes = new ArrayList<>();
        try
        {
            myCluster = ClusterFrameworkFactory.newCluster("myCluster", "192.168.33.12:2181", new ExponentialBackoffRetry(1000, 3));
            myCluster.start();
            for(int i =0;i<10;i++) {
                ClusterNode clusterNode = myCluster.createNode()
                        .schema("http")
                        .host("127.0.0.1")
                        .port(8080)
                        .properties(new HashMap<>())
                        .registerListener(new ClusterChangeListener() {
                            @Override
                            public void nodeAdded() {
                                System.out.println("node added");
                            }

                            @Override
                            public void nodeRemoved() {
                                System.out.println("node removed");
                            }

                            @Override
                            public void clusterSizeChanged(int newClusterSize) {
                                System.out.println("clusterSizeChanged = " + newClusterSize);
                            }
                        })
                        .forName("node-"+i);
                clusterNode.start();
                nodes.add(clusterNode);
                Thread.sleep(5000);
            }
            System.out.println("Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        }
        finally
        {
            CloseableUtils.closeQuietly(myCluster);
            nodes.forEach(CloseableUtils::closeQuietly);
        }
    }
}
