/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.endpoints;

import java.util.List;

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

import com.alliander.osgp.adapter.ws.core.application.mapping.FirmwareManagementMapper;
import com.alliander.osgp.adapter.ws.core.application.services.FirmwareManagementService;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.core.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.AddDeviceModelRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.AddDeviceModelResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.AddManufacturerRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.AddManufacturerResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeDeviceModelRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeDeviceModelResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeManufacturerRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeManufacturerResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FindAllDeviceModelsRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FindAllDeviceModelsResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FindAllManufacturersRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FindAllManufacturersResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FirmwareVersion;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.RemoveDeviceModelRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.RemoveDeviceModelResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.RemoveManufacturerRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.RemoveManufacturerResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.SwitchFirmwareAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.SwitchFirmwareAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.SwitchFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.SwitchFirmwareResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareResponse;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;
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
    private final FirmwareManagementMapper firmwareManagementMapper;

    @Autowired
    public FirmwareManagementEndpoint(
            @Qualifier(value = "wsCoreFirmwareManagementService") final FirmwareManagementService firmwareManagementService,
            @Qualifier(value = "coreFirmwareManagementMapper") final FirmwareManagementMapper firmwareManagementMapper) {
        this.firmwareManagementService = firmwareManagementService;
        this.firmwareManagementMapper = firmwareManagementMapper;
    }

    // === UPDATE FIRMWARE ===

    @PayloadRoot(localPart = "UpdateFirmwareRequest", namespace = NAMESPACE)
    @ResponsePayload
    public UpdateFirmwareAsyncResponse updateFirmware(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateFirmwareRequest request) throws OsgpException {

        LOGGER.info("UpdateFirmware Request received from organisation {} for device {} with firmware name {}.",
                organisationIdentification, request.getDeviceIdentification(), request.getFirmwareIdentification(),
                request.getScheduledTime());

        final UpdateFirmwareAsyncResponse response = new UpdateFirmwareAsyncResponse();

        try {
            // Get the request parameters, make sure that they are in UTC.
            // Maybe add an adapter to the service, so that all datetime are
            // converted to utc automatically.
            final DateTime scheduleTime = request.getScheduledTime() == null ? null : new DateTime(request
                    .getScheduledTime().toGregorianCalendar()).toDateTime(DateTimeZone.UTC);

            final String correlationUid = this.firmwareManagementService.enqueueUpdateFirmwareRequest(
                    organisationIdentification, request.getDeviceIdentification(), request.getFirmwareIdentification(),
                    scheduleTime);

            final AsyncResponse asyncResponse = new AsyncResponse();
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

    @PayloadRoot(localPart = "UpdateFirmwareAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public UpdateFirmwareResponse getUpdateFirmwareResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateFirmwareAsyncRequest request) throws OsgpException {

        LOGGER.info("GetUpdateFirmwareResponse Request received from organisation {} for device: {}.",
                organisationIdentification, request.getAsyncRequest().getDeviceId());

        final UpdateFirmwareResponse response = new UpdateFirmwareResponse();

        try {
            final ResponseMessage message = this.firmwareManagementService.dequeueUpdateFirmwareResponse(request
                    .getAsyncRequest().getCorrelationUid());
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
    public GetFirmwareVersionAsyncResponse getFirmwareVersion(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetFirmwareVersionRequest request) throws OsgpException {

        LOGGER.info("GetFirmwareVersion Request received from organisation {} for device {}.",
                organisationIdentification, request.getDeviceIdentification());

        final GetFirmwareVersionAsyncResponse response = new GetFirmwareVersionAsyncResponse();

        try {
            final AsyncResponse asyncResponse = new AsyncResponse();
            final String correlationUid = this.firmwareManagementService.enqueueGetFirmwareRequest(
                    organisationIdentification, request.getDeviceIdentification());
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("exception", e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "GetFirmwareVersionAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetFirmwareVersionResponse getGetFirmwareVersionResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetFirmwareVersionAsyncRequest request) throws OsgpException {

        LOGGER.info("GetFirmwareVersionResponse Request received from organisation {} for device: {}.",
                organisationIdentification, request.getAsyncRequest().getDeviceId());

        final GetFirmwareVersionResponse response = new GetFirmwareVersionResponse();
        ResponseMessage message = null;

        try {
            message = this.firmwareManagementService.dequeueGetFirmwareResponse(request.getAsyncRequest()
                    .getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
                if (message.getDataObject() != null) {
                    final List<FirmwareVersion> target = response.getFirmwareVersion();
                    target.addAll(this.firmwareManagementMapper.mapAsList(
                            (List<FirmwareVersionDto>) message.getDataObject(), FirmwareVersion.class));
                } else {
                    LOGGER.info("Get Firmware Version firmware is null");
                }
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    // === MANUFACTURERS LOGIC ===

    @PayloadRoot(localPart = "FindAllManufacturersRequest", namespace = NAMESPACE)
    @ResponsePayload
    public FindAllManufacturersResponse findAllManufacturers(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindAllManufacturersRequest request) throws OsgpException {

        LOGGER.info("Find all Manufacturers for organisation: {}.", organisationIdentification);

        final FindAllManufacturersResponse response = new FindAllManufacturersResponse();

        try {
            final List<Manufacturer> manufacturers = this.firmwareManagementService
                    .findAllManufacturers(organisationIdentification);

            response.getManufacturers().addAll(
                    this.firmwareManagementMapper.mapAsList(manufacturers,
                            com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.Manufacturer.class));
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "AddManufacturerRequest", namespace = NAMESPACE)
    @ResponsePayload
    public AddManufacturerResponse adddManufacturer(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final AddManufacturerRequest request) throws OsgpException {

        LOGGER.info("Adding manufacturer:{}.", request.getManufacturer().getName());

        try {
            this.firmwareManagementService.addManufacturer(organisationIdentification, new Manufacturer(request
                    .getManufacturer().getManufacturerId(), request.getManufacturer().getName(), request.getManufacturer()
                    .isUsePrefix()));
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception adding manufacturer: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            LOGGER.error("Exception: {} while adding manufacturer: {} for organisation {}",
                    new Object[] { e.getMessage(), request.getManufacturer().getManufacturerId(), organisationIdentification }, e);
            this.handleException(e);
        }

        final AddManufacturerResponse addManufacturerResponse = new AddManufacturerResponse();
        addManufacturerResponse.setResult(OsgpResultType.OK);

        return addManufacturerResponse;
    }

    @PayloadRoot(localPart = "ChangeManufacturerRequest", namespace = NAMESPACE)
    @ResponsePayload
    public ChangeManufacturerResponse changeManufacturer(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ChangeManufacturerRequest request) throws OsgpException {

        LOGGER.info("Changeing manufacturer:{}.", request.getManufacturer().getName());

        try {
            this.firmwareManagementService.changeManufacturer(organisationIdentification, new Manufacturer(request
                    .getManufacturer().getManufacturerId(), request.getManufacturer().getName(), request.getManufacturer()
                    .isUsePrefix()));
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception Changeing manufacturer: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            LOGGER.error("Exception: {} while Changeing manufacturer: {} for organisation {}",
                    new Object[] { e.getMessage(), request.getManufacturer().getManufacturerId(), organisationIdentification }, e);
            this.handleException(e);
        }

        final ChangeManufacturerResponse changeManufacturerResponse = new ChangeManufacturerResponse();
        changeManufacturerResponse.setResult(OsgpResultType.OK);

        return changeManufacturerResponse;
    }

    @PayloadRoot(localPart = "RemoveManufacturerRequest", namespace = NAMESPACE)
    @ResponsePayload
    public RemoveManufacturerResponse removedManufacturer(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final RemoveManufacturerRequest request) throws OsgpException {

        LOGGER.info("Removing manufacturer:{}.", request.getManufacturerId());

        try {
            this.firmwareManagementService.removeManufacturer(organisationIdentification, request.getManufacturerId());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception Removeing manufacturer: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            LOGGER.error("Exception: {} while Removeing manufacturer: {} for organisation {}",
                    new Object[] { e.getMessage(), request.getManufacturerId(), organisationIdentification }, e);
            this.handleException(e);
        }

        final RemoveManufacturerResponse removeManufacturerResponse = new RemoveManufacturerResponse();
        removeManufacturerResponse.setResult(OsgpResultType.OK);

        return removeManufacturerResponse;
    }

    @PayloadRoot(localPart = "SwitchFirmwareRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SwitchFirmwareAsyncResponse switchFirmware(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SwitchFirmwareRequest request) throws OsgpException {

        LOGGER.info("Switch Firmware Request received from organisation: {} for device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        final SwitchFirmwareAsyncResponse response = new SwitchFirmwareAsyncResponse();

        try {
            final String correlationUid = this.firmwareManagementService
                    .enqueueSwitchFirmwareRequest(organisationIdentification, request.getDeviceIdentification(),
                            String.valueOf(request.getVersion()));

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception switch firmware: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "SwitchFirmwareAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SwitchFirmwareResponse getSwitchFirmwareResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SwitchFirmwareAsyncRequest request) throws OsgpException {

        LOGGER.info("Switch Firmware Async Request received from organisation: {} for device: {}.",
                organisationIdentification, request.getAsyncRequest().getDeviceId());

        final SwitchFirmwareResponse response = new SwitchFirmwareResponse();

        try {
            final ResponseMessage message = this.firmwareManagementService.dequeueSwitchFirmwareResponse(request
                    .getAsyncRequest().getCorrelationUid());

            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
            } else {
                LOGGER.debug("Switch Firmware data is null");
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }


    // === DEVICEMODELS LOGIC ===

    @PayloadRoot(localPart = "FindAllDeviceModelsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public FindAllDeviceModelsResponse findAllDeviceModels(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindAllDeviceModelsRequest request) throws OsgpException {

        LOGGER.info("Find all DeviceModels for organisation: {}.", organisationIdentification);

        final FindAllDeviceModelsResponse response = new FindAllDeviceModelsResponse();

        try {
            final List<DeviceModel> deviceModels = this.firmwareManagementService
                    .findAllDeviceModels(organisationIdentification);

            response.getDeviceModels().addAll(this.firmwareManagementMapper.mapAsList(deviceModels,
                    com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.DeviceModel.class));
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception find all devicemodels {}: ", e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "AddDeviceModelRequest", namespace = NAMESPACE)
    @ResponsePayload
    public AddDeviceModelResponse addDeviceModel(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final AddDeviceModelRequest request) throws OsgpException {

        LOGGER.info("Adding deviceModel:{}.", request.getDeviceModel().getModelCode());

        try {
            this.firmwareManagementService.addDeviceModel(organisationIdentification,
                    request.getDeviceModel().getManufacturer(),
                    request.getDeviceModel().getModelCode(),
                    request.getDeviceModel().getDescription());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception adding manufacturer: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            LOGGER.error("Exception: {} while adding devicemodel: {} for organisation {}",
                    new Object[] { e.getMessage(), request.getDeviceModel().getModelCode(), organisationIdentification }, e);

            this.handleException(e);
        }

        final AddDeviceModelResponse addDeviceModelResponse = new AddDeviceModelResponse();
        addDeviceModelResponse.setResult(OsgpResultType.OK);

        return addDeviceModelResponse;
    }

    @PayloadRoot(localPart = "RemoveDeviceModelRequest", namespace = NAMESPACE)
    @ResponsePayload
    public RemoveDeviceModelResponse removedDeviceModel(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final RemoveDeviceModelRequest request) throws OsgpException {

        LOGGER.info("Removing devicemodel:{}.", request.getDeviceModelId());

        try {
            this.firmwareManagementService.removeDeviceModel(organisationIdentification, request.getDeviceManufacturerId(), request.getDeviceModelId());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception removing deviceModel: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            LOGGER.error("Exception: {} while removing deviceModel: {} for organisation {}",
                    new Object[] { e.getMessage(), request.getDeviceModelId(), organisationIdentification }, e);
            this.handleException(e);
        }

        final RemoveDeviceModelResponse removeDeviceModelResponse = new RemoveDeviceModelResponse();
        removeDeviceModelResponse.setResult(OsgpResultType.OK);

        return removeDeviceModelResponse;
    }

    @PayloadRoot(localPart = "ChangeDeviceModelRequest", namespace = NAMESPACE)
    @ResponsePayload
    public ChangeDeviceModelResponse changeDeviceModel(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ChangeDeviceModelRequest request) throws OsgpException {

        LOGGER.info("Changing devicemodel:{}.", request.getDeviceModel().getModelCode());

        try {
            this.firmwareManagementService.changeDeviceModel(organisationIdentification,
                    request.getDeviceModel().getManufacturer(),
                    request.getDeviceModel().getModelCode(),
                    request.getDeviceModel().getDescription());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception Changing devicemodel: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            LOGGER.error("Exception: {} while Changing devicemodel: {} for organisation {}",
                    new Object[] { e.getMessage(), request.getDeviceModel().getModelCode(), organisationIdentification }, e);
            this.handleException(e);
        }

        final ChangeDeviceModelResponse changeDeviceModelResponse = new ChangeDeviceModelResponse();
        changeDeviceModelResponse.setResult(OsgpResultType.OK);

        return changeDeviceModelResponse;
    }

    private void handleException(final Exception e) throws OsgpException {
        // Rethrow exception if it already is a functional or technical
        // exception,
        // otherwise throw new technical exception.
        if (e instanceof OsgpException) {
            throw (OsgpException) e;
        } else {
            throw new TechnicalException(COMPONENT_WS_CORE, e);
        }
    }
}
