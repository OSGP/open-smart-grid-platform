package org.osgpfoundation.osgp.domain.da.valueobjects.iec61850;

import java.io.Serializable;
import java.util.List;

public class LogicalDevice implements Serializable {
    private static final long serialVersionUID = 4776483459295843437L;

    private final String name;
    private List<LogicalNode> logicalNodes;

    public LogicalDevice( final String name, final List<LogicalNode> logicalNodes ) {
        this.name = name;
        this.logicalNodes = logicalNodes;
    }

    public String getName() {
        return name;
    }

    public List<LogicalNode> getLogicalNodes() {
        return logicalNodes;
    }
}
