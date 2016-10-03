/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands;

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

public class Iec61850SetEventNotificationFilterCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850SetEventNotificationFilterCommand.class);

    public void setEventNotificationFilterOnDevice(final Iec61850Client iec61850Client,
            final DeviceConnection deviceConnection, final String filter) throws ProtocolAdapterException {
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {

                LOGGER.info("Setting the event notification filter");

                final NodeContainer eventBufferConfiguration = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                        LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.EVENT_BUFFER, Fc.CF);
                iec61850Client.readNodeDataValues(deviceConnection.getConnection().getClientAssociation(),
                        eventBufferConfiguration.getFcmodelNode());

                LOGGER.info("Updating the enabled EventType filter to {}", filter);
                eventBufferConfiguration.writeString(SubDataAttribute.EVENT_BUFFER_FILTER, filter);

                return null;
            }
        };

        iec61850Client.sendCommandWithRetry(function);
    }
}
