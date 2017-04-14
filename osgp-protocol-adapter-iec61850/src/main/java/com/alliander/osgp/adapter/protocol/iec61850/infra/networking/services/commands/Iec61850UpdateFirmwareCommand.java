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

import com.alliander.osgp.adapter.protocol.iec61850.exceptions.NodeException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.NodeReadException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.dto.valueobjects.FirmwareModuleData;
import com.alliander.osgp.dto.valueobjects.FirmwareModuleType;

public class Iec61850UpdateFirmwareCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850UpdateFirmwareCommand.class);

    public void pushFirmwareToDevice(final Iec61850Client iec61850Client, final DeviceConnection deviceConnection,
            final String fullUrl, final FirmwareModuleData firmwareModuleData) throws ProtocolAdapterException {
        final Function<Void> function = new Function<Void>() {

            @Override
            public Void apply() throws Exception {
                final int count = firmwareModuleData.countNumberOfModules();
                if (count != 1) {
                    throw new ProtocolAdapterException(String.format(
                            "Number of firmware modules is not equal to 1 but %d", count));
                }

                // Check if the functional or security firmware needs to be
                // updated.
                if (FirmwareModuleType.FUNCTIONAL.name().equalsIgnoreCase(firmwareModuleData.getModuleVersionFunc())) {
                    Iec61850UpdateFirmwareCommand.this.updateFunctionalFirmware(iec61850Client, deviceConnection,
                            fullUrl);
                } else if (FirmwareModuleType.SECURITY.name()
                        .equalsIgnoreCase(firmwareModuleData.getModuleVersionSec())) {
                    Iec61850UpdateFirmwareCommand.this
                    .updateSecurityFirmware(iec61850Client, deviceConnection, fullUrl);
                } else {
                    throw new ProtocolAdapterException(
                            String.format(
                                    "Unsupported firmwareModuleData (only functional and security are allowed): communication: %s, functional: %s, module-active: %s, m-bus: %s, security: %s, fullUrl: %s",
                                    firmwareModuleData.getModuleVersionComm(),
                                    firmwareModuleData.getModuleVersionFunc(), firmwareModuleData.getModuleVersionMa(),
                                    firmwareModuleData.getModuleVersionMbus(),
                                    firmwareModuleData.getModuleVersionSec(), fullUrl));
                }

                return null;
            }
        };

        iec61850Client.sendCommandWithRetry(function, deviceConnection.getDeviceIdentification());
    }

    private void updateFunctionalFirmware(final Iec61850Client iec61850Client, final DeviceConnection deviceConnection,
            final String fullUrl) throws NodeException {
        LOGGER.info("Reading the functional firmware node for device: {}", deviceConnection.getDeviceIdentification());
        final NodeContainer functionalFirmwareNode = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.FUNCTIONAL_FIRMWARE, Fc.CF);
        iec61850Client.readNodeDataValues(deviceConnection.getConnection().getClientAssociation(),
                functionalFirmwareNode.getFcmodelNode());

        final String currentFunctionalFirmwareDownloadUrl = functionalFirmwareNode.getString(SubDataAttribute.URL);
        final Date currentFunctionalFirmwareUpdateDateTime = functionalFirmwareNode
                .getDate(SubDataAttribute.START_TIME);
        LOGGER.info("Current functional firmware download url: {}, start time: {} for device: {}",
                currentFunctionalFirmwareDownloadUrl, currentFunctionalFirmwareUpdateDateTime,
                deviceConnection.getDeviceIdentification());

        LOGGER.info("Updating the functional firmware download url to: {} for device: {}", fullUrl,
                deviceConnection.getDeviceIdentification());
        functionalFirmwareNode.writeString(SubDataAttribute.URL, fullUrl);

        final Date oneMinuteFromNow = this.determineFirmwareUpdateDateTime(iec61850Client, deviceConnection);
        LOGGER.info("Updating the functional firmware download start time to: {} for device: {}", oneMinuteFromNow,
                deviceConnection.getDeviceIdentification());
        functionalFirmwareNode.writeDate(SubDataAttribute.START_TIME, oneMinuteFromNow);
    }

    private void updateSecurityFirmware(final Iec61850Client iec61850Client, final DeviceConnection deviceConnection,
            final String fullUrl) throws NodeException {
        LOGGER.info("Reading the security firmware node for device: {}", deviceConnection.getDeviceIdentification());
        final NodeContainer securityFirmwareNode = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.SECURITY_FIRMWARE, Fc.CF);
        iec61850Client.readNodeDataValues(deviceConnection.getConnection().getClientAssociation(),
                securityFirmwareNode.getFcmodelNode());

        final String currentSecurityFirmwareDownloadUrl = securityFirmwareNode.getString(SubDataAttribute.URL);
        final Date currentSecurityFirmwareUpdateDateTime = securityFirmwareNode.getDate(SubDataAttribute.START_TIME);
        LOGGER.info("Current security firmware download url: {}, start time: {} for device: {}",
                currentSecurityFirmwareDownloadUrl, currentSecurityFirmwareUpdateDateTime,
                deviceConnection.getDeviceIdentification());

        LOGGER.info("Updating the security firmware download url to : {} for device: {}", fullUrl,
                deviceConnection.getDeviceIdentification());
        securityFirmwareNode.writeString(SubDataAttribute.URL, fullUrl);

        final Date oneMinuteFromNow = this.determineFirmwareUpdateDateTime(iec61850Client, deviceConnection);
        LOGGER.info("Updating the security firmware download start time to: {} for device: {}", oneMinuteFromNow,
                deviceConnection.getDeviceIdentification());
        securityFirmwareNode.writeDate(SubDataAttribute.START_TIME, oneMinuteFromNow);
    }

    private Date determineFirmwareUpdateDateTime(final Iec61850Client iec61850Client,
            final DeviceConnection deviceConnection) throws NodeReadException {
        final NodeContainer clock = deviceConnection.getFcModelNode(LogicalDevice.LIGHTING,
                LogicalNode.STREET_LIGHT_CONFIGURATION, DataAttribute.CLOCK, Fc.CF);
        iec61850Client.readNodeDataValues(deviceConnection.getConnection().getClientAssociation(),
                clock.getFcmodelNode());

        final DateTime deviceTime = new DateTime(clock.getDate(SubDataAttribute.CURRENT_TIME));
        // Creating a DateTime one minute from now.
        return deviceTime.plusMinutes(1).toDate();
    }
}
