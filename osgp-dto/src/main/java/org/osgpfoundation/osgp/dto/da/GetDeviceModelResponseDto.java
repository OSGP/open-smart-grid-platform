package org.osgpfoundation.osgp.dto.da;

import org.osgpfoundation.osgp.dto.da.iec61850.PhysicalDeviceDto;

import java.io.Serializable;

public class GetDeviceModelResponseDto implements Serializable {
    private final PhysicalDeviceDto physicalDevice;

    public GetDeviceModelResponseDto( final PhysicalDeviceDto physicalDevice ) {
        this.physicalDevice = physicalDevice;
    }

    public PhysicalDeviceDto getPhysicalDevice() {
        return physicalDevice;
    }
}
