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

            final List<com.alliander.osgp.dto.valueobjects.RelayMapDto> newRelayMap = new ArrayList<com.alliander.osgp.dto.valueobjects.RelayMapDto>();
            final List<com.alliander.osgp.dto.valueobjects.RelayMapDto> relayMapDto = configurationDto
                    .getRelayConfiguration().getRelayMap();
            for (final DeviceOutputSetting ouputSetting : outputSettings) {
                for (final com.alliander.osgp.dto.valueobjects.RelayMapDto relaySettingDto : relayMapDto) {
                    if (relaySettingDto.getIndex() == ouputSetting.getExternalId()) {
                        final com.alliander.osgp.dto.valueobjects.RelayMapDto newRelayMapDto = new com.alliander.osgp.dto.valueobjects.RelayMapDto(
                                ouputSetting.getExternalId(), ouputSetting.getInternalId(),
                                com.alliander.osgp.dto.valueobjects.RelayTypeDto.valueOf(ouputSetting.getOutputType()
                                        .name()), ouputSetting.getAlias());
                        newRelayMap.add(newRelayMapDto);
                    }
                }
            }
            configurationDto.getRelayConfiguration().getRelayMap().clear();
            configurationDto.getRelayConfiguration().getRelayMap().addAll(newRelayMap);

            configuration = this.domainCoreMapper.map(configurationDto, Configuration.class);

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
            osgpException = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);
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
}
