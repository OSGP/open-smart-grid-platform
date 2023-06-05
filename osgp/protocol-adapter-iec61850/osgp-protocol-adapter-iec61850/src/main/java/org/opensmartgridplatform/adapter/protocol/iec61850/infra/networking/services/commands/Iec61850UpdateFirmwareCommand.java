// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands;

import com.beanit.openiec61850.Fc;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
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
import org.opensmartgridplatform.dto.valueobjects.FirmwareModuleData;
import org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850UpdateFirmwareCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850UpdateFirmwareCommand.class);

  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  private final DeviceMessageLoggingService loggingService;

  public Iec61850UpdateFirmwareCommand(final DeviceMessageLoggingService loggingService) {
    this.loggingService = loggingService;
  }

  public void pushFirmwareToDevice(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final String fullUrl,
      final FirmwareModuleData firmwareModuleData)
      throws ProtocolAdapterException {
    final Function<Void> function =
        new Function<Void>() {

          @Override
          public Void apply(final DeviceMessageLog deviceMessageLog)
              throws ProtocolAdapterException {
            final int count = firmwareModuleData.countNumberOfModules();
            if (count != 1) {
              throw new ProtocolAdapterException(
                  String.format("Number of firmware modules is not equal to 1 but %d", count));
            }

            // Check if the functional or security firmware needs to be
            // updated.
            if (FirmwareModuleType.FUNCTIONAL
                .name()
                .equalsIgnoreCase(firmwareModuleData.getModuleVersionFunc())) {
              Iec61850UpdateFirmwareCommand.this.updateFunctionalFirmware(
                  iec61850Client, deviceConnection, fullUrl, deviceMessageLog);
            } else if (FirmwareModuleType.SECURITY
                .name()
                .equalsIgnoreCase(firmwareModuleData.getModuleVersionSec())) {
              Iec61850UpdateFirmwareCommand.this.updateSecurityFirmware(
                  iec61850Client, deviceConnection, fullUrl, deviceMessageLog);
            } else {
              throw new ProtocolAdapterException(
                  String.format(
                      "Unsupported firmwareModuleData (only functional and security are allowed): communication: %s, functional: %s, module-active: %s, m-bus: %s, security: %s, fullUrl: %s",
                      firmwareModuleData.getModuleVersionComm(),
                      firmwareModuleData.getModuleVersionFunc(),
                      firmwareModuleData.getModuleVersionMa(),
                      firmwareModuleData.getModuleVersionMbus(),
                      firmwareModuleData.getModuleVersionSec(),
                      fullUrl));
            }

            Iec61850UpdateFirmwareCommand.this.loggingService.logMessage(
                deviceMessageLog,
                deviceConnection.getDeviceIdentification(),
                deviceConnection.getOrganisationIdentification(),
                false);

            return null;
          }
        };

    iec61850Client.sendCommandWithRetry(
        function, "UpdateFirmware", deviceConnection.getDeviceIdentification());
  }

  private void updateFunctionalFirmware(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final String fullUrl,
      final DeviceMessageLog deviceMessageLog)
      throws NodeException {
    LOGGER.info(
        "Reading the functional firmware node for device: {}",
        deviceConnection.getDeviceIdentification());
    final NodeContainer functionalFirmwareNode =
        deviceConnection.getFcModelNode(
            LogicalDevice.LIGHTING,
            LogicalNode.STREET_LIGHT_CONFIGURATION,
            DataAttribute.FUNCTIONAL_FIRMWARE,
            Fc.CF);
    iec61850Client.readNodeDataValues(
        deviceConnection.getConnection().getClientAssociation(),
        functionalFirmwareNode.getFcmodelNode());

    final String currentFunctionalFirmwareDownloadUrl =
        functionalFirmwareNode.getString(SubDataAttribute.URL);
    final Date currentFunctionalFirmwareUpdateDateTime =
        functionalFirmwareNode.getDate(SubDataAttribute.START_TIME);
    LOGGER.info(
        "Current functional firmware download url: {}, start time: {} for device: {}",
        currentFunctionalFirmwareDownloadUrl,
        currentFunctionalFirmwareUpdateDateTime,
        deviceConnection.getDeviceIdentification());

    LOGGER.info(
        "Updating the functional firmware download url to: {} for device: {}",
        fullUrl,
        deviceConnection.getDeviceIdentification());
    functionalFirmwareNode.writeString(SubDataAttribute.URL, fullUrl);

    deviceMessageLog.addVariable(
        LogicalNode.STREET_LIGHT_CONFIGURATION,
        DataAttribute.FUNCTIONAL_FIRMWARE,
        Fc.CF,
        SubDataAttribute.URL,
        fullUrl);

    final Date oneMinuteFromNow =
        this.determineFirmwareUpdateDateTime(iec61850Client, deviceConnection);
    LOGGER.info(
        "Updating the functional firmware download start time to: {} for device: {}",
        oneMinuteFromNow,
        deviceConnection.getDeviceIdentification());
    functionalFirmwareNode.writeDate(SubDataAttribute.START_TIME, oneMinuteFromNow);

    deviceMessageLog.addVariable(
        LogicalNode.STREET_LIGHT_CONFIGURATION,
        DataAttribute.FUNCTIONAL_FIRMWARE,
        Fc.CF,
        SubDataAttribute.START_TIME,
        this.simpleDateFormat.format(oneMinuteFromNow));
  }

  private void updateSecurityFirmware(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final String fullUrl,
      final DeviceMessageLog deviceMessageLog)
      throws NodeException {
    LOGGER.info(
        "Reading the security firmware node for device: {}",
        deviceConnection.getDeviceIdentification());
    final NodeContainer securityFirmwareNode =
        deviceConnection.getFcModelNode(
            LogicalDevice.LIGHTING,
            LogicalNode.STREET_LIGHT_CONFIGURATION,
            DataAttribute.SECURITY_FIRMWARE,
            Fc.CF);
    iec61850Client.readNodeDataValues(
        deviceConnection.getConnection().getClientAssociation(),
        securityFirmwareNode.getFcmodelNode());

    final String currentSecurityFirmwareDownloadUrl =
        securityFirmwareNode.getString(SubDataAttribute.URL);
    final Date currentSecurityFirmwareUpdateDateTime =
        securityFirmwareNode.getDate(SubDataAttribute.START_TIME);
    LOGGER.info(
        "Current security firmware download url: {}, start time: {} for device: {}",
        currentSecurityFirmwareDownloadUrl,
        currentSecurityFirmwareUpdateDateTime,
        deviceConnection.getDeviceIdentification());

    LOGGER.info(
        "Updating the security firmware download url to : {} for device: {}",
        fullUrl,
        deviceConnection.getDeviceIdentification());
    securityFirmwareNode.writeString(SubDataAttribute.URL, fullUrl);

    deviceMessageLog.addVariable(
        LogicalNode.STREET_LIGHT_CONFIGURATION,
        DataAttribute.SECURITY_FIRMWARE,
        Fc.CF,
        SubDataAttribute.URL,
        fullUrl);

    final Date oneMinuteFromNow =
        this.determineFirmwareUpdateDateTime(iec61850Client, deviceConnection);
    LOGGER.info(
        "Updating the security firmware download start time to: {} for device: {}",
        oneMinuteFromNow,
        deviceConnection.getDeviceIdentification());
    securityFirmwareNode.writeDate(SubDataAttribute.START_TIME, oneMinuteFromNow);

    deviceMessageLog.addVariable(
        LogicalNode.STREET_LIGHT_CONFIGURATION,
        DataAttribute.SECURITY_FIRMWARE,
        Fc.CF,
        SubDataAttribute.START_TIME,
        this.simpleDateFormat.format(oneMinuteFromNow));
  }

  private Date determineFirmwareUpdateDateTime(
      final Iec61850Client iec61850Client, final DeviceConnection deviceConnection)
      throws NodeException {
    final NodeContainer clock =
        deviceConnection.getFcModelNode(
            LogicalDevice.LIGHTING,
            LogicalNode.STREET_LIGHT_CONFIGURATION,
            DataAttribute.CLOCK,
            Fc.CF);
    iec61850Client.readNodeDataValues(
        deviceConnection.getConnection().getClientAssociation(), clock.getFcmodelNode());

    final DateTime deviceTime = new DateTime(clock.getDate(SubDataAttribute.CURRENT_TIME));
    // Creating a DateTime one minute from now.
    return deviceTime.plusMinutes(1).toDate();
  }
}
