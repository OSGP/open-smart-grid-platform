/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.microgrids.application.services;

import javax.persistence.OptimisticLockException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.opensmartgridplatform.adapter.domain.microgrids.application.mapping.DomainMicrogridsMapper;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.services.CorrelationIdProviderUUIDService;
import org.opensmartgridplatform.domain.microgrids.entities.RtuDevice;
import org.opensmartgridplatform.domain.microgrids.valueobjects.EmptyResponse;
import org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataRequest;
import org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataResponse;
import org.opensmartgridplatform.domain.microgrids.valueobjects.SetDataRequest;
import org.opensmartgridplatform.dto.valueobjects.microgrids.EmptyResponseDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataRequestDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetDataRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainMicrogridsAdHocManagementService")
@Transactional(value = "transactionManager")
public class AdHocManagementService extends BaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

    @Autowired
    private DomainMicrogridsMapper mapper;

    @Autowired
    private Integer lastCommunicationUpdateInterval;

    @Autowired
    private CorrelationIdProviderUUIDService correlationIdProviderUUIDService;

    /**
     * Constructor
     */
    public AdHocManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === GET DATA ===

    public void getData(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType, final GetDataRequest dataRequest)
            throws FunctionalException {

        LOGGER.info("Get data for device [{}] with correlation id [{}]", deviceIdentification, correlationUid);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final GetDataRequestDto dto = this.mapper.map(dataRequest, GetDataRequestDto.class);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, dto), messageType,
                device.getIpAddress());
    }

    public void handleGetDataResponse(final GetDataResponseDto dataResponseDto, final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType responseMessageResultType, final OsgpException osgpException) {

        LOGGER.info("handleResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        GetDataResponse dataResponse = null;
        OsgpException exception = null;

        try {
            if (responseMessageResultType == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }

            this.handleResponseMessageReceived(deviceIdentification);

            dataResponse = this.mapper.map(dataResponseDto, GetDataResponse.class);

        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            exception = this.ensureOsgpException(e, "Exception occurred while getting data");
        }

        // Support for Push messages, generate correlationUid
        String actualCorrelationUid = correlationUid;
        if ("no-correlationUid".equals(actualCorrelationUid)) {
            actualCorrelationUid = this.correlationIdProviderUUIDService.getCorrelationId("DeviceGenerated",
                    deviceIdentification);
        }

        final ResponseMessage resopnseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(actualCorrelationUid).withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification).withResult(result).withOsgpException(osgpException)
                .withDataObject(dataResponse).build();
        this.webServiceResponseMessageSender.send(resopnseMessage, messageType);
    }

    // === SET DATA ===

    public void handleSetDataRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType, final SetDataRequest setDataRequest)
            throws FunctionalException {

        LOGGER.info("Set data for device [{}] with correlation id [{}]", deviceIdentification, correlationUid);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final SetDataRequestDto dto = this.mapper.map(setDataRequest, SetDataRequestDto.class);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, dto), messageType,
                device.getIpAddress());
    }

    public void handleSetDataResponse(final EmptyResponseDto emptyResponseDto, final String deviceIdentification,
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

            this.handleResponseMessageReceived(deviceIdentification);

            emptyResponse = this.mapper.map(emptyResponseDto, EmptyResponse.class);

        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            exception = this.ensureOsgpException(e, "Exception occurred while setting data");
        }

        final ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(correlationUid).withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification).withResult(result).withOsgpException(exception)
                .withDataObject(emptyResponse).build();
        this.webServiceResponseMessageSender.send(responseMessage, messageType);
    }

    private void handleResponseMessageReceived(final String deviceIdentification) {
        try {
            final RtuDevice device = this.rtuDeviceRepository.findByDeviceIdentification(deviceIdentification);
            if (this.shouldUpdateCommunicationTime(device)) {
                device.messageReceived();
                this.rtuDeviceRepository.save(device);
            } else {
                LOGGER.info("Last communication time within {} seconds. Skipping last communication date update.",
                        this.lastCommunicationUpdateInterval);
            }
        } catch (final OptimisticLockException ex) {
            LOGGER.warn("Last communication time not updated due to optimistic lock exception", ex);
        }
    }

    private boolean shouldUpdateCommunicationTime(final RtuDevice device) {
        final DateTime timeToCheck = DateTime.now().minusSeconds(this.lastCommunicationUpdateInterval);
        final DateTime timeOfLastCommunication = new DateTime(device.getLastCommunicationTime());
        return timeOfLastCommunication.isBefore(timeToCheck);
    }
}
