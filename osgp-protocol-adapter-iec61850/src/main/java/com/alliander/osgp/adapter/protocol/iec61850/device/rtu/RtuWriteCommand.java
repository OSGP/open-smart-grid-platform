package com.alliander.osgp.adapter.protocol.iec61850.device.rtu;

import com.alliander.osgp.adapter.protocol.iec61850.exceptions.NodeWriteException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;

public interface RtuWriteCommand<T> {
    void executeWrite(Iec61850Client client, DeviceConnection connection, LogicalDevice logicalDevice, T writeData)
            throws NodeWriteException;
}
