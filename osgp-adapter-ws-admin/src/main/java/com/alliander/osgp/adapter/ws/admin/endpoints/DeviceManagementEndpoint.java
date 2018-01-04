/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.admin.endpoints;

import java.util.List;

import org.hibernate.validator.method.MethodConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.admin.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.admin.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.admin.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeactivateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeactivateDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeviceAuthorisation;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDeviceAuthorisationsRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDeviceAuthorisationsResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindMessageLogsRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindMessageLogsResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.GetProtocolInfosRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.GetProtocolInfosResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.MessageLog;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.MessageLogPage;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ProtocolInfo;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RevokeKeyRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RevokeKeyResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateDeviceAuthorisationsRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateDeviceAuthorisationsResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateDeviceProtocolRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateDeviceProtocolResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateKeyRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateKeyResponse;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.logging.domain.entities.DeviceLogItem;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

/**
 * Device Management Endpoint class
 */
// MethodConstraintViolationException is deprecated.
// Will by replaced by equivalent functionality defined
// by the Bean Validation 1.1 API as of Hibernate Validator 5.
@SuppressWarnings("deprecation")
@Endpoint(value = "adminDeviceManagmentEndpoint")
public class DeviceManagementEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementEndpoint.class);

    private static final String DEVICE_MANAGEMENT_NAMESPACE = "http://www.alliander.com/schemas/osgp/admin/devicemanagement/2014/10";
    private static final ComponentType COMPONENT_TYPE_WS_ADMIN = ComponentType.WS_ADMIN;
    private static final String EXCEPTION_OCCURED = "Exception Occured.";

    private final DeviceManagementService deviceManagementService;
    private final DeviceManagementMapper deviceManagementMapper;

    /**
     * Constructor
     *
     * @param deviceManagementService
     */
    @Autowired()
    public DeviceManagementEndpoint(
            @Qualifier(value = "wsAdminDeviceManagementService") final DeviceManagementService deviceManagementService,
            @Qualifier(value = "adminDeviceManagementMapper") final DeviceManagementMapper deviceManagementMapper) {
        this.deviceManagementService = deviceManagementService;
        this.deviceManagementMapper = deviceManagementMapper;
    }

    @PayloadRoot(localPart = "CreateOrganisationRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public CreateOrganisationResponse createOrganisation(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final CreateOrganisationRequest request) throws OsgpException {

        LOGGER.info("Create organisation: {}, with name: {}.",
                request.getOrganisation().getOrganisationIdentification(), request.getOrganisation().getName());

        final Organisation organisation = this.deviceManagementMapper.map(request.getOrganisation(),
                Organisation.class);
        // mapping fails for the 'enabled' field of the DeviceManagement Schema
        // Organisation / Organisation
        organisation.setIsEnabled(request.getOrganisation().isEnabled());

        try {
            this.deviceManagementService.addOrganisation(organisationIdentification, organisation);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final TransactionSystemException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new TechnicalException(COMPONENT_TYPE_WS_ADMIN, e);
        } catch (final Exception e) {
            this.handleException(e);
        }

        return new CreateOrganisationResponse();
    }

    @PayloadRoot(localPart = "RemoveOrganisationRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public RemoveOrganisationResponse removeOrganisation(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final RemoveOrganisationRequest request) throws OsgpException {

        LOGGER.info("Remove organisation: {}.", request.getOrganisationIdentification());

        try {
            this.deviceManagementService.removeOrganisation(organisationIdentification,
                    request.getOrganisationIdentification());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final TransactionSystemException ex) {
            LOGGER.error("Exception: {}, StackTrace: {}", ex.getMessage(), ex.getStackTrace(), ex);
            throw new TechnicalException(COMPONENT_TYPE_WS_ADMIN, ex.getApplicationException());
        } catch (final Exception e) {
            this.handleException(e);
        }

        return new RemoveOrganisationResponse();
    }

    @PayloadRoot(localPart = "ActivateOrganisationRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public ActivateOrganisationResponse activateOrganisation(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ActivateOrganisationRequest request) throws OsgpException {

        LOGGER.info("Activate organisation: {}.", request.getOrganisationIdentification());

        try {
            this.deviceManagementService.activateOrganisation(organisationIdentification,
                    request.getOrganisationIdentification());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final TransactionSystemException ex) {
            LOGGER.error("Exception: {}, StackTrace: {}", ex.getMessage(), ex.getStackTrace(), ex);
            throw new TechnicalException(COMPONENT_TYPE_WS_ADMIN, ex.getApplicationException());
        } catch (final Exception e) {
            this.handleException(e);
        }

        return new ActivateOrganisationResponse();
    }

    @PayloadRoot(localPart = "ChangeOrganisationRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public ChangeOrganisationResponse changeOrganisation(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ChangeOrganisationRequest request) throws OsgpException {

        LOGGER.info("Change organisation: {}.", request.getOrganisationIdentification());

        try {
            this.deviceManagementService.changeOrganisation(organisationIdentification,
                    request.getOrganisationIdentification(), request.getNewOrganisationName(),
                    PlatformFunctionGroup.valueOf(request.getNewOrganisationPlatformFunctionGroup().value()),
                    this.deviceManagementMapper.mapAsList(request.getNewOrganisationPlatformDomains(),
                            PlatformDomain.class));
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_TYPE_WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final TransactionSystemException ex) {
            LOGGER.error("Exception: {}, StackTrace: {}", ex.getMessage(), ex.getStackTrace(), ex);
            throw new TechnicalException(COMPONENT_TYPE_WS_ADMIN, ex.getApplicationException());
        } catch (final Exception e) {
            this.handleException(e);
        }

        return new ChangeOrganisationResponse();
    }

    @PayloadRoot(localPart = "FindMessageLogsRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public FindMessageLogsResponse findMessageLogs(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindMessageLogsRequest request) throws OsgpException {

        LOGGER.info("Find message logs of device '{}' for organisation: {}.", request.getDeviceIdentification(),
                organisationIdentification);

        final FindMessageLogsResponse response = new FindMessageLogsResponse();

        try {
            final Page<DeviceLogItem> page = this.deviceManagementService.findOslpMessages(organisationIdentification,
                    request.getDeviceIdentification(), request.getPage());

            // Map to output
            final MessageLogPage logPage = new MessageLogPage();
            logPage.setTotalPages(page.getTotalPages());
            logPage.getMessageLogs().addAll(this.deviceManagementMapper.mapAsList(page.getContent(), MessageLog.class));

            response.setMessageLogPage(logPage);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_TYPE_WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "UpdateDeviceAuthorisationsRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public UpdateDeviceAuthorisationsResponse updateDeviceAuthorisations(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateDeviceAuthorisationsRequest request) throws OsgpException {

        LOGGER.info("Update device autorisations for organisation: {}.", organisationIdentification);
        try {
            for (final DeviceAuthorisation authorization : request.getDeviceAuthorisations()) {
                if (authorization.isRevoked() != null && authorization.isRevoked()) {
                    this.deviceManagementService.removeDeviceAuthorization(organisationIdentification,
                            authorization.getOrganisationIdentification(), authorization.getDeviceIdentification(),
                            this.deviceManagementMapper.map(authorization.getFunctionGroup(),
                                    DeviceFunctionGroup.class));
                } else {
                    this.deviceManagementService.addDeviceAuthorization(organisationIdentification,
                            authorization.getOrganisationIdentification(), authorization.getDeviceIdentification(),
                            this.deviceManagementMapper.map(authorization.getFunctionGroup(),
                                    DeviceFunctionGroup.class));
                }
            }
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_TYPE_WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return new UpdateDeviceAuthorisationsResponse();
    }

    @PayloadRoot(localPart = "FindDeviceAuthorisationsRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public FindDeviceAuthorisationsResponse findDeviceAuthorisations(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindDeviceAuthorisationsRequest request) throws OsgpException {

        LOGGER.info("Find device autorisations for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        final FindDeviceAuthorisationsResponse response = new FindDeviceAuthorisationsResponse();

        try {
            final List<com.alliander.osgp.domain.core.entities.DeviceAuthorization> authorizations = this.deviceManagementService
                    .findDeviceAuthorisations(organisationIdentification, request.getDeviceIdentification());
            response.getDeviceAuthorisations()
                    .addAll(this.deviceManagementMapper.mapAsList(authorizations, DeviceAuthorisation.class));
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_TYPE_WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "FindDevicesWhichHaveNoOwnerRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public FindDevicesWhichHaveNoOwnerResponse findDevicesWhichHaveNoOwner(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindDevicesWhichHaveNoOwnerRequest request) throws OsgpException {

        LOGGER.info("Finding devices which have no owner for organisation: {}.", organisationIdentification);

        final FindDevicesWhichHaveNoOwnerResponse response = new FindDevicesWhichHaveNoOwnerResponse();

        try {
            final List<com.alliander.osgp.domain.core.entities.Device> devicesWithoutOwner = this.deviceManagementService
                    .findDevicesWhichHaveNoOwner(organisationIdentification);

            response.getDevices().addAll(this.deviceManagementMapper.mapAsList(devicesWithoutOwner,
                    com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.Device.class));
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception find device with no owner: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_TYPE_WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "RemoveDeviceRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public RemoveDeviceResponse removeDevice(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final RemoveDeviceRequest request) throws OsgpException {

        LOGGER.info("Remove decvice for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        try {
            this.deviceManagementService.removeDevice(organisationIdentification, request.getDeviceIdentification());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_TYPE_WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return new RemoveDeviceResponse();
    }

    @PayloadRoot(localPart = "SetOwnerRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public SetOwnerResponse setOwner(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetOwnerRequest request) throws OsgpException {

        LOGGER.info("Set owner for organisation: {} and device: {} new owner organisation: {}.",
                organisationIdentification, request.getDeviceIdentification(), request.getOrganisationIdentification());

        try {
            this.deviceManagementService.setOwner(organisationIdentification, request.getDeviceIdentification(),
                    request.getOrganisationIdentification());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_TYPE_WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return new SetOwnerResponse();
    }

    @PayloadRoot(localPart = "UpdateKeyRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public UpdateKeyResponse updateKey(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateKeyRequest request) throws OsgpException {

        LOGGER.info("Update key for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        try {
            this.deviceManagementService.updateKey(organisationIdentification, request.getDeviceIdentification(),
                    request.getPublicKey(), request.getProtocolInfoId());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_TYPE_WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return new UpdateKeyResponse();
    }

    @PayloadRoot(localPart = "RevokeKeyRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public RevokeKeyResponse revokeKey(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final RevokeKeyRequest request) throws OsgpException {

        LOGGER.info("Revoke key for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        try {
            this.deviceManagementService.revokeKey(organisationIdentification, request.getDeviceIdentification());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_TYPE_WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return new RevokeKeyResponse();
    }

    @PayloadRoot(localPart = "GetProtocolInfosRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public GetProtocolInfosResponse getProtocolInfos(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetProtocolInfosRequest request) throws OsgpException {

        LOGGER.info("Get protocol infos for organisation: {}.", organisationIdentification);

        final GetProtocolInfosResponse getProtocolInfosResponse = new GetProtocolInfosResponse();

        try {
            final List<com.alliander.osgp.domain.core.entities.ProtocolInfo> protocolInfos = this.deviceManagementService
                    .getProtocolInfos(organisationIdentification);
            getProtocolInfosResponse.getProtocolInfos().addAll(this.deviceManagementMapper.mapAsList(protocolInfos,
                    com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ProtocolInfo.class));
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_TYPE_WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return getProtocolInfosResponse;
    }

    @PayloadRoot(localPart = "UpdateDeviceProtocolRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public UpdateDeviceProtocolResponse updateDeviceProtocol(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateDeviceProtocolRequest request) throws OsgpException {

        LOGGER.info("Update device protocol for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        try {
            final ProtocolInfo protocolInfo = request.getProtocolInfo();
            this.deviceManagementService.updateDeviceProtocol(organisationIdentification,
                    request.getDeviceIdentification(), protocolInfo.getProtocol(), protocolInfo.getProtocolVersion());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_TYPE_WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return new UpdateDeviceProtocolResponse();
    }

    @PayloadRoot(localPart = "ActivateDeviceRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public ActivateDeviceResponse activateDevice(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ActivateDeviceRequest request) throws OsgpException {

        LOGGER.info("Incoming ActivateDeviceRequest for device: {}.", request.getDeviceIdentification());

        try {

            this.deviceManagementService.activateDeviceRequest(organisationIdentification,
                    request.getDeviceIdentification());

        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception: {} while activating device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_TYPE_WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));

        } catch (final Exception e) {

            LOGGER.error("Exception: {} while activating device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }
        final ActivateDeviceResponse response = new ActivateDeviceResponse();
        response.setResult(OsgpResultType.OK);
        return response;
    }

    @PayloadRoot(localPart = "DeactivateDeviceRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public DeactivateDeviceResponse deactivateDevice(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final DeactivateDeviceRequest request) throws OsgpException {

        LOGGER.info("Incoming DeactivateDeviceRequest for device: {}.", request.getDeviceIdentification());

        try {

            this.deviceManagementService.deactivateDeviceRequest(organisationIdentification,
                    request.getDeviceIdentification());

        } catch (final MethodConstraintViolationException e) {

            LOGGER.error("Exception: {} while deactivating device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_TYPE_WS_ADMIN,
                    new ValidationException(e.getConstraintViolations()));

        } catch (final Exception e) {

            LOGGER.error("Exception: {} while deactivating device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }
        final DeactivateDeviceResponse response = new DeactivateDeviceResponse();
        response.setResult(OsgpResultType.OK);
        return response;
    }

    private void handleException(final Exception e) throws OsgpException {
        // Rethrow exception if it already is a functional or technical
        // exception,
        // otherwise throw new technical exception.
        if (e instanceof OsgpException) {
            throw (OsgpException) e;
        } else {
            throw new TechnicalException(COMPONENT_TYPE_WS_ADMIN, e);
        }
    }
}
