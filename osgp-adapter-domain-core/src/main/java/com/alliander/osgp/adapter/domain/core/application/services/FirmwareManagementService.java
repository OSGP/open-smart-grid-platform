/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.core.application.services;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.FirmwareUpdateMessageDataContainer;
import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainCoreFirmwareManagementService")
@Transactional(value = "transactionManager")
public class FirmwareManagementService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

    /**
     * Constructor
     */
    public FirmwareManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === UPDATE FIRMWARE ===

    public void updateFirmware(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final FirmwareUpdateMessageDataContainer firmwareUpdateMessageDataContainer,
            final Long scheduleTime, final String messageType) throws FunctionalException {

        LOGGER.debug("Update firmware called with organisation [{}], device [{}], firmwareIdentification [{}].",
                organisationIdentification, deviceIdentification, firmwareUpdateMessageDataContainer.getFirmwareUrl());

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification,
                        this.domainCoreMapper.map(firmwareUpdateMessageDataContainer,
                                com.alliander.osgp.dto.valueobjects.FirmwareUpdateMessageDataContainer.class)),
                messageType, device.getIpAddress(), scheduleTime);
    }

    // === GET FIRMWARE VERSION ===

    public void getFirmwareVersion(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final String correlationUid, final String messageType)
            throws FunctionalException {

        LOGGER.debug("Get firmware version called with organisation [{}], device [{}].", organisationIdentification,
                deviceIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, null), messageType,
                device.getIpAddress());
    }

    public void handleGetFirmwareVersionResponse(final List<FirmwareVersionDto> firmwareVersions,
            final String deviceIdentification, final String organisationIdentification, final String correlationUid,
            final String messageType, final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = exception;

        try {
            if (deviceResult == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }
        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.UNKNOWN,
                    "Exception occurred while getting device firmware version", e);
        }

        ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(correlationUid).withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification).withResult(result).withOsgpException(osgpException)
                .withDataObject((Serializable) firmwareVersions).build();
        this.webServiceResponseMessageSender.send(responseMessage);
    }

    // === SWITCH TO OTHER FIRMWARE VERSION ===

    public void switchFirmware(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType, final String version) throws FunctionalException {
        LOGGER.debug("switchFirmware called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, version),
                messageType, device.getIpAddress());
    }
}
