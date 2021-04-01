/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import java.io.Serializable;

import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActivityCalendar;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotifications;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DefinableLoadProfileConfigurationData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetConfigurationObjectRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetFirmwareVersionQuery;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupSms;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetClockConfigurationRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetRandomisationSettingsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.UpdateFirmwareRequestData;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.validation.Identification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "wsSmartMeteringConfigurationService")
@Validated
public class ConfigurationService {

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    public String requestSetAdministrativeStatus(final String organisationIdentification,
            final String deviceIdentification, final AdministrativeStatusType requestData, final int messagePriority,
            final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.SET_ADMINISTRATIVE_STATUS, MessageType.SET_ADMINISTRATIVE_STATUS,
                requestData);
    }

    public String enqueueGetFirmwareRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final GetFirmwareVersionQuery requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.GET_FIRMWARE_VERSION, MessageType.GET_FIRMWARE_VERSION, requestData);
    }

    public String enqueueUpdateFirmwareRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final UpdateFirmwareRequestData updateFirmwareRequestData, final int messagePriority,
            final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.UPDATE_FIRMWARE, MessageType.UPDATE_FIRMWARE, updateFirmwareRequestData);
    }

    public String requestGetAdministrativeStatus(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime)
            throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.GET_ADMINISTRATIVE_STATUS, MessageType.GET_ADMINISTRATIVE_STATUS,
                AdministrativeStatusType.UNDEFINED);
    }

    public String enqueueSetSpecialDaysRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SpecialDaysRequest requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.SET_SPECIAL_DAYS, MessageType.SET_SPECIAL_DAYS, requestData);
    }

    public String enqueueSetConfigurationObjectRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SetConfigurationObjectRequest requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.SET_CONFIGURATION_OBJECT, MessageType.SET_CONFIGURATION_OBJECT,
                requestData);
    }

    public String enqueueSetPushSetupAlarmRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final PushSetupAlarm pushSetupAlarm,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.SET_PUSH_SETUP_ALARM, MessageType.SET_PUSH_SETUP_ALARM, pushSetupAlarm);
    }

    public String enqueueSetPushSetupSmsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final PushSetupSms pushSetupSms,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.SET_PUSH_SETUP_SMS, MessageType.SET_PUSH_SETUP_SMS, pushSetupSms);
    }

    public String enqueueSetAlarmNotificationsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final AlarmNotifications alarmSwitches,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.SET_ALARM_NOTIFICATIONS, MessageType.SET_ALARM_NOTIFICATIONS,
                alarmSwitches);
    }

    public String enqueueSetEncryptionKeyExchangeOnGMeterRequest(
            @Identification final String organisationIdentification, @Identification final String deviceIdentification,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER,
                MessageType.SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER, null);
    }

    public String enqueueGetMbusEncryptionKeyStatusRequest(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime)
            throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.GET_MBUS_ENCRYPTION_KEY_STATUS, MessageType.GET_MBUS_ENCRYPTION_KEY_STATUS,
                null);
    }

    public String enqueueGetMbusEncryptionKeyStatusByChannelRequest(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime, final short channel)
            throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.GET_MBUS_ENCRYPTION_KEY_STATUS_BY_CHANNEL,
                MessageType.GET_MBUS_ENCRYPTION_KEY_STATUS_BY_CHANNEL,
                new GetMbusEncryptionKeyStatusByChannelRequestData(channel));
    }

    public String enqueueSetActivityCalendarRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ActivityCalendar activityCalendar,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.SET_ACTIVITY_CALENDAR, MessageType.SET_ACTIVITY_CALENDAR,
                activityCalendar);
    }

    public String enqueueReplaceKeysRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SetKeysRequestData keySet,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.REPLACE_KEYS, MessageType.REPLACE_KEYS, keySet);
    }

    public String enqueueSetClockConfigurationRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final SetClockConfigurationRequestData clockConfigurationRequestData, final int messagePriority,
            final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.SET_CLOCK_CONFIGURATION, MessageType.SET_CLOCK_CONFIGURATION,
                clockConfigurationRequestData);
    }

    public String enqueueGetConfigurationObjectRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final GetConfigurationObjectRequest requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.GET_CONFIGURATION_OBJECT, MessageType.GET_CONFIGURATION_OBJECT,
                requestData);
    }

    public String enqueueGenerateAndReplaceKeysRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final int messagePriority, final Long scheduleTime)
            throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.GENERATE_AND_REPLACE_KEYS, MessageType.GENERATE_AND_REPLACE_KEYS, null);
    }

    public String enqueueConfigureDefinableLoadProfileRequest(final String organisationIdentification,
            final String deviceIdentification,
            final DefinableLoadProfileConfigurationData definableLoadProfileConfigurationData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.CONFIGURE_DEFINABLE_LOAD_PROFILE,
                MessageType.CONFIGURE_DEFINABLE_LOAD_PROFILE, definableLoadProfileConfigurationData);
    }

    public String enqueueSetMbusUserKeyByChannelRequest(final String organisationIdentification,
            final String deviceIdentification,
            final SetMbusUserKeyByChannelRequestData setMbusUserKeyByChannelRequestData, final int messagePriority,
            final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.SET_MBUS_USER_KEY_BY_CHANNEL, MessageType.SET_MBUS_USER_KEY_BY_CHANNEL,
                setMbusUserKeyByChannelRequestData);
    }

    public String enqueueSetRandomisationSettingsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SetRandomisationSettingsRequestData requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {
        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, DeviceFunction.SET_CONFIGURATION_OBJECT, MessageType.SET_RANDOMISATION_SETTINGS,
                requestData);
    }

    /**
     * Checks if the organization (identified by the organisationIdentification)
     * is allowed to execute this function. Creates a correlation id, sends the
     * request from the ws-adapter to the domain-adapter and returns the
     * correlation id.
     *
     * @param organisationIdentification
     *         {@link String} containing the organization identification
     * @param deviceIdentification
     *         {@link String} containing the device identification for the
     *         given device
     * @param messagePriority
     *         contains the message priority
     * @param scheduleTime
     *         contains the time when the message is scheduled to be executed
     * @param deviceFunction
     *         used to check if the organisation is allowed to execute this
     *         request on the given device
     * @param messageType
     *         messageType is added to the message metadata
     * @param requestObject
     *         contains request data if applicable
     *
     * @return the correlation id belonging to the request
     *
     * @throws FunctionalException
     *         is thrown when either the device or organization cannot be
     *         found or the organization is not allowed to execute the
     *         function
     */
    private String enqueueAndSendRequest(final String organisationIdentification, final String deviceIdentification,
            final int messagePriority, final Long scheduleTime, final DeviceFunction deviceFunction,
            final MessageType messageType, final Serializable requestObject) throws FunctionalException {
        log.debug("enqueueAndSendRequest called for messageType {} with organisation {} and device {}", messageType,
                organisationIdentification, deviceIdentification);

        this.checkAllowed(organisationIdentification, deviceIdentification, deviceFunction);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, messageType.name(), messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message;

        if (requestObject == null) {
            message = new SmartMeteringRequestMessage.Builder().deviceMessageMetadata(deviceMessageMetadata).build();
        } else {
            message = new SmartMeteringRequestMessage.Builder().deviceMessageMetadata(deviceMessageMetadata)
                    .request(requestObject)
                    .build();
        }

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    private void checkAllowed(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final DeviceFunction deviceFunction)
            throws FunctionalException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.checkAllowed(organisation, device, deviceFunction);
    }

}
