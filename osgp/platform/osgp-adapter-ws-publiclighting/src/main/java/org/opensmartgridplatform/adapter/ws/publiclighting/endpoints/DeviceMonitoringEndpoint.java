/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.endpoints;

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

import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.DeviceMonitoringMapper;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.services.DeviceMonitoringService;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryResponse;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.valueobjects.PowerUsageHistoryMessageDataContainer;
import org.opensmartgridplatform.domain.core.valueobjects.PowerUsageHistoryResponse;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;

@SuppressWarnings("deprecation")
@Endpoint
public class DeviceMonitoringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMonitoringEndpoint.class);
    private static final String NAMESPACE = "http://www.opensmartgridplatform.org/schemas/publiclighting/devicemonitoring/2014/10";
    private static final ComponentType COMPONENT_WS_PUBLIC_LIGHTING = ComponentType.WS_PUBLIC_LIGHTING;

    private final DeviceMonitoringService deviceMonitoringService;
    private final DeviceMonitoringMapper deviceMonitoringMapper;

    @Autowired
    public DeviceMonitoringEndpoint(
            @Qualifier(value = "wsPublicLightingDeviceMonitoringService") final DeviceMonitoringService deviceMonitoringService,
            @Qualifier(value = "publicLightingDeviceMonitoringMapper") final DeviceMonitoringMapper deviceMonitoringMapper) {
        this.deviceMonitoringService = deviceMonitoringService;
        this.deviceMonitoringMapper = deviceMonitoringMapper;
    }

    @PayloadRoot(localPart = "GetPowerUsageHistoryRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetPowerUsageHistoryAsyncResponse getPowerUsageHistory(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetPowerUsageHistoryRequest request, @MessagePriority final String messagePriority)
            throws OsgpException {

        LOGGER.info(
                "Get Power Usage History Request received from organisation: {} for device: {} with message priority: {}.",
                organisationIdentification, request.getDeviceIdentification(), messagePriority);

        final GetPowerUsageHistoryAsyncResponse response = new GetPowerUsageHistoryAsyncResponse();

        try {
            // Get the request parameters, make sure that they are in UTC.
            // Maybe add an adapter to the service, so that all datetime are
            // converted to utc automatically.
            final DateTime scheduleTime = request.getScheduledTime() == null ? null
                    : new DateTime(request.getScheduledTime().toGregorianCalendar()).toDateTime(DateTimeZone.UTC);

            final PowerUsageHistoryMessageDataContainer powerUsageHistoryMessageDataContainer = new PowerUsageHistoryMessageDataContainer();

            powerUsageHistoryMessageDataContainer.setHistoryTermType(this.deviceMonitoringMapper.map(
                    request.getHistoryTermType(), org.opensmartgridplatform.domain.core.valueobjects.HistoryTermType.class));

            powerUsageHistoryMessageDataContainer.setTimePeriod(this.deviceMonitoringMapper.map(request.getTimePeriod(),
                    org.opensmartgridplatform.domain.core.valueobjects.TimePeriod.class));

            final String correlationUid = this.deviceMonitoringService.enqueueGetPowerUsageHistoryRequest(
                    organisationIdentification, request.getDeviceIdentification(),
                    powerUsageHistoryMessageDataContainer, scheduleTime,
                    MessagePriorityEnum.getMessagePriority(messagePriority));

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_PUBLIC_LIGHTING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "GetPowerUsageHistoryAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetPowerUsageHistoryResponse getGetPowerUsageHistoryResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetPowerUsageHistoryAsyncRequest request) throws OsgpException {

        LOGGER.info("Get PowerUsage History Response received from organisation: {} for device: {}.",
                organisationIdentification, request.getAsyncRequest().getDeviceId());

        final GetPowerUsageHistoryResponse response = new GetPowerUsageHistoryResponse();

        try {
            final ResponseMessage message = this.deviceMonitoringService.dequeueGetPowerUsageHistoryResponse(
                    organisationIdentification, request.getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));

                if (message.getDataObject() != null) {
                    final PowerUsageHistoryResponse powerUsageHistoryResponse = (PowerUsageHistoryResponse) message
                            .getDataObject();
                    response.getPowerUsageData().addAll(this.deviceMonitoringMapper.mapAsList(
                            powerUsageHistoryResponse.getPowerUsageData(),
                            org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.PowerUsageData.class));
                }
            } else {
                LOGGER.info("Get Power Usage History data is null");
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    private void handleException(final Exception e) throws OsgpException {
        // Rethrow exception if it already is a functional or technical
        // exception, otherwise throw new technical exception.
        LOGGER.error("Exception occurred: ", e);
        if (e instanceof OsgpException) {
            throw (OsgpException) e;
        } else {
            throw new TechnicalException(COMPONENT_WS_PUBLIC_LIGHTING, e);
        }
    }
}