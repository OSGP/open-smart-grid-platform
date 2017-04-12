package org.osgpfoundation.osgp.dto.da.iec61850;

import java.util.List;

public class LogicalDeviceDto {
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
