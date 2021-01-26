/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.endpoints;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ResponseUrl;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ScheduleTime;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.DevicePage;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.DisableDebuggingAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.DisableDebuggingAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.DisableDebuggingRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.DisableDebuggingResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EnableDebuggingAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EnableDebuggingAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EnableDebuggingRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EnableDebuggingResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindEventsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindEventsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindEventsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindEventsRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindEventsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetDevicesRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetDevicesResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.MessageLog;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.MessageLogPage;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelResponseData;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.ManagementMapper;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.ManagementService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.Event;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
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
public class SmartMeteringManagementEndpoint extends SmartMeteringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringManagementEndpoint.class);
    private static final String NAMESPACE = "http://www.opensmartgridplatform.org/schemas/smartmetering/sm-management/2014/10";
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

    @PayloadRoot(localPart = "FindEventsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public FindEventsAsyncResponse findEventsRequest(
            @OrganisationIdentification final String organisationIdentification,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime,
            @ResponseUrl final String responseUrl, @RequestPayload final FindEventsRequest request)
            throws OsgpException {

        LOGGER.info("Find events request for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        FindEventsAsyncResponse response = null;
        try {
            // Create response.
            response = new FindEventsAsyncResponse();

            // Get the request parameters, make sure that date time are in UTC.
            final String deviceIdentification = request.getDeviceIdentification();
            final List<FindEventsRequestData> findEventsQuery = request.getFindEventsRequestData();

            final String correlationUid = this.managementService.enqueueFindEventsRequest(organisationIdentification,
                    deviceIdentification,
                    this.managementMapper.mapAsList(findEventsQuery,
                            org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FindEventsRequestData.class),
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.managementMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "FindEventsAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public FindEventsResponse getFindEventsResponse(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindEventsAsyncRequest request) throws OsgpException {
        LOGGER.info("Get find events response for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        FindEventsResponse response = null;
        try {
            // Create response.
            response = new FindEventsResponse();

            // Get the request parameters, make sure that date time are in UTC.
            final String correlationUid = request.getCorrelationUid();

            final List<Event> events = this.managementService.findEventsByCorrelationUid(organisationIdentification,
                    correlationUid);

            LOGGER.info("Get find events response: number of events: {}", events.size());
            for (final Event event : events) {
                LOGGER.info("EventCode: {}, Timestamp: {}, EventCounter: {}", event.getEventCode(),
                        event.getTimestamp(), event.getEventCounter());
            }

            response.getEvents().addAll(this.managementMapper.mapAsList(events,
                    org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event.class));

        } catch (final ConstraintViolationException e) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_SMART_METERING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "GetDevicesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetDevicesResponse getDevices(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetDevicesRequest request) throws OsgpException {

        LOGGER.info("Get Devices Request received from organisation: {}.", organisationIdentification);

        GetDevicesResponse response = null;
        try {
            response = new GetDevicesResponse();
            final Page<Device> page = this.managementService.findAllDevices(organisationIdentification,
                    request.getPage());

            final DevicePage devicePage = new DevicePage();
            devicePage.setTotalPages(page.getTotalPages());
            devicePage.getDevices().addAll(this.managementMapper.mapAsList(page.getContent(),
                    org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Device.class));
            response.setDevicePage(devicePage);
        } catch (final ConstraintViolationException e) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_WS_SMART_METERING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "EnableDebuggingRequest", namespace = NAMESPACE)
    @ResponsePayload
    public EnableDebuggingAsyncResponse enableDebuggingRequest(
            @OrganisationIdentification final String organisationIdentification,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime,
            @ResponseUrl final String responseUrl, @RequestPayload final EnableDebuggingRequest request)
            throws OsgpException {

        LOGGER.info("Enable debugging request for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        EnableDebuggingAsyncResponse response = null;
        try {
            response = new EnableDebuggingAsyncResponse();

            // Get the request parameters, make sure that date time are in UTC.
            final String deviceIdentification = request.getDeviceIdentification();

            final String correlationUid = this.managementService.enqueueEnableDebuggingRequest(
                    organisationIdentification, deviceIdentification,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.managementMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());
            this.responseUrlService.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "EnableDebuggingAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public EnableDebuggingResponse getEnableDebuggingResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final EnableDebuggingAsyncRequest request) throws OsgpException {

        LOGGER.info("EnableDebugging response for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        EnableDebuggingResponse response = null;
        try {
            response = new EnableDebuggingResponse();

            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "Enable Debugging");

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
            }
        } catch (final ConstraintViolationException e) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_SMART_METERING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "DisableDebuggingRequest", namespace = NAMESPACE)
    @ResponsePayload
    public DisableDebuggingAsyncResponse disableDebuggingRequest(
            @OrganisationIdentification final String organisationIdentification,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime,
            @ResponseUrl final String responseUrl, @RequestPayload final DisableDebuggingRequest request)
            throws OsgpException {

        LOGGER.info("Disable debugging request for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        DisableDebuggingAsyncResponse response = null;
        try {
            response = new DisableDebuggingAsyncResponse();

            // Get the request parameters, make sure that date time are in UTC.
            final String deviceIdentification = request.getDeviceIdentification();

            final String correlationUid = this.managementService.enqueueDisableDebuggingRequest(
                    organisationIdentification, deviceIdentification,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.managementMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());
            this.responseUrlService.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "DisableDebuggingAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public DisableDebuggingResponse getDisableDebuggingResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final DisableDebuggingAsyncRequest request) throws OsgpException {

        LOGGER.info("DisableDebugging response for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        DisableDebuggingResponse response = null;
        try {
            response = new DisableDebuggingResponse();

            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "Disable Debugging");

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
            }
        } catch (final ConstraintViolationException e) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_SMART_METERING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    /**
     * Retrieve log messages. Looks like it will be executed asynchronously but
     * actually it is executed synchronously. This was implemented like this to
     * duplicate the behavior of the implementation in ws-admin, but supporting
     * async notifications. Once there is a wider implementation of asynchronous
     * requests and notifications, the ws-admin implementation will replace this
     * one and this method can be removed.
     *
     * @param messagePriority
     *            unused because this request fakes asynchronous behavior.
     * @param scheduleTime
     *            unused because this request fakes asynchronous behavior.
     * @return AsyncResponse
     * @throws OsgpException
     */
    @PayloadRoot(localPart = "FindMessageLogsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public FindMessageLogsAsyncResponse findMessageLogsRequest(
            @OrganisationIdentification final String organisationIdentification,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime,
            @ResponseUrl final String responseUrl, @RequestPayload final FindMessageLogsRequest request)
            throws OsgpException {

        LOGGER.info("Find message logs request for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        FindMessageLogsAsyncResponse response = null;
        try {
            response = new FindMessageLogsAsyncResponse();

            final String deviceIdentification = request.getDeviceIdentification();

            final String correlationUid = this.managementService.findMessageLogsRequest(organisationIdentification,
                    deviceIdentification, request.getPage());

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    /**
     * Retrieve the result of the
     * {@link #findMessageLogsRequest(String, String, String, String, FindMessageLogsRequest)}
     * method.
     *
     * @return FindMessageLogsResponse
     * @throws OsgpException
     */
    @PayloadRoot(localPart = "FindMessageLogsAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public FindMessageLogsResponse getFindMessageLogsResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindMessageLogsAsyncRequest request) throws OsgpException {

        LOGGER.info("FindMessageLogs response for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        FindMessageLogsResponse response = null;
        try {
            response = new FindMessageLogsResponse();

            @SuppressWarnings("unchecked")
            final Page<DeviceLogItem> page = (Page<DeviceLogItem>) this.responseDataService
                    .dequeue(request.getCorrelationUid(), Page.class, ComponentType.WS_SMART_METERING).getMessageData();

            // Map to output
            final MessageLogPage logPage = new MessageLogPage();
            logPage.setTotalPages(page.getTotalPages());
            logPage.getMessageLogs().addAll(this.managementMapper.mapAsList(page.getContent(), MessageLog.class));

            response.setMessageLogPage(logPage);
        } catch (final ConstraintViolationException e) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_SMART_METERING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetDeviceCommunicationSettingsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SetDeviceCommunicationSettingsAsyncResponse setDeviceCommunicationSettingsRequest(
            @OrganisationIdentification final String organisationIdentification,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime,
            @ResponseUrl final String responseUrl, @RequestPayload final SetDeviceCommunicationSettingsRequest request)
            throws OsgpException {

        LOGGER.info("Set device communication settings request for organisation: {} and device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        final SetDeviceCommunicationSettingsAsyncResponse response = new SetDeviceCommunicationSettingsAsyncResponse();

        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetDeviceCommunicationSettingsRequest dataRequest = this.managementMapper
                .map(request,
                        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetDeviceCommunicationSettingsRequest.class);

        try {
            final String deviceIdentification = request.getDeviceIdentification();

            final String correlationUid = this.managementService.enqueueSetDeviceCommunicationSettingsRequest(
                    organisationIdentification, deviceIdentification, dataRequest,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.managementMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());
            this.responseUrlService.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetDeviceCommunicationSettingsAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SetDeviceCommunicationSettingsResponse setDeviceCommunicationSettingsResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetDeviceCommunicationSettingsAsyncRequest request) throws OsgpException {

        LOGGER.info("Set device communication settings response for organisation: {} and device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        SetDeviceCommunicationSettingsResponse response = null;
        try {
            response = new SetDeviceCommunicationSettingsResponse();

            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "Set device communication settings");

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
            }
        } catch (final ConstraintViolationException e) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_SMART_METERING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetDeviceLifecycleStatusByChannelRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SetDeviceLifecycleStatusByChannelAsyncResponse setDeviceLifecycleStatusByChannel(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetDeviceLifecycleStatusByChannelRequest request,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime,
            @ResponseUrl final String responseUrl) throws OsgpException {

        LOGGER.info("Set device lifecycle status by channel request received from organisation {} for device {}",
                organisationIdentification, request.getGatewayDeviceIdentification());

        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestData requestData = this.managementMapper
                .map(request.getSetDeviceLifecycleStatusByChannelRequestData(),
                        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestData.class);

        SetDeviceLifecycleStatusByChannelAsyncResponse asyncResponse = null;
        try {
            final String correlationUid = this.managementService.enqueueSetDeviceLifecycleStatusByChannelRequest(
                    organisationIdentification, request.getGatewayDeviceIdentification(), requestData,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    (this.managementMapper.map(scheduleTime, Long.class)));

            asyncResponse = new SetDeviceLifecycleStatusByChannelAsyncResponse();

            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getGatewayDeviceIdentification());
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            this.handleException(e);
        }
        return asyncResponse;
    }

    @PayloadRoot(localPart = "SetDeviceLifecycleStatusByChannelAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SetDeviceLifecycleStatusByChannelResponse setDeviceLifecycleStatusByChannelResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetDeviceLifecycleStatusByChannelAsyncRequest request) throws OsgpException {

        LOGGER.info("Set device lifecycle status by channel response for organisation: {} and device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        SetDeviceLifecycleStatusByChannelResponse response = null;
        try {

            response = new SetDeviceLifecycleStatusByChannelResponse();

            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "Set device lifecycle status by channel");

            response.setSetDeviceLifecycleStatusByChannelResponseData(this.managementMapper
                    .map(responseData.getMessageData(), SetDeviceLifecycleStatusByChannelResponseData.class));
            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));

        } catch (final ConstraintViolationException e) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_SMART_METERING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

}
