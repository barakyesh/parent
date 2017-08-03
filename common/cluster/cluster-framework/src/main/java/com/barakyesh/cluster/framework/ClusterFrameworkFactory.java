package com.barakyesh.cluster.framework;

import com.barakyesh.cluster.framework.api.ClusterFramework;
import com.barakyesh.cluster.framework.impl.ClusterFrameworkImpl;

/**
 * Created by Barak Yeshoua.
 */
public class ClusterFrameworkFactory {

    /**
     * Return a new builder that builds a ClusterFramework
     *
     * @return new builder
     */
    static Builder builder()
    {
        return new Builder();
    }

    public static ClusterFramework newCluster(String clusterName, String connectString, int baseSleepTimeMs, int maxRetries){
        return builder().
                clusterName(clusterName).
                connectString(connectString).
                baseSleepTimeMs(baseSleepTimeMs).
                maxRetries(maxRetries).
                build();
    }


    public static class Builder{

        private String connectString;
        private int baseSleepTimeMs;
        private int maxRetries;
        private String clusterName;

        Builder connectString(String connectString) {
            this.connectString = connectString;
            return this;
        }

        Builder baseSleepTimeMs(int baseSleepTimeMs) {
            this.baseSleepTimeMs = baseSleepTimeMs;
            return this;
        }

        Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        Builder clusterName(String clusterName) {
            this.clusterName = clusterName;
            return this;
        }

        ClusterFramework build() {
            return new ClusterFrameworkImpl(this);
        }

        public String getConnectString() {
            return connectString;
        }

        public int getBaseSleepTimeMs() {
            return baseSleepTimeMs;
        }

        public int getMaxRetries() {
            return maxRetries;
        }

        public String getClusterName() {
            return clusterName;
        }
    }
}
