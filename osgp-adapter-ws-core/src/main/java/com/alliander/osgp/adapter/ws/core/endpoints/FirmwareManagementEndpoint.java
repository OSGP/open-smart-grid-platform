/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.endpoints;

import org.hibernate.validator.method.MethodConstraintViolationException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.core.application.services.FirmwareManagementService;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.core.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareResponse;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

//MethodConstraintViolationException is deprecated.
//Will by replaced by equivalent functionality defined
//by the Bean Validation 1.1 API as of Hibernate Validator 5.
@SuppressWarnings("deprecation")
@Endpoint
public class FirmwareManagementEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareManagementEndpoint.class);
    private static final String NAMESPACE = "http://www.alliander.com/schemas/osgp/common/firmwaremanagement/2014/10";
    private static final ComponentType COMPONENT_WS_CORE = ComponentType.WS_CORE;

    private final FirmwareManagementService firmwareManagementService;

    @Autowired
    public FirmwareManagementEndpoint(@Qualifier(value = "wsCoreFirmwareManagementService") final FirmwareManagementService firmwareManagementService) {
        this.firmwareManagementService = firmwareManagementService;
    }

    // === UPDATE FIRMWARE ===

    @PayloadRoot(localPart = "UpdateFirmwareRequest", namespace = NAMESPACE)
    @ResponsePayload
    public UpdateFirmwareAsyncResponse updateFirmware(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateFirmwareRequest request) throws OsgpException {

        LOGGER.info("UpdateFirmware Request received from organisation {} for device {} with firmware name {}.", organisationIdentification,
                request.getDeviceIdentification(), request.getFirmwareIdentification(), request.getScheduledTime());

        final UpdateFirmwareAsyncResponse response = new UpdateFirmwareAsyncResponse();

        try {
            // Get the request parameters, make sure that they are in UTC.
            // Maybe add an adapter to the service, so that all datetime are
            // converted to utc automatically.
            final DateTime scheduleTime = request.getScheduledTime() == null ? null : new DateTime(request.getScheduledTime().toGregorianCalendar())
                    .toDateTime(DateTimeZone.UTC);

            final String correlationUid = this.firmwareManagementService.enqueueUpdateFirmwareRequest(organisationIdentification,
                    request.getDeviceIdentification(), request.getFirmwareIdentification(), scheduleTime);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE, new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "UpdateFirmwareAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public UpdateFirmwareResponse getUpdateFirmwareResponse(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateFirmwareAsyncRequest request) throws OsgpException {

        LOGGER.info("GetUpdateFirmwareResponse Request received from organisation {} for device: {}.", organisationIdentification, request.getAsyncRequest()
                .getDeviceId());

        final UpdateFirmwareResponse response = new UpdateFirmwareResponse();

        try {
            final ResponseMessage message = this.firmwareManagementService.dequeueUpdateFirmwareResponse(organisationIdentification, request.getAsyncRequest()
                    .getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    // === GET FIRMWARE VERSION ===

    @PayloadRoot(localPart = "GetFirmwareVersionRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetFirmwareVersionAsyncResponse getFirmwareVersion(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetFirmwareVersionRequest request) throws OsgpException {

        LOGGER.info("GetFirmwareVersion Request received from organisation {} for device {}.", organisationIdentification, request.getDeviceIdentification());

        final GetFirmwareVersionAsyncResponse response = new GetFirmwareVersionAsyncResponse();

        try {
            final AsyncResponse asyncResponse = new AsyncResponse();
            final String correlationUid = this.firmwareManagementService.enqueueGetFirmwareRequest(organisationIdentification,
                    request.getDeviceIdentification());
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final MethodConstraintViolationException e) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE, new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "GetFirmwareVersionAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetFirmwareVersionResponse getGetFirmwareVersionResponse(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetFirmwareVersionAsyncRequest request) throws OsgpException {

        LOGGER.info("GetFirmwareVersionResponse Request received from organisation {} for device: {}.", organisationIdentification, request.getAsyncRequest()
                .getDeviceId());

        final GetFirmwareVersionResponse response = new GetFirmwareVersionResponse();
        ResponseMessage message = null;

        try {
            message = this.firmwareManagementService.dequeueGetFirmwareResponse(organisationIdentification, request.getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
                if (message.getDataObject() != null) {
                    response.setFirmwareVersion((String) message.getDataObject());
                } else {
                    LOGGER.info("Get Firmware Version firmware is null");
                }
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    private void handleException(final Exception e) throws OsgpException {
        // Rethrow exception if it already is a functional or technical exception,
        // otherwise throw new technical exception.
        if (e instanceof OsgpException) {
            throw (OsgpException) e;
        } else {
            throw new TechnicalException(COMPONENT_WS_CORE, e);
        }
    }
}