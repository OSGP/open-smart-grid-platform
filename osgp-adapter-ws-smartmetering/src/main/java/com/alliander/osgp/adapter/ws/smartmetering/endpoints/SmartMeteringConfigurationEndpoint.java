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
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarDataType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequestData;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysResponse;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.ConfigurationService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Endpoint
public class SmartMeteringConfigurationEndpoint extends SmartMeteringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringConfigurationEndpoint.class);
    private static final String SMARTMETER_CONFIGURATION_NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-configuration/2014/10";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ConfigurationMapper configurationMapper;

    public SmartMeteringConfigurationEndpoint() {
    }

    @PayloadRoot(localPart = "SetAdministrativeStatusRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetAdministrativeStatusAsyncResponse setAdministrativeStatus(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetAdministrativeStatusRequest request) throws OsgpException {

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType dataRequest = this.configurationMapper
                .map(request.getEnabled(),
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType.class);

        final String correlationUid = this.configurationService.requestSetAdministrativeStatus(
                organisationIdentification, request.getDeviceIdentification(), dataRequest);

        final SetAdministrativeStatusAsyncResponse asyncResponse = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ObjectFactory()
        .createSetAdministrativeStatusAsyncResponse();
        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(request.getDeviceIdentification());

        return asyncResponse;
    }

    @PayloadRoot(localPart = "SetAdministrativeStatusAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetAdministrativeStatusResponse retrieveSetAdministrativeStatusResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetAdministrativeStatusAsyncRequest request) throws OsgpException {

        try {
            final SetAdministrativeStatusResponse response = new SetAdministrativeStatusResponse();
            final MeterResponseData meterResponseData = this.configurationService
                    .dequeueSetAdministrativeStatusResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }

            return response;
        } catch (final Exception e) {
            throw this.handleException(e);
        }
    }

    @PayloadRoot(localPart = "GetAdministrativeStatusRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetAdministrativeStatusAsyncResponse getAdministrativeStatus(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetAdministrativeStatusRequest request) throws OsgpException {

        final String correlationUid = this.configurationService.requestGetAdministrativeStatus(
                organisationIdentification, request.getDeviceIdentification());

        final GetAdministrativeStatusAsyncResponse asyncResponse = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ObjectFactory()
        .createGetAdministrativeStatusAsyncResponse();

        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(request.getDeviceIdentification());

        return asyncResponse;
    }

    @PayloadRoot(localPart = "GetAdministrativeStatusAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetAdministrativeStatusResponse retrieveGetAdministrativeStatusResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetAdministrativeStatusAsyncRequest request) throws OsgpException {

        try {
            final MeterResponseData meterResponseData = this.configurationService
                    .dequeueGetAdministrativeStatusResponse(request.getCorrelationUid());

            final GetAdministrativeStatusResponse response = new GetAdministrativeStatusResponse();
            final AdministrativeStatusType dataRequest = this.configurationMapper.map(
                    meterResponseData.getMessageData(), AdministrativeStatusType.class);
            response.setEnabled(dataRequest);

            return response;

        } catch (final Exception e) {
            this.handleException(e);
        }

        return null;
    }

    @PayloadRoot(localPart = "SetSpecialDaysRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetSpecialDaysAsyncResponse requestSpecialDaysData(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetSpecialDaysRequest request) throws OsgpException {

        final SetSpecialDaysAsyncResponse response = new SetSpecialDaysAsyncResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest dataRequest = this.configurationMapper
                .map(request, com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest.class);

        final String correlationUid = this.configurationService.enqueueSetSpecialDaysRequest(
                organisationIdentification, dataRequest.getDeviceIdentification(), dataRequest);

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        return response;
    }

    @PayloadRoot(localPart = "SetSpecialDaysAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetSpecialDaysResponse getSetSpecialDaysResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetSpecialDaysAsyncRequest request) throws OsgpException {

        try {
            final SetSpecialDaysResponse response = new SetSpecialDaysResponse();
            final MeterResponseData meterResponseData = this.configurationService.dequeueSetSpecialDaysResponse(request
                    .getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }

            return response;
        } catch (final Exception e) {
            throw this.handleException(e);
        }
    }

    @PayloadRoot(localPart = "SetConfigurationObjectRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetConfigurationObjectAsyncResponse setConfigurationObject(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetConfigurationObjectRequest request) throws OsgpException {

        final SetConfigurationObjectAsyncResponse response = new SetConfigurationObjectAsyncResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest dataRequest = this.configurationMapper
                .map(request,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest.class);

        final String correlationUid = this.configurationService.enqueueSetConfigurationObjectRequest(
                organisationIdentification, dataRequest.getDeviceIdentification(), dataRequest);

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        return response;
    }

    @PayloadRoot(localPart = "SetConfigurationObjectAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetConfigurationObjectResponse getSetConfigurationObjectResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetConfigurationObjectAsyncRequest request) throws OsgpException {

        try {
            final SetConfigurationObjectResponse response = new SetConfigurationObjectResponse();
            final MeterResponseData meterResponseData = this.configurationService
                    .dequeueSetConfigurationObjectResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }

            return response;
        } catch (final Exception e) {
            throw this.handleException(e);
        }
    }

    @PayloadRoot(localPart = "SetActivityCalendarRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetActivityCalendarAsyncResponse setActivityCalendar(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetActivityCalendarRequest request) throws OsgpException {

        LOGGER.info("Incoming SetActivityCalendarRequest for meter: {}.", request.getDeviceIdentification());

        try {
            final SetActivityCalendarAsyncResponse response = new SetActivityCalendarAsyncResponse();
            final String deviceIdentification = request.getDeviceIdentification();
            final ActivityCalendarDataType requestData = request.getActivityCalendarData();

            final ActivityCalendar activityCalendar = this.configurationMapper.map(requestData.getActivityCalendar(),
                    ActivityCalendar.class);

            final String correlationUid = this.configurationService.enqueueSetActivityCalendarRequest(
                    organisationIdentification, deviceIdentification, activityCalendar);

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());
            return response;
        } catch (final Exception e) {

            LOGGER.error("Exception: {} while setting activity calendar on device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            throw this.handleException(e);
        }
    }

    @PayloadRoot(localPart = "SetActivityCalendarAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetActivityCalendarResponse getSetActivityCalendarResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetActivityCalendarAsyncRequest request) throws OsgpException {

        LOGGER.info("Incoming retrieveSetActivityCalendarResponse for meter: {}", request.getDeviceIdentification());

        try {
            final SetActivityCalendarResponse response = new SetActivityCalendarResponse();
            final MeterResponseData meterResponseData = this.configurationService
                    .dequeueSetActivityCalendarResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }
            return response;
        } catch (final Exception e) {
            throw this.handleException(e);
        }
    }

    @PayloadRoot(localPart = "SetAlarmNotificationsRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetAlarmNotificationsAsyncResponse setAlarmNotifications(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetAlarmNotificationsRequest request) throws OsgpException {

        LOGGER.info("Incoming SetAlarmNotificationsRequest for meter: {}.", request.getDeviceIdentification());

        try {
            final SetAlarmNotificationsAsyncResponse response = new SetAlarmNotificationsAsyncResponse();
            final String deviceIdentification = request.getDeviceIdentification();
            final SetAlarmNotificationsRequestData requestData = request.getSetAlarmNotificationsRequestData();

            final AlarmNotifications alarmNotifications = this.configurationMapper.map(
                    requestData.getAlarmNotifications(), AlarmNotifications.class);

            final String correlationUid = this.configurationService.enqueueSetAlarmNotificationsRequest(
                    organisationIdentification, deviceIdentification, alarmNotifications);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

            return response;
        } catch (final Exception e) {

            LOGGER.error("Exception: {} while setting alarm notifications on device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            throw this.handleException(e);
        }
    }

    @PayloadRoot(localPart = "SetAlarmNotificationsAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetAlarmNotificationsResponse getSetAlarmNotificationsResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetAlarmNotificationsAsyncRequest request) throws OsgpException {

        try {
            final SetAlarmNotificationsResponse response = new SetAlarmNotificationsResponse();
            final MeterResponseData meterResponseData = this.configurationService
                    .dequeueSetConfigurationObjectResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }
            return response;
        } catch (final Exception e) {
            throw this.handleException(e);
        }
    }

}
