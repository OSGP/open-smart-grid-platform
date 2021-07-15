/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.jms.JMSException;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.mapping.Iec61850Mapper;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.SsldDeviceService;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetConfigurationDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetEventNotificationsDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetLightDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetScheduleDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.SetTransitionDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.UpdateDeviceSslCertificationDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests.UpdateFirmwareDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.EmptyDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.GetConfigurationDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.GetFirmwareVersionDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.GetStatusDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.EventType;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ConnectionFailureException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.IED;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ClearReportCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850EnableReportingCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850GetConfigurationCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850GetFirmwareVersionCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850GetStatusCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850RebootCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850SetConfigurationCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850SetEventNotificationFilterCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850SetLightCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850SetScheduleCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850TransitionCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850UpdateFirmwareCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850UpdateSslCertificateCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.services.DeviceMessageLoggingService;
import org.opensmartgridplatform.core.db.api.iec61850.application.services.SsldDataService;
import org.opensmartgridplatform.core.db.api.iec61850.entities.DeviceOutputSetting;
import org.opensmartgridplatform.core.db.api.iec61850.entities.Ssld;
import org.opensmartgridplatform.core.db.api.iec61850valueobjects.RelayType;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.DeviceStatusDto;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationTypeDto;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.dto.valueobjects.LightValueDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Iec61850SsldDeviceService implements SsldDeviceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850SsldDeviceService.class);

  @Autowired private Iec61850DeviceConnectionService iec61850DeviceConnectionService;

  @Autowired
  @Qualifier(value = "protocolIec61850DeviceMessageLoggingService")
  private DeviceMessageLoggingService deviceMessageLoggingService;

  @Autowired private SsldDataService ssldDataService;

  @Autowired private Iec61850Client iec61850Client;

  @Autowired private Iec61850Mapper mapper;

  // Timeout between the setLight and getStatus during the device self-test
  @Autowired private int selftestTimeout;

  @Autowired private int disconnectDelay;

  @Autowired private Boolean isBufferedReportingEnabled;

  @Override
  public void getStatus(
      final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    DeviceConnection devCon = null;
    try {
      final DeviceConnection deviceConnection = this.connectToDevice(deviceRequest);
      devCon = deviceConnection;

      // Getting the SSLD for the device output-settings.
      final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());
      final DeviceStatusDto deviceStatus =
          new Iec61850GetStatusCommand(this.deviceMessageLoggingService)
              .getStatusFromDevice(this.iec61850Client, deviceConnection, ssld);

      final GetStatusDeviceResponse deviceResponse =
          new GetStatusDeviceResponse(deviceRequest, deviceStatus);

      deviceResponseHandler.handleResponse(deviceResponse);

      this.enableReporting(deviceConnection, deviceRequest);
    } catch (final ConnectionFailureException se) {
      this.handleConnectionFailureException(deviceRequest, deviceResponseHandler, se);
      this.iec61850DeviceConnectionService.disconnect(devCon, deviceRequest);
    } catch (final Exception e) {
      this.handleException(deviceRequest, deviceResponseHandler, e);
      this.iec61850DeviceConnectionService.disconnect(devCon, deviceRequest);
    }
  }

  @Override
  public void setLight(
      final SetLightDeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    DeviceConnection devCon = null;
    try {
      final DeviceConnection deviceConnection = this.connectToDevice(deviceRequest);
      devCon = deviceConnection;

      // Getting the SSLD for the device output-settings.
      final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());
      final List<DeviceOutputSetting> deviceOutputSettings =
          this.ssldDataService.findByRelayType(ssld, RelayType.LIGHT);
      final List<LightValueDto> lightValues =
          deviceRequest.getLightValuesContainer().getLightValues();
      List<LightValueDto> relaysWithInternalIdToSwitch;

      // Check if external index 0 is used.
      final LightValueDto index0LightValue = this.checkForIndex0(lightValues);
      if (index0LightValue != null) {
        // If external index 0 is used, create a list of all light
        // relays according to the device output settings.
        relaysWithInternalIdToSwitch =
            this.createListOfInternalIndicesToSwitch(deviceOutputSettings, index0LightValue.isOn());
      } else {
        // Else, create a list of internal indices based on the given
        // external indices in the light values list.
        relaysWithInternalIdToSwitch =
            this.createListOfInternalIndicesToSwitch(deviceOutputSettings, lightValues);
      }

      // Switch light relays based on internal indices.
      final Iec61850SetLightCommand iec61850SetLightCommand =
          new Iec61850SetLightCommand(this.deviceMessageLoggingService);
      iec61850SetLightCommand.switchLightRelays(
          this.iec61850Client, deviceConnection, relaysWithInternalIdToSwitch, null);

      this.createSuccessfulDefaultResponse(deviceRequest, deviceResponseHandler);

      this.enableReporting(deviceConnection, deviceRequest);
    } catch (final ConnectionFailureException se) {
      this.handleConnectionFailureException(deviceRequest, deviceResponseHandler, se);
      this.iec61850DeviceConnectionService.disconnect(devCon, deviceRequest);
    } catch (final Exception e) {
      this.handleException(deviceRequest, deviceResponseHandler, e);
      this.iec61850DeviceConnectionService.disconnect(devCon, deviceRequest);
    }
  }

  private LightValueDto checkForIndex0(final List<LightValueDto> lightValues) {
    for (final LightValueDto lightValue : lightValues) {
      if (lightValue == null) {
        break;
      }
      if (lightValue.getIndex() == null) {
        return lightValue;
      }
      if (lightValue.getIndex() == 0) {
        return lightValue;
      }
    }
    return null;
  }

  private List<LightValueDto> createListOfInternalIndicesToSwitch(
      final List<DeviceOutputSetting> deviceOutputSettings, final boolean on) {
    LOGGER.info("creating list of internal indices using device output settings");
    final List<LightValueDto> relaysWithInternalIdToSwitch = new ArrayList<>();
    for (final DeviceOutputSetting deviceOutputSetting : deviceOutputSettings) {
      if (RelayType.LIGHT.equals(deviceOutputSetting.getRelayType())) {
        final LightValueDto relayWithInternalIdToSwitch =
            new LightValueDto(deviceOutputSetting.getInternalId(), on, null);
        relaysWithInternalIdToSwitch.add(relayWithInternalIdToSwitch);
      }
    }
    return relaysWithInternalIdToSwitch;
  }

  private List<LightValueDto> createListOfInternalIndicesToSwitch(
      final List<DeviceOutputSetting> deviceOutputSettings, final List<LightValueDto> lightValues)
      throws FunctionalException {
    final List<LightValueDto> relaysWithInternalIdToSwitch = new ArrayList<>();
    LOGGER.info(
        "creating list of internal indices using device output settings and external indices from light values");
    for (final LightValueDto lightValue : lightValues) {
      if (lightValue == null) {
        break;
      }
      DeviceOutputSetting deviceOutputSettingForExternalId = null;
      for (final DeviceOutputSetting deviceOutputSetting : deviceOutputSettings) {
        if (deviceOutputSetting.getExternalId() == lightValue.getIndex()) {
          // You can only switch LIGHT relays that are used.
          this.checkRelay(
              deviceOutputSetting.getRelayType(),
              RelayType.LIGHT,
              deviceOutputSetting.getInternalId());
          deviceOutputSettingForExternalId = deviceOutputSetting;
        }
      }
      if (deviceOutputSettingForExternalId != null) {
        final LightValueDto relayWithInternalIdToSwitch =
            new LightValueDto(
                deviceOutputSettingForExternalId.getInternalId(),
                lightValue.isOn(),
                lightValue.getDimValue());
        relaysWithInternalIdToSwitch.add(relayWithInternalIdToSwitch);
      }
    }
    return relaysWithInternalIdToSwitch;
  }

  @Override
  public void setConfiguration(
      final SetConfigurationDeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    DeviceConnection deviceConnection = null;
    try {
      deviceConnection = this.connectToDevice(deviceRequest);
      final ConfigurationDto configuration = deviceRequest.getConfiguration();

      // Ignoring required, unused fields DALI-configuration and
      // preferredLinkType.
      new Iec61850SetConfigurationCommand(this.deviceMessageLoggingService)
          .setConfigurationOnDevice(this.iec61850Client, deviceConnection, configuration);

      this.createSuccessfulDefaultResponse(deviceRequest, deviceResponseHandler);
    } catch (final ConnectionFailureException se) {
      this.handleConnectionFailureException(deviceRequest, deviceResponseHandler, se);
    } catch (final Exception e) {
      this.handleException(deviceRequest, deviceResponseHandler, e);
    }
    this.iec61850DeviceConnectionService.disconnect(deviceConnection, deviceRequest);
  }

  @Override
  public void getConfiguration(
      final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    DeviceConnection deviceConnection = null;
    try {
      deviceConnection = this.connectToDevice(deviceRequest);

      // Getting the SSLD for the device output-settings.
      final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());

      final ConfigurationDto configuration =
          new Iec61850GetConfigurationCommand(this.deviceMessageLoggingService)
              .getConfigurationFromDevice(this.iec61850Client, deviceConnection, ssld, this.mapper);

      final GetConfigurationDeviceResponse response =
          new GetConfigurationDeviceResponse(deviceRequest, DeviceMessageStatus.OK, configuration);

      deviceResponseHandler.handleResponse(response);
    } catch (final ConnectionFailureException se) {
      this.handleConnectionFailureException(deviceRequest, deviceResponseHandler, se);
    } catch (final Exception e) {
      this.handleException(deviceRequest, deviceResponseHandler, e);
    }
    this.iec61850DeviceConnectionService.disconnect(deviceConnection, deviceRequest);
  }

  @Override
  public void setReboot(
      final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    DeviceConnection deviceConnection = null;
    try {
      deviceConnection = this.connectToDevice(deviceRequest);

      new Iec61850RebootCommand(this.deviceMessageLoggingService)
          .rebootDevice(this.iec61850Client, deviceConnection);

      this.createSuccessfulDefaultResponse(deviceRequest, deviceResponseHandler);
    } catch (final ConnectionFailureException se) {
      this.handleConnectionFailureException(deviceRequest, deviceResponseHandler, se);
    } catch (final Exception e) {
      this.handleException(deviceRequest, deviceResponseHandler, e);
    }
    this.iec61850DeviceConnectionService.disconnect(deviceConnection, deviceRequest);
  }

  @Override
  public void runSelfTest(
      final DeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler,
      final boolean startOfTest)
      throws JMSException {
    // Assuming all goes well.
    final DeviceMessageStatus status = DeviceMessageStatus.OK;
    DeviceConnection deviceConnection = null;

    try {
      deviceConnection = this.connectToDevice(deviceRequest);

      // Getting the SSLD for the device output-settings.
      final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());

      LOGGER.info("Turning all lights relays {}", startOfTest ? "on" : "off");
      final Iec61850SetLightCommand iec61850SetLightCommand =
          new Iec61850SetLightCommand(this.deviceMessageLoggingService);

      final List<LightValueDto> relaysWithInternalIdToSwitch =
          this.createListOfInternalIndicesToSwitch(
              this.ssldDataService.findByRelayType(ssld, RelayType.LIGHT), startOfTest);
      iec61850SetLightCommand.switchLightRelays(
          this.iec61850Client,
          deviceConnection,
          relaysWithInternalIdToSwitch,
          startOfTest ? "StartSelfTest" : "StopSelfTest");

      // Sleep and wait.
      this.selfTestSleep();

      // Getting the status.
      final DeviceStatusDto deviceStatus =
          new Iec61850GetStatusCommand(this.deviceMessageLoggingService)
              .getStatusFromDevice(this.iec61850Client, deviceConnection, ssld);

      LOGGER.info("Fetching and checking the devicestatus");

      // Checking to see if all light relays have the correct state.
      this.checkLightRelaysState(startOfTest, relaysWithInternalIdToSwitch, deviceStatus);

      LOGGER.info("All lights relays are {}, returning OK", startOfTest ? "on" : "off");

      this.createSuccessfulDefaultResponse(deviceRequest, deviceResponseHandler, status);
    } catch (final ConnectionFailureException se) {
      LOGGER.info("Original ConnectionFailureException message: {}", se.getMessage());
      final ConnectionFailureException seGeneric =
          new ConnectionFailureException("Connection failure", se);

      this.handleConnectionFailureException(deviceRequest, deviceResponseHandler, seGeneric);
    } catch (final Exception e) {
      LOGGER.info("Selftest failure", e);
      final TechnicalException te =
          new TechnicalException(
              ComponentType.PROTOCOL_IEC61850, "Selftest failure - " + e.getMessage());
      this.handleException(deviceRequest, deviceResponseHandler, te);
    }
    this.iec61850DeviceConnectionService.disconnect(deviceConnection, deviceRequest);
  }

  private void selfTestSleep() throws TechnicalException {
    try {
      LOGGER.info("Waiting {} milliseconds before getting the device status", this.selftestTimeout);
      Thread.sleep(this.selftestTimeout);
    } catch (final InterruptedException e) {
      LOGGER.error("An InterruptedException occurred during the device selftest timeout.", e);
      throw new TechnicalException(
          ComponentType.PROTOCOL_IEC61850, "An error occurred during the device selftest timeout.");
    }
  }

  private void checkLightRelaysState(
      final boolean startOfTest,
      final List<LightValueDto> relaysWithInternalIdToSwitch,
      final DeviceStatusDto deviceStatus)
      throws ProtocolAdapterException {
    for (final LightValueDto lightValue : deviceStatus.getLightValues()) {
      for (final LightValueDto lightValueDto : relaysWithInternalIdToSwitch) {
        LOGGER.info(
            "relaysWithInternalIdToSwitch.getIndex().equals(lightValue.getIndex()): {} for lightValue.getIndex(): {} and lightValueDto.getIndex(): {}",
            lightValue.getIndex().equals(lightValueDto.getIndex()),
            lightValue.getIndex(),
            lightValueDto.getIndex());

        if (lightValue.getIndex().equals(lightValueDto.getIndex())
            && lightValue.isOn() != startOfTest) {
          // One the the light relays is not in the correct state,
          // request failed.
          throw new ProtocolAdapterException(
              "not all relays are ".concat(startOfTest ? "on" : "off"));
        }
      }
    }
  }

  @Override
  public void setSchedule(
      final SetScheduleDeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    DeviceConnection deviceConnection = null;
    try {
      deviceConnection = this.connectToDevice(deviceRequest);

      // Getting the SSLD for the device output-settings.
      final Ssld ssld = this.ssldDataService.findDevice(deviceRequest.getDeviceIdentification());

      new Iec61850SetScheduleCommand(this.deviceMessageLoggingService, this.ssldDataService)
          .setScheduleOnDevice(
              this.iec61850Client,
              deviceConnection,
              deviceRequest.getRelayType(),
              deviceRequest.getSchedule(),
              ssld);

      this.createSuccessfulDefaultResponse(deviceRequest, deviceResponseHandler);
    } catch (final ConnectionFailureException se) {
      this.handleConnectionFailureException(deviceRequest, deviceResponseHandler, se);
    } catch (final ProtocolAdapterException e) {
      this.handleProtocolAdapterException(deviceRequest, deviceResponseHandler, e);
    } catch (final Exception e) {
      this.handleException(deviceRequest, deviceResponseHandler, e);
    }
    this.iec61850DeviceConnectionService.disconnect(deviceConnection, deviceRequest);
  }

  @Override
  public void getFirmwareVersion(
      final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    DeviceConnection deviceConnection = null;
    try {
      deviceConnection = this.connectToDevice(deviceRequest);

      final List<FirmwareVersionDto> firmwareVersions =
          new Iec61850GetFirmwareVersionCommand(this.deviceMessageLoggingService)
              .getFirmwareVersionFromDevice(this.iec61850Client, deviceConnection);

      final GetFirmwareVersionDeviceResponse deviceResponse =
          new GetFirmwareVersionDeviceResponse(deviceRequest, firmwareVersions);

      deviceResponseHandler.handleResponse(deviceResponse);
    } catch (final ConnectionFailureException se) {
      this.handleConnectionFailureException(deviceRequest, deviceResponseHandler, se);
    } catch (final Exception e) {
      this.handleException(deviceRequest, deviceResponseHandler, e);
    }
    this.iec61850DeviceConnectionService.disconnect(deviceConnection, deviceRequest);
  }

  @Override
  public void setTransition(
      final SetTransitionDeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    DeviceConnection devCon = null;
    try {
      final DeviceConnection deviceConnection = this.connectToDevice(deviceRequest);
      devCon = deviceConnection;

      new Iec61850TransitionCommand(this.deviceMessageLoggingService)
          .transitionDevice(
              this.iec61850Client, deviceConnection, deviceRequest.getTransitionTypeContainer());

      this.createSuccessfulDefaultResponse(deviceRequest, deviceResponseHandler);

      this.enableReporting(deviceConnection, deviceRequest);
    } catch (final ConnectionFailureException se) {
      this.handleConnectionFailureException(deviceRequest, deviceResponseHandler, se);
      this.iec61850DeviceConnectionService.disconnect(devCon, deviceRequest);
    } catch (final Exception e) {
      this.handleException(deviceRequest, deviceResponseHandler, e);
      this.iec61850DeviceConnectionService.disconnect(devCon, deviceRequest);
    }
  }

  @Override
  public void updateFirmware(
      final UpdateFirmwareDeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    DeviceConnection deviceConnection = null;
    try {
      deviceConnection = this.connectToDevice(deviceRequest);

      new Iec61850UpdateFirmwareCommand(this.deviceMessageLoggingService)
          .pushFirmwareToDevice(
              this.iec61850Client,
              deviceConnection,
              deviceRequest.getFirmwareDomain().concat(deviceRequest.getFirmwareUrl()),
              deviceRequest.getFirmwareModuleData());

      this.createSuccessfulDefaultResponse(deviceRequest, deviceResponseHandler);
    } catch (final ConnectionFailureException se) {
      this.handleConnectionFailureException(deviceRequest, deviceResponseHandler, se);
    } catch (final Exception e) {
      this.handleException(deviceRequest, deviceResponseHandler, e);
    }
    this.iec61850DeviceConnectionService.disconnect(deviceConnection, deviceRequest);
  }

  @Override
  public void updateDeviceSslCertification(
      final UpdateDeviceSslCertificationDeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    DeviceConnection deviceConnection = null;
    try {
      deviceConnection = this.connectToDevice(deviceRequest);

      new Iec61850UpdateSslCertificateCommand(this.deviceMessageLoggingService)
          .pushSslCertificateToDevice(
              this.iec61850Client, deviceConnection, deviceRequest.getCertification());

      this.createSuccessfulDefaultResponse(deviceRequest, deviceResponseHandler);
    } catch (final ConnectionFailureException se) {
      this.handleConnectionFailureException(deviceRequest, deviceResponseHandler, se);
    } catch (final Exception e) {
      this.handleException(deviceRequest, deviceResponseHandler, e);
    }
    this.iec61850DeviceConnectionService.disconnect(deviceConnection, deviceRequest);
  }

  @Override
  public void setEventNotifications(
      final SetEventNotificationsDeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    final List<EventNotificationTypeDto> eventNotifications =
        deviceRequest.getEventNotificationsContainer().getEventNotifications();
    final String filter = EventType.getEventTypeFilterMaskForNotificationTypes(eventNotifications);

    DeviceConnection deviceConnection = null;
    try {
      deviceConnection = this.connectToDevice(deviceRequest);

      new Iec61850SetEventNotificationFilterCommand(this.deviceMessageLoggingService)
          .setEventNotificationFilterOnDevice(this.iec61850Client, deviceConnection, filter);

      this.createSuccessfulDefaultResponse(deviceRequest, deviceResponseHandler);
    } catch (final ConnectionFailureException se) {
      this.handleConnectionFailureException(deviceRequest, deviceResponseHandler, se);
    } catch (final Exception e) {
      this.handleException(deviceRequest, deviceResponseHandler, e);
    }
    this.iec61850DeviceConnectionService.disconnect(deviceConnection, deviceRequest);
  }

  // ======================================
  // PRIVATE DEVICE COMMUNICATION METHODS =
  // ======================================

  private DeviceConnection connectToDevice(final DeviceRequest deviceRequest)
      throws ConnectionFailureException {

    final DeviceConnectionParameters deviceConnectionParameters =
        DeviceConnectionParameters.newBuilder()
            .ipAddress(deviceRequest.getIpAddress())
            .deviceIdentification(deviceRequest.getDeviceIdentification())
            .ied(IED.FLEX_OVL)
            .serverName(IED.FLEX_OVL.getDescription())
            .logicalDevice(LogicalDevice.LIGHTING.getDescription())
            .build();

    return this.iec61850DeviceConnectionService.connectWithoutConnectionCaching(
        deviceConnectionParameters, deviceRequest.getOrganisationIdentification());
  }

  // ========================
  // PRIVATE HELPER METHODS =
  // ========================

  private EmptyDeviceResponse createDefaultResponse(
      final DeviceRequest deviceRequest, final DeviceMessageStatus deviceMessageStatus) {
    return new EmptyDeviceResponse(
        deviceRequest.getOrganisationIdentification(),
        deviceRequest.getDeviceIdentification(),
        deviceRequest.getCorrelationUid(),
        deviceRequest.getMessagePriority(),
        deviceMessageStatus);
  }

  private void createSuccessfulDefaultResponse(
      final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler) {
    this.createSuccessfulDefaultResponse(
        deviceRequest, deviceResponseHandler, DeviceMessageStatus.OK);
  }

  private void createSuccessfulDefaultResponse(
      final DeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler,
      final DeviceMessageStatus deviceMessageStatus) {
    final EmptyDeviceResponse deviceResponse =
        this.createDefaultResponse(deviceRequest, deviceMessageStatus);
    deviceResponseHandler.handleResponse(deviceResponse);
  }

  private void handleConnectionFailureException(
      final DeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler,
      final ConnectionFailureException connectionFailureException)
      throws JMSException {
    LOGGER.error("Could not connect to device", connectionFailureException);
    final EmptyDeviceResponse deviceResponse =
        this.createDefaultResponse(deviceRequest, DeviceMessageStatus.FAILURE);
    deviceResponseHandler.handleConnectionFailure(connectionFailureException, deviceResponse);
  }

  private void handleProtocolAdapterException(
      final SetScheduleDeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler,
      final ProtocolAdapterException protocolAdapterException) {
    LOGGER.error(
        "Could not complete the request: "
            + deviceRequest.getMessageType()
            + " for device: "
            + deviceRequest.getDeviceIdentification(),
        protocolAdapterException);
    final EmptyDeviceResponse deviceResponse =
        this.createDefaultResponse(deviceRequest, DeviceMessageStatus.FAILURE);
    deviceResponseHandler.handleException(protocolAdapterException, deviceResponse);
  }

  private void handleException(
      final DeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler,
      final Exception exception) {
    LOGGER.error("Unexpected exception", exception);
    final EmptyDeviceResponse deviceResponse =
        this.createDefaultResponse(deviceRequest, DeviceMessageStatus.FAILURE);
    deviceResponseHandler.handleException(exception, deviceResponse);
  }

  // ========================
  // This method is duplicated in one of the command implementations. This
  // needs to be refactored. =
  // ========================

  /*
   * Checks to see if the relay has the correct type, throws an exception when
   * that't not the case
   */
  private void checkRelay(
      final RelayType actual, final RelayType expected, final Integer internalAddress)
      throws FunctionalException {
    if (!actual.equals(expected)) {
      if (RelayType.LIGHT.equals(expected)) {
        LOGGER.error(
            "Relay with internal address: {} is not configured as light relay", internalAddress);
        throw new FunctionalException(
            FunctionalExceptionType.ACTION_NOT_ALLOWED_FOR_LIGHT_RELAY,
            ComponentType.PROTOCOL_IEC61850);
      } else {
        LOGGER.error(
            "Relay with internal address: {} is not configured as tariff relay", internalAddress);
        throw new FunctionalException(
            FunctionalExceptionType.ACTION_NOT_ALLOWED_FOR_TARIFF_RELAY,
            ComponentType.PROTOCOL_IEC61850);
      }
    }
  }

  private void enableReporting(
      final DeviceConnection deviceConnection, final DeviceRequest deviceRequest)
      throws NodeException {
    // Enabling device reporting.
    if (this.isBufferedReportingEnabled) {
      new Iec61850EnableReportingCommand()
          .enableBufferedReportingOnDeviceWithoutUsingSequenceNumber(deviceConnection);
    } else {
      new Iec61850EnableReportingCommand()
          .enableUnbufferedReportingOnDeviceWithoutUsingSequenceNumber(deviceConnection);
    }

    // Don't disconnect now! The device should be able to send reports.
    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                try {
                  if (Iec61850SsldDeviceService.this.isBufferedReportingEnabled) {
                    new Iec61850ClearReportCommand().clearBufferedReportOnDevice(deviceConnection);
                  } else {
                    new Iec61850ClearReportCommand()
                        .disableUnbufferedReportOnDevice(deviceConnection);
                  }
                } catch (final ProtocolAdapterException e) {
                  LOGGER.error(
                      "Unable to clear report for device: "
                          + deviceRequest.getDeviceIdentification(),
                      e);
                }
                Iec61850SsldDeviceService.this.iec61850DeviceConnectionService.disconnect(
                    deviceConnection, deviceRequest);
              }
            },
            this.disconnectDelay);
  }
}
