package org.osgpfoundation.osgp.dto.da.iec61850;

import java.io.Serializable;
import java.util.List;

public class LogicalDeviceDto implements Serializable {
    private static final long serialVersionUID = 3263349435741609185L;

    private final String name;
    private List<LogicalNodeDto> nodes;

    public LogicalDeviceDto( final String name, final List<LogicalNodeDto> nodes ) {
        this.name = name;
        this.nodes = nodes;
    }

    public String getName() {
        return name;
    }

    public List<LogicalNodeDto> getNodes() {
        return nodes;
    }
}
