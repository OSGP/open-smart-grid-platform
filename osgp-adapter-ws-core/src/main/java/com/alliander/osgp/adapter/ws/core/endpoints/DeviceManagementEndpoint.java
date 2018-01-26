/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.endpoints;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.method.MethodConstraintViolationException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.core.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.core.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Event;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindDevicesResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindEventsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindEventsResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindScheduledTasksRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindScheduledTasksResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceAliasRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceAliasResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceVerificationKeyAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceVerificationKeyAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceVerificationKeyRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetDeviceVerificationKeyResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetMaintenanceStatusRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetMaintenanceStatusResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.UpdateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.UpdateDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.UpdateDeviceSslCertificationAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.UpdateDeviceSslCertificationAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.UpdateDeviceSslCertificationRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.UpdateDeviceSslCertificationResponse;
import com.alliander.osgp.adapter.ws.schema.core.notification.NotificationType;
import com.alliander.osgp.adapter.ws.shared.services.NotificationService;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.ScheduledTask;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.valueobjects.Certification;
import com.alliander.osgp.domain.core.valueobjects.EventNotificationType;
import com.alliander.osgp.domain.core.valueobjects.EventType;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

/**
 * Device Management Endpoint class
 */
// MethodConstraintViolationException is deprecated.
// Will by replaced by equivalent functionality defined
// by the Bean Validation 1.1 API as of Hibernate Validator 5.
@SuppressWarnings("deprecation")
@Endpoint(value = "coreDeviceManagementEndpoint")
public class DeviceManagementEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementEndpoint.class);
    private static final String DEVICE_MANAGEMENT_NAMESPACE = "http://www.alliander.com/schemas/osgp/devicemanagement/2014/10";
    private static final ComponentType COMPONENT_WS_CORE = ComponentType.WS_CORE;

    private static final String EXCEPTION = "Exception: {}, StackTrace: {}";
    private static final String EXCEPTION_WHILE_UPDATING_DEVICE = "Exception: {} while updating device: {} for organisation: {}.";

    private final DeviceManagementService deviceManagementService;
    private final DeviceManagementMapper deviceManagementMapper;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    public DeviceManagementEndpoint(
            @Qualifier(value = "wsCoreDeviceManagementService") final DeviceManagementService deviceManagementService,
            @Qualifier(value = "coreDeviceManagementMapper") final DeviceManagementMapper deviceManagementMapper) {
        this.deviceManagementService = deviceManagementService;
        this.deviceManagementMapper = deviceManagementMapper;
    }

    @PayloadRoot(localPart = "FindAllOrganisationsRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public FindAllOrganisationsResponse findAllOrganisations(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindAllOrganisationsRequest request) throws OsgpException {

        LOGGER.info("Find all organisations for organisation: {}.", organisationIdentification);

        final FindAllOrganisationsResponse response = new FindAllOrganisationsResponse();

        try {
            final List<Organisation> organisations = this.deviceManagementService
                    .findAllOrganisations(organisationIdentification);
            response.getOrganisations().addAll(this.deviceManagementMapper.mapAsList(organisations,
                    com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Organisation.class));
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION, e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "SetEventNotificationsRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public SetEventNotificationsAsyncResponse setEventNotifications(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetEventNotificationsRequest request) throws OsgpException {

        LOGGER.info("Set EventNotifications Request received from organisation: {} for event device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        final SetEventNotificationsAsyncResponse response = new SetEventNotificationsAsyncResponse();

        try {
            final List<EventNotificationType> eventNotifications = new ArrayList<>();
            eventNotifications.addAll(this.deviceManagementMapper.mapAsList(request.getEventNotifications(),
                    EventNotificationType.class));

            final String correlationUid = this.deviceManagementService.enqueueSetEventNotificationsRequest(
                    organisationIdentification, request.getDeviceIdentification(), eventNotifications);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "SetEventNotificationsAsyncRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public SetEventNotificationsResponse getSetEventNotificationsResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetEventNotificationsAsyncRequest request) throws OsgpException {

        LOGGER.info("Get Set Event Notifications Response received from organisation: {} with correlationUid: {}.",
                organisationIdentification, request.getAsyncRequest().getCorrelationUid());

        final SetEventNotificationsResponse response = new SetEventNotificationsResponse();

        try {
            final ResponseMessage message = this.deviceManagementService
                    .dequeueSetEventNotificationsResponse(request.getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "FindEventsRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public FindEventsResponse findEventsRequest(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindEventsRequest request) throws OsgpException {

        LOGGER.info("Find events response for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        // Create response.
        final FindEventsResponse response = new FindEventsResponse();

        try {
            // Get the request parameters, make sure that they are in UTC.
            // Maybe add an adapter to the service, so that all datetime are
            // converted to utc automatically.
            final DateTime from = request.getFrom() == null ? null
                    : new DateTime(request.getFrom().toGregorianCalendar()).toDateTime(DateTimeZone.UTC);
            final DateTime until = request.getUntil() == null ? null
                    : new DateTime(request.getUntil().toGregorianCalendar()).toDateTime(DateTimeZone.UTC);

            // Get all events matching the request.
            final Page<com.alliander.osgp.domain.core.entities.Event> result = this.deviceManagementService.findEvents(
                    organisationIdentification, request.getDeviceIdentification(), request.getPageSize(),
                    request.getPage(), from, until,
                    this.deviceManagementMapper.mapAsList(request.getEventTypes(), EventType.class));

            response.getEvents().addAll(this.deviceManagementMapper.mapAsList(result.getContent(), Event.class));
            response.setPage(new com.alliander.osgp.adapter.ws.schema.core.common.Page());
            response.getPage().setPageSize(result.getSize());
            response.getPage().setTotalPages(result.getTotalPages());
            response.getPage().setCurrentPage(result.getNumber());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION, e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "FindDevicesRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public FindDevicesResponse findDevices(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindDevicesRequest request) throws OsgpException {

        LOGGER.info("Find devices for organisation: {}.", organisationIdentification);

        final FindDevicesResponse response = new FindDevicesResponse();

        try {
            Page<com.alliander.osgp.domain.core.entities.Device> result = this.deviceManagementService.findDevices(
                    organisationIdentification, request.getPageSize(), request.getPage(),
                    this.deviceManagementMapper.map(request.getDeviceFilter(),
                            com.alliander.osgp.domain.core.valueobjects.DeviceFilter.class));

            if (result != null && response.getDevices() != null) {
                response.getDevices().addAll(this.deviceManagementMapper.mapAsList(result.getContent(), Device.class));
                response.setPage(new com.alliander.osgp.adapter.ws.schema.core.common.Page());
                response.getPage().setPageSize(result.getSize());
                response.getPage().setTotalPages(result.getTotalPages());
                response.getPage().setCurrentPage(result.getNumber());
            }

            if (request.isUsePages() != null && !request.isUsePages()) {
                int calls = 0;
                while ((calls += 1) < result.getTotalPages()) {
                    request.setPage(calls);
                    result = this.deviceManagementService.findDevices(organisationIdentification, request.getPageSize(),
                            request.getPage(), this.deviceManagementMapper.map(request.getDeviceFilter(),
                                    com.alliander.osgp.domain.core.valueobjects.DeviceFilter.class));
                    response.getDevices()
                            .addAll(this.deviceManagementMapper.mapAsList(result.getContent(), Device.class));
                }
            }
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION, e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            LOGGER.error(EXCEPTION, e.getMessage(), e.getStackTrace(), e);
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "FindScheduledTasksRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public FindScheduledTasksResponse findScheduledTasks(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindScheduledTasksRequest request) throws OsgpException {

        LOGGER.info("Finding scheduled tasks for organisation: {}.", organisationIdentification);

        final FindScheduledTasksResponse response = new FindScheduledTasksResponse();

        try {
            List<ScheduledTask> scheduledTasks;
            if (request.getDeviceIdentification() == null) {
                scheduledTasks = this.deviceManagementService.findScheduledTasks(organisationIdentification);
            } else {
                scheduledTasks = this.deviceManagementService.findScheduledTasks(organisationIdentification,
                        request.getDeviceIdentification());
            }

            response.getScheduledTask().addAll(this.deviceManagementMapper.mapAsList(scheduledTasks,
                    com.alliander.osgp.adapter.ws.schema.core.devicemanagement.ScheduledTask.class));
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception find Scheduled tasks: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "UpdateDeviceRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public UpdateDeviceResponse updateDevice(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateDeviceRequest request) throws OsgpException {

        LOGGER.info("UpdateDeviceRequest received for device: {} for organisation: {}.",
                request.getDeviceIdentification(), organisationIdentification);

        try {
            final com.alliander.osgp.domain.core.entities.Ssld device = this.deviceManagementMapper
                    .map(request.getUpdatedDevice(), com.alliander.osgp.domain.core.entities.Ssld.class);

            this.deviceManagementService.updateDevice(organisationIdentification, device);

        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception update Device: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            LOGGER.error(EXCEPTION_WHILE_UPDATING_DEVICE, e.getMessage(), request.getDeviceIdentification(),
                    organisationIdentification, e);
            this.handleException(e);
        }

        final UpdateDeviceResponse updateDeviceResponse = new UpdateDeviceResponse();

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                request.getDeviceIdentification());

        final AsyncResponse asyncResponse = new AsyncResponse();
        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceId(request.getDeviceIdentification());

        updateDeviceResponse.setAsyncResponse(asyncResponse);

        try {
            this.notificationService.sendNotification(organisationIdentification, request.getDeviceIdentification(),
                    null, null, null, NotificationType.DEVICE_UPDATED);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return updateDeviceResponse;
    }

    @PayloadRoot(localPart = "SetDeviceAliasRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public SetDeviceAliasResponse setDeviceAlias(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetDeviceAliasRequest request) throws OsgpException {

        LOGGER.info("Setting device alias for device: {} to: {}.", request.getDeviceIdentification(),
                request.getDeviceAlias());

        try {
            this.deviceManagementService.setDeviceAlias(organisationIdentification, request.getDeviceIdentification(),
                    request.getDeviceAlias(), request.getDeviceOutputSettings());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception setting alias Device: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            LOGGER.error(EXCEPTION_WHILE_UPDATING_DEVICE, e.getMessage(), request.getDeviceIdentification(),
                    organisationIdentification, e);
            this.handleException(e);
        }

        final SetDeviceAliasResponse setDeviceAliasResponse = new SetDeviceAliasResponse();

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                request.getDeviceIdentification());

        final AsyncResponse asyncResponse = new AsyncResponse();
        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceId(request.getDeviceIdentification());

        setDeviceAliasResponse.setAsyncResponse(asyncResponse);

        try {
            this.notificationService.sendNotification(organisationIdentification, request.getDeviceIdentification(),
                    null, null, null, NotificationType.DEVICE_UPDATED);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return setDeviceAliasResponse;
    }

    @PayloadRoot(localPart = "SetMaintenanceStatusRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public SetMaintenanceStatusResponse setMaintenanceStatus(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetMaintenanceStatusRequest request) throws OsgpException {

        LOGGER.info("Setting maintenance for device:{} to: {}.", request.getDeviceIdentification(), request.isStatus());

        try {
            this.deviceManagementService.setMaintenanceStatus(organisationIdentification,
                    request.getDeviceIdentification(), request.isStatus());
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception update Device: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            LOGGER.error(EXCEPTION_WHILE_UPDATING_DEVICE, e.getMessage(), request.getDeviceIdentification(),
                    organisationIdentification, e);
            this.handleException(e);
        }

        final SetMaintenanceStatusResponse setMaintenanceStatusResponse = new SetMaintenanceStatusResponse();
        setMaintenanceStatusResponse.setResult(OsgpResultType.OK);

        try {
            this.notificationService.sendNotification(organisationIdentification, request.getDeviceIdentification(),
                    null, null, null, NotificationType.DEVICE_UPDATED);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return setMaintenanceStatusResponse;
    }

    @PayloadRoot(localPart = "UpdateDeviceSslCertificationRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public UpdateDeviceSslCertificationAsyncResponse updateDeviceSslCertification(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateDeviceSslCertificationRequest request) throws OsgpException {

        LOGGER.info("Update Device Ssl Certification Request received from organisation: {} for device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        final UpdateDeviceSslCertificationAsyncResponse response = new UpdateDeviceSslCertificationAsyncResponse();

        try {
            final Certification certification = this.deviceManagementMapper.map(request.getCertification(),
                    Certification.class);

            final String correlationUid = this.deviceManagementService.enqueueUpdateDeviceSslCertificationRequest(
                    organisationIdentification, request.getDeviceIdentification(), certification);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION, e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "UpdateDeviceSslCertificationAsyncRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public UpdateDeviceSslCertificationResponse getUpdateDeviceSslCertificationResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateDeviceSslCertificationAsyncRequest request) throws OsgpException {

        LOGGER.info("Update Device Ssl Certification Response received from organisation: {} with correlationUid: {}.",
                organisationIdentification, request.getAsyncRequest().getCorrelationUid());

        final UpdateDeviceSslCertificationResponse response = new UpdateDeviceSslCertificationResponse();

        try {
            final ResponseMessage message = this.deviceManagementService
                    .dequeueUpdateDeviceSslCertificationResponse(request.getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
            } else {
                LOGGER.debug("Update Device Ssl Certification data is null");
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "SetDeviceVerificationKeyRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public SetDeviceVerificationKeyAsyncResponse setDeviceVerificationKey(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetDeviceVerificationKeyRequest request) throws OsgpException {

        LOGGER.info("Set Device Verification Key Request received from organisation: {} for device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        final SetDeviceVerificationKeyAsyncResponse response = new SetDeviceVerificationKeyAsyncResponse();

        try {
            final String correlationUid = this.deviceManagementService.enqueueSetDeviceVerificationKeyRequest(
                    organisationIdentification, request.getDeviceIdentification(), request.getVerificationKey());

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);

        } catch (final MethodConstraintViolationException e) {
            LOGGER.error(EXCEPTION, e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "SetDeviceVerificationKeyAsyncRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public SetDeviceVerificationKeyResponse getSetDeviceVerificationKeyResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetDeviceVerificationKeyAsyncRequest request) throws OsgpException {

        LOGGER.info("Set Device Verification Key Response received from organisation: {} with correlationUid: {}.",
                organisationIdentification, request.getAsyncRequest().getCorrelationUid());

        final SetDeviceVerificationKeyResponse response = new SetDeviceVerificationKeyResponse();

        try {
            final ResponseMessage message = this.deviceManagementService
                    .dequeueSetDeviceVerificationKeyResponse(request.getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
            } else {
                LOGGER.debug("Set Device Verification Key is null");
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "SetDeviceLifecycleStatusRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public SetDeviceLifecycleStatusAsyncResponse setDeviceLifecycleRequest(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetDeviceLifecycleStatusRequest request) throws OsgpException {

        LOGGER.info("SetDeviceLifecycleStatusRequest received for device: {} and organisation: {}.",
                request.getDeviceIdentification(), organisationIdentification);

        final SetDeviceLifecycleStatusAsyncResponse asyncResponse = new SetDeviceLifecycleStatusAsyncResponse();

        try {
            final String correlationUid = this.deviceManagementService.enqueueSetDeviceLifecycleStatusRequest(
                    organisationIdentification, request.getDeviceIdentification(), request.getDeviceLifecycleStatus());

            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());

        } catch (final Exception e) {
            this.handleException(e);
        }

        return asyncResponse;
    }

    @PayloadRoot(localPart = "SetDeviceLifecycleStatusAsyncRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
    @ResponsePayload
    public SetDeviceLifecycleStatusResponse getSetDeviceLifecycleStatusResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetDeviceLifecycleStatusAsyncRequest asyncRequest) throws OsgpException {

        LOGGER.info(
                "Get Set Device Lifecycle Status Notifications Response received from organisation: {} with correlationUid: {}.",
                organisationIdentification, asyncRequest.getCorrelationUid());

        final SetDeviceLifecycleStatusResponse response = new SetDeviceLifecycleStatusResponse();

        try {
            final ResponseMessage message = this.deviceManagementService
                    .dequeueSetDeviceLifecycleStatusResponse(asyncRequest.getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        if (OsgpResultType.OK.equals(response.getResult())) {
            try {
                this.notificationService.sendNotification(organisationIdentification, asyncRequest.getDeviceId(),
                        response.getResult().name(), asyncRequest.getCorrelationUid(), null,
                        NotificationType.DEVICE_UPDATED);
            } catch (final Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
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
