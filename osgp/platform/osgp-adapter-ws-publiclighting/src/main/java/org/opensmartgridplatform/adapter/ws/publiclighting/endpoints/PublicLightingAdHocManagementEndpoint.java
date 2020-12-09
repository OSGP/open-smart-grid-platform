/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.endpoints;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.AdHocManagementMapper;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.services.AdHocManagementService;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.DevicePage;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.FindAllDevicesRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.FindAllDevicesResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightMeasurementDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightMeasurementDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.OsgpResultType;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatus;
import org.opensmartgridplatform.domain.core.valueobjects.LightValue;
import org.opensmartgridplatform.domain.core.valueobjects.ResumeScheduleData;
import org.opensmartgridplatform.domain.core.valueobjects.TransitionMessageDataContainer;
import org.opensmartgridplatform.shared.application.config.PageSpecifier;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class PublicLightingAdHocManagementEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicLightingAdHocManagementEndpoint.class);
    private static final String NAMESPACE = "http://www.opensmartgridplatform.org/schemas/publiclighting/adhocmanagement/2014/10";
    private static final ComponentType COMPONENT_WS_PUBLIC_LIGHTING = ComponentType.WS_PUBLIC_LIGHTING;

    private static final String EXCEPTION_OCCURRED = "Exception Occurred";

    private final AdHocManagementService adHocManagementService;
    private final AdHocManagementMapper adHocManagementMapper;

    @Autowired
    public PublicLightingAdHocManagementEndpoint(
            @Qualifier("wsPublicLightingAdHocManagementService") final AdHocManagementService adHocManagementService,
            @Qualifier("publicLightingAdhocManagementMapper") final AdHocManagementMapper adHocManagementMapper) {
        this.adHocManagementService = adHocManagementService;
        this.adHocManagementMapper = adHocManagementMapper;
    }

    @PayloadRoot(localPart = "FindAllDevicesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public FindAllDevicesResponse findAllDevices(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindAllDevicesRequest request) throws OsgpException {

        LOGGER.info("Finding All Devices Request received from organisation: {}.", organisationIdentification);

        final FindAllDevicesResponse response = new FindAllDevicesResponse();

        try {
            final PageSpecifier pageSpecifier = new PageSpecifier(request.getPageSize(), request.getPage());
            final Page<Device> page = this.adHocManagementService.findAllDevices(organisationIdentification,
                    pageSpecifier);

            if (page != null && !page.isEmpty()) {
                final List<Ssld> sslds = page.filter(d -> d instanceof Ssld).map(d -> (Ssld) d).toList();
                final List<LightMeasurementDevice> lmds = page.filter(d -> d instanceof LightMeasurementDevice)
                        .map(d -> (LightMeasurementDevice) d)
                        .toList();

                final DevicePage devicePage = new DevicePage();
                devicePage.setPage(new org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.Page());
                devicePage.getPage().setPageSize(page.getSize());
                devicePage.getPage().setTotalPages(page.getTotalPages());
                devicePage.getPage().setCurrentPage(page.getNumber());
                devicePage.getDevices()
                        .addAll(this.adHocManagementMapper.mapAsList(sslds,
                                org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Ssld.class));
                devicePage.getDevices()
                        .addAll(this.adHocManagementMapper.mapAsList(lmds,
                                org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightMeasurementDevice.class));
                response.setDevicePage(devicePage);
            } else {
                final DevicePage devicePage = new DevicePage();
                devicePage.setPage(new org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.Page());
                devicePage.getPage().setCurrentPage(0);
                devicePage.getPage().setPageSize(request.getPageSize() == null ? 0 : request.getPageSize());
                devicePage.getPage().setTotalPages(0);
                response.setDevicePage(devicePage);
            }
        } catch (final ConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURRED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_WS_PUBLIC_LIGHTING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    // === SET LIGHT ===

    @PayloadRoot(localPart = "SetLightRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SetLightAsyncResponse setLight(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetLightRequest request, @MessagePriority final String messagePriority)
            throws OsgpException {

        LOGGER.info("Set Light Request received from organisation: {} for device: {} with message priority: {}.",
                organisationIdentification, request.getDeviceIdentification(), messagePriority);

        final SetLightAsyncResponse response = new SetLightAsyncResponse();

        try {
            final List<LightValue> lightValues = new ArrayList<>();
            lightValues.addAll(this.adHocManagementMapper.mapAsList(request.getLightValue(), LightValue.class));

            final String correlationUid = this.adHocManagementService.enqueueSetLightRequest(organisationIdentification,
                    request.getDeviceIdentification(), lightValues,
                    MessagePriorityEnum.getMessagePriority(messagePriority));

            final AsyncResponse asyncResponse = new AsyncResponse();

            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());

            response.setAsyncResponse(asyncResponse);
        } catch (final ConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURRED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_WS_PUBLIC_LIGHTING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "SetLightAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SetLightResponse getSetLightResponse(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetLightAsyncRequest request) throws OsgpException {

        LOGGER.info("Get Set Light Response received from organisation: {} with correlationUid: {}.",
                organisationIdentification, request.getAsyncRequest().getCorrelationUid());

        final SetLightResponse response = new SetLightResponse();

        try {
            final ResponseMessage message = this.adHocManagementService
                    .dequeueSetLightResponse(request.getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
            }
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
            final String correlationUid = this.adHocManagementService.enqueueGetStatusRequest(
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
                    .dequeueGetStatusResponse(request.getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
                final DeviceStatus deviceStatus = (DeviceStatus) message.getDataObject();
                if (deviceStatus != null) {
                    response.setDeviceStatus(this.adHocManagementMapper.map(deviceStatus,
                            org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.DeviceStatus.class));
                }
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    // === RESUME SCHEDULE ===

    @PayloadRoot(localPart = "ResumeScheduleRequest", namespace = NAMESPACE)
    @ResponsePayload
    public ResumeScheduleAsyncResponse resumeSchedule(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ResumeScheduleRequest request, @MessagePriority final String messagePriority)
            throws OsgpException {

        LOGGER.info("Resume Schedule Request received from organisation: {} for device: {} with message priority: {}.",
                organisationIdentification, request.getDeviceIdentification(), messagePriority);

        final ResumeScheduleAsyncResponse response = new ResumeScheduleAsyncResponse();

        try {
            final ResumeScheduleData resumeScheduleData = new ResumeScheduleData();
            if (request.getIndex() != null) {
                resumeScheduleData.setIndex(request.getIndex());
            }
            resumeScheduleData.setIsImmediate(request.isIsImmediate());

            final String correlationUid = this.adHocManagementService.enqueueResumeScheduleRequest(
                    organisationIdentification, request.getDeviceIdentification(), resumeScheduleData,
                    MessagePriorityEnum.getMessagePriority(messagePriority));

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
        } catch (final ConstraintViolationException e) {
            LOGGER.error(EXCEPTION_OCCURRED, e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_WS_PUBLIC_LIGHTING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "ResumeScheduleAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public ResumeScheduleResponse getResumeScheduleResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ResumeScheduleAsyncRequest request) throws OsgpException {

        LOGGER.info("Resume Schedule Async Request received from organisation: {} for device: {}.",
                organisationIdentification, request.getAsyncRequest().getDeviceId());

        final ResumeScheduleResponse response = new ResumeScheduleResponse();

        try {
            final ResponseMessage message = this.adHocManagementService
                    .dequeueResumeScheduleResponse(request.getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    // === SET TRANSITION ===

    @PayloadRoot(localPart = "SetTransitionRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SetTransitionAsyncResponse setTransition(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetTransitionRequest request, @MessagePriority final String messagePriority)
            throws OsgpException {

        LOGGER.info("Set Transition Request received from organisation: {} for device: {} with message priority: {}.",
                organisationIdentification, request.getDeviceIdentification(), messagePriority);

        final SetTransitionAsyncResponse response = new SetTransitionAsyncResponse();

        try {
            final TransitionMessageDataContainer transitionMessageDataContainer = new TransitionMessageDataContainer();

            if (request.getTransitionType() != null) {
                transitionMessageDataContainer
                        .setTransitionType(this.adHocManagementMapper.map(request.getTransitionType(),
                                org.opensmartgridplatform.domain.core.valueobjects.TransitionType.class));
            }
            DateTime dateTime = null;
            if (request.getTime() != null) {
                dateTime = new DateTime(request.getTime().toGregorianCalendar().getTime());
            }
            transitionMessageDataContainer.setDateTime(dateTime);

            final String correlationUid = this.adHocManagementService.enqueueTransitionRequest(
                    organisationIdentification, request.getDeviceIdentification(), transitionMessageDataContainer,
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

    @PayloadRoot(localPart = "SetTransitionAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SetTransitionResponse getSetTransitionResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetTransitionAsyncRequest request) throws OsgpException {

        LOGGER.info("Get Set Transition Response received from organisation: {} with correlationUid: {}.",
                organisationIdentification, request.getAsyncRequest().getCorrelationUid());

        final SetTransitionResponse response = new SetTransitionResponse();

        try {
            final ResponseMessage message = this.adHocManagementService
                    .dequeueSetTransitionResponse(request.getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    // === SET LIGHT MEASUREMENT DEVICE ===

    @PayloadRoot(localPart = "SetLightMeasurementDeviceRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SetLightMeasurementDeviceResponse setLightMeasurementDevice(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetLightMeasurementDeviceRequest request,
            @MessagePriority final String messagePriority) throws OsgpException {

        LOGGER.info(
                "Set Light Measurement Device Request received from organisation: {} for device: {} for light measurement device: {} with message priority: {}",
                organisationIdentification, request.getDeviceIdentification(),
                request.getLightMeasurementDeviceIdentification(), messagePriority);

        final SetLightMeasurementDeviceResponse response = new SetLightMeasurementDeviceResponse();

        try {
            this.adHocManagementService.coupleLightMeasurementDeviceForSsld(organisationIdentification,
                    request.getDeviceIdentification(), request.getLightMeasurementDeviceIdentification(),
                    MessagePriorityEnum.getMessagePriority(messagePriority));

            response.setResult(OsgpResultType.OK);
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
            throw new TechnicalException(COMPONENT_WS_PUBLIC_LIGHTING, e);
        }
    }
}
