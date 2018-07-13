/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.tariffswitching.endpoints;

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

import com.alliander.osgp.adapter.ws.endpointinterceptors.MessagePriority;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleRequest;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleResponse;
import com.alliander.osgp.adapter.ws.tariffswitching.application.mapping.ScheduleManagementMapper;
import com.alliander.osgp.adapter.ws.tariffswitching.application.services.ScheduleManagementService;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.wsheaderattribute.priority.MessagePriorityEnum;

@Endpoint
public class TariffSwitchingScheduleManagementEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(TariffSwitchingScheduleManagementEndpoint.class);
    private static final String NAMESPACE = "http://www.alliander.com/schemas/osgp/tariffswitching/schedulemanagement/2014/10";
    private static final ComponentType COMPONENT_WS_TARIFF_SWITCHING = ComponentType.WS_TARIFF_SWITCHING;

    private final ScheduleManagementService scheduleManagementService;
    private final ScheduleManagementMapper scheduleManagementMapper;

    /**
     * Constructor.
     *
     * @param scheduleManagementService
     *            The service instance.
     * @param scheduleManagementMapper
     *            The mapper instance.
     */
    @Autowired
    public TariffSwitchingScheduleManagementEndpoint(
            @Qualifier(value = "wsTariffSwitchingScheduleManagementService") final ScheduleManagementService scheduleManagementService,
            @Qualifier(value = "tariffSwitchingScheduleManagementMapper") final ScheduleManagementMapper scheduleManagementMapper) {
        this.scheduleManagementService = scheduleManagementService;
        this.scheduleManagementMapper = scheduleManagementMapper;
    }

    @PayloadRoot(localPart = "SetScheduleRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SetScheduleAsyncResponse setSchedule(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetScheduleRequest request, @MessagePriority final String messagePriority)
            throws OsgpException {

        LOGGER.info(
                "Set Tariff Schedule Request received from organisation: {} for device: {} with message priority: {}.",
                organisationIdentification, request.getDeviceIdentification(), messagePriority);

        // Get the request parameters, make sure that they are in UTC.
        // Maybe add an adapter to the service, so that all datetime are
        // converted to utc automatically.
        final DateTime scheduleTime = request.getScheduledTime() == null ? null
                : new DateTime(request.getScheduledTime().toGregorianCalendar()).toDateTime(DateTimeZone.UTC);

        final SetScheduleAsyncResponse response = new SetScheduleAsyncResponse();

        try {
            final String correlationUid = this.scheduleManagementService.enqueueSetTariffSchedule(
                    organisationIdentification, request.getDeviceIdentification(),
                    this.scheduleManagementMapper.mapAsList(request.getSchedules(),
                            com.alliander.osgp.domain.core.valueobjects.ScheduleEntry.class),
                    scheduleTime, MessagePriorityEnum.getMessagePriority(messagePriority));

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_TARIFF_SWITCHING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "SetScheduleAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SetScheduleResponse getSetScheduleResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetScheduleAsyncRequest request) throws OsgpException {

        LOGGER.info("Get Tariff Schedule Response Request received from organisation: {} for correlationUID: {}.",
                organisationIdentification, request.getAsyncRequest().getCorrelationUid());

        final SetScheduleResponse response = new SetScheduleResponse();

        try {
            final ResponseMessage message = this.scheduleManagementService
                    .dequeueSetTariffScheduleResponse(request.getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    private void handleException(final Exception e) throws OsgpException {
        // Rethrow exception if it already is a functional or technical
        // exception,
        // otherwise throw new technical exception.
        LOGGER.error("Exception occurred: ", e);
        if (e instanceof OsgpException) {
            throw (OsgpException) e;
        } else {
            throw new TechnicalException(COMPONENT_WS_TARIFF_SWITCHING, e);
        }
    }
}