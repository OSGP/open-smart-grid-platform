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

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequest;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainSmartMeteringConfigurationService")
@Transactional(value = "transactionManager")
public class ConfigurationService {

    private static final String SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG = "Sending request message to core.";
    private static final String DEVICE_RESPONSE_NOT_OK_LOG_MSG = "Device Response not ok. Unexpected Exception";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    @Autowired
    @Qualifier(value = "domainSmartMeteringOutgoingOsgpCoreRequestMessageSender")
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    private ConfigurationMapper configurationMapper;

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    @Autowired
    private DomainHelperService domainHelperService;

    public ConfigurationService() {
        // Parameterless constructor required for transactions...
    }

    public void requestSpecialDays(
            @Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final String correlationUid,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest specialDaysRequestValueObject,
            final String messageType) throws FunctionalException {

        LOGGER.info("requestSpecialDays for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeteringDevice(deviceIdentification);

        LOGGER.info(SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG);

        final SpecialDaysRequest specialDaysRequestDto = this.configurationMapper.map(specialDaysRequestValueObject,
                SpecialDaysRequest.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, smartMeteringDevice.getIpAddress(), specialDaysRequestDto), messageType);
    }

    public void setConfigurationObject(
            @Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final String correlationUid,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest setConfigurationObjectRequestValueObject,
            final String messageType) throws FunctionalException {

        LOGGER.info("setConfigurationObject for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeteringDevice(deviceIdentification);

        LOGGER.info(SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG);

        final SetConfigurationObjectRequest setConfigurationObjectRequestDto = this.configurationMapper.map(
                setConfigurationObjectRequestValueObject, SetConfigurationObjectRequest.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, smartMeteringDevice.getIpAddress(), setConfigurationObjectRequestDto),
                messageType);
    }

    public void handleSpecialDaysResponse(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final ResponseMessageResultType deviceResult,
            final OsgpException exception) {

        LOGGER.info("handleSpecialDaysresponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, null), messageType);
    }

    public void setAlarmNotifications(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final String correlationUid,
            final AlarmNotifications alarmNotifications, final String messageType) throws FunctionalException {

        LOGGER.info("setAlarmNotifications for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        // TODO: bypassing authorization, this should be fixed.

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeteringDevice(deviceIdentification);

        final com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotifications alarmNotificationsDto = this.configurationMapper
                .map(alarmNotifications, com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotifications.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, smartMeteringDevice.getIpAddress(), alarmNotificationsDto), messageType);
    }

    public void setAdministrativeStatus(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final String correlationUid,
            final AdministrativeStatusType administrativeStatusType, final String messageType)
                    throws FunctionalException {

        LOGGER.info(
                "Set Administrative Status for organisationIdentification: {} for deviceIdentification: {} to status: {}",
                organisationIdentification, deviceIdentification, administrativeStatusType);

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeteringDevice(deviceIdentification);

        final com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusType administrativeStatusTypeDto = this.configurationMapper
                .map(administrativeStatusType,
                        com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusType.class);

        final RequestMessage requestMessage = new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, smartMeteringDevice.getIpAddress(), administrativeStatusTypeDto);
        this.osgpCoreRequestMessageSender.send(requestMessage, messageType);
    }

    public void handleSetAdministrativeStatusResponse(final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleSetAdministrativeStatusResponse for MessageType: {}, with result: {}", messageType,
                deviceResult.toString());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, null), messageType);
    }

    public void getAdministrativeStatus(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final AdministrativeStatusType administrativeStatusType,
            final String messageType) throws FunctionalException {

        LOGGER.info(
                "Get Administrative Status for organisationIdentification: {} for deviceIdentification: {} to status: {}",
                organisationIdentification, deviceIdentification, administrativeStatusType);

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeteringDevice(deviceIdentification);

        LOGGER.info(SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG);
        final RequestMessage requestMessage = new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, smartMeteringDevice.getIpAddress(), this.configurationMapper.map(
                        administrativeStatusType,
                        com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusType.class));
        this.osgpCoreRequestMessageSender.send(requestMessage, messageType);

    }

    public void handleGetAdministrativeStatusResponse(final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType responseMessageResultType, final OsgpException osgpException,
            final com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusType administrativeStatusTypeDto) {

        LOGGER.info("handleGetAdministrativeStatusResponse for MessageType: {}, with result: {}", messageType,
                responseMessageResultType.toString());

        ResponseMessageResultType result = responseMessageResultType;
        if (osgpException != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, osgpException);
            result = ResponseMessageResultType.NOT_OK;
        }

        final AdministrativeStatusType administrativeStatusType = this.configurationMapper.map(
                administrativeStatusTypeDto, AdministrativeStatusType.class);

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, osgpException, administrativeStatusType), messageType);
    }

    public void setActivityCalendar(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final String correlationUid,
            final ActivityCalendar activityCalendar, final String messageType) throws FunctionalException {

        LOGGER.info("set Activity Calendar for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        // TODO: bypassing authorization, this should be fixed.

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeteringDevice(deviceIdentification);

        final com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar activityCalendarDto = this.configurationMapper
                .map(activityCalendar, com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, smartMeteringDevice.getIpAddress(), activityCalendarDto), messageType);
    }

    public void handleSetAlarmNotificationsResponse(final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleSetAlarmNotificationsResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Set Alarm Notifications Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, null), messageType);
    }

    public void handleSetConfigurationObjectResponse(final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handle SetConfigurationObject response for MessageType: {}", messageType);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, null), messageType);
    }

    public void handleSetActivityCalendarResponse(final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType responseMessageResultType, final OsgpException exception,
            final String resultString) {
        LOGGER.info("handleSetActivityCalendarResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = responseMessageResultType;
        if (exception != null) {
            LOGGER.error("Set Activity Calendar Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, resultString), messageType);

    }
}
