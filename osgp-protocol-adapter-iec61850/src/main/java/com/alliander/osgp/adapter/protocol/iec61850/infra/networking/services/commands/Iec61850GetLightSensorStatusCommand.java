/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.openiec61850.BdaBoolean;
import org.openmuc.openiec61850.Fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.services.DeviceMessageLoggingService;
import com.alliander.osgp.core.db.api.iec61850.entities.LightMeasurementDevice;
import com.alliander.osgp.dto.valueobjects.DeviceStatusDto;
import com.alliander.osgp.dto.valueobjects.LightTypeDto;
import com.alliander.osgp.dto.valueobjects.LightValueDto;
import com.alliander.osgp.dto.valueobjects.LinkTypeDto;

public class Iec61850GetLightSensorStatusCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850GetLightSensorStatusCommand.class);

    public DeviceStatusDto getStatusFromDevice(final Iec61850Client iec61850Client,
            final DeviceConnection deviceConnection, final LightMeasurementDevice lmd) throws ProtocolAdapterException {

        final Iec61850GetLightSensorStatusFunction function = new Iec61850GetLightSensorStatusFunction(iec61850Client,
                deviceConnection, lmd);

        return iec61850Client.sendCommandWithRetry(function, "GetLightSensorStatus",
                deviceConnection.getDeviceIdentification());
    }

    private class Iec61850GetLightSensorStatusFunction implements Function<DeviceStatusDto> {

        final Iec61850Client iec61850Client;
        final DeviceConnection deviceConnection;
        final LightMeasurementDevice lmd;

        public Iec61850GetLightSensorStatusFunction(final Iec61850Client iec61850Client,
                final DeviceConnection deviceConnection, final LightMeasurementDevice lmd) {
            this.iec61850Client = iec61850Client;
            this.deviceConnection = deviceConnection;
            this.lmd = lmd;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper
         * .Function #apply(com.alliander.osgp.adapter.protocol.iec61850.domain.
         * valueobjects .DeviceMessageLog)
         */
        @Override
        public DeviceStatusDto apply(final DeviceMessageLog deviceMessageLog) throws Exception {
            // Use digital input number as index for SPPGIO node.
            final int index = this.lmd.getDigitalInput();

            // Hard-coded values which are not retrieved from the light
            // measurement device.
            final int luxValue = -1;
            final LinkTypeDto preferredLinkType = LinkTypeDto.ETHERNET;
            final LinkTypeDto actualLinkType = LinkTypeDto.ETHERNET;
            final LightTypeDto lightType = LightTypeDto.ONE_TO_TWENTY_FOUR_VOLT;
            final int notificationMask = -1;

            // Read the data attribute of the logical node.
            final LogicalNode logicalNode = LogicalNode.getSpggioByIndex(index);
            final NodeContainer indNode = this.deviceConnection.getFcModelNode(LogicalDevice.LD0, logicalNode,
                    DataAttribute.IND, Fc.ST);
            this.iec61850Client.readNodeDataValues(this.deviceConnection.getConnection().getClientAssociation(),
                    indNode.getFcmodelNode());
            LOGGER.info("device: {}, indNode: {}", this.deviceConnection.getDeviceIdentification(), indNode);

            // Read the value of the data object.
            final BdaBoolean stVal = indNode.getBoolean(SubDataAttribute.STATE);
            LOGGER.info("device: {}, stVal: {}", this.deviceConnection.getDeviceIdentification(), stVal);
            deviceMessageLog.addVariable(logicalNode, DataAttribute.IND, Fc.ST, SubDataAttribute.STATE,
                    Boolean.toString(stVal.getValue()));

            final List<LightValueDto> sensorValues = new ArrayList<>();
            sensorValues.add(new LightValueDto(index, !stVal.getValue(), luxValue));

            DeviceMessageLoggingService.logMessage(deviceMessageLog, this.deviceConnection.getDeviceIdentification(),
                    this.deviceConnection.getOrganisationIdentification(), false);

            return new DeviceStatusDto(sensorValues, preferredLinkType, actualLinkType, lightType, notificationMask);
        }
    }
}
