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

import java.util.List;

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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DefinableLoadProfileConfigurationData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareVersion;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareVersionResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetConfigurationObjectRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetConfigurationObjectResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetClockConfigurationRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetKeysRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.UpdateFirmwareRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.UpdateFirmwareResponse;
import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendarDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DefinableLoadProfileConfigurationDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GMeterInfoDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetConfigurationObjectRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetConfigurationObjectResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetFirmwareVersionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSmsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetClockConfigurationRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetKeysRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.UpdateFirmwareResponseDto;
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

    @Autowired
    private FirmwareService firmwareService;

    public ConfigurationService() {
        // Parameterless constructor required for transactions...
    }

    public void setSpecialDays(final DeviceMessageMetadata deviceMessageMetadata,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest specialDaysRequestValueObject)
                    throws FunctionalException {

        LOGGER.info("setSpecialDays for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final SpecialDaysRequestDto specialDaysRequestDto = this.configurationMapper.map(specialDaysRequestValueObject,
                SpecialDaysRequestDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), specialDaysRequestDto), deviceMessageMetadata.getMessageType(),
                deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());
    }

    public void setConfigurationObject(final DeviceMessageMetadata deviceMessageMetadata,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest setConfigurationObjectRequestValueObject)
                    throws FunctionalException {

        LOGGER.info("setConfigurationObject for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final SetConfigurationObjectRequestDto setConfigurationObjectRequestDto = this.configurationMapper
                .map(setConfigurationObjectRequestValueObject, SetConfigurationObjectRequestDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), setConfigurationObjectRequestDto),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                deviceMessageMetadata.getScheduleTime());
    }

    public void setPushSetupAlarm(final DeviceMessageMetadata deviceMessageMetadata,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm pushSetupAlarm)
                    throws FunctionalException {

        LOGGER.info("setPushSetupAlarm for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

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

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

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
                result, exception, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    public void setAlarmNotifications(final DeviceMessageMetadata deviceMessageMetadata,
            final AlarmNotifications alarmNotifications) throws FunctionalException {

        LOGGER.info("setAlarmNotifications for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

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

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final AdministrativeStatusTypeDto administrativeStatusTypeDto = this.configurationMapper
                .map(administrativeStatusType, AdministrativeStatusTypeDto.class);

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
                result, exception, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    public void getAdministrativeStatus(final DeviceMessageMetadata deviceMessageMetadata,
            final AdministrativeStatusType administrativeStatusType) throws FunctionalException {

        LOGGER.info(
                "Get Administrative Status for organisationIdentification: {} for deviceIdentification: {} to status: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                administrativeStatusType);

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final RequestMessage requestMessage = new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(),
                this.configurationMapper.map(administrativeStatusType, AdministrativeStatusTypeDto.class));
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

        final AdministrativeStatusType administrativeStatusType = this.configurationMapper
                .map(administrativeStatusTypeDto, AdministrativeStatusType.class);

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, osgpException, administrativeStatusType, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    public void setActivityCalendar(final DeviceMessageMetadata deviceMessageMetadata,
            final ActivityCalendar activityCalendar) throws FunctionalException {

        LOGGER.info("set Activity Calendar for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

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
                result, exception, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
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
                result, exception, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
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
                result, exception, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
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
                result, exception, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
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
                result, exception, resultString, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());

    }

    public void setEncryptionKeyExchangeOnGMeter(final DeviceMessageMetadata deviceMessageMetadata)
            throws FunctionalException {

        LOGGER.info(
                "set Encryption Key Exchange On G-Meter for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter gasDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final Device gatewayDevice = gasDevice.getGatewayDevice();
        if (gatewayDevice == null) {
            /*
             * For now throw a FunctionalException, based on the same reasoning
             * as with the channel a couple of lines up. As soon as we have
             * scenario's with direct communication with gas meters this will
             * have to be changed.
             */
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.DOMAIN_SMART_METERING,
                    new AssertionError("Meter for gas reads should have an energy meter as gateway device."));
        }

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                        deviceMessageMetadata.getOrganisationIdentification(), gatewayDevice.getDeviceIdentification(),
                        gatewayDevice.getIpAddress(),
                        new GMeterInfoDto(gasDevice.getChannel(), gasDevice.getDeviceIdentification())),
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

    public void setMbusUserKeyByChannel(final DeviceMessageMetadata deviceMessageMetadata,
            final SetMbusUserKeyByChannelRequestData setMbusUserKeyByChannelRequestData) throws FunctionalException {

        LOGGER.info("Set M-Bus User Key By Channel for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter gatewayDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                        deviceMessageMetadata.getOrganisationIdentification(),
                        deviceMessageMetadata.getDeviceIdentification(), gatewayDevice.getIpAddress(),
                        new SetMbusUserKeyByChannelRequestDataDto(setMbusUserKeyByChannelRequestData.getChannel())),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                deviceMessageMetadata.getScheduleTime());
    }

    public void handleSetMbusUserKeyByChannelResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType responseMessageResultType, final OsgpException exception) {
        LOGGER.info("handleSetMbusUserKeyByChannelResponse for MessageType: {}",
                deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = responseMessageResultType;
        if (exception != null) {
            LOGGER.error("Set M-Bus User Key By Channel Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, deviceMessageMetadata.getMessagePriority()), deviceMessageMetadata.getMessageType());
    }

    public void replaceKeys(final DeviceMessageMetadata deviceMessageMetadata, final SetKeysRequestData keySet)
            throws FunctionalException {

        LOGGER.info("replaceKeys for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final SetKeysRequestDto keySetDto = this.configurationMapper.map(keySet, SetKeysRequestDto.class);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                        deviceMessageMetadata.getOrganisationIdentification(),
                        deviceMessageMetadata.getDeviceIdentification(), smartMeteringDevice.getIpAddress(), keySetDto),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                deviceMessageMetadata.getScheduleTime());
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
                result, exception, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    public void generateAndReplaceKeys(final DeviceMessageMetadata deviceMessageMetadata)
            throws FunctionalException {
        LOGGER.info("Generate and replace keys for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                        deviceMessageMetadata.getOrganisationIdentification(),
                        deviceMessageMetadata.getDeviceIdentification(),
                        smartMeteringDevice.getIpAddress(), null),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                deviceMessageMetadata.getScheduleTime());
    }

    public void handleGenerateAndReplaceKeysResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("Handle generate and replace keys response for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Generate and replace keys response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    /**
     * Delegates the requests of the retrieval of the firmware version(s) from
     * the protocol adapter layer to the core layer
     *
     * @param deviceMessageMetadata
     *            contains the message meta data
     * @param getFirmwareVersion
     *            marker object to request the firmware version(s)
     * @throws FunctionalException
     *             is thrown when the device cannot be found in the database
     */
    public void requestFirmwareVersion(final DeviceMessageMetadata deviceMessageMetadata) throws FunctionalException {

        LOGGER.info("requestFirmwareVersion for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), new GetFirmwareVersionRequestDto()),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                deviceMessageMetadata.getScheduleTime());
    }

    /**
     *
     * Maps the firmware Dto's to value objects and sends it back to the
     * ws-adapter layer
     *
     * @param deviceMessageMetadata
     *            contains the message meta data
     * @param result
     *            indicates whether the execution was successful
     * @param exception
     *            contains the exception if one was thrown
     * @param firmwareVersionList
     *            contains the firmware result list
     */
    public void handleGetFirmwareVersionResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final List<FirmwareVersionDto> firmwareVersionList) {

        LOGGER.info("handleGetFirmwareVersionResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Get firmware version response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        final List<FirmwareVersion> firmwareVersions = this.configurationMapper.mapAsList(firmwareVersionList,
                FirmwareVersion.class);

        final FirmwareVersionResponse firmwareVersionResponse = new FirmwareVersionResponse(firmwareVersions);

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, firmwareVersionResponse, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());

    }

    public void requestUpdateFirmware(final DeviceMessageMetadata deviceMessageMetadata,
            final UpdateFirmwareRequestData updateFirmwareRequestData) throws FunctionalException {

        LOGGER.info("requestUpdateFirmware for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeter = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final String firmwareFileIdentifier = this.firmwareService.determineFirmwareFileIdentifier(smartMeter,
                updateFirmwareRequestData.getVersionByModuleType());

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                        deviceMessageMetadata.getOrganisationIdentification(),
                        deviceMessageMetadata.getDeviceIdentification(), smartMeter.getIpAddress(), firmwareFileIdentifier),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                deviceMessageMetadata.getScheduleTime());
    }

    public void handleUpdateFirmwareResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final UpdateFirmwareResponseDto updateFirmwareResponseDto) throws FunctionalException {

        LOGGER.info("handleUpdateFirmwareResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Update firmware response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        final List<FirmwareVersion> firmwareVersions = this.configurationMapper
                .mapAsList(updateFirmwareResponseDto.getFirmwareVersions(), FirmwareVersion.class);
        final SmartMeter smartMeter = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        this.firmwareService.storeFirmware(smartMeter, updateFirmwareResponseDto.getFirmwareIdentification(),
                firmwareVersions, deviceMessageMetadata.getOrganisationIdentification());

        final UpdateFirmwareResponse updateFirmwareResponse = new UpdateFirmwareResponse(firmwareVersions);

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, updateFirmwareResponse, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    public void setClockConfiguration(final DeviceMessageMetadata deviceMessageMetadata,
            final SetClockConfigurationRequestData setClockConfigurationRequest) throws FunctionalException {

        LOGGER.info("setClockConfiguration for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final SetClockConfigurationRequestDto setClockConfigurationRequestDto = this.configurationMapper
                .map(setClockConfigurationRequest, SetClockConfigurationRequestDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), setClockConfigurationRequestDto),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                deviceMessageMetadata.getScheduleTime());
    }

    public void handleSetClockConfigurationResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleSetClockConfigurationResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Set Clock Configuration Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    public void getConfigurationObject(final DeviceMessageMetadata deviceMessageMetadata,
            final GetConfigurationObjectRequest getConfigurationObjectRequest) throws FunctionalException {

        LOGGER.info("getConfigurationObject for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final GetConfigurationObjectRequestDto getConfigurationObjectRequestDto = this.configurationMapper
                .map(getConfigurationObjectRequest, GetConfigurationObjectRequestDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), getConfigurationObjectRequestDto),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                deviceMessageMetadata.getScheduleTime());
    }

    public void handleGetConfigurationObjectResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception,
            final GetConfigurationObjectResponseDto resultData) {

        LOGGER.info("handle GetConfigurationObject response for MessageType: {}",
                deviceMessageMetadata.getMessageType());

        final GetConfigurationObjectResponse response = this.configurationMapper.map(resultData,
                GetConfigurationObjectResponse.class);

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, response, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }

    public void configureDefinableLoadProfile(final DeviceMessageMetadata deviceMessageMetadata,
            final DefinableLoadProfileConfigurationData definableLoadProfileConfigurationData)
            throws FunctionalException {

        LOGGER.info("configureDefinableLoadProfile for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final DefinableLoadProfileConfigurationDto definableLoadProfileConfigurationDto = this.configurationMapper
                .map(definableLoadProfileConfigurationData, DefinableLoadProfileConfigurationDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeteringDevice.getIpAddress(), definableLoadProfileConfigurationDto),
                deviceMessageMetadata.getMessageType(), deviceMessageMetadata.getMessagePriority(),
                deviceMessageMetadata.getScheduleTime());
    }

    public void handleConfigureDefinableLoadProfileResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleConfigureDefinableLoadProfileResponse for MessageType: {}",
                deviceMessageMetadata.getMessageType());

        ResponseMessageResultType result = deviceResult;
        if (exception != null) {
            LOGGER.error("Configure Definable Load Profile Response not ok. Unexpected Exception", exception);
            result = ResponseMessageResultType.NOT_OK;
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                result, exception, null, deviceMessageMetadata.getMessagePriority()),
                deviceMessageMetadata.getMessageType());
    }
}
