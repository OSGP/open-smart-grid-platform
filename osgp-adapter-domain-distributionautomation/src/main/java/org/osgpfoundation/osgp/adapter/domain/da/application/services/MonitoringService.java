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
import org.osgpfoundation.osgp.domain.da.valueobjects.GetPQValuesPeriodicRequest;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetPQValuesRequest;
import org.osgpfoundation.osgp.domain.da.valueobjects.GetPQValuesResponse;
import org.osgpfoundation.osgp.dto.da.GetPQValuesPeriodicRequestDto;
import org.osgpfoundation.osgp.dto.da.GetPQValuesRequestDto;
import org.osgpfoundation.osgp.dto.da.GetPQValuesResponseDto;
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

@Service(value = "domainDistributionAutomationMonitoringService")
@Transactional(value = "transactionManager")
public class MonitoringService extends BaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringService.class);

    @Autowired
    private DomainDistributionAutomationMapper mapper;

    /**
     * Constructor
     */
    public MonitoringService() {
        // Parameterless constructor required for transactions...
    }

    public void getPQValues(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType, final GetPQValuesRequest getPQValuesRequest)
            throws FunctionalException {

        LOGGER.info("Get PQ Values for device [{}] with correlation id [{}]", deviceIdentification, correlationUid);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final GetPQValuesRequestDto dto = this.mapper.map(getPQValuesRequest, GetPQValuesRequestDto.class);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, dto), messageType,
                device.getIpAddress());
    }

    public void getPQValuesPeriodic(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType,
            final GetPQValuesPeriodicRequest getPQValuesPeriodicRequest) throws FunctionalException {

        LOGGER.info("Get PQ Values periodic for device [{}] with correlation id [{}]", deviceIdentification,
                correlationUid);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final GetPQValuesPeriodicRequestDto dto = this.mapper.map(getPQValuesPeriodicRequest,
                GetPQValuesPeriodicRequestDto.class);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, dto), messageType,
                device.getIpAddress());
    }

    public void handleGetPQValuesResponse(final GetPQValuesResponseDto getPQValuesResponseDto,
            final String deviceIdentification, final String organisationIdentification, final String correlationUid,
            final String messageType, final ResponseMessageResultType responseMessageResultType,
            final OsgpException osgpException) {

        LOGGER.info("handleResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        GetPQValuesResponse getPQValuesResponse = null;
        OsgpException exception = null;

        try {
            if (responseMessageResultType == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }

            this.handleResponseMessageReceived(LOGGER, deviceIdentification);

            getPQValuesResponse = this.mapper.map(getPQValuesResponseDto, GetPQValuesResponse.class);

        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            exception = this.ensureOsgpException(e, "Exception occurred while getting PQ Values Response Data");
        }

        // Support for Push messages, generate correlationUid
        String actualCorrelationUid = correlationUid;
        if ("no-correlationUid".equals(actualCorrelationUid)) {
            actualCorrelationUid = getCorrelationId("DeviceGenerated", deviceIdentification);
        }

        ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(correlationUid).withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification).withResult(result).withOsgpException(osgpException)
                .withDataObject(getPQValuesResponse).build();
        this.webServiceResponseMessageSender.send(responseMessage, messageType);
    }
}
