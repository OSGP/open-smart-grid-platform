//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.Fc;
import java.util.ArrayList;
import java.util.List;
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
import org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850GetFirmwareVersionCommand {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850GetFirmwareVersionCommand.class);

  private DeviceMessageLoggingService loggingService;

  public Iec61850GetFirmwareVersionCommand(final DeviceMessageLoggingService loggingService) {
    this.loggingService = loggingService;
  }

  public List<FirmwareVersionDto> getFirmwareVersionFromDevice(
      final Iec61850Client iec61850Client, final DeviceConnection deviceConnection)
      throws ProtocolAdapterException {
    final Function<List<FirmwareVersionDto>> function =
        new Function<List<FirmwareVersionDto>>() {

          @Override
          public List<FirmwareVersionDto> apply(final DeviceMessageLog deviceMessageLog)
              throws ProtocolAdapterException {
            final List<FirmwareVersionDto> output = new ArrayList<>();

            // Getting the functional firmware version
            LOGGER.info("Reading the functional firmware version");
            final NodeContainer functionalFirmwareNode =
                deviceConnection.getFcModelNode(
                    LogicalDevice.LIGHTING,
                    LogicalNode.STREET_LIGHT_CONFIGURATION,
                    DataAttribute.FUNCTIONAL_FIRMWARE,
                    Fc.ST);
            iec61850Client.readNodeDataValues(
                deviceConnection.getConnection().getClientAssociation(),
                functionalFirmwareNode.getFcmodelNode());
            final String functionalFirmwareVersion =
                functionalFirmwareNode.getString(SubDataAttribute.CURRENT_VERSION);

            // Adding it to the list
            output.add(
                new FirmwareVersionDto(FirmwareModuleType.FUNCTIONAL, functionalFirmwareVersion));

            deviceMessageLog.addVariable(
                LogicalNode.STREET_LIGHT_CONFIGURATION,
                DataAttribute.FUNCTIONAL_FIRMWARE,
                Fc.ST,
                SubDataAttribute.CURRENT_VERSION,
                functionalFirmwareVersion);

            // Getting the security firmware version
            LOGGER.info("Reading the security firmware version");
            final NodeContainer securityFirmwareNode =
                deviceConnection.getFcModelNode(
                    LogicalDevice.LIGHTING,
                    LogicalNode.STREET_LIGHT_CONFIGURATION,
                    DataAttribute.SECURITY_FIRMWARE,
                    Fc.ST);
            iec61850Client.readNodeDataValues(
                deviceConnection.getConnection().getClientAssociation(),
                securityFirmwareNode.getFcmodelNode());
            final String securityFirmwareVersion =
                securityFirmwareNode.getString(SubDataAttribute.CURRENT_VERSION);

            // Adding it to the list
            output.add(
                new FirmwareVersionDto(FirmwareModuleType.SECURITY, securityFirmwareVersion));

            deviceMessageLog.addVariable(
                LogicalNode.STREET_LIGHT_CONFIGURATION,
                DataAttribute.SECURITY_FIRMWARE,
                Fc.ST,
                SubDataAttribute.CURRENT_VERSION,
                securityFirmwareVersion);

            Iec61850GetFirmwareVersionCommand.this.loggingService.logMessage(
                deviceMessageLog,
                deviceConnection.getDeviceIdentification(),
                deviceConnection.getOrganisationIdentification(),
                false);

            return output;
          }
        };

    return iec61850Client.sendCommandWithRetry(
        function, "GetFirmwareVersion", deviceConnection.getDeviceIdentification());
  }
}
