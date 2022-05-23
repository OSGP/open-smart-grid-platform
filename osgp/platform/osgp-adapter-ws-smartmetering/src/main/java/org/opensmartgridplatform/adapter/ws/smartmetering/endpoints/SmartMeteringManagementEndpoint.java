/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.endpoints;

import java.util.List;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.BypassRetry;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ResponseUrl;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ScheduleTime;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusOnAllChannelsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusOnAllChannelsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusOnAllChannelsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusOnAllChannelsResponse;
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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindEventsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetDevicesRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetDevicesResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetGsmDiagnosticAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetGsmDiagnosticAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetGsmDiagnosticRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetGsmDiagnosticResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetGsmDiagnosticResponseData;
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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.UpdateProtocolAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.UpdateProtocolAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.UpdateProtocolRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.UpdateProtocolResponse;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.ManagementMapper;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.ManagementService;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.RequestService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.Event;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FindEventsRequestDataList;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Slf4j
@Endpoint
public class SmartMeteringManagementEndpoint extends SmartMeteringEndpoint {

  private static final String NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/smartmetering/sm-management/2014/10";
  private static final ComponentType COMPONENT_WS_SMART_METERING = ComponentType.WS_SMART_METERING;

  private final RequestService requestService;
  private final ManagementService managementService;
  private final ManagementMapper managementMapper;

  @Autowired
  public SmartMeteringManagementEndpoint(
      final RequestService requestService,
      final ManagementService managementService,
      @Qualifier(value = "smartMeteringManagementMapper") final ManagementMapper managementMapper) {
    this.requestService = requestService;
    this.managementService = managementService;
    this.managementMapper = managementMapper;
  }

  @PayloadRoot(localPart = "FindEventsRequest", namespace = NAMESPACE)
  @ResponsePayload
  public FindEventsAsyncResponse findEventsRequest(
      @OrganisationIdentification final String organisationIdentification,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @RequestPayload final FindEventsRequest request,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final List<
            org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FindEventsRequestData>
        findEventsQueryList =
            this.managementMapper.mapAsList(
                request.getFindEventsRequestData(),
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .FindEventsRequestData.class);

    this.validateFindEventsQueries(findEventsQueryList);

    final FindEventsRequestDataList requestData =
        new FindEventsRequestDataList(findEventsQueryList);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.FIND_EVENTS)
            .withMessageType(MessageType.FIND_EVENTS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.managementMapper.map(asyncResponse, FindEventsAsyncResponse.class);
  }

  private void validateFindEventsQueries(
      final List<
              org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                  .FindEventsRequestData>
          findEventsQueryList)
      throws FunctionalException {
    for (final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .FindEventsRequestData
        findEventsQuery : findEventsQueryList) {
      if (!findEventsQuery.getFrom().isBefore(findEventsQuery.getUntil())) {
        throw new FunctionalException(
            FunctionalExceptionType.VALIDATION_ERROR,
            ComponentType.WS_SMART_METERING,
            new Exception("The 'from' timestamp designates a time after 'until' timestamp."));
      }
    }
  }

