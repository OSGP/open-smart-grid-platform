/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmsDetails;
import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeRequest;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainSmartMeteringAdhocService")
@Transactional(value = "transactionManager")
public class AdhocService {

    private static final String DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION = "Device Response not ok. Unexpected Exception";

    private static final Logger LOGGER = LoggerFactory.getLogger(AdhocService.class);

    @Autowired
    @Qualifier(value = "domainSmartMeteringOutgoingOsgpCoreRequestMessageSender")
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private AdhocMapper adhocMapper;

    public AdhocService() {
        // Parameterless constructor required for transactions...
    }

    public void synchronizeTime(
            @Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final String correlationUid,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequest synchronizeTimeRequestValueObject,
            final String messageType) throws FunctionalException {

        LOGGER.debug("synchronizeTime for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceIdentification);

        final SynchronizeTimeRequest synchronizeTimeRequestDto = new SynchronizeTimeRequest(
                synchronizeTimeRequestValueObject.getDeviceIdentification());

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, smartMeteringDevice.getIpAddress(), synchronizeTimeRequestDto), messageType);
    }

    public void handleSynchronizeTimeResponse(final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.debug("handleSynchronizeTimeResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, null), messageType);
    }

    public void sendWakeupSms(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final SmsDetails smsDetailsValueObject, final String messageType)
            throws FunctionalException {

        LOGGER.debug("send wakeup sms request for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceIdentification);

        final com.alliander.osgp.dto.valueobjects.smartmetering.SmsDetails smsDetailsDto = this.adhocMapper.map(
                smsDetailsValueObject, com.alliander.osgp.dto.valueobjects.smartmetering.SmsDetails.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, smartMeteringDevice.getIpAddress(), smsDetailsDto), messageType);

    }

    public void handleSendWakeupSmsResponse(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType,
            final ResponseMessageResultType responseMessageResultType, final OsgpException exception,
            final com.alliander.osgp.dto.valueobjects.smartmetering.SmsDetails smsDetailsDto) {

        LOGGER.debug("handleSendWakeupSmsResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = responseMessageResultType;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        final SmsDetails smsDetails = this.adhocMapper.map(smsDetailsDto, SmsDetails.class);

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, smsDetails), messageType);
    }

    public void getSmsDetails(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final SmsDetails smsDetailsValueObject, final String messageType)
                    throws FunctionalException {

        LOGGER.debug("retrieve sms details request for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceIdentification);

        final com.alliander.osgp.dto.valueobjects.smartmetering.SmsDetails smsDetailsDto = this.adhocMapper.map(
                smsDetailsValueObject, com.alliander.osgp.dto.valueobjects.smartmetering.SmsDetails.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, smartMeteringDevice.getIpAddress(), smsDetailsDto), messageType);

    }

    public void handleGetSmsDetailsResponse(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final ResponseMessageResultType deviceResult,
            final OsgpException exception,
            final com.alliander.osgp.dto.valueobjects.smartmetering.SmsDetails smsDetailsDto) {

        LOGGER.debug("handleGetSmsDetailsResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        final SmsDetails smsDetails = this.adhocMapper.map(smsDetailsDto, SmsDetails.class);

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, smsDetails), messageType);
    }

    public void retrieveConfigurationObjects(
            @Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final String correlationUid,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.RetrieveConfigurationObjectsRequest request,
            final String messageType) throws FunctionalException {

        LOGGER.debug("retrieveConfigurationObjects for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceIdentification);

        final com.alliander.osgp.dto.valueobjects.smartmetering.RetrieveConfigurationObjectsRequest requestDto = new com.alliander.osgp.dto.valueobjects.smartmetering.RetrieveConfigurationObjectsRequest(
                request.getDeviceIdentification());

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, smartMeteringDevice.getIpAddress(), requestDto), messageType);

    }

    public void handleRetrieveConfigurationObjectsResponse(final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception, final String resultData) {

        LOGGER.debug("handleRetrieveConfigurationObjectsResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, resultData), messageType);

    }
}
