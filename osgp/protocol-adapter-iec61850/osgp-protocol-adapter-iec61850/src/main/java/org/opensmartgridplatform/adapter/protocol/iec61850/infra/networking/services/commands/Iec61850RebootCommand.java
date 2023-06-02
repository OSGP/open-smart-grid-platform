//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.BdaBoolean;
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

public class Iec61850RebootCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850RebootCommand.class);

  private DeviceMessageLoggingService loggingService;

  public Iec61850RebootCommand(final DeviceMessageLoggingService loggingService) {
    this.loggingService = loggingService;
  }

  public void rebootDevice(
      final Iec61850Client iec61850Client, final DeviceConnection deviceConnection)
      throws ProtocolAdapterException {
    final Function<Void> function =
        new Function<Void>() {

          @Override
          public Void apply(final DeviceMessageLog deviceMessageLog)
              throws ProtocolAdapterException {
            final NodeContainer rebootOperationNode =
                deviceConnection.getFcModelNode(
                    LogicalDevice.LIGHTING,
                    LogicalNode.STREET_LIGHT_CONFIGURATION,
                    DataAttribute.REBOOT_OPERATION,
                    Fc.CO);
            iec61850Client.readNodeDataValues(
                deviceConnection.getConnection().getClientAssociation(),
                rebootOperationNode.getFcmodelNode());
            LOGGER.info(
                "device: {}, rebootOperationNode: {}",
                deviceConnection.getDeviceIdentification(),
                rebootOperationNode);

            final NodeContainer oper = rebootOperationNode.getChild(SubDataAttribute.OPERATION);
            iec61850Client.readNodeDataValues(
                deviceConnection.getConnection().getClientAssociation(), oper.getFcmodelNode());
            LOGGER.info("device: {}, oper: {}", deviceConnection.getDeviceIdentification(), oper);

            final BdaBoolean ctlVal = oper.getBoolean(SubDataAttribute.CONTROL_VALUE);
            LOGGER.info(
                "device: {}, ctlVal: {}", deviceConnection.getDeviceIdentification(), ctlVal);

            ctlVal.setValue(true);
            LOGGER.info(
                "device: {}, set ctlVal to true in order to reboot the device",
                deviceConnection.getDeviceIdentification());
            oper.write();

            deviceMessageLog.addVariable(
                LogicalNode.STREET_LIGHT_CONFIGURATION,
                DataAttribute.REBOOT_OPERATION,
                Fc.ST,
                SubDataAttribute.OPERATION,
                SubDataAttribute.CONTROL_VALUE,
                Boolean.toString(true));

            Iec61850RebootCommand.this.loggingService.logMessage(
                deviceMessageLog,
                deviceConnection.getDeviceIdentification(),
                deviceConnection.getOrganisationIdentification(),
                false);

            return null;
          }
        };

    iec61850Client.sendCommandWithRetry(
        function, "Reboot", deviceConnection.getDeviceIdentification());
  }
}
