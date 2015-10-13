/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

import java.util.Collections;
import java.util.Set;

import org.hibernate.validator.method.MethodConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.Alarms;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.SetAlarmNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.SetAlarmNotificationsRequestData;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.SetAlarmNotificationsResponse;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.InstallationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.InstallationService;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmSwitches;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

// MethodConstraintViolationException is deprecated.
// Will by replaced by equivalent functionality defined
// by the Bean Validation 1.1 API as of Hibernate Validator 5.
@SuppressWarnings("deprecation")
@Endpoint
public class SmartMeteringInstallationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringInstallationEndpoint.class);
    private static final String SMARTMETER_INSTALLATION_NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-installation/2014/10";

    @Autowired
    private InstallationService installationService;

    @Autowired
    private InstallationMapper installationMapper;

    public SmartMeteringInstallationEndpoint() {
    }

    @PayloadRoot(localPart = "AddDeviceRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public AddDeviceResponse addDevice(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final AddDeviceRequest request) throws OsgpException {

        LOGGER.info("Incoming AddDeviceRequest for meter: {}.", request.getDevice().getDeviceIdentification());

        final AddDeviceResponse response = new AddDeviceResponse();

        try {

            final SmartMeteringDevice device = this.installationMapper.map(request.getDevice(),
                    SmartMeteringDevice.class);

            final String correlationUid = this.installationService.addDevice(organisationIdentification, device);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDevice().getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final MethodConstraintViolationException e) {

            LOGGER.error("Exception: {} while adding device: {} for organisation {}.", new Object[] { e.getMessage(),
                    request.getDevice().getDeviceIdentification(), organisationIdentification }, e);

            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));

        } catch (final Exception e) {

            LOGGER.error("Exception: {} while adding device: {} for organisation {}.", new Object[] { e.getMessage(),
                    request.getDevice().getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "SetAlarmNotificationsRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public SetAlarmNotificationsResponse setAlarmNotifications(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetAlarmNotificationsRequest request) throws OsgpException {

        LOGGER.info("Incoming SetAlarmNotificationsRequest for meter: {}.", request.getDeviceIdentification());

        final SetAlarmNotificationsResponse response = new SetAlarmNotificationsResponse();

        try {

            final String deviceIdentification = request.getDeviceIdentification();
            final SetAlarmNotificationsRequestData requestData = request.getSetAlarmNotificationsRequestData();
            final Set<AlarmType> disableAlarms = this.mapAlarms(requestData.getDisable());
            final Set<AlarmType> enableAlarms = this.mapAlarms(requestData.getEnable());
            final AlarmSwitches alarmSwitches = new AlarmSwitches(deviceIdentification, enableAlarms, disableAlarms);

            final String correlationUid = this.installationService.setAlarmNotifications(organisationIdentification,
                    deviceIdentification, alarmSwitches);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final MethodConstraintViolationException e) {

            LOGGER.error("Exception: {} while setting alarm notifications on device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));

        } catch (final Exception e) {

            LOGGER.error("Exception: {} while setting alarm notifications on device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }

        return response;
    }

    private Set<AlarmType> mapAlarms(final Alarms alarms) {

        final Set<AlarmType> setOfAlarms;

        if (alarms == null || alarms.getAlarm() == null || alarms.getAlarm().isEmpty()) {
            setOfAlarms = Collections.emptySet();
        } else {
            setOfAlarms = this.installationMapper.mapAsSet(alarms.getAlarm(), AlarmType.class);
        }

        return setOfAlarms;
    }

    private void handleException(final Exception e) throws OsgpException {
        // Rethrow exception if it already is a functional or technical
        // exception,
        // otherwise throw new technical exception.
        if (e instanceof OsgpException) {
            LOGGER.error("Exception occurred: ", e);
            throw (OsgpException) e;
        } else {
            LOGGER.error("Exception occurred: ", e);
            throw new TechnicalException(ComponentType.WS_SMART_METERING, e);
        }
    }
}
