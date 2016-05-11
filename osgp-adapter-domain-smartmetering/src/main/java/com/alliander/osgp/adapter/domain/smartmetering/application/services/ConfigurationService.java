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
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendarDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GMeterInfoDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.KeySetDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSmsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
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
            final DeviceMessageMetadata deviceMessageMetadata,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest specialDaysRequestValueObject)
            throws FunctionalException {

        LOGGER.info("requestSpecialDays for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        LOGGER.info(SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG);

        final SpecialDaysRequestDto specialDaysRequestDto = this.configurationMapper.map(specialDaysRequestValueObject,
                SpecialDaysRequestDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), specialDaysRequestDto), deviceMessageMetadata.getMessageType(),
                deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());
    }

    public void setConfigurationObject(
            final DeviceMessageMetadata deviceMessageMetadata,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest setConfigurationObjectRequestValueObject)
            throws FunctionalException {

        LOGGER.info("setConfigurationObject for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        LOGGER.info(SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG);

        final SetConfigurationObjectRequestDto setConfigurationObjectRequestDto = this.configurationMapper.map(
                setConfigurationObjectRequestValueObject, SetConfigurationObjectRequestDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), setConfigurationObjectRequestDto), deviceMessageMetadata
                .getMessageType(), deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());
    }

    public void setPushSetupAlarm(final DeviceMessageMetadata deviceMessageMetadata,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm pushSetupAlarm)
            throws FunctionalException {

        LOGGER.info("setPushSetupAlarm for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        LOGGER.info(SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG);

        final PushSetupAlarmDto pushSetupAlarmDto = this.configurationMapper.map(pushSetupAlarm,
                PushSetupAlarmDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), pushSetupAlarmDto), deviceMessageMetadata.getMessageType(),
                deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());
    }

    public void setPushSetupSms(final DeviceMessageMetadata deviceMessageMetadata,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms pushSetupSms)
            throws FunctionalException {

        LOGGER.info("setPushSetupSms for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        LOGGER.info(SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG);

        final PushSetupSmsDto pushSetupSmsDto = this.configurationMapper.map(pushSetupSms, PushSetupSmsDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), pushSetupSmsDto), deviceMessageMetadata.getMessageType(),
                deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());
    }

    public void handleSpecialDaysResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleSpecialDaysresponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, null, deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata
                .getMessageType());
    }

    public void setAlarmNotifications(final DeviceMessageMetadata deviceMessageMetadata,
            final AlarmNotifications alarmNotifications) throws FunctionalException {

        LOGGER.info("setAlarmNotifications for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        final AlarmNotificationsDto alarmNotificationsDto = this.configurationMapper.map(alarmNotifications,
                AlarmNotificationsDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), alarmNotificationsDto), deviceMessageMetadata.getMessageType(),
                deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());
    }

    public void setAdministrativeStatus(final DeviceMessageMetadata deviceMessageMetadata,
            final AdministrativeStatusType administrativeStatusType) throws FunctionalException {

        LOGGER.info(
                "Set Administrative Status for organisationIdentification: {} for deviceIdentification: {} to status: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                administrativeStatusType);

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        final AdministrativeStatusTypeDto administrativeStatusTypeDto = this.configurationMapper.map(
                administrativeStatusType, AdministrativeStatusTypeDto.class);

        final RequestMessage requestMessage = new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), administrativeStatusTypeDto);
        this.osgpCoreRequestMessageSender.send(requestMessage, deviceMessageMetadata.getMessageType(),
                deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());
    }

    public void handleSetAdministrativeStatusResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleSetAdministrativeStatusResponse for MessageType: {}, with result: {}",
                deviceMessageMetadata.getMessageType(), deviceResult.toString());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, null, deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata
                .getMessageType());
    }

    public void getAdministrativeStatus(final DeviceMessageMetadata deviceMessageMetadata,
            final AdministrativeStatusType administrativeStatusType) throws FunctionalException {

        LOGGER.info(
                "Get Administrative Status for organisationIdentification: {} for deviceIdentification: {} to status: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                administrativeStatusType);

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        LOGGER.info(SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG);
        final RequestMessage requestMessage = new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), this.configurationMapper.map(administrativeStatusType,
                        AdministrativeStatusTypeDto.class));
        this.osgpCoreRequestMessageSender.send(requestMessage, deviceMessageMetadata.getMessageType(),
                deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());

    }

    public void handleGetAdministrativeStatusResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType responseMessageResultType, final OsgpException osgpException,
            final AdministrativeStatusTypeDto administrativeStatusTypeDto) {

        LOGGER.info("handleGetAdministrativeStatusResponse for MessageType: {}, with result: {}",
                deviceMessageMetadata.getMessageType(), responseMessageResultType.toString());

        ResponseMessageResultType result = responseMessageResultType;
        if (osgpException != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, osgpException);
            result = ResponseMessageResultType.NOT_OK;
        }

        final AdministrativeStatusType administrativeStatusType = this.configurationMapper.map(
                administrativeStatusTypeDto, AdministrativeStatusType.class);

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, osgpException, administrativeStatusType, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    public void setActivityCalendar(final DeviceMessageMetadata deviceMessageMetadata,
            final ActivityCalendar activityCalendar) throws FunctionalException {

        LOGGER.info("set Activity Calendar for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        final ActivityCalendarDto activityCalendarDto = this.configurationMapper.map(activityCalendar,
                ActivityCalendarDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), activityCalendarDto), deviceMessageMetadata.getMessageType(),
                deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());
    }

    public void handleSetAlarmNotificationsResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleSetAlarmNotificationsResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Set Alarm Notifications Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, null, deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata
                .getMessageType());
    }

    public void handleSetConfigurationObjectResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handle SetConfigurationObject response for MessageType: {}",
                deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, null, deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata
                .getMessageType());
    }

    public void handleSetPushSetupAlarmResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleSetPushSetupAlarmResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Set Push Setup Alarm Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, null, deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata
                .getMessageType());
    }

    public void handleSetPushSetupSmsResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleSetPushSetupSmsResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Set Push Setup Sms Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, null, deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata
                .getMessageType());
    }

    public void handleSetActivityCalendarResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType responseMessageResultType, final OsgpException exception,
            final String resultString) {
        LOGGER.info("handleSetActivityCalendarResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = responseMessageResultType;
        if (exception != null) {
            LOGGER.error("Set Activity Calendar Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, resultString, deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata
                .getMessageType());

    }

    public void setEncryptionKeyExchangeOnGMeter(final DeviceMessageMetadata deviceMessageMetadata)
            throws FunctionalException {

        LOGGER.info(
                "set Encryption Key Exchange On G-Meter for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter gasDevice = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        final Device gatewayDevice = gasDevice.getGatewayDevice();
        if (gatewayDevice == null) {
            /*
             * For now throw a FunctionalException, based on the same reasoning
             * as with the channel a couple of lines up. As soon as we have
             * scenario's with direct communication with gas meters this will
             * have to be changed.
             */
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                    ComponentType.DOMAIN_SMART_METERING, new AssertionError(
                            "Meter for gas reads should have an energy meter as gateway device."));
        }

        this.osgpCoreRequestMessageSender
        .send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(), deviceMessageMetadata
                .getOrganisationIdentification(), gatewayDevice.getDeviceIdentification(), gatewayDevice
                .getIpAddress(), new GMeterInfoDto(gasDevice.getChannel(), gasDevice.getDeviceIdentification())),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                        deviceMessageMetadata.getScheduleTime());
    }

    public void handleSetEncryptionKeyExchangeOnGMeterResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType responseMessageResultType, final OsgpException exception) {
        LOGGER.info("handleSetEncryptionKeyExchangeOnGMeterResponse for MessageType: {}",
                deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = responseMessageResultType;
        if (exception != null) {
            LOGGER.error("Set Encryption Key Exchange On G-Meter Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata.getMessageType());
    }

    public void replaceKeys(final DeviceMessageMetadata deviceMessageMetadata, final KeySet keySet)
            throws FunctionalException {

        LOGGER.info("replaceKeys for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        final KeySetDto keySetDto = this.configurationMapper.map(keySet, KeySetDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), keySetDto), deviceMessageMetadata.getMessageType(),
                deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());
    }

    public void handleReplaceKeysResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleReplaceKeysResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Replace Keys Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, null, deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata
                .getMessageType());
    }
}
