/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.services;

import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.Configuration;
import org.opensmartgridplatform.domain.core.valueobjects.RelayMap;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.RelayConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.RelayMapDto;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service(value = "domainCoreConfigurationManagementService")
@Transactional(value = "transactionManager")
public class ConfigurationManagementService extends AbstractService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ConfigurationManagementService.class);

  /** Constructor */
  public ConfigurationManagementService() {
    // Parameterless constructor required for transactions...
  }

  // === SET CONFIGURATION ===

  public void setConfiguration(
      final CorrelationIds ids,
      final Configuration configuration,
      final Long scheduleTime,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    LOGGER.debug(
        "setConfiguration called with organisation {} and device {}",
        ids.getOrganisationIdentification(),
        ids.getDeviceIdentification());

    this.findOrganisation(ids.getOrganisationIdentification());
    final Device device = this.findActiveDevice(ids.getDeviceIdentification());

    if (configuration == null) {
      LOGGER.info("Configuration is empty, skip sending a request to device");
      return;
    }

    // First, persist the configuration of the device output settings.
    this.updateDeviceOutputSettings(device, configuration);

    // Second, replace TARIFF_REVERSED with TARIFF, since the device doesn't
    // contain a TARIFF_REVERSED enum entry.
    if (configuration.getRelayConfiguration() != null
        && configuration.getRelayConfiguration().getRelayMap() != null) {
      for (final RelayMap rm : configuration.getRelayConfiguration().getRelayMap()) {
        if (rm.getRelayType().equals(RelayType.TARIFF_REVERSED)) {
          rm.changeRelayType(RelayType.TARIFF);
        }
      }
    }

    final org.opensmartgridplatform.dto.valueobjects.ConfigurationDto configurationDto =
        this.domainCoreMapper.map(
            configuration, org.opensmartgridplatform.dto.valueobjects.ConfigurationDto.class);

    this.osgpCoreRequestMessageSender.sendWithScheduledTime(
        new RequestMessage(ids, configurationDto),
        messageType,
        messagePriority,
        device.getIpAddress(),
        scheduleTime);
  }

  private void updateDeviceOutputSettings(final Device device, final Configuration configuration) {

    // Check if device output settings can/should be updated
    if (device == null || configuration == null || configuration.getRelayConfiguration() == null) {
      // Nothing to update
      return;
    }
    if (configuration.getRelayConfiguration().getRelayMap() == null
        || configuration.getRelayConfiguration().getRelayMap().isEmpty()) {
      // Nothing to update
      return;
    }

    final List<DeviceOutputSetting> outputSettings = new ArrayList<>();
    for (final RelayMap rm : configuration.getRelayConfiguration().getRelayMap()) {
      outputSettings.add(
          new DeviceOutputSetting(
              rm.getAddress(), rm.getIndex(), rm.getRelayType(), rm.getAlias()));
    }

    final Ssld ssld = this.findSsldForDevice(device);
    ssld.updateOutputSettings(outputSettings);
    this.ssldRepository.save(ssld);
  }

  // === GET CONFIGURATION ===

  public void getConfiguration(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    LOGGER.debug(
        "getConfiguration called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, null),
        messageType,
        messagePriority,
        device.getIpAddress());
  }

  public void handleGetConfigurationResponse(
      final ConfigurationDto configurationDto,
      final CorrelationIds ids,
      final String messageType,
      final int messagePriority,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    OsgpException osgpException = exception;
    Configuration configuration = null;

    try {
      if (deviceResult == ResponseMessageResultType.NOT_OK || osgpException != null) {
        LOGGER.error("Device Response not ok.", osgpException);
        throw osgpException;
      }

      final Ssld ssld =
          this.ssldRepository.findByDeviceIdentification(ids.getDeviceIdentification());
      final List<DeviceOutputSetting> outputSettings = ssld.getOutputSettings();

      this.replaceEmptyOutputSettings(configurationDto, outputSettings);

      configuration = this.domainCoreMapper.map(configurationDto, Configuration.class);

      // Make sure that a relay that has been configured with
      // TARIFF_REVERSED will be changed to the correct RelayType.
      for (final DeviceOutputSetting dos : outputSettings) {
        if (dos.getOutputType().equals(RelayType.TARIFF_REVERSED)) {
          for (final RelayMap rm : configuration.getRelayConfiguration().getRelayMap()) {
            if (rm.getIndex() == dos.getExternalId()
                && rm.getRelayType().equals(RelayType.TARIFF)) {
              rm.changeRelayType(RelayType.TARIFF_REVERSED);
            }
          }
        }
      }

    } catch (final Exception e) {
      LOGGER.error("Unexpected Exception for messageType: {}", messageType, e);
      result = ResponseMessageResultType.NOT_OK;
      osgpException = new TechnicalException(ComponentType.UNKNOWN, e.getMessage(), e);
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(ids)
            .withResult(result)
            .withOsgpException(osgpException)
            .withDataObject(configuration)
            .withMessagePriority(messagePriority)
            .withMessageType(MessageType.GET_CONFIGURATION.name())
            .build();
    this.webServiceResponseMessageSender.send(responseMessage);
  }

  public void switchConfiguration(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final String messageType,
      final int messagePriority,
      final String configurationBank)
      throws FunctionalException {
    LOGGER.debug(
        "switchConfiguration called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            correlationUid, organisationIdentification, deviceIdentification, configurationBank),
        messageType,
        messagePriority,
        device.getIpAddress());
  }

  private void replaceEmptyOutputSettings(
      final ConfigurationDto originalConfig, final List<DeviceOutputSetting> outputSettings) {
    // In case the relay map is not null or not empty, return it.
    if (originalConfig.getRelayConfiguration() == null
        || CollectionUtils.isEmpty(originalConfig.getRelayConfiguration().getRelayMap())) {
      // Fall back to output settings when no relay configuration
      // available to generate configuration
      final List<RelayMapDto> relayMap = new ArrayList<>();

      outputSettings.forEach(
          outputSetting ->
              relayMap.add(
                  new org.opensmartgridplatform.dto.valueobjects.RelayMapDto(
                      outputSetting.getExternalId(), outputSetting.getInternalId(),
                      RelayTypeDto.valueOf(outputSetting.getOutputType().toString()),
                          outputSetting.getAlias())));

      originalConfig.setRelayConfiguration(new RelayConfigurationDto(relayMap));
    }
  }
}