  @PayloadRoot(localPart = "FindEventsAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public FindEventsResponse getFindEventsResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final FindEventsAsyncRequest request)
      throws OsgpException {
    log.info(
        "Get find events response for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    FindEventsResponse response = null;
    try {
      // Create response.
      response = new FindEventsResponse();

      // Get the request parameters, make sure that date time are in UTC.
      final String correlationUid = request.getCorrelationUid();

      final List<Event> events =
          this.managementService.findEventsByCorrelationUid(
              organisationIdentification, correlationUid);

      log.info("Get find events response: number of events: {}", events.size());
      for (final Event event : events) {
        log.info(
            "EventCode: {}, Timestamp: {}, EventCounter: {}",
            event.getEventCode(),
            event.getTimestamp(),
            event.getEventCounter());
      }

      response
          .getEvents()
          .addAll(
              this.managementMapper.mapAsList(
                  events,
                  org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event
                      .class));

    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "GetDevicesRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetDevicesResponse getDevices(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetDevicesRequest request)
      throws OsgpException {

    log.info("Get Devices Request received from organisation: {}.", organisationIdentification);

    GetDevicesResponse response = null;
    try {
      response = new GetDevicesResponse();
      final Page<Device> page =
          this.managementService.findAllDevices(organisationIdentification, request.getPage());

      final DevicePage devicePage = new DevicePage();
      devicePage.setTotalPages(page.getTotalPages());
      devicePage
          .getDevices()
          .addAll(
              this.managementMapper.mapAsList(
                  page.getContent(),
                  org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Device
                      .class));
      response.setDevicePage(devicePage);
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_WS_SMART_METERING,
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
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @RequestPayload final EnableDebuggingRequest request,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.ENABLE_DEBUGGING)
            .withMessageType(MessageType.ENABLE_DEBUGGING)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, null);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.managementMapper.map(asyncResponse, EnableDebuggingAsyncResponse.class);
  }

  @PayloadRoot(localPart = "EnableDebuggingAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public EnableDebuggingResponse getEnableDebuggingResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final EnableDebuggingAsyncRequest request)
      throws OsgpException {

    log.info(
        "EnableDebugging response for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    EnableDebuggingResponse response = null;
    try {
      response = new EnableDebuggingResponse();

      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Enable Debugging");

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
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
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @RequestPayload final DisableDebuggingRequest request,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.DISABLE_DEBUGGING)
            .withMessageType(MessageType.DISABLE_DEBUGGING)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, null);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.managementMapper.map(asyncResponse, DisableDebuggingAsyncResponse.class);
  }

  @PayloadRoot(localPart = "DisableDebuggingAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public DisableDebuggingResponse getDisableDebuggingResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final DisableDebuggingAsyncRequest request)
      throws OsgpException {

    log.info(
        "DisableDebugging response for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    DisableDebuggingResponse response = null;
    try {
      response = new DisableDebuggingResponse();

      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Disable Debugging");

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  /**
   * Retrieve log messages. Looks like it will be executed asynchronously but actually it is
   * executed synchronously. This was implemented like this to duplicate the behavior of the
   * implementation in ws-admin, but supporting async notifications. Once there is a wider
   * implementation of asynchronous requests and notifications, the ws-admin implementation will
   * replace this one and this method can be removed.
   *
   * @param messagePriority unused because this request fakes asynchronous behavior.
   * @param scheduleTime unused because this request fakes asynchronous behavior.
   * @return AsyncResponse
   * @throws OsgpException
   */
  @PayloadRoot(localPart = "FindMessageLogsRequest", namespace = NAMESPACE)
  @ResponsePayload
  public FindMessageLogsAsyncResponse findMessageLogsRequest(
      @OrganisationIdentification final String organisationIdentification,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @RequestPayload final FindMessageLogsRequest request,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_MESSAGES)
            .withMessageType(MessageType.GET_MESSAGES)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.managementService.enqueueAndSendFindLogsRequest(
            requestMessageMetadata, request.getPage());

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.managementMapper.map(asyncResponse, FindMessageLogsAsyncResponse.class);
  }

  /**
   * Retrieve the result of the {@link #findMessageLogsRequest(String, String, String, String,
   * FindMessageLogsRequest, String)} method.
   *
   * @return FindMessageLogsResponse
   * @throws OsgpException
   */
  @PayloadRoot(localPart = "FindMessageLogsAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public FindMessageLogsResponse getFindMessageLogsResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final FindMessageLogsAsyncRequest request)
      throws OsgpException {

    log.info(
        "FindMessageLogs response for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    FindMessageLogsResponse response = null;
    try {
      response = new FindMessageLogsResponse();

      @SuppressWarnings("unchecked")
      final Page<DeviceLogItem> page =
          (Page<DeviceLogItem>)
              this.responseDataService
                  .get(request.getCorrelationUid(), Page.class, ComponentType.WS_SMART_METERING)
                  .getMessageData();

      // Map to output
      final MessageLogPage logPage = new MessageLogPage();
      logPage.setTotalPages(page.getTotalPages());
      logPage
          .getMessageLogs()
          .addAll(this.managementMapper.mapAsList(page.getContent(), MessageLog.class));

      response.setMessageLogPage(logPage);
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
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
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @RequestPayload final SetDeviceCommunicationSettingsRequest request,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .SetDeviceCommunicationSettingsRequest
        dataRequest =
            this.managementMapper.map(
                request,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .SetDeviceCommunicationSettingsRequest.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_DEVICE_COMMUNICATION_SETTINGS)
            .withMessageType(MessageType.SET_DEVICE_COMMUNICATION_SETTINGS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, dataRequest);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.managementMapper.map(
        asyncResponse, SetDeviceCommunicationSettingsAsyncResponse.class);
  }

  @PayloadRoot(localPart = "SetDeviceCommunicationSettingsAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SetDeviceCommunicationSettingsResponse setDeviceCommunicationSettingsResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetDeviceCommunicationSettingsAsyncRequest request)
      throws OsgpException {

    log.info(
        "Set device communication settings response for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    SetDeviceCommunicationSettingsResponse response = null;
    try {
      response = new SetDeviceCommunicationSettingsResponse();

      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Set device communication settings");

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
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
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .SetDeviceLifecycleStatusByChannelRequestData
        requestData =
            this.managementMapper.map(
                request.getSetDeviceLifecycleStatusByChannelRequestData(),
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .SetDeviceLifecycleStatusByChannelRequestData.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getGatewayDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_DEVICE_LIFECYCLE_STATUS_BY_CHANNEL)
            .withMessageType(MessageType.SET_DEVICE_LIFECYCLE_STATUS_BY_CHANNEL)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.managementMapper.map(
        asyncResponse, SetDeviceLifecycleStatusByChannelAsyncResponse.class);
  }

  @PayloadRoot(localPart = "SetDeviceLifecycleStatusByChannelAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SetDeviceLifecycleStatusByChannelResponse setDeviceLifecycleStatusByChannelResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetDeviceLifecycleStatusByChannelAsyncRequest request)
      throws OsgpException {

    log.info(
        "Set device lifecycle status by channel response for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    SetDeviceLifecycleStatusByChannelResponse response = null;
    try {

      response = new SetDeviceLifecycleStatusByChannelResponse();

      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Set device lifecycle status by channel");

      response.setSetDeviceLifecycleStatusByChannelResponseData(
          this.managementMapper.map(
              responseData.getMessageData(), SetDeviceLifecycleStatusByChannelResponseData.class));
      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));

    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(localPart = "GetGsmDiagnosticRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetGsmDiagnosticAsyncResponse getGsmDiagnostic(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetGsmDiagnosticRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .GetGsmDiagnosticRequestData
        requestData =
            this.managementMapper.map(
                request.getGetGsmDiagnosticRequestData(),
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .GetGsmDiagnosticRequestData.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_GSM_DIAGNOSTIC)
            .withMessageType(MessageType.GET_GSM_DIAGNOSTIC)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.managementMapper.map(asyncResponse, GetGsmDiagnosticAsyncResponse.class);
  }

  @PayloadRoot(localPart = "GetGsmDiagnosticAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetGsmDiagnosticResponse getGsmDiagnosticResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetGsmDiagnosticAsyncRequest request)
      throws OsgpException {

    log.info(
        "Get gsm diagnostic response for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    GetGsmDiagnosticResponse response = null;
    try {

      response = new GetGsmDiagnosticResponse();

      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Get gsm diagnostic");

      response.setGetGsmDiagnosticResponseData(
          this.managementMapper.map(
              responseData.getMessageData(), GetGsmDiagnosticResponseData.class));
      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));

    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(localPart = "ClearMBusStatusOnAllChannelsRequest", namespace = NAMESPACE)
  @ResponsePayload
  public ClearMBusStatusOnAllChannelsAsyncResponse clearMBusStatusOnAllChannels(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ClearMBusStatusOnAllChannelsRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .ClearMBusStatusOnAllChannelsRequestData
        requestData =
            this.managementMapper.map(
                request.getClearMBusStatusOnAllChannelsRequestData(),
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .ClearMBusStatusOnAllChannelsRequestData.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.CLEAR_MBUS_STATUS_ON_ALL_CHANNELS)
            .withMessageType(MessageType.CLEAR_MBUS_STATUS_ON_ALL_CHANNELS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.managementMapper.map(
        asyncResponse, ClearMBusStatusOnAllChannelsAsyncResponse.class);
  }

  @PayloadRoot(localPart = "ClearMBusStatusOnAllChannelsAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public ClearMBusStatusOnAllChannelsResponse clearMBusStatusOnAllChannelsResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ClearMBusStatusOnAllChannelsAsyncRequest request)
      throws OsgpException {

    log.info(
        "Clear M-Bus Status On All Channels response for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    ClearMBusStatusOnAllChannelsResponse response = null;
    try {

      response = new ClearMBusStatusOnAllChannelsResponse();

      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Clear M-Bus Status On All Channels");

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }

    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(localPart = "UpdateProtocolRequest", namespace = NAMESPACE)
  @ResponsePayload
  public UpdateProtocolAsyncResponse updateProtocolRequest(
      @OrganisationIdentification final String organisationIdentification,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @RequestPayload final UpdateProtocolRequest request,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.UPDATE_PROTOCOL)
            .withMessageType(MessageType.UPDATE_PROTOCOL)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, null);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.managementMapper.map(asyncResponse, UpdateProtocolAsyncResponse.class);
  }

  @PayloadRoot(localPart = "UpdateProtocolAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public UpdateProtocolResponse getUpdateProtocolResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final UpdateProtocolAsyncRequest request)
      throws OsgpException {

    log.info(
        "UpdateProtocol response for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    UpdateProtocolResponse response = null;
    try {
      response = new UpdateProtocolResponse();

      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Update Protocol");

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_SMART_METERING,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }
}
