package com.barakyesh.cluster.discovery.framework;

import com.barakyesh.cluster.discovery.framework.api.ClusterFramework;
import com.barakyesh.cluster.discovery.framework.impl.ClusterFrameworkImpl;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;

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

    public static ClusterFramework newCluster(String clusterName,String connectString, int baseSleepTimeMs, int maxRetries){
        return builder().
                clusterName(clusterName).
                connectString(connectString).
                retryPolicy(new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries)).
                build();
    }


    public static class Builder{

        private String connectString;
        private RetryPolicy retryPolicy;
        private String clusterName;

        Builder connectString(String connectString) {
            this.connectString = connectString;
            return this;
        }

        Builder retryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
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

        public RetryPolicy getRetryPolicy() {
            return retryPolicy;
        }

        public String getClusterName() {
            return clusterName;
        }
    }
}
