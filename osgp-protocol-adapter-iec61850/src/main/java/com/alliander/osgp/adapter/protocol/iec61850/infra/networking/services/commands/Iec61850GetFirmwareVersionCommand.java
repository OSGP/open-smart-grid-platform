/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands;

import java.util.ArrayList;
import java.util.List;

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
import com.alliander.osgp.dto.valueobjects.FirmwareModuleType;
import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;

public class Iec61850GetFirmwareVersionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850GetFirmwareVersionCommand.class);

    public List<FirmwareVersionDto> getFirmwareVersionFromDevice(final Iec61850Client iec61850Client,
            final DeviceConnection deviceConnection) throws ProtocolAdapterException {
        final Function<List<FirmwareVersionDto>> function = new Function<List<FirmwareVersionDto>>() {

            @Override
            public List<FirmwareVersionDto> apply() throws Exception {
                final List<FirmwareVersionDto> output = new ArrayList<>();

                // Getting the functional firmware version
                LOGGER.info("Reading the functional firmware version");
                final NodeContainer functionalFirmwareNode = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                        LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.FUNCTIONAL_FIRMWARE, Fc.ST);
                iec61850Client.readNodeDataValues(deviceConnection.getConnection().getClientAssociation(),
                        functionalFirmwareNode.getFcmodelNode());
                final String functionalFirmwareVersion = functionalFirmwareNode
                        .getString(SubDataAttribute.CURRENT_VERSION);

                // Adding it to the list
                output.add(new FirmwareVersionDto(FirmwareModuleType.FUNCTIONAL, functionalFirmwareVersion));

                // Getting the security firmware version
                LOGGER.info("Reading the security firmware version");
                final NodeContainer securityFirmwareNode = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                        LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.SECURITY_FIRMWARE, Fc.ST);
                iec61850Client.readNodeDataValues(deviceConnection.getConnection().getClientAssociation(),
                        securityFirmwareNode.getFcmodelNode());
                final String securityFirmwareVersion = securityFirmwareNode.getString(SubDataAttribute.CURRENT_VERSION);

                // Adding it to the list
                output.add(new FirmwareVersionDto(FirmwareModuleType.SECURITY, securityFirmwareVersion));

                return output;
            }
        };

        return iec61850Client.sendCommandWithRetry(function, deviceConnection.getDeviceIdentification());
    }
}
