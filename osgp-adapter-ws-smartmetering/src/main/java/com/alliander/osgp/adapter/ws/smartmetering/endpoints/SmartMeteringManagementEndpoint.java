/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

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

import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.DevicePage;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.GetDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.GetDevicesResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.GetStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.GetStatusRequest;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.ManagementService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

//MethodConstraintViolationException is deprecated.
//Will by replaced by equivalent functionality defined
//by the Bean Validation 1.1 API as of Hibernate Validator 5.

@SuppressWarnings("deprecation")
@Endpoint
public class SmartMeteringManagementEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringManagementEndpoint.class);
    private static final String NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-management/2014/10";
    private static final ComponentType COMPONENT_WS_SMART_METERING = ComponentType.WS_SMART_METERING;

    private final ManagementService managementService;
    private final ManagementMapper managementMapper;

    @Autowired
    public SmartMeteringManagementEndpoint(
            @Qualifier(value = "wsSmartMeteringManagementService") final ManagementService managementService,
            @Qualifier(value = "smartMeteringManagementMapper") final ManagementMapper managementMapper) {
        this.managementService = managementService;
        this.managementMapper = managementMapper;
    }

    @PayloadRoot(localPart = "GetDevicesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetDevicesResponse getDevices(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetDevicesRequest request) throws OsgpException {

        LOGGER.info("Get Devices Request received from organisation: {}.", organisationIdentification);

        final GetDevicesResponse response = new GetDevicesResponse();

        try {
            final Page<Device> page = this.managementService.findAllDevices(organisationIdentification,
                    request.getPage());

            final DevicePage devicePage = new DevicePage();
            devicePage.setTotalPages(page.getTotalPages());
            devicePage.getDevices().addAll(
                    this.managementMapper.mapAsList(page.getContent(),
                            com.alliander.osgp.adapter.ws.schema.smartmetering.management.Device.class));
            response.setDevicePage(devicePage);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_WS_SMART_METERING,
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
            @RequestPayload final GetStatusRequest request) throws OsgpException {

        LOGGER.info("Get Status received from organisation: {} for device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        final GetStatusAsyncResponse response = new GetStatusAsyncResponse();

        try {
            final String correlationUid = this.managementService.enqueueGetSmartMeterStatusRequest(
                    organisationIdentification, request.getDeviceIdentification());

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
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
            throw new TechnicalException(COMPONENT_WS_SMART_METERING, e);
        }
    }
}