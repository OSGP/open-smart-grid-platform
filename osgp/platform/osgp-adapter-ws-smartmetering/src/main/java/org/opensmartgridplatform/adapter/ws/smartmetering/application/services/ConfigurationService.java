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
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetRandomisationSettingsRequest;
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
            final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.SET_ADMINISTRATIVE_STATUS);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.SET_ADMINISTRATIVE_STATUS, requestData, bypassRetry);
    }

    public String enqueueGetFirmwareRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final GetFirmwareVersionQuery requestData,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.GET_FIRMWARE_VERSION);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.GET_FIRMWARE_VERSION, requestData, bypassRetry);
    }

    public String enqueueUpdateFirmwareRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final UpdateFirmwareRequestData updateFirmwareRequestData, final int messagePriority,
            final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.UPDATE_FIRMWARE);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.UPDATE_FIRMWARE, updateFirmwareRequestData, bypassRetry);
    }

    public String requestGetAdministrativeStatus(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime,
        final boolean bypassRetry)
            throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.GET_ADMINISTRATIVE_STATUS);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.GET_ADMINISTRATIVE_STATUS,
                AdministrativeStatusType.UNDEFINED, bypassRetry);
    }

    public String enqueueSetSpecialDaysRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SpecialDaysRequest requestData,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.SET_SPECIAL_DAYS);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.SET_SPECIAL_DAYS, requestData, bypassRetry);
    }

    public String enqueueSetConfigurationObjectRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SetConfigurationObjectRequest requestData,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.SET_CONFIGURATION_OBJECT);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.SET_CONFIGURATION_OBJECT, requestData, bypassRetry);
    }

    public String enqueueSetPushSetupAlarmRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final PushSetupAlarm pushSetupAlarm,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.SET_PUSH_SETUP_ALARM);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.SET_PUSH_SETUP_ALARM, pushSetupAlarm, bypassRetry);
    }

    public String enqueueSetPushSetupSmsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final PushSetupSms pushSetupSms,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.SET_PUSH_SETUP_SMS);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.SET_PUSH_SETUP_SMS, pushSetupSms, bypassRetry);
    }

    public String enqueueSetAlarmNotificationsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final AlarmNotifications alarmSwitches,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.SET_ALARM_NOTIFICATIONS);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.SET_ALARM_NOTIFICATIONS, alarmSwitches, bypassRetry);
    }

    public String enqueueSetEncryptionKeyExchangeOnGMeterRequest(
            @Identification final String organisationIdentification, @Identification final String deviceIdentification,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER, null, bypassRetry);
    }

    public String enqueueGetMbusEncryptionKeyStatusRequest(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime,
        final boolean bypassRetry)
            throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.GET_MBUS_ENCRYPTION_KEY_STATUS);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.GET_MBUS_ENCRYPTION_KEY_STATUS,
                null, bypassRetry);
    }

    public String enqueueGetMbusEncryptionKeyStatusByChannelRequest(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime, final short channel,
        final boolean bypassRetry)
            throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.GET_MBUS_ENCRYPTION_KEY_STATUS_BY_CHANNEL);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.GET_MBUS_ENCRYPTION_KEY_STATUS_BY_CHANNEL,
                new GetMbusEncryptionKeyStatusByChannelRequestData(channel), bypassRetry);
    }

    public String enqueueSetActivityCalendarRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ActivityCalendar activityCalendar,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.SET_ACTIVITY_CALENDAR);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.SET_ACTIVITY_CALENDAR,
                activityCalendar, bypassRetry);
    }

    public String enqueueReplaceKeysRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SetKeysRequestData keySet,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.REPLACE_KEYS);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.REPLACE_KEYS, keySet, bypassRetry);
    }

    public String enqueueSetClockConfigurationRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final SetClockConfigurationRequestData clockConfigurationRequestData, final int messagePriority,
            final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.SET_CLOCK_CONFIGURATION);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.SET_CLOCK_CONFIGURATION, clockConfigurationRequestData, bypassRetry);
    }

    public String enqueueGetConfigurationObjectRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final GetConfigurationObjectRequest requestData,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.GET_CONFIGURATION_OBJECT);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.GET_CONFIGURATION_OBJECT, requestData, bypassRetry);
    }

    public String enqueueGenerateAndReplaceKeysRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final int messagePriority, final Long scheduleTime,
        final boolean bypassRetry)
            throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.GENERATE_AND_REPLACE_KEYS);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.GENERATE_AND_REPLACE_KEYS
            , null, bypassRetry);
    }

    public String enqueueConfigureDefinableLoadProfileRequest(final String organisationIdentification,
            final String deviceIdentification,
            final DefinableLoadProfileConfigurationData definableLoadProfileConfigurationData,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.CONFIGURE_DEFINABLE_LOAD_PROFILE);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.CONFIGURE_DEFINABLE_LOAD_PROFILE, definableLoadProfileConfigurationData,
            bypassRetry);
    }

    public String enqueueSetMbusUserKeyByChannelRequest(final String organisationIdentification,
            final String deviceIdentification,
            final SetMbusUserKeyByChannelRequestData setMbusUserKeyByChannelRequestData, final int messagePriority,
            final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.SET_MBUS_USER_KEY_BY_CHANNEL);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.SET_MBUS_USER_KEY_BY_CHANNEL,
                setMbusUserKeyByChannelRequestData, bypassRetry);
    }

    public String enqueueSetRandomisationSettingsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SetRandomisationSettingsRequest requestData,
            final int messagePriority, final Long scheduleTime, final boolean bypassRetry) throws FunctionalException {

        this.checkAllowed(organisationIdentification, deviceIdentification, DeviceFunction.SET_RANDOMISATION_SETTINGS);

        return this.enqueueAndSendRequest(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime, MessageType.SET_RANDOMISATION_SETTINGS,
                requestData, bypassRetry);
    }


    /**
     * Creates a correlation id, and a device message Metadata.
     *
     * @param organisationIdentification
     *            {@link String} containing the organization identification
     * @param deviceIdentification
     *            {@link String} containing the device identification for the
     *            given device
     * @param messagePriority
     *            contains the message priority
     * @param scheduleTime
     *            contains the time when the message is scheduled to be executed
     * @param messageType
     *            messageType is added to the message metadata
     * @param requestObject
     *            contains request data if applicable
     * @param bypassRetry
     *            contains the bypass retry option
     *
     * @return the correlation id belonging to the request
     *             found or the organization is not allowed to execute the
     *             function
     */
    private String enqueueAndSendRequest(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime,
            final MessageType messageType, final Serializable requestObject, final boolean bypassRetry) {

        if (log.isDebugEnabled()) {
            log.debug("Enqueue called for messageType {} with organisation {} and device {}", messageType,
                organisationIdentification, deviceIdentification);
        }
        final DeviceMessageMetadata deviceMessageMetadata = this.createMetadata(organisationIdentification,
            deviceIdentification, messagePriority, scheduleTime, MessageType.SET_RANDOMISATION_SETTINGS,
            bypassRetry);

        this.sendMessage(requestObject, deviceMessageMetadata);

        return deviceMessageMetadata.getCorrelationUid();
    }

    /**
     * Sends the request from the ws-adapter to the domain-adapter
     *
     * @param requestObject
     *            contains request data if applicable
     * @param deviceMessageMetadata
     *            contains meta data of the message
     *
     */
    private void sendMessage(final Serializable requestObject,
            final DeviceMessageMetadata deviceMessageMetadata) {
        final SmartMeteringRequestMessage message;

        if (requestObject == null) {
            message = new SmartMeteringRequestMessage.Builder().deviceMessageMetadata(deviceMessageMetadata).build();
        } else {
            message = new SmartMeteringRequestMessage.Builder().deviceMessageMetadata(deviceMessageMetadata)
                .request(requestObject)
                .build();
        }

        this.smartMeteringRequestMessageSender.send(message);
    }

    /**
     * Creates a correlation id, and a device message Metadata.
     *
     * @param organisationIdentification
     *            {@link String} containing the organization identification
     * @param deviceIdentification
     *            {@link String} containing the device identification for the
     *            given device
     * @param messagePriority
     *            contains the message priority
     * @param scheduleTime
     *            contains the time when the message is scheduled to be executed
     * @param messageType
     *            messageType is added to the message metadata
     * @param bypassRetry
     *            contains the bypass retry option
     *
     * @return the device message data
     *
     */
    private DeviceMessageMetadata createMetadata(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime,
            final MessageType messageType, final boolean bypassRetry) {
        if (log.isDebugEnabled()) {
            log.debug("Enqueue called for messageType {} with organisation {} and device {}", messageType,
                organisationIdentification, deviceIdentification);
        }

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
            deviceIdentification);

        return new DeviceMessageMetadata(deviceIdentification,
            organisationIdentification, correlationUid, messageType.name(), messagePriority, scheduleTime, bypassRetry);
    }

    private void checkAllowed(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final DeviceFunction deviceFunction)
            throws FunctionalException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.checkAllowed(organisation, device, deviceFunction);
    }

}
