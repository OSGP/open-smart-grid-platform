/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgpfoundation.osgp.adapter.domain.da.application.services;

import org.osgpfoundation.osgp.adapter.domain.da.application.mapping.DomainDistributionAutomationMapper;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetHealthStatusRequest;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetHealthStatusResponse;
import org.osgpfoundation.osgp.dto.da.GetHealthStatusRequestDto;
import org.osgpfoundation.osgp.dto.da.GetHealthStatusResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainDistributionAutomationDeviceManagementService")
@Transactional(value = "transactionManager")
public class DeviceManagementService extends BaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

    @Autowired
    private DomainDistributionAutomationMapper mapper;

    /**
     * Constructor
     */
    public DeviceManagementService() {
        // Parameterless constructor required for transactions...
    }

    public void getHealthStatus(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType, final GetHealthStatusRequest getHealthStatusRequest)
            throws FunctionalException {

        LOGGER.info("Get Health Status for device [{}] with correlation id [{}]", deviceIdentification, correlationUid);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final GetHealthStatusRequestDto dto = this.mapper.map(getHealthStatusRequest, GetHealthStatusRequestDto.class);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, dto), messageType,
                device.getIpAddress());
    }

    public void handleHealthStatusResponse(final GetHealthStatusResponseDto getHealthStatusResponseDto,
            final String deviceIdentification, final String organisationIdentification, final String correlationUid,
            final String messageType, final ResponseMessageResultType responseMessageResultType,
            final OsgpException osgpException) {

        LOGGER.info("handleResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        GetHealthStatusResponse getHealthStatusResponse = null;
        OsgpException exception = null;

        try {
            if (responseMessageResultType == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }

            this.handleResponseMessageReceived(LOGGER, deviceIdentification);

            getHealthStatusResponse = this.mapper.map(getHealthStatusResponseDto, GetHealthStatusResponse.class);

        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            exception = this.ensureOsgpException(e, "Exception occurred while getting Health Status Response Data");
        }

        ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(correlationUid).withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification).withResult(result).withOsgpException(exception)
                .withDataObject(getHealthStatusResponse).build();
        this.webServiceResponseMessageSender.send(responseMessage, messageType);
    }
}
