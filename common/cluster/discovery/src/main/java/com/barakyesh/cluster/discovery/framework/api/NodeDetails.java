package com.barakyesh.cluster.discovery.framework.api;

import com.barakyesh.cluster.discovery.framework.status.NodeStatus;
import org.codehaus.jackson.map.annotate.JsonRootName;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Barak Yeshoua.
 */
@JsonRootName("details")
public class NodeDetails {
    private NodeStatus status = NodeStatus.WHITE;
    private Map<String,String> nodeProperties = new HashMap<>();

    public NodeDetails(Map<String, String> nodeProperties) {
        this.nodeProperties = nodeProperties;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    public Map<String, String> getNodeProperties() {
        return nodeProperties;
    }

    public void setNodeProperties(Map<String, String> nodeProperties) {
        this.nodeProperties = nodeProperties;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeProperties);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final NodeDetails other = (NodeDetails) obj;
        return Objects.equals(this.nodeProperties, other.nodeProperties);
    }
}
