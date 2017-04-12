package org.osgpfoundation.osgp.domain.da.valueobjects;

import org.osgpfoundation.osgp.domain.da.valueobjects.iec61850.PhysicalDevice;

import java.io.Serializable;

public class GetDeviceModelResponse implements Serializable {
    private final PhysicalDevice physicalDevice;

    public GetDeviceModelResponse( final PhysicalDevice physicalDevice ) {
        this.physicalDevice = physicalDevice;
    }

    public PhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }
}
