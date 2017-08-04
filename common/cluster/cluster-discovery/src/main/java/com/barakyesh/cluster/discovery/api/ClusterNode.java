package com.barakyesh.cluster.discovery.api;

import java.io.Closeable;
import java.util.Map;

/**
 * Created by Barak Yeshoua.
 */
public interface ClusterNode extends Closeable{

    void start() throws Exception;

    String getName();

    String getId();

    String getHost();

    Integer getPort();

    Map<String,String> getServicePropeties();

    long getRegistrationTimeUTC();

    NodeStatus getServiceStatus();

    String getServiceUrl();
}
