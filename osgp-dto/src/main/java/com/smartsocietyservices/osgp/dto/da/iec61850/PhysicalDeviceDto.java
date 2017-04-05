package com.smartsocietyservices.osgp.dto.da.iec61850;

import java.util.List;

public class PhysicalDeviceDto {
    private final String id;
    private List<LogicalDeviceDto> logicalDevices;

    public PhysicalDeviceDto( final String id, final List<LogicalDeviceDto> logicalDevices ) {
        this.id = id;
        this.logicalDevices = logicalDevices;
    }

    public String getId() {
        return id;
    }

    public List<LogicalDeviceDto> getLogicalDevices() {
        return logicalDevices;
    }
}
