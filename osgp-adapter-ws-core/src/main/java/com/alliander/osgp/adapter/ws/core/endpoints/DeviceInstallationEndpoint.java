/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.endpoints;

import java.util.List;

import org.hibernate.validator.method.MethodConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceInstallationMapper;
import com.alliander.osgp.adapter.ws.core.application.services.DeviceInstallationService;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.core.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.AddDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.AddDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.UpdateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.UpdateDeviceResponse;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.valueobjects.DeviceStatus;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

// MethodConstraintViolationException is deprecated.
// Will by replaced by equivalent functionality defined
// by the Bean Validation 1.1 API as of Hibernate Validator 5.
@SuppressWarnings("deprecation")
@Endpoint
public class DeviceInstallationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceInstallationEndpoint.class);
    private static final String DEVICE_INSTALLATION_NAMESPACE = "http://www.alliander.com/schemas/osgp/deviceinstallation/2014/10";
    private static final ComponentType COMPONENT_WS_CORE = ComponentType.WS_CORE;

    private static final String EXCEPTION_WHILE_ADDING_DEVICE = "Exception: {} while adding device: {} for organisation {}.";

    private DeviceInstallationService deviceInstallationService;
    private DeviceInstallationMapper deviceInstallationMapper;

    public DeviceInstallationEndpoint() {
    }

    @Autowired
    public DeviceInstallationEndpoint(
            @Qualifier(value = "wsCoreDeviceInstallationService") final DeviceInstallationService deviceInstallationService,
            @Qualifier(value = "coreDeviceInstallationMapper") final DeviceInstallationMapper deviceInstallationMapper) {
        this.deviceInstallationService = deviceInstallationService;
        this.deviceInstallationMapper = deviceInstallationMapper;
    }

    @PayloadRoot(localPart = "GetStatusRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public GetStatusAsyncResponse getStatus(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetStatusRequest request) throws OsgpException {

        LOGGER.info("Get Status received from organisation: {} for device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        final GetStatusAsyncResponse response = new GetStatusAsyncResponse();
        try {
            final String correlationUid = this.deviceInstallationService.enqueueGetStatusRequest(
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

    @PayloadRoot(localPart = "GetStatusAsyncRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public GetStatusResponse getGetStatusResponse(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetStatusAsyncRequest request) throws OsgpException {

        LOGGER.info("Get Status Response received from organisation: {}.", organisationIdentification);

        final GetStatusResponse response = new GetStatusResponse();

        try {
            final ResponseMessage message = this.deviceInstallationService.dequeueGetStatusResponse(request
                    .getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));

                if (message.getDataObject() != null) {
                    final DeviceStatus deviceStatus = (DeviceStatus) message.getDataObject();
                    if (deviceStatus != null) {
                        response.setDeviceStatus(this.deviceInstallationMapper.map(deviceStatus,
                                com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.DeviceStatus.class));
                    }
                }
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "AddDeviceRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public AddDeviceResponse addDevice(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final AddDeviceRequest request) throws OsgpException {

        LOGGER.info("Adding device: {}.", request.getDevice().getDeviceIdentification());

        try {
            final Device device = this.deviceInstallationMapper.map(request.getDevice(), Device.class);

            this.deviceInstallationService.addDevice(organisationIdentification, device);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_WHILE_ADDING_DEVICE, new Object[] { e.getMessage(),
                    request.getDevice().getDeviceIdentification(), organisationIdentification }, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            LOGGER.error(EXCEPTION_WHILE_ADDING_DEVICE, new Object[] { e.getMessage(),
                    request.getDevice().getDeviceIdentification(), organisationIdentification }, e);
            this.handleException(e);
        }

        return new AddDeviceResponse();
    }

    @PayloadRoot(localPart = "UpdateDeviceRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public UpdateDeviceResponse updateDevice(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateDeviceRequest request) throws OsgpException {

        LOGGER.info("Updating device: Original {}, Updated: {}.", request.getDeviceIdentification(), request
                .getUpdatedDevice().getDeviceIdentification());

        try {
            final Ssld device = this.deviceInstallationMapper.map(request.getUpdatedDevice(), Ssld.class);

            this.deviceInstallationService.updateDevice(organisationIdentification, device);

        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception update Device: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            LOGGER.error(EXCEPTION_WHILE_ADDING_DEVICE, new Object[] { e.getMessage(),
                    request.getUpdatedDevice().getDeviceIdentification(), organisationIdentification }, e);
            this.handleException(e);
        }

        return new UpdateDeviceResponse();
    }

    @PayloadRoot(localPart = "FindRecentDevicesRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public FindRecentDevicesResponse findRecentDevices(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindRecentDevicesRequest request) throws OsgpException {

        LOGGER.info("Finding recent devices for organisation: {}.", organisationIdentification);

        final FindRecentDevicesResponse response = new FindRecentDevicesResponse();

        try {
            final List<Device> recentDevices = this.deviceInstallationService
                    .findRecentDevices(organisationIdentification);
            response.getDevices().addAll(
                    this.deviceInstallationMapper.mapAsList(recentDevices,
                            com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device.class));
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception find recent device: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    // === START DEVICE TEST ===

    @PayloadRoot(localPart = "StartDeviceTestRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public StartDeviceTestAsyncResponse startDeviceTest(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final StartDeviceTestRequest request) throws OsgpException {

        LOGGER.info("Start Device Test Request received from organisation: {} for device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        final StartDeviceTestAsyncResponse response = new StartDeviceTestAsyncResponse();

        try {
            final AsyncResponse asyncResponse = new AsyncResponse();
            final String correlationUid = this.deviceInstallationService.enqueueStartDeviceTestRequest(
                    organisationIdentification, request.getDeviceIdentification());
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "StartDeviceTestAsyncRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public StartDeviceTestResponse getStartDeviceTestResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final StartDeviceTestAsyncRequest request) throws OsgpException {

        LOGGER.info("Get Start Device Test Response received from organisation: {} for device: {}.",
                organisationIdentification, request.getAsyncRequest().getDeviceId());

        final StartDeviceTestResponse response = new StartDeviceTestResponse();

        try {
            final ResponseMessage message = this.deviceInstallationService.dequeueStartDeviceTestResponse(request
                    .getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    // === STOP DEVICE TEST ===

    @PayloadRoot(localPart = "StopDeviceTestRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public StopDeviceTestAsyncResponse stopDeviceTest(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final StopDeviceTestRequest request) throws OsgpException {

        LOGGER.info("Stop Device Test Request received from organisation: {} for device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        final StopDeviceTestAsyncResponse response = new StopDeviceTestAsyncResponse();

        try {
            final AsyncResponse asyncResponse = new AsyncResponse();
            final String correlationUid = this.deviceInstallationService.enqueueStopDeviceTestRequest(
                    organisationIdentification, request.getDeviceIdentification());
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "StopDeviceTestAsyncRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
    @ResponsePayload
    public StopDeviceTestResponse getStopDeviceTestResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final StopDeviceTestAsyncRequest request) throws OsgpException {

        LOGGER.info("Get Stop Device Test Response received from organisation: {} for device: {}.",
                organisationIdentification, request.getAsyncRequest().getDeviceId());

        final StopDeviceTestResponse response = new StopDeviceTestResponse();

        try {
            final ResponseMessage message = this.deviceInstallationService.dequeueStopDeviceTestResponse(request
                    .getAsyncRequest().getCorrelationUid());
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
        if (e instanceof OsgpException) {
            LOGGER.error("Exception occurred: ", e);
            throw (OsgpException) e;
        } else {
            LOGGER.error("Exception occurred: ", e);
            throw new TechnicalException(COMPONENT_WS_CORE, e);
        }
    }
}
