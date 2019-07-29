package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.ServerModel;

public class SwitchDevice extends LogicalDevice {

    public SwitchDevice(final String physicalDeviceName, final String logicalDeviceName,
            final ServerModel serverModel) {
        super(physicalDeviceName, logicalDeviceName, serverModel);
    }

    @Override
    public List<BasicDataAttribute> getAttributesAndSetValues(final Date timestamp) {
        // Not yet needed for the switch device
        return new ArrayList<>();
    }

}
