package com.barakyesh.cluster.framework.impl.async;

import com.barakyesh.cluster.framework.api.async.LeaderAction;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class ClusterLeaderActionRunner extends LeaderSelectorListenerAdapter implements Closeable {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final LeaderAction leaderAction;
    private final String name;
    private final LeaderSelector leaderSelector;
    private final AtomicInteger leaderCount = new AtomicInteger();
    private boolean isRunning = true;

    public ClusterLeaderActionRunner(LeaderAction leaderAction, CuratorFramework client, String path, String name) {
        this.leaderAction = leaderAction;
        this.name = name;
        this.leaderSelector = new LeaderSelector(client, path, this);
        this.leaderSelector.autoRequeue();
    }

    public void start() throws IOException {
        leaderSelector.start();
    }

    @Override
    public void close() throws IOException {
        isRunning = false;
        leaderSelector.close();
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        try {
            while (isRunning) {
                leaderAction.doAction();
                Thread.sleep(leaderAction.getRunInterval());
            }
        } catch (InterruptedException e) {
            log.warn("Leader concurrent {} got interrupted", name);
        } catch (Exception e) {
            log.error("Error while running Leader concurrent {}", name, e);
        }
        log.info("Leader concurrent {} stop running", name);


    }

}
