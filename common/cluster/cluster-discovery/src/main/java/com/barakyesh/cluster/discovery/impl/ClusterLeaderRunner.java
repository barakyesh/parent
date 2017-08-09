package com.barakyesh.cluster.discovery.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ClusterLeaderRunner extends LeaderSelectorListenerAdapter implements Closeable {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final String name;
    private final LeaderSelector leaderSelector;
    private final AtomicInteger leaderCount = new AtomicInteger();
    private boolean isRunning = true;

    public ClusterLeaderRunner(CuratorFramework client, String path, String name)
    {
        this.name = name;
        leaderSelector = new LeaderSelector(client, path, this);
        leaderSelector.autoRequeue();
    }

    public void start() throws IOException
    {
        leaderSelector.start();
    }

    @Override
    public void close() throws IOException
    {
        isRunning = false;
        leaderSelector.close();
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception
    {
        try {
            while(isRunning) {
                doAction();
                Thread.sleep(getSleepInterval());
            }
        } catch (InterruptedException e) {
            log.warn("Leader thread got interrupted");
        } catch (Exception e) {
            log.error("Error while running Leader thread",e);
        }
        log.info("Leader thread {} stop running");


    }

    private void doAction() {
        final int  waitSeconds = (int)(5 * Math.random()) + 1;

        log.info(name + " is now the leader. Waiting " + waitSeconds + " seconds...");
        log.info(name + " has been leader " + leaderCount.getAndIncrement() + " time(s) before.");
        try
        {
            Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
        }
        catch ( InterruptedException e )
        {
            System.err.println(name + " was interrupted.");
            Thread.currentThread().interrupt();
        }
        finally
        {
            System.out.println(name + " relinquishing leadership.\n");
        }
    }

    public long getSleepInterval() {
        return 1000;
    }
}
