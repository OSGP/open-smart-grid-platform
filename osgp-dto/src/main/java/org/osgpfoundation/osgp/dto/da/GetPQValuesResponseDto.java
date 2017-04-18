package org.osgpfoundation.osgp.dto.da;

import org.osgpfoundation.osgp.dto.da.iec61850.LogicalDeviceDto;

import java.io.Serializable;
import java.util.List;

public class GetPQValuesResponseDto implements Serializable {
    private List<LogicalDeviceDto> logicalDevices;

    public GetPQValuesResponseDto( final List<LogicalDeviceDto> logicalDevices ) {
        this.logicalDevices = logicalDevices;
    }

    public List<LogicalDeviceDto> getLogicalDevices() {
        return logicalDevices;
    }
}
