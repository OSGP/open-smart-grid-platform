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

import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageType;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

@Service(value = "wsSmartMeteringConfigurationService")
@Validated
public class ConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    /**
     * @param organisationIdentification
     * @param requestData
     * @throws FunctionalException
     */
    // public String requestGetAdministrationState(final String
    // organisationIdentification, final AdministrativeState requestData)
    // throws FunctionalException {
    // return this.enqueueGetAdministrationState(organisationIdentification,
    // requestData.getDeviceIdentification(),
    // requestData);
    // }
    //
    // public String enqueueGetAdministrationState(@Identification final String
    // organisationIdentification,
    // @Identification final String deviceIdentification, @Identification final
    // AdministrativeState requestData)
    // throws FunctionalException {
    //
    // LOGGER.info("enqueueDaysRequest called with organisation {} and device {}",
    // organisationIdentification,
    // deviceIdentification);
    //
    // final String correlationUid =
    // this.correlationIdProviderService.getCorrelationId(organisationIdentification,
    // deviceIdentification);
    //
    // final SmartMeteringRequestMessage message = new
    // SmartMeteringRequestMessage(
    // SmartMeteringRequestMessageType.GET_ADMINISTRATIVE_STATUS,
    // correlationUid,
    // organisationIdentification,
    // requestData.getDeviceIdentification(), requestData);
    //
    // this.smartMeteringRequestMessageSender.send(message);
    //
    // return correlationUid;
    // }

    /**
     * @param organisationIdentification
     * @param requestData
     * @throws FunctionalException
     */
    public String requestSetAdministrativeStatus(final String organisationIdentification,
            final String deviceIdentification, final AdministrativeStatusType requestData) throws FunctionalException {
        return this.enqueueSetAdministrativeStatus(organisationIdentification, deviceIdentification, requestData);
    }

    public String enqueueSetAdministrativeStatus(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            @Identification final AdministrativeStatusType requestData) throws FunctionalException {

        LOGGER.info(
                "enqueueSetAdministrativeStatus called with organisation {} and device {}, set administrative status to {}",
                organisationIdentification, deviceIdentification, requestData);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.SET_ADMINISTRATIVE_STATUS, correlationUid, organisationIdentification,
                deviceIdentification, requestData);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSpecialDaysRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @Identification final SpecialDaysRequest requestData)
            throws FunctionalException {

        LOGGER.debug("enqueueSpecialDaysRequest called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.REQUEST_SPECIAL_DAYS, correlationUid, organisationIdentification,
                deviceIdentification, requestData);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSetConfigurationObjectRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            @Identification final SetConfigurationObjectRequest requestData) throws FunctionalException {

        LOGGER.debug("enqueueSetConfigurationObjectRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.SET_CONFIGURATION_OBJECT, correlationUid, organisationIdentification,
                deviceIdentification, requestData);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    /**
     * @param organisationIdentification
     * @param requestData
     * @throws FunctionalException
     */
    public String requestSpecialDaysData(final String organisationIdentification, final SpecialDaysRequest requestData)
            throws FunctionalException {
        return this.enqueueSpecialDaysRequest(organisationIdentification, requestData.getDeviceIdentification(),
                requestData);
    }

    /**
     * @param organisationIdentification
     * @param requestData
     * @throws FunctionalException
     */
    public String setConfigurationObject(final String organisationIdentification,
            final SetConfigurationObjectRequest requestData) throws FunctionalException {
        return this.enqueueSetConfigurationObjectRequest(organisationIdentification,
                requestData.getDeviceIdentification(), requestData);
    }

    public String enqueueSetAlarmNotificationsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final AlarmNotifications alarmSwitches)
            throws FunctionalException {

        LOGGER.debug("enqueueSetAlarmNotificationsRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.SET_ALARM_NOTIFICATIONS, correlationUid, organisationIdentification,
                deviceIdentification, alarmSwitches);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSetActivityCalendarRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final ActivityCalendar activityCalendar)
            throws FunctionalException {

        LOGGER.debug("enqueueSetActivityCalendarRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.SET_ACTIVITY_CALENDAR, correlationUid, organisationIdentification,
                deviceIdentification, activityCalendar);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    /**
     * @param organisationIdentification
     * @param deviceIdentification
     * @param alarmSwitches
     * @throws FunctionalException
     */
    public String setAlarmNotifications(final String organisationIdentification, final String deviceIdentification,
            final AlarmNotifications alarmSwitches) throws FunctionalException {
        return this
                .enqueueSetAlarmNotificationsRequest(organisationIdentification, deviceIdentification, alarmSwitches);
    }

    public String setActivityCalendar(final String organisationIdentification, final String deviceIdentification,
            final ActivityCalendar activityCalendar) throws FunctionalException {
        return this.enqueueSetActivityCalendarRequest(organisationIdentification, deviceIdentification,
                activityCalendar);
    }
}
