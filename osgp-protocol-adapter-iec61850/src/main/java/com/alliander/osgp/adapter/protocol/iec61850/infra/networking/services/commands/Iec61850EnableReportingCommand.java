/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands;

import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.FcModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.exceptions.NodeException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting.Iec61850ClientBaseEventListener;

public class Iec61850EnableReportingCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850EnableReportingCommand.class);

    /**
     * Enable reporting so the device can send reports.
     *
     * @throws NodeException
     *             In case writing or reading of data-attributes fails.
     */
    public void enableReportingOnDevice(final Iec61850Client iec61850Client, final DeviceConnection deviceConnection)
            throws NodeException {
        final NodeContainer reporting = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.LOGICAL_NODE_ZERO, DataAttribute.REPORTING, Fc.BR);
        // Only reading the sequence number for the report node, as the report
        // node is not fully described by the ServerModel when using an ICD
        // file. Since the report node differs from the ServerModel, a full read
        // of the node and all data-attributes will fail. Therefore, only the
        // needed data-attributes are read.
        iec61850Client.readNodeDataValues(deviceConnection.getConnection().getClientAssociation(),
                (FcModelNode) reporting.getFcmodelNode().getChild(SubDataAttribute.SEQUENCE_NUMBER.getDescription()));

        final Iec61850ClientBaseEventListener reportListener = deviceConnection.getConnection()
                .getIec61850ClientAssociation().getReportListener();

        final short sqNum = reporting.getUnsignedByte(SubDataAttribute.SEQUENCE_NUMBER).getValue();
        reportListener.setSqNum(sqNum);
        reporting.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
        LOGGER.info("Allowing device {} to send reports containing events", deviceConnection.getDeviceIdentification());
    }

    /**
     * Enable reporting so the device can send reports. This version of the
     * function does not use the 'sequence number' to filter incoming reports.
     * When using the {@link Iec61850ClearReportCommand} the 'sequence number'
     * will always be reset to 0.
     *
     * @throws NodeException
     *             In case writing or reading of data-attributes fails.
     */
    public void enableReportingOnDeviceWithoutUsingSequenceNumber(final Iec61850Client iec61850Client,
            final DeviceConnection deviceConnection) throws NodeException {
        final NodeContainer reporting = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.LOGICAL_NODE_ZERO, DataAttribute.REPORTING, Fc.BR);

        final Iec61850ClientBaseEventListener reportListener = deviceConnection.getConnection()
                .getIec61850ClientAssociation().getReportListener();

        reportListener.setSqNum(0);
        reporting.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
        LOGGER.info("Allowing device {} to send reports containing events", deviceConnection.getDeviceIdentification());
    }
}
