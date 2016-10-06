/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands;

import java.util.Date;

import org.joda.time.DateTime;
import org.openmuc.openiec61850.Fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;

public class Iec61850UpdateFirmwareCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850UpdateFirmwareCommand.class);

    public void pushFirmwareToDevice(final Iec61850Client iec61850Client, final DeviceConnection deviceConnection,
            final String fullUrl) throws ProtocolAdapterException {
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {
                // Getting the functional firmware version.
                LOGGER.info("Reading the functional firmware version");
                final NodeContainer functionalFirmwareNode = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                        LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.FUNCTIONAL_FIRMWARE, Fc.CF);
                iec61850Client.readNodeDataValues(deviceConnection.getConnection().getClientAssociation(),
                        functionalFirmwareNode.getFcmodelNode());

                LOGGER.info("Updating the firmware download url");
                functionalFirmwareNode.writeString(SubDataAttribute.URL, fullUrl);

                final NodeContainer clock = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                        LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.CLOCK, Fc.CF);
                iec61850Client.readNodeDataValues(deviceConnection.getConnection().getClientAssociation(),
                        clock.getFcmodelNode());

                final DateTime deviceTime = new DateTime(clock.getDate(SubDataAttribute.CURRENT_TIME));
                // Creating a Date one minute from now.
                final Date oneMinuteFromNow = deviceTime.plusMinutes(1).toDate();

                LOGGER.info("Updating the firmware download start time to: {}", oneMinuteFromNow);
                functionalFirmwareNode.writeDate(SubDataAttribute.START_TIME, oneMinuteFromNow);

                return null;
            }
        };

        iec61850Client.sendCommandWithRetry(function);
    }
}
