// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.Fc;
import java.time.ZonedDateTime;
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
import org.opensmartgridplatform.dto.valueobjects.TransitionMessageDataContainerDto;
import org.opensmartgridplatform.dto.valueobjects.TransitionTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850TransitionCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850TransitionCommand.class);

  private final DeviceMessageLoggingService loggingService;

  public Iec61850TransitionCommand(final DeviceMessageLoggingService loggingService) {
    this.loggingService = loggingService;
  }

  public void transitionDevice(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final TransitionMessageDataContainerDto transitionMessageDataContainer)
      throws ProtocolAdapterException {
    final TransitionTypeDto transitionType = transitionMessageDataContainer.getTransitionType();
    LOGGER.info(
        "device: {}, transition: {}", deviceConnection.getDeviceIdentification(), transitionType);
    final boolean controlValueForTransition = transitionType.equals(TransitionTypeDto.DAY_NIGHT);

    final ZonedDateTime dateTime = transitionMessageDataContainer.getDateTime();
    if (dateTime != null) {
      LOGGER.warn(
          "device: {}, setting date/time {} for transition {} not supported",
          deviceConnection.getDeviceIdentification(),
          dateTime,
          transitionType);
    }

    final Function<Void> function =
        new Function<Void>() {
          @Override
          public Void apply(final DeviceMessageLog deviceMessageLog)
              throws ProtocolAdapterException {

            final NodeContainer sensorNode =
                deviceConnection.getFcModelNode(
                    LogicalDevice.LIGHTING,
                    LogicalNode.STREET_LIGHT_CONFIGURATION,
                    DataAttribute.SENSOR,
                    Fc.CO);
            iec61850Client.readNodeDataValues(
                deviceConnection.getConnection().getClientAssociation(),
                sensorNode.getFcmodelNode());
            LOGGER.info(
                "device: {}, sensorNode: {}",
                deviceConnection.getDeviceIdentification(),
                sensorNode);

            final NodeContainer oper = sensorNode.getChild(SubDataAttribute.OPERATION);
            LOGGER.info("device: {}, oper: {}", deviceConnection.getDeviceIdentification(), oper);

            final BdaBoolean ctlVal = oper.getBoolean(SubDataAttribute.CONTROL_VALUE);
            LOGGER.info(
                "device: {}, ctlVal: {}", deviceConnection.getDeviceIdentification(), ctlVal);

            ctlVal.setValue(controlValueForTransition);
            LOGGER.info(
                "device: {}, set ctlVal to {} in order to transition the device",
                deviceConnection.getDeviceIdentification(),
                controlValueForTransition);
            oper.write();

            deviceMessageLog.addVariable(
                LogicalNode.STREET_LIGHT_CONFIGURATION,
                DataAttribute.SENSOR,
                Fc.CO,
                SubDataAttribute.OPERATION,
                SubDataAttribute.CONTROL_VALUE,
                Boolean.toString(controlValueForTransition));

            Iec61850TransitionCommand.this.loggingService.logMessage(
                deviceMessageLog,
                deviceConnection.getDeviceIdentification(),
                deviceConnection.getOrganisationIdentification(),
                false);

            return null;
          }
        };

    iec61850Client.sendCommandWithRetry(
        function, "SetTransition", deviceConnection.getDeviceIdentification());
  }
}
