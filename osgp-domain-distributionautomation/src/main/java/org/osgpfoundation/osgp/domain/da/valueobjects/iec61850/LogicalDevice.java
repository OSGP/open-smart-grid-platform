package org.osgpfoundation.osgp.domain.da.valueobjects.iec61850;

import java.util.List;

public class LogicalDevice {
    private final String name;
    private List<LogicalNode> nodes;

    public LogicalDevice( final String name, final List<LogicalNode> nodes ) {
        this.name = name;
        this.nodes = nodes;
    }

    public String getName() {
        return name;
    }

    public List<LogicalNode> getNodes() {
        return nodes;
    }
}
