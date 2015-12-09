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
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.RetrieveSetActivityCalendarResultRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.RetrieveSetActivityCalendarResultResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarRequest;
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
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
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

    @Autowired
    private MeterResponseDataRepository meterResponseDataRepository;

    public SmartMeteringConfigurationEndpoint() {
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

        final AsyncResponse asyncResponse = new AsyncResponse();
        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
        response.setAsyncResponse(asyncResponse);

        return response;
    }

    @PayloadRoot(localPart = "SetSpecialDaysAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetSpecialDaysResponse getSetSpecialDaysResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetSpecialDaysAsyncRequest request) throws OsgpException {

        final MeterResponseData meterResponseData = this.configurationService.dequeueSetSpecialDaysResponse(request
                .getCorrelationUid());

        // Map response type, and return.
        final SetSpecialDaysResponse response = new SetSpecialDaysResponse();
        if (meterResponseData != null) {
            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
        } else {
            response.setResult(OsgpResultType.NOT_FOUND);
        }

        return response;
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

        final AsyncResponse asyncResponse = new AsyncResponse();
        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
        response.setAsyncResponse(asyncResponse);

        return response;
    }

    @PayloadRoot(localPart = "SetConfigurationObjectAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetConfigurationObjectResponse getSetConfigurationObjectResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetConfigurationObjectAsyncRequest request) throws OsgpException {

        final MeterResponseData meterResponseData = this.configurationService
                .dequeueSetConfigurationObjectResponse(request.getCorrelationUid());

        // Map response type, and return.
        final SetConfigurationObjectResponse response = new SetConfigurationObjectResponse();
        if (meterResponseData != null) {
            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
        } else {
            response.setResult(OsgpResultType.NOT_FOUND);
        }

        return response;
    }

    @PayloadRoot(localPart = "SetActivityCalendarRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetActivityCalendarAsyncResponse setActivityCalendar(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetActivityCalendarRequest request) throws OsgpException {

        LOGGER.info("Incoming SetActivityCalendarRequest for meter: {}.", request.getDeviceIdentification());
        final SetActivityCalendarAsyncResponse response = new SetActivityCalendarAsyncResponse();

        try {

            final String deviceIdentification = request.getDeviceIdentification();
            final ActivityCalendarDataType requestData = request.getActivityCalendarData();

            final ActivityCalendar activityCalendar = this.configurationMapper.map(requestData.getActivityCalendar(),
                    ActivityCalendar.class);

            final String correlationUid = this.configurationService.setActivityCalendar(organisationIdentification,
                    deviceIdentification, activityCalendar);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final Exception e) {

            LOGGER.error("Exception: {} while setting activity calendar on device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }

        return response;

    }

    @PayloadRoot(localPart = "RetrieveSetActivityCalendarResultRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public RetrieveSetActivityCalendarResultResponse retrieveSetActivityCalendarResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final RetrieveSetActivityCalendarResultRequest request) throws OsgpException {

        LOGGER.info("Incoming retrieveSetActivityCalendarResponse for meter: {}", request.getDeviceIdentification());

        final RetrieveSetActivityCalendarResultResponse response = new RetrieveSetActivityCalendarResultResponse();

        try {
            final MeterResponseData meterResponseData = this.meterResponseDataRepository
                    .findSingleResultByCorrelationUid(request.getCorrelationUid());

            if (meterResponseData == null) {
                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_CORRELATION_UID,
                        ComponentType.WS_SMART_METERING);
            }

            if (meterResponseData.getMessageData() instanceof String) {
                response.setResult((String) meterResponseData.getMessageData());
                this.meterResponseDataRepository.delete(meterResponseData);
            } else {
                LOGGER.warn("Incorrect type of response data: {} for correlation UID: {}", meterResponseData.getClass()
                        .getName(), request.getCorrelationUid());
            }

        } catch (final Exception e) {
            if ((e instanceof FunctionalException)
                    && ((FunctionalException) e).getExceptionType() == FunctionalExceptionType.UNKNOWN_CORRELATION_UID) {

                LOGGER.warn("No response data for correlation UID {} in RetrieveSetActivityCalendarResultRequest",
                        request.getCorrelationUid());

                throw e;

            } else {
                LOGGER.error(
                        "Exception: {} while sending SetActivityCalendarResult of device: {} for organisation {}.",
                        new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification });

                this.handleException(e);
            }
        }

        return response;
    }

    @PayloadRoot(localPart = "SetAlarmNotificationsRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetAlarmNotificationsResponse setAlarmNotifications(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetAlarmNotificationsRequest request) throws OsgpException {

        LOGGER.info("Incoming SetAlarmNotificationsRequest for meter: {}.", request.getDeviceIdentification());

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
