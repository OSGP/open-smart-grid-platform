/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.core.application.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceOutputSetting;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.valueobjects.Configuration;
import com.alliander.osgp.domain.core.valueobjects.RelayMap;
import com.alliander.osgp.domain.core.valueobjects.RelayType;
import com.alliander.osgp.dto.valueobjects.ConfigurationDto;
import com.alliander.osgp.dto.valueobjects.RelayConfigurationDto;
import com.alliander.osgp.dto.valueobjects.RelayMapDto;
import com.alliander.osgp.dto.valueobjects.RelayTypeDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainCoreConfigurationManagementService")
@Transactional(value = "transactionManager")
public class ConfigurationManagementService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManagementService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    /**
     * Constructor
     */
    public ConfigurationManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === SET CONFIGURATION ===

    public void setConfiguration(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final Configuration configuration, final Long scheduleTime,
            final String messageType) throws FunctionalException {

        LOGGER.debug("setConfiguration called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

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

        final com.alliander.osgp.dto.valueobjects.ConfigurationDto configurationDto = this.domainCoreMapper.map(
                configuration, com.alliander.osgp.dto.valueobjects.ConfigurationDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, configurationDto), messageType, device.getIpAddress(), scheduleTime);
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
            outputSettings
            .add(new DeviceOutputSetting(rm.getAddress(), rm.getIndex(), rm.getRelayType(), rm.getAlias()));
        }

        final Ssld ssld = this.findSsldForDevice(device);
        ssld.updateOutputSettings(outputSettings);
        this.ssldRepository.save(ssld);
    }

    // === GET CONFIGURATION ===

    public void getConfiguration(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType) throws FunctionalException {

        LOGGER.debug("getConfiguration called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, null), messageType, device.getIpAddress());
    }

    public void handleGetConfigurationResponse(
            final com.alliander.osgp.dto.valueobjects.ConfigurationDto configurationDto,
            final String deviceIdentification, final String organisationIdentification, final String correlationUid,
            final String messageType, final ResponseMessageResultType deviceResult, final OsgpException exception) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = exception;
        Configuration configuration = null;

        try {
            if (deviceResult == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }

            final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
            final List<DeviceOutputSetting> outputSettings = this.findSsldForDevice(device).getOutputSettings();

            configuration = this.domainCoreMapper.map(this.mergeOutputSettings(configurationDto, outputSettings),
                    Configuration.class);

            // Make sure that a relay that has been configured with
            // TARIFF_REVERSED will be changed to the correct RelayType.
            for (final DeviceOutputSetting dos : outputSettings) {
                if (dos.getOutputType().equals(RelayType.TARIFF_REVERSED)) {
                    for (final RelayMap rm : configuration.getRelayConfiguration().getRelayMap()) {
                        if (rm.getIndex() == dos.getExternalId() && rm.getRelayType().equals(RelayType.TARIFF)) {
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

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, osgpException, configuration));
    }

    public void switchConfiguration(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType, final String configurationBank)
                    throws FunctionalException {
        LOGGER.debug("switchConfiguration called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, configurationBank), messageType, device.getIpAddress());
    }

    private ConfigurationDto mergeOutputSettings(final ConfigurationDto originalConfig,
            final List<DeviceOutputSetting> outputSettings) {
        // In case the relay map is not null or not empty, return it.
        if (originalConfig.getRelayConfiguration() != null
                && originalConfig.getRelayConfiguration().getRelayMap() != null
                && !originalConfig.getRelayConfiguration().getRelayMap().isEmpty()) {
            return originalConfig;
        }

        // Fall back to output settings when no relay configuration available to
        // generate configuration
        final List<RelayMapDto> relayMap = new ArrayList<>();

        outputSettings.forEach(outputSetting -> 
            relayMap.add(new com.alliander.osgp.dto.valueobjects.RelayMapDto(
                outputSetting.getExternalId(), outputSetting.getInternalId(),
                RelayTypeDto.valueOf(outputSetting.getOutputType().toString()), 
                outputSetting.getAlias()))
        );

        final RelayConfigurationDto relayConfig = new RelayConfigurationDto(relayMap);

        // Override relay configuration based on default output settings
        return new ConfigurationDto(originalConfig.getLightType(), originalConfig.getDaliConfiguration(), relayConfig,
                originalConfig.getShortTermHistoryIntervalMinutes(), originalConfig.getPreferredLinkType(),
                originalConfig.getMeterType(), originalConfig.getLongTermHistoryInterval(),
                originalConfig.getLongTermHistoryIntervalType());
    }
}
