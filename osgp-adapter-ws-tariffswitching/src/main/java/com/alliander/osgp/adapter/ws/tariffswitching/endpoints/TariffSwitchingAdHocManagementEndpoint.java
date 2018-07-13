/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.tariffswitching.endpoints;

import org.hibernate.validator.method.MethodConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.endpointinterceptors.MessagePriority;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.adhocmanagement.DevicePage;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.adhocmanagement.GetDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.adhocmanagement.GetDevicesResponse;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusRequest;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusResponse;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.tariffswitching.application.mapping.AdHocManagementMapper;
import com.alliander.osgp.adapter.ws.tariffswitching.application.services.AdHocManagementService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.valueobjects.DeviceStatusMapped;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.wsheaderattribute.priority.MessagePriorityEnum;

//MethodConstraintViolationException is deprecated.
//Will by replaced by equivalent functionality defined
//by the Bean Validation 1.1 API as of Hibernate Validator 5.

@SuppressWarnings("deprecation")
@Endpoint
public class TariffSwitchingAdHocManagementEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(TariffSwitchingAdHocManagementEndpoint.class);
    private static final String NAMESPACE = "http://www.alliander.com/schemas/osgp/tariffswitching/adhocmanagement/2014/10";
    private static final ComponentType COMPONENT_WS_TARIFF_SWITCHING = ComponentType.WS_TARIFF_SWITCHING;

    private final AdHocManagementService adHocManagementService;
    private final AdHocManagementMapper adHocManagementMapper;

    @Autowired
    public TariffSwitchingAdHocManagementEndpoint(
            @Qualifier(value = "wsTariffSwitchingAdHocManagementService") final AdHocManagementService adHocManagementService,
            @Qualifier(value = "tariffSwitchingAdhocManagementMapper") final AdHocManagementMapper adHocManagementMapper) {
        this.adHocManagementService = adHocManagementService;
        this.adHocManagementMapper = adHocManagementMapper;
    }

    @PayloadRoot(localPart = "GetDevicesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetDevicesResponse getDevices(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetDevicesRequest request) throws OsgpException {

        LOGGER.info("Get Devices Request received from organisation: {}.", organisationIdentification);

        final GetDevicesResponse response = new GetDevicesResponse();

        try {
            final Page<Device> page = this.adHocManagementService.findAllDevices(organisationIdentification,
                    request.getPage());

            final DevicePage devicePage = new DevicePage();
            devicePage.setTotalPages(page.getTotalPages());
            devicePage.getDevices().addAll(this.adHocManagementMapper.mapAsList(page.getContent(),
                    com.alliander.osgp.adapter.ws.schema.tariffswitching.adhocmanagement.Device.class));
            response.setDevicePage(devicePage);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_WS_TARIFF_SWITCHING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    // === GET STATUS ===

    @PayloadRoot(localPart = "GetStatusRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetStatusAsyncResponse getStatus(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetStatusRequest request, @MessagePriority final String messagePriority)
            throws OsgpException {

        LOGGER.info("Get Status received from organisation: {} for device: {} with message priority: {}.",
                organisationIdentification, request.getDeviceIdentification(), messagePriority);

        final GetStatusAsyncResponse response = new GetStatusAsyncResponse();

        try {
            final String correlationUid = this.adHocManagementService.enqueueGetTariffStatusRequest(
                    organisationIdentification, request.getDeviceIdentification(),
                    MessagePriorityEnum.getMessagePriority(messagePriority));

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "GetStatusAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetStatusResponse getGetStatusResponse(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetStatusAsyncRequest request) throws OsgpException {

        LOGGER.info("Get Status Response received from organisation: {} for correlationUid: {}.",
                organisationIdentification, request.getAsyncRequest().getCorrelationUid());

        final GetStatusResponse response = new GetStatusResponse();

        try {
            final ResponseMessage message = this.adHocManagementService
                    .dequeueGetTariffStatusResponse(request.getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
                final DeviceStatusMapped deviceStatus = (DeviceStatusMapped) message.getDataObject();
                if (deviceStatus != null) {
                    response.setDeviceStatus(this.adHocManagementMapper.map(deviceStatus,
                            com.alliander.osgp.adapter.ws.schema.tariffswitching.adhocmanagement.DeviceStatus.class));
                }
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