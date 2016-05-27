/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageType;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetKeysRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.UnknownCorrelationUidException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;

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

    @Autowired
    private MeterResponseDataService meterResponseDataService;

    /**
     * @param organisationIdentification
     * @param requestData
     * @throws FunctionalException
     */
    public String requestSetAdministrativeStatus(final String organisationIdentification,
            final String deviceIdentification, final AdministrativeStatusType requestData, final int messagePriority,
            final Long scheduleTime) throws FunctionalException {
        return this.enqueueSetAdministrativeStatus(organisationIdentification, deviceIdentification, requestData,
                messagePriority,scheduleTime);
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
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.SET_ADMINISTRATIVE_STATUS.toString(), messagePriority,scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(requestData).build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueSetAdministrativeStatusResponse(final String correlationUid)
            throws FunctionalException {
        return this.meterResponseDataService.dequeue(correlationUid);
    }

    public String requestGetAdministrativeStatus(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime)
            throws FunctionalException {
        return this.enqueueGetAdministrativeStatus(organisationIdentification, deviceIdentification, messagePriority,scheduleTime);
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
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.GET_ADMINISTRATIVE_STATUS.toString(), messagePriority,scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(AdministrativeStatusType.UNDEFINED).build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueGetAdministrativeStatusResponse(final String correlationUid)
            throws FunctionalException {
        return this.meterResponseDataService.dequeue(correlationUid);
    }

    public String enqueueSetSpecialDaysRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SpecialDaysRequest requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.REQUEST_SPECIAL_DAYS);

        LOGGER.debug("enqueueSetSpecialDaysRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.REQUEST_SPECIAL_DAYS.toString(), messagePriority,scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(requestData).build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueSetSpecialDaysResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
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
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.SET_CONFIGURATION_OBJECT.toString(), messagePriority,scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(requestData).build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueSetConfigurationObjectResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
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
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.SET_PUSH_SETUP_ALARM.toString(), messagePriority,scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(pushSetupAlarm).build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueSetPushSetupAlarmResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
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
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.SET_PUSH_SETUP_SMS.toString(), messagePriority,scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(pushSetupSms).build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueSetPushSetupSmsResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
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
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.SET_ALARM_NOTIFICATIONS.toString(), messagePriority,scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(alarmSwitches).build();
        // @formatter:on

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
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER.toString(), messagePriority,scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder().deviceMessageMetadata(
                deviceMessageMetadata).build();
        // @formatter:on

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
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.SET_ACTIVITY_CALENDAR.toString(), messagePriority,scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(activityCalendar).build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueSetActivityCalendarResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
    }

    public String enqueueReplaceKeysRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SetKeysRequestData keySet, final int messagePriority,
            final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.REPLACE_KEYS);

        LOGGER.debug("enqueueReplaceKeysRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, SmartMeteringRequestMessageType.REPLACE_KEYS.toString(),
                messagePriority,scheduleTime);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(keySet).build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueReplaceKeysResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
    }
}
