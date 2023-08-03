// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.Fc;
import java.time.ZonedDateTime;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DaylightSavingTimeTransition;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeNotFoundException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeReadException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeWriteException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.services.DeviceMessageLoggingService;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.DeviceFixedIpDto;
import org.opensmartgridplatform.dto.valueobjects.RelayMapDto;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850SetConfigurationFunction implements Function<Void> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850SetConfigurationFunction.class);

  private static final int SWITCH_TYPE_TARIFF = 0;
  private static final int SWITCH_TYPE_LIGHT = 1;

  private final Iec61850Client iec61850Client;
  private final DeviceConnection deviceConnection;
  private final ConfigurationDto configuration;
  private final DeviceMessageLoggingService loggingService;

  public Iec61850SetConfigurationFunction(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final ConfigurationDto configuration,
      final DeviceMessageLoggingService loggingService) {
    this.iec61850Client = iec61850Client;
    this.deviceConnection = deviceConnection;
    this.configuration = configuration;
    this.loggingService = loggingService;
  }

  @Override
  public Void apply(final DeviceMessageLog deviceMessageLog) throws ProtocolAdapterException {

    this.setRelayConfiguration(
        this.iec61850Client, this.deviceConnection, this.configuration, deviceMessageLog);

    this.setOsgpIpAddressAndPort(
        this.iec61850Client, this.deviceConnection, this.configuration, deviceMessageLog);

    this.setAstronomicalOffsets(
        this.iec61850Client, this.deviceConnection, this.configuration, deviceMessageLog);

    this.setClockConfigurationIfChanged(
        this.iec61850Client, this.deviceConnection, this.configuration, deviceMessageLog);

    this.setDhcpConfiguration(
        this.iec61850Client, this.deviceConnection, this.configuration, deviceMessageLog);

    this.loggingService.logMessage(
        deviceMessageLog,
        this.deviceConnection.getDeviceIdentification(),
        this.deviceConnection.getOrganisationIdentification(),
        false);

    return null;
  }

  private void setRelayConfiguration(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final ConfigurationDto configuration,
      final DeviceMessageLog deviceMessageLog)
      throws NodeException {
    if (configuration.getRelayConfiguration() != null
        && configuration.getRelayConfiguration().getRelayMap() != null) {

      final List<RelayMapDto> relayMaps = configuration.getRelayConfiguration().getRelayMap();
      for (final RelayMapDto relayMap : relayMaps) {
        final Integer internalIndex = relayMap.getAddress();
        final RelayTypeDto relayType = relayMap.getRelayType();

        final LogicalNode logicalNode = LogicalNode.getSwitchComponentByIndex(internalIndex);
        final NodeContainer switchType =
            deviceConnection.getFcModelNode(
                LogicalDevice.LIGHTING, logicalNode, DataAttribute.SWITCH_TYPE, Fc.CO);
        iec61850Client.readNodeDataValues(
            deviceConnection.getConnection().getClientAssociation(), switchType.getFcmodelNode());

        final NodeContainer operation = switchType.getChild(SubDataAttribute.OPERATION);
        iec61850Client.readNodeDataValues(
            deviceConnection.getConnection().getClientAssociation(), operation.getFcmodelNode());
        final BdaInt8 ctlVal = operation.getByte(SubDataAttribute.CONTROL_VALUE);

        final byte switchTypeValue =
            (byte) (RelayTypeDto.LIGHT.equals(relayType) ? SWITCH_TYPE_LIGHT : SWITCH_TYPE_TARIFF);
        LOGGER.info(
            "Updating Switch for internal index {} to {} ({})",
            internalIndex,
            switchTypeValue,
            relayType);

        ctlVal.setValue(switchTypeValue);
        operation.write();

        deviceMessageLog.addVariable(
            logicalNode,
            DataAttribute.SWITCH_TYPE,
            Fc.CO,
            SubDataAttribute.OPERATION,
            SubDataAttribute.CONTROL_VALUE,
            Byte.toString(switchTypeValue));
      }
    }
  }

  private void setOsgpIpAddressAndPort(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final ConfigurationDto configuration,
      final DeviceMessageLog deviceMessageLog)
      throws NodeException {
    // Checking to see if all register values are null, so that we
    // don't read the values for no reason.
    if (!(configuration.getOsgpIpAddres() == null && configuration.getOsgpPortNumber() == null)) {

      final NodeContainer registration =
          deviceConnection.getFcModelNode(
              LogicalDevice.LIGHTING,
              LogicalNode.STREET_LIGHT_CONFIGURATION,
              DataAttribute.REGISTRATION,
              Fc.CF);
      iec61850Client.readNodeDataValues(
          deviceConnection.getConnection().getClientAssociation(), registration.getFcmodelNode());

      if (configuration.getOsgpIpAddres() != null) {
        LOGGER.info("Updating OspgIpAddress to {}", configuration.getOsgpIpAddres());
        registration.writeString(SubDataAttribute.SERVER_ADDRESS, configuration.getOsgpIpAddres());

        deviceMessageLog.addVariable(
            LogicalNode.STREET_LIGHT_CONFIGURATION,
            DataAttribute.REGISTRATION,
            Fc.CF,
            SubDataAttribute.SERVER_ADDRESS,
            configuration.getOsgpIpAddres());
      }

      if (configuration.getOsgpPortNumber() != null) {
        LOGGER.info("Updating OsgpPortNumber to {}", configuration.getOsgpPortNumber());
        registration.writeInteger(SubDataAttribute.SERVER_PORT, configuration.getOsgpPortNumber());

        deviceMessageLog.addVariable(
            LogicalNode.STREET_LIGHT_CONFIGURATION,
            DataAttribute.REGISTRATION,
            Fc.CF,
            SubDataAttribute.SERVER_PORT,
            configuration.getOsgpPortNumber().toString());
      }
    }
  }

  private void setAstronomicalOffsets(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final ConfigurationDto configuration,
      final DeviceMessageLog deviceMessageLog)
      throws NodeException {
    // Checking to see if all software configuration values are
    // null, so that we don't read the values for no reason.
    if (!(configuration.getAstroGateSunRiseOffset() == null
        && configuration.getAstroGateSunSetOffset() == null
        && configuration.getLightType() == null)) {

      final NodeContainer softwareConfiguration =
          deviceConnection.getFcModelNode(
              LogicalDevice.LIGHTING,
              LogicalNode.STREET_LIGHT_CONFIGURATION,
              DataAttribute.SOFTWARE_CONFIGURATION,
              Fc.CF);
      iec61850Client.readNodeDataValues(
          deviceConnection.getConnection().getClientAssociation(),
          softwareConfiguration.getFcmodelNode());

      if (configuration.getAstroGateSunRiseOffset() != null) {
        LOGGER.info(
            "Updating AstroGateSunRiseOffset to {}", configuration.getAstroGateSunRiseOffset());
        softwareConfiguration.writeShort(
            SubDataAttribute.ASTRONOMIC_SUNRISE_OFFSET,
            configuration.getAstroGateSunRiseOffset().shortValue());

        deviceMessageLog.addVariable(
            LogicalNode.STREET_LIGHT_CONFIGURATION,
            DataAttribute.SOFTWARE_CONFIGURATION,
            Fc.CF,
            SubDataAttribute.ASTRONOMIC_SUNRISE_OFFSET,
            Short.toString(configuration.getAstroGateSunRiseOffset().shortValue()));
      }

      if (configuration.getAstroGateSunSetOffset() != null) {
        LOGGER.info(
            "Updating AstroGateSunSetOffset to {}", configuration.getAstroGateSunSetOffset());
        softwareConfiguration.writeShort(
            SubDataAttribute.ASTRONOMIC_SUNSET_OFFSET,
            configuration.getAstroGateSunSetOffset().shortValue());

        deviceMessageLog.addVariable(
            LogicalNode.STREET_LIGHT_CONFIGURATION,
            DataAttribute.SOFTWARE_CONFIGURATION,
            Fc.CF,
            SubDataAttribute.ASTRONOMIC_SUNSET_OFFSET,
            Short.toString(configuration.getAstroGateSunSetOffset().shortValue()));
      }

      if (configuration.getLightType() != null) {
        LOGGER.info("Updating LightType to {}", configuration.getLightType());
        softwareConfiguration.writeString(
            SubDataAttribute.LIGHT_TYPE, configuration.getLightType().name());

        deviceMessageLog.addVariable(
            LogicalNode.STREET_LIGHT_CONFIGURATION,
            DataAttribute.SOFTWARE_CONFIGURATION,
            Fc.CF,
            SubDataAttribute.LIGHT_TYPE,
            configuration.getLightType().name());
      }
    }
  }

  private boolean isClockConfigurationPresent(final ConfigurationDto configuration) {
    return configuration.getTimeSyncFrequency() != null
        || configuration.isAutomaticSummerTimingEnabled() != null
        || configuration.getSummerTimeDetails() != null
            && configuration.getWinterTimeDetails() != null;
  }

  private boolean isNtpConfigurationPresent(final ConfigurationDto configuration) {
    return configuration.getNtpEnabled() != null && configuration.getNtpHost() != null
        || configuration.getNtpSyncInterval() != null;
  }

  private void setClockConfigurationIfChanged(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final ConfigurationDto configuration,
      final DeviceMessageLog deviceMessageLog)
      throws NodeException {

    final boolean clockConfigurationChanged =
        this.isClockConfigurationPresent(configuration)
            && this.isNtpConfigurationPresent(configuration);

    if (clockConfigurationChanged) {
      this.setClockConfiguration(iec61850Client, deviceConnection, configuration, deviceMessageLog);
    }
  }

  private void setClockConfiguration(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final ConfigurationDto configuration,
      final DeviceMessageLog deviceMessageLog)
      throws NodeNotFoundException, NodeReadException, NodeWriteException {
    final NodeContainer clock =
        deviceConnection.getFcModelNode(
            LogicalDevice.LIGHTING,
            LogicalNode.STREET_LIGHT_CONFIGURATION,
            DataAttribute.CLOCK,
            Fc.CF);
    iec61850Client.readNodeDataValues(
        deviceConnection.getConnection().getClientAssociation(), clock.getFcmodelNode());

    if (configuration.getTimeSyncFrequency() != null) {
      LOGGER.info("Updating TimeSyncFrequency to {}", configuration.getTimeSyncFrequency());
      clock.writeUnsignedShort(
          SubDataAttribute.TIME_SYNC_FREQUENCY, configuration.getTimeSyncFrequency());

      deviceMessageLog.addVariable(
          LogicalNode.STREET_LIGHT_CONFIGURATION,
          DataAttribute.CLOCK,
          Fc.CF,
          SubDataAttribute.TIME_SYNC_FREQUENCY,
          Integer.toString(configuration.getTimeSyncFrequency()));
    }

    if (configuration.isAutomaticSummerTimingEnabled() != null) {
      LOGGER.info(
          "Updating AutomaticSummerTimingEnabled to {}",
          configuration.isAutomaticSummerTimingEnabled());
      clock.writeBoolean(
          SubDataAttribute.AUTOMATIC_SUMMER_TIMING_ENABLED,
          configuration.isAutomaticSummerTimingEnabled());

      deviceMessageLog.addVariable(
          LogicalNode.STREET_LIGHT_CONFIGURATION,
          DataAttribute.CLOCK,
          Fc.CF,
          SubDataAttribute.AUTOMATIC_SUMMER_TIMING_ENABLED,
          Boolean.toString(configuration.isAutomaticSummerTimingEnabled()));
    }

    /*
     * Perform some effort to create dstBegT/dstEndt information
     * based on provided DateTime values. This will work in a
     * number of cases, but to be able to do this accurately in
     * an international context, DST transition times will
     * probably have to be based on information about the
     * time-zone the device is operating in, instead of a
     * particular DateTime provided by the caller without
     * further information.
     */
    final DaylightSavingTimeTransition.DstTransitionFormat dstFormatMwd =
        DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH;
    final ZonedDateTime summerTimeDetails = configuration.getSummerTimeDetails();
    final ZonedDateTime winterTimeDetails = configuration.getWinterTimeDetails();
    if (summerTimeDetails != null) {

      final String mwdValueForBeginOfDst =
          DaylightSavingTimeTransition.forDateTimeAccordingToFormat(summerTimeDetails, dstFormatMwd)
              .getTransition();
      LOGGER.info(
          "Updating DstBeginTime to {} based on SummerTimeDetails {}",
          mwdValueForBeginOfDst,
          summerTimeDetails);
      clock.writeString(SubDataAttribute.SUMMER_TIME_DETAILS, mwdValueForBeginOfDst);

      deviceMessageLog.addVariable(
          LogicalNode.STREET_LIGHT_CONFIGURATION,
          DataAttribute.CLOCK,
          Fc.CF,
          SubDataAttribute.SUMMER_TIME_DETAILS,
          mwdValueForBeginOfDst);
    }
    if (winterTimeDetails != null) {

      final String mwdValueForEndOfDst =
          DaylightSavingTimeTransition.forDateTimeAccordingToFormat(winterTimeDetails, dstFormatMwd)
              .getTransition();
      LOGGER.info(
          "Updating DstEndTime to {} based on WinterTimeDetails {}",
          mwdValueForEndOfDst,
          winterTimeDetails);
      clock.writeString(SubDataAttribute.WINTER_TIME_DETAILS, mwdValueForEndOfDst);

      deviceMessageLog.addVariable(
          LogicalNode.STREET_LIGHT_CONFIGURATION,
          DataAttribute.CLOCK,
          Fc.CF,
          SubDataAttribute.WINTER_TIME_DETAILS,
          mwdValueForEndOfDst);
    }
    if (configuration.getNtpEnabled() != null) {
      LOGGER.info("Updating ntpEnabled to {}", configuration.getNtpEnabled());

      clock.writeBoolean(SubDataAttribute.NTP_ENABLED, configuration.getNtpEnabled());

      deviceMessageLog.addVariable(
          LogicalNode.STREET_LIGHT_CONFIGURATION,
          DataAttribute.CLOCK,
          Fc.CF,
          SubDataAttribute.NTP_ENABLED,
          Boolean.toString(configuration.getNtpEnabled()));
    }

    if (configuration.getNtpHost() != null) {
      LOGGER.info("Updating ntpHost to {}", configuration.getNtpHost());

      clock.writeString(SubDataAttribute.NTP_HOST, configuration.getNtpHost());

      deviceMessageLog.addVariable(
          LogicalNode.STREET_LIGHT_CONFIGURATION,
          DataAttribute.CLOCK,
          Fc.CF,
          SubDataAttribute.NTP_HOST,
          configuration.getNtpHost());
    }

    if (configuration.getNtpSyncInterval() != null) {
      LOGGER.info("Updating ntpSyncInterval to {}", configuration.getNtpSyncInterval());

      clock.writeUnsignedShort(
          SubDataAttribute.NTP_SYNC_INTERVAL, configuration.getNtpSyncInterval());

      deviceMessageLog.addVariable(
          LogicalNode.STREET_LIGHT_CONFIGURATION,
          DataAttribute.CLOCK,
          Fc.CF,
          SubDataAttribute.NTP_SYNC_INTERVAL,
          configuration.getNtpSyncInterval().toString());
    }
  }

  private void setDhcpConfiguration(
      final Iec61850Client iec61850Client,
      final DeviceConnection deviceConnection,
      final ConfigurationDto configuration,
      final DeviceMessageLog deviceMessageLog)
      throws NodeException {
    // Checking to see if all network values are null, so that we
    // don't read the values for no reason.
    if (!(configuration.isDhcpEnabled() == null && configuration.getDeviceFixedIp() == null)) {

      final NodeContainer ipConfiguration =
          deviceConnection.getFcModelNode(
              LogicalDevice.LIGHTING,
              LogicalNode.STREET_LIGHT_CONFIGURATION,
              DataAttribute.IP_CONFIGURATION,
              Fc.CF);
      iec61850Client.readNodeDataValues(
          deviceConnection.getConnection().getClientAssociation(),
          ipConfiguration.getFcmodelNode());

      if (configuration.isDhcpEnabled() != null) {
        LOGGER.info("Updating DhcpEnabled to {}", configuration.isDhcpEnabled());
        ipConfiguration.writeBoolean(SubDataAttribute.ENABLE_DHCP, configuration.isDhcpEnabled());

        deviceMessageLog.addVariable(
            LogicalNode.STREET_LIGHT_CONFIGURATION,
            DataAttribute.IP_CONFIGURATION,
            Fc.CF,
            SubDataAttribute.ENABLE_DHCP,
            Boolean.toString(configuration.isDhcpEnabled()));
      }

      // All values in DeviceFixedIpDto are non-nullable, so no
      // null-checks are needed.
      final DeviceFixedIpDto deviceFixedIp = configuration.getDeviceFixedIp();

      LOGGER.info(
          "Updating deviceFixedIpAddress to {}", configuration.getDeviceFixedIp().getIpAddress());
      ipConfiguration.writeString(SubDataAttribute.IP_ADDRESS, deviceFixedIp.getIpAddress());

      deviceMessageLog.addVariable(
          LogicalNode.STREET_LIGHT_CONFIGURATION,
          DataAttribute.IP_CONFIGURATION,
          Fc.CF,
          SubDataAttribute.IP_ADDRESS,
          deviceFixedIp.getIpAddress());

      LOGGER.info(
          "Updating deviceFixedIpNetmask to {}", configuration.getDeviceFixedIp().getNetMask());
      ipConfiguration.writeString(SubDataAttribute.NETMASK, deviceFixedIp.getNetMask());

      deviceMessageLog.addVariable(
          LogicalNode.STREET_LIGHT_CONFIGURATION,
          DataAttribute.IP_CONFIGURATION,
          Fc.CF,
          SubDataAttribute.NETMASK,
          deviceFixedIp.getNetMask());

      LOGGER.info(
          "Updating deviceFixIpGateway to {}", configuration.getDeviceFixedIp().getGateWay());
      ipConfiguration.writeString(SubDataAttribute.GATEWAY, deviceFixedIp.getGateWay());

      deviceMessageLog.addVariable(
          LogicalNode.STREET_LIGHT_CONFIGURATION,
          DataAttribute.IP_CONFIGURATION,
          Fc.CF,
          SubDataAttribute.GATEWAY,
          deviceFixedIp.getGateWay());
    }
  }
}
