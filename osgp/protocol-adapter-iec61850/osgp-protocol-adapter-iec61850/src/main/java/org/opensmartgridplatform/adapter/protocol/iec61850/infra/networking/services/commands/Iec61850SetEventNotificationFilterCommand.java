// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.Fc;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850SetEventNotificationFilterCommand {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850SetEventNotificationFilterCommand.class);

  private DeviceMessageLoggingService loggingService;

  public Iec61850SetEventNotificationFilterCommand(
      final DeviceMessageLoggingService loggingService) {
    this.loggingService = loggingService;
  }

  public void setEventNotificationFilterOnDevice(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final String filter)
      throws ProtocolAdapterException {
    final Function<Void> function =
        new Function<Void>() {

          @Override
          public Void apply(final DeviceMessageLog deviceMessageLog)
              throws ProtocolAdapterException {

            LOGGER.info("Setting the event notification filter");

            final NodeContainer eventBufferConfiguration =
                deviceConnection.getFcModelNode(
                    LogicalDevice.LIGHTING,
                    LogicalNode.STREET_LIGHT_CONFIGURATION,
                    DataAttribute.EVENT_BUFFER,
                    Fc.CF);
            iec61850Client.readNodeDataValues(
                deviceConnection.getConnection().getClientAssociation(),
                eventBufferConfiguration.getFcmodelNode());

            LOGGER.info("Updating the enabled EventType filter to {}", filter);
            eventBufferConfiguration.writeString(SubDataAttribute.EVENT_BUFFER_FILTER, filter);

            deviceMessageLog.addVariable(
                LogicalNode.STREET_LIGHT_CONFIGURATION,
                DataAttribute.EVENT_BUFFER,
                Fc.CF,
                SubDataAttribute.EVENT_BUFFER_FILTER,
                filter);

            Iec61850SetEventNotificationFilterCommand.this.loggingService.logMessage(
                deviceMessageLog,
                deviceConnection.getDeviceIdentification(),
                deviceConnection.getOrganisationIdentification(),
                false);

            return null;
          }
        };

    iec61850Client.sendCommandWithRetry(
        function, "SetEventNoficationFilter", deviceConnection.getDeviceIdentification());
  }
}
