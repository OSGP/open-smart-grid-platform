/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.services.CorrelationIdProviderService;
import org.opensmartgridplatform.domain.core.validation.Identification;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActivityCalendar;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotifications;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DefinableLoadProfileConfigurationData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetConfigurationObjectRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupSms;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetClockConfigurationRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.UpdateFirmwareRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service(value = "wsSmartMeteringConfigurationService")
@Validated
public class ConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    /**
     * @param organisationIdentification
     * @param requestData
     * @throws FunctionalException
     */
    public String requestSetAdministrativeStatus(final String organisationIdentification,
            final String deviceIdentification, final AdministrativeStatusType requestData, final int messagePriority,
            final Long scheduleTime) throws FunctionalException {
        return this.enqueueSetAdministrativeStatus(organisationIdentification, deviceIdentification, requestData,
                messagePriority, scheduleTime);
    }

    public String enqueueSetAdministrativeStatus(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            @Identification final AdministrativeStatusType requestData, final int messagePriority,
            final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_ADMINISTRATIVE_STATUS);

        LOGGER.info(
                "enqueueSetAdministrativeStatus called with organisation {} and device {}, set administrative status to {}",
                organisationIdentification, deviceIdentification, requestData);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.SET_ADMINISTRATIVE_STATUS.name(),
                messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(requestData).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    /**
     * Checks if the organization (identified by the organisationIdentification)
     * is allowed to execute this function. Creates a correlation id, sends the
     * get firmware request from the ws-adapter to the domain-adapter and
     * returns the correlation id.
     *
     * @param organisationIdentification
     *            {@link String} containing the organization identification
     * @param deviceIdentification
     *            {@link String} containing the device identification
     * @param messagePriority
     *            contains the message priority
     * @param scheduleTime
     *            contains the time when the message is scheduled to be executed
     * @return the correlation id belonging to the request
     * @throws FunctionalException
     *             is thrown when either the device or organization cannot be
     *             found or the organization is not allowed to execute the
     *             function
     */
    public String enqueueGetFirmwareRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final int messagePriority, final Long scheduleTime)
            throws FunctionalException {
        LOGGER.debug("Queue get firmware request");

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_FIRMWARE_VERSION);

        LOGGER.debug("enqueueGetFirmwareRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.GET_FIRMWARE_VERSION.name(), messagePriority,
                scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueUpdateFirmwareRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final UpdateFirmwareRequestData updateFirmwareRequestData, final int messagePriority,
            final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.UPDATE_FIRMWARE);

        LOGGER.debug("enqueueUpdateFirmwareRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.UPDATE_FIRMWARE.name(), messagePriority,
                scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(updateFirmwareRequestData).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;

    }

    public String requestGetAdministrativeStatus(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime)
            throws FunctionalException {
        return this.enqueueGetAdministrativeStatus(organisationIdentification, deviceIdentification, messagePriority,
                scheduleTime);
    }

    private String enqueueGetAdministrativeStatus(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_ADMINISTRATIVE_STATUS);

        LOGGER.debug("enqueueGetAdministrativeStatus called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.GET_ADMINISTRATIVE_STATUS.name(),
                messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(AdministrativeStatusType.UNDEFINED).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSetSpecialDaysRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SpecialDaysRequest requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_SPECIAL_DAYS);

        LOGGER.debug("enqueueSetSpecialDaysRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.SET_SPECIAL_DAYS.name(), messagePriority,
                scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(requestData).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSetConfigurationObjectRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SetConfigurationObjectRequest requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_CONFIGURATION_OBJECT);

        LOGGER.debug("enqueueSetConfigurationObjectRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.SET_CONFIGURATION_OBJECT.name(),
                messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(requestData).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSetPushSetupAlarmRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final PushSetupAlarm pushSetupAlarm,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_PUSH_SETUP_ALARM);

        LOGGER.debug("enqueueSetPushSetupAlarmRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.SET_PUSH_SETUP_ALARM.name(), messagePriority,
                scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(pushSetupAlarm).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSetPushSetupSmsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final PushSetupSms pushSetupSms,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_PUSH_SETUP_SMS);

        LOGGER.debug("enqueueSetPushSetupSmsRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.SET_PUSH_SETUP_SMS.name(), messagePriority,
                scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(pushSetupSms).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSetAlarmNotificationsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final AlarmNotifications alarmSwitches,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_ALARM_NOTIFICATIONS);

        LOGGER.debug("enqueueSetAlarmNotificationsRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.SET_ALARM_NOTIFICATIONS.name(), messagePriority,
                scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(alarmSwitches).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSetEncryptionKeyExchangeOnGMeterRequest(
            @Identification final String organisationIdentification, @Identification final String deviceIdentification,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER);

        LOGGER.debug("enqueueSetEncryptionKeyExchangeOnGMeterRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER.name(),
                messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueGetMbusEncryptionKeyStatusRequest(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_MBUS_ENCRYPTION_KEY_STATUS);

        LOGGER.debug("enqueueGetMbusEncryptionKeyStatusRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.GET_MBUS_ENCRYPTION_KEY_STATUS.name(),
                messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueGetMbusEncryptionKeyStatusByChannelRequest(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime, final short channel)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device,
                DeviceFunction.GET_MBUS_ENCRYPTION_KEY_STATUS_BY_CHANNEL);

        LOGGER.debug("enqueueGetMbusEncryptionKeyStatusByChannelRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                MessageType.GET_MBUS_ENCRYPTION_KEY_STATUS_BY_CHANNEL.name(), messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata)
                .request(new GetMbusEncryptionKeyStatusByChannelRequestData(channel)).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSetActivityCalendarRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ActivityCalendar activityCalendar,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_ACTIVITY_CALENDAR);

        LOGGER.debug("enqueueSetActivityCalendarRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.SET_ACTIVITY_CALENDAR.name(), messagePriority,
                scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(activityCalendar).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueReplaceKeysRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SetKeysRequestData keySet,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.REPLACE_KEYS);

        LOGGER.debug("enqueueReplaceKeysRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.REPLACE_KEYS.name(), messagePriority,
                scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(keySet).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSetClockConfigurationRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final SetClockConfigurationRequestData clockConfigurationRequestData, final int messagePriority,
            final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_CLOCK_CONFIGURATION);

        LOGGER.debug("enqueueSetClockConfigurationRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.SET_CLOCK_CONFIGURATION.name(), messagePriority,
                scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(clockConfigurationRequestData).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueGetConfigurationObjectRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final GetConfigurationObjectRequest requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_CONFIGURATION_OBJECT);

        LOGGER.debug("enqueueGetConfigurationObjectRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.GET_CONFIGURATION_OBJECT.name(),
                messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(requestData).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    /**
     * Checks if the organization (identified by the organisationIdentification)
     * is allowed to execute this function. Creates a correlation id, sends the
     * generate and replace request from the ws-adapter to the domain-adapter
     * and returns the correlation id.
     *
     * @param organisationIdentification
     *            {@link String} containing the organization identification
     * @param deviceIdentification
     *            {@link String} containing the device identification
     * @param messagePriority
     *            contains the message priority
     * @param scheduleTime
     *            contains the time when the message is scheduled to be executed
     * @return the correlation id belonging to the request
     * @throws FunctionalException
     *             is thrown when either the device or organization cannot be
     *             found or the organization is not allowed to execute the
     *             function
     */
    public String enqueueGenerateAndReplaceKeysRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final int messagePriority, final Long scheduleTime)
            throws FunctionalException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GENERATE_AND_REPLACE_KEYS);

        LOGGER.debug("Enqueue generate and replace keys request called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.GENERATE_AND_REPLACE_KEYS.name(),
                messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueConfigureDefinableLoadProfileRequest(final String organisationIdentification,
            final String deviceIdentification,
            final DefinableLoadProfileConfigurationData definableLoadProfileConfigurationData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.CONFIGURE_DEFINABLE_LOAD_PROFILE);

        LOGGER.debug("enqueueConfigureDefinableLoadProfileRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.CONFIGURE_DEFINABLE_LOAD_PROFILE.name(),
                messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(definableLoadProfileConfigurationData).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSetMbusUserKeyByChannelRequest(final String organisationIdentification,
            final String deviceIdentification,
            final SetMbusUserKeyByChannelRequestData setMbusUserKeyByChannelRequestData, final int messagePriority,
            final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_MBUS_USER_KEY_BY_CHANNEL);

        LOGGER.debug("enqueueSetMbusUserKeyByChannelRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.SET_MBUS_USER_KEY_BY_CHANNEL.name(),
                messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(setMbusUserKeyByChannelRequestData).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

}
