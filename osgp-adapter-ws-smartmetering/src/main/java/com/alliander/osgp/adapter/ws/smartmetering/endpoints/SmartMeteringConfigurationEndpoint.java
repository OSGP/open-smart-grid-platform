/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequestData;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetTariffRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetTariffRequestData;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetTariffResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDaysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDaysResponse;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.ConfigurationService;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

@Endpoint
public class SmartMeteringConfigurationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringConfigurationEndpoint.class);
    private static final String SMARTMETER_CONFIGURATION_NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-configuration/2014/10";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ConfigurationMapper configurationMapper;

    public SmartMeteringConfigurationEndpoint() {
    }

    @PayloadRoot(localPart = "SpecialDaysRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SpecialDaysResponse requestSpecialDaysData(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SpecialDaysRequest request) throws OsgpException {

        final SpecialDaysResponse response = new SpecialDaysResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest dataRequest = this.configurationMapper
                .map(request, com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest.class);

        final String correlationUid = this.configurationService.requestSpecialDaysData(organisationIdentification,
                dataRequest);

        final AsyncResponse asyncResponse = new AsyncResponse();
        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
        response.setAsyncResponse(asyncResponse);

        return response;
    }

    @PayloadRoot(localPart = "SetConfigurationObjectRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetConfigurationObjectResponse setConfigurationObject(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetConfigurationObjectRequest request) throws OsgpException {

        final SetConfigurationObjectResponse response = new SetConfigurationObjectResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest dataRequest = this.configurationMapper
                .map(request,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest.class);

        final String correlationUid = this.configurationService.setConfigurationObject(organisationIdentification,
                dataRequest);

        final AsyncResponse asyncResponse = new AsyncResponse();
        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
        response.setAsyncResponse(asyncResponse);

        return response;
    }

    @PayloadRoot(localPart = "SetTariffRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetTariffResponse setTariff(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetTariffRequest request) throws OsgpException {

        LOGGER.info("Incoming SetTariffRequest for meter: {}.", request.getDeviceIdentification());
        final SetTariffResponse response = new SetTariffResponse();

        try {

            final String deviceIdentification = request.getDeviceIdentification();
            final SetTariffRequestData requestData = request.getSetTariffRequestData();

            final ActivityCalendar activityCalendar = this.configurationMapper.map(requestData.getTariff(),
                    ActivityCalendar.class);

            final String correlationUid = this.configurationService.setTariff(organisationIdentification,
                    deviceIdentification, activityCalendar);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final Exception e) {

            LOGGER.error("Exception: {} while setting alarm notifications on device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }

        return response;

    }

    @PayloadRoot(localPart = "SetAlarmNotificationsRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetAlarmNotificationsResponse setAlarmNotifications(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetAlarmNotificationsRequest request) throws OsgpException {

        LOGGER.info("Incoming SetAlarmNotificationsRequest for meter: {}.", request.getDeviceIdentification());

        LOGGER.info("=============> Platform, setAlarmNotifications, call received");

        final SetAlarmNotificationsResponse response = new SetAlarmNotificationsResponse();

        try {

            final String deviceIdentification = request.getDeviceIdentification();
            final SetAlarmNotificationsRequestData requestData = request.getSetAlarmNotificationsRequestData();

            final AlarmNotifications alarmNotifications = this.configurationMapper.map(
                    requestData.getAlarmNotifications(), AlarmNotifications.class);

            final String correlationUid = this.configurationService.setAlarmNotifications(organisationIdentification,
                    deviceIdentification, alarmNotifications);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final Exception e) {

            LOGGER.error("Exception: {} while setting alarm notifications on device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }

        return response;
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
