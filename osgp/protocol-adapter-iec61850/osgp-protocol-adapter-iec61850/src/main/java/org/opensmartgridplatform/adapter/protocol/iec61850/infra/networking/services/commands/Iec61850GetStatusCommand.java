/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.Fc;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.EventType;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.services.DeviceMessageLoggingService;
import org.opensmartgridplatform.core.db.api.iec61850.entities.DeviceOutputSetting;
import org.opensmartgridplatform.core.db.api.iec61850.entities.Ssld;
import org.opensmartgridplatform.dto.valueobjects.DeviceStatusDto;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationTypeDto;
import org.opensmartgridplatform.dto.valueobjects.LightTypeDto;
import org.opensmartgridplatform.dto.valueobjects.LightValueDto;
import org.opensmartgridplatform.dto.valueobjects.LinkTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850GetStatusCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850GetStatusCommand.class);

  private DeviceMessageLoggingService loggingService;

  public Iec61850GetStatusCommand(final DeviceMessageLoggingService loggingService) {
    this.loggingService = loggingService;
  }

  public DeviceStatusDto getStatusFromDevice(
      final Iec61850Client iec61850Client, final DeviceConnection deviceConnection, final Ssld ssld)
      throws ProtocolAdapterException {
    final Function<DeviceStatusDto> function =
        new Function<DeviceStatusDto>() {

          @Override
          public DeviceStatusDto apply(final DeviceMessageLog deviceMessageLog)
              throws ProtocolAdapterException {
            // getting the light relay values
            final List<LightValueDto> lightValues = new ArrayList<>();

            for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {
              final LogicalNode logicalNode =
                  LogicalNode.getSwitchComponentByIndex(deviceOutputSetting.getInternalId());
              final NodeContainer position =
                  deviceConnection.getFcModelNode(
                      LogicalDevice.LIGHTING, logicalNode, DataAttribute.POSITION, Fc.ST);
              iec61850Client.readNodeDataValues(
                  deviceConnection.getConnection().getClientAssociation(),
                  position.getFcmodelNode());
              final BdaBoolean state = position.getBoolean(SubDataAttribute.STATE);
              final boolean on = state.getValue();
              lightValues.add(new LightValueDto(deviceOutputSetting.getExternalId(), on, null));

              LOGGER.info(
                  String.format(
                      "Got status of relay %d => %s",
                      deviceOutputSetting.getInternalId(), on ? "on" : "off"));

              deviceMessageLog.addVariable(
                  logicalNode,
                  DataAttribute.POSITION,
                  Fc.ST,
                  SubDataAttribute.STATE,
                  Boolean.toString(on));
            }

            final NodeContainer eventBuffer =
                deviceConnection.getFcModelNode(
                    LogicalDevice.LIGHTING,
                    LogicalNode.STREET_LIGHT_CONFIGURATION,
                    DataAttribute.EVENT_BUFFER,
                    Fc.CF);
            iec61850Client.readNodeDataValues(
                deviceConnection.getConnection().getClientAssociation(),
                eventBuffer.getFcmodelNode());
            final String filter = eventBuffer.getString(SubDataAttribute.EVENT_BUFFER_FILTER);
            LOGGER.info("Got EvnBuf.enbEvnType filter {}", filter);

            deviceMessageLog.addVariable(
                LogicalNode.STREET_LIGHT_CONFIGURATION,
                DataAttribute.EVENT_BUFFER,
                Fc.CF,
                SubDataAttribute.EVENT_BUFFER_FILTER,
                filter);

            final Set<EventNotificationTypeDto> notificationTypes =
                EventType.getNotificationTypesForFilter(filter);
            int eventNotificationsMask = 0;
            for (final EventNotificationTypeDto notificationType : notificationTypes) {
              eventNotificationsMask |= notificationType.getValue();
            }

            final NodeContainer softwareConfiguration =
                deviceConnection.getFcModelNode(
                    LogicalDevice.LIGHTING,
                    LogicalNode.STREET_LIGHT_CONFIGURATION,
                    DataAttribute.SOFTWARE_CONFIGURATION,
                    Fc.CF);
            iec61850Client.readNodeDataValues(
                deviceConnection.getConnection().getClientAssociation(),
                softwareConfiguration.getFcmodelNode());
            String lightTypeValue = softwareConfiguration.getString(SubDataAttribute.LIGHT_TYPE);
            // Fix for Kaifa bug KI-31
            if (lightTypeValue == null || lightTypeValue.isEmpty()) {
              lightTypeValue = "RELAY";
            }
            final LightTypeDto lightType = LightTypeDto.valueOf(lightTypeValue);

            deviceMessageLog.addVariable(
                LogicalNode.STREET_LIGHT_CONFIGURATION,
                DataAttribute.SOFTWARE_CONFIGURATION,
                Fc.CF,
                SubDataAttribute.LIGHT_TYPE,
                lightTypeValue);

            Iec61850GetStatusCommand.this.loggingService.logMessage(
                deviceMessageLog,
                deviceConnection.getDeviceIdentification(),
                deviceConnection.getOrganisationIdentification(),
                false);

            /*
             * The preferredLinkType and actualLinkType are hard-coded to
             * LinkTypeDto.ETHERNET, other link types do not apply to the
             * device type in use.
             */
            return new DeviceStatusDto(
                lightValues,
                LinkTypeDto.ETHERNET,
                LinkTypeDto.ETHERNET,
                lightType,
                eventNotificationsMask);
          }
        };

    return iec61850Client.sendCommandWithRetry(
        function, "GetStatus", deviceConnection.getDeviceIdentification());
  }
}
