package org.osgpfoundation.osgp.domain.da.valueobjects;

import org.osgpfoundation.osgp.domain.da.valueobjects.iec61850.DataSample;
import org.osgpfoundation.osgp.domain.da.valueobjects.iec61850.LogicalDevice;

import java.io.Serializable;
import java.util.List;

public class GetPQValuesResponse implements Serializable{
    private List<LogicalDevice> logicalDevices;

    public GetPQValuesResponse( final List<LogicalDevice> logicalDevices ) {
        this.logicalDevices = logicalDevices;
    }

    public List<LogicalDevice> getLogicalDevices() {
        return logicalDevices;
    }
}
