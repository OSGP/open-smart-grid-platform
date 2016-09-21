/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.microgrids.application.services;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.domain.microgrids.application.mapping.DomainMicrogridsMapper;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.microgrids.valueobjects.DataRequest;
import com.alliander.osgp.domain.microgrids.valueobjects.DataResponse;
import com.alliander.osgp.domain.microgrids.valueobjects.EmptyResponse;
import com.alliander.osgp.domain.microgrids.valueobjects.SetPointsRequest;
import com.alliander.osgp.dto.valueobjects.microgrids.DataRequestDto;
import com.alliander.osgp.dto.valueobjects.microgrids.DataResponseDto;
import com.alliander.osgp.dto.valueobjects.microgrids.EmptyResponseDto;
import com.alliander.osgp.dto.valueobjects.microgrids.SetPointsRequestDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainMicrogridsAdHocManagementService")
@Transactional(value = "transactionManager")
public class AdHocManagementService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

    @Autowired
    private DomainMicrogridsMapper mapper;

    /**
     * Constructor
     */
    public AdHocManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === GET DATA ===

    public void getData(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType, final DataRequest dataRequest)
                    throws FunctionalException {

        LOGGER.info("Get data for device [{}] with correlation id [{}]", deviceIdentification, correlationUid);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final DataRequestDto dto = this.mapper.map(dataRequest, DataRequestDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, dto), messageType, device.getIpAddress());
    }

    public void handleGetDataResponse(final DataResponseDto dataResponseDto, final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType responseMessageResultType, final OsgpException osgpException) {

        LOGGER.info("handleResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        DataResponse dataResponse = null;
        OsgpException exception = null;

        try {
            if (responseMessageResultType == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }

            dataResponse = this.mapper.map(dataResponseDto, DataResponse.class);

        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            exception = new TechnicalException(ComponentType.DOMAIN_MICROGRIDS,
                    "Exception occurred while getting data", e);
        }

        // Support for Push messages, generate correlationUid
        String actualCorrelationUid = correlationUid;
        if ("no-correlationUid".equals(actualCorrelationUid)) {
            actualCorrelationUid = this.getCorrelationId("DeviceGenerated", deviceIdentification);
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(actualCorrelationUid, organisationIdentification,
                deviceIdentification, result, exception, dataResponse), messageType);
    }

    // === SET SETPOINTS ===

    public void handleSetPointsRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType, final SetPointsRequest setPointsRequest)
                    throws FunctionalException {

        LOGGER.info("Set setpoints for device [{}] with correlation id [{}]", deviceIdentification, correlationUid);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final SetPointsRequestDto dto = this.mapper.map(setPointsRequest, SetPointsRequestDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, dto), messageType, device.getIpAddress());
    }

    public void handleSetPointsResponse(final EmptyResponseDto emptyResponseDto, final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType responseMessageResultType, final OsgpException osgpException) {

        LOGGER.info("handleResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        EmptyResponse emptyResponse = null;
        OsgpException exception = null;

        try {
            if (responseMessageResultType == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }

            emptyResponse = this.mapper.map(emptyResponseDto, EmptyResponse.class);

        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            exception = new TechnicalException(ComponentType.DOMAIN_MICROGRIDS,
                    "Exception occurred while setting setpoints", e);
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, emptyResponse), messageType);
    }

    private String getCorrelationId(final String organisationIdentification, final String deviceIdentification) {

        return organisationIdentification + "|||" + deviceIdentification + "|||" + UUID.randomUUID().toString();
    }
}
