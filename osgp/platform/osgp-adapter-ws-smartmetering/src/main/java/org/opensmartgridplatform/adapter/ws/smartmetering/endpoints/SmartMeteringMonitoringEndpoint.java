//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.endpoints;

import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.BypassRetry;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ResponseUrl;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ScheduleTime;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetPowerQualityProfileAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetPowerQualityProfileAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetPowerQualityProfileResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetSystemEventAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetSystemEventResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.RetrievePushNotificationAlarmRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.RetrievePushNotificationAlarmResponse;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.RequestService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmRegister;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MeterReads;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MeterReadsGas;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushNotificationAlarm;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Slf4j
@Endpoint
public class SmartMeteringMonitoringEndpoint extends SmartMeteringEndpoint {

  private static final String SMARTMETER_MONITORING_NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/smartmetering/sm-monitoring/2014/10";

  @Autowired private RequestService requestService;

  @Autowired private MonitoringMapper monitoringMapper;

  public SmartMeteringMonitoringEndpoint() {
    // Empty constructor
  }

  @PayloadRoot(localPart = "PeriodicMeterReadsRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public PeriodicMeterReadsAsyncResponse getPeriodicMeterReads(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final PeriodicMeterReadsRequest request,
      @MessagePriority final String messagePriority,
      @ResponseUrl final String responseUrl,
      @ScheduleTime final String scheduleTime,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery
        requestData =
            this.monitoringMapper.map(
                request,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .PeriodicMeterReadsQuery.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.REQUEST_PERIODIC_METER_DATA)
            .withMessageType(MessageType.REQUEST_PERIODIC_METER_DATA)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.monitoringMapper.map(asyncResponse, PeriodicMeterReadsAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "PeriodicMeterReadsGasRequest",
      namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public PeriodicMeterReadsGasAsyncResponse getPeriodicMeterReadsGas(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final PeriodicMeterReadsGasRequest request,
      @MessagePriority final String messagePriority,
      @ResponseUrl final String responseUrl,
      @ScheduleTime final String scheduleTime,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery
        requestData =
            this.monitoringMapper.map(
                request,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .PeriodicMeterReadsQuery.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.REQUEST_PERIODIC_METER_DATA)
            .withMessageType(MessageType.REQUEST_PERIODIC_METER_DATA)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.monitoringMapper.map(asyncResponse, PeriodicMeterReadsGasAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "PeriodicMeterReadsAsyncRequest",
      namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public PeriodicMeterReadsResponse getPeriodicMeterReadsResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final PeriodicMeterReadsAsyncRequest request)
      throws OsgpException {

    log.debug(
        "Incoming PeriodicMeterReadsAsyncRequest for meter: {}.",
        request.getDeviceIdentification());

    PeriodicMeterReadsResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(),
              PeriodicMeterReadsContainer.class,
              ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "retrieving the periodic meter reads");

      response =
          this.monitoringMapper.map(
              responseData.getMessageData(),
              org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                  .PeriodicMeterReadsResponse.class);
    } catch (final Exception e) {
      this.handleRetrieveException(e, request, organisationIdentification);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "PeriodicMeterReadsGasAsyncRequest",
      namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public PeriodicMeterReadsGasResponse getPeriodicMeterReadsGasResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final PeriodicMeterReadsGasAsyncRequest request)
      throws OsgpException {

    log.debug(
        "Incoming PeriodicMeterReadsGasAsyncRequest for meter: {}.",
        request.getDeviceIdentification());

    PeriodicMeterReadsGasResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(),
              PeriodicMeterReadsContainerGas.class,
              ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "retrieving the periodic meter reads for gas");

      response =
          this.monitoringMapper.map(
              responseData.getMessageData(),
              org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                  .PeriodicMeterReadsGasResponse.class);
    } catch (final Exception e) {
      this.handleRetrieveException(e, request, organisationIdentification);
    }
    return response;
  }

  void handleRetrieveException(
      final Exception e, final AsyncRequest request, final String organisationIdentification)
      throws OsgpException {
    if (!(e instanceof FunctionalException)) {
      log.error(
          "Exception: {} while sending PeriodicMeterReads of device: {} for organisation {}.",
          e.getMessage(),
          request.getDeviceIdentification(),
          organisationIdentification);
    }

    this.handleException(e);
  }

  @PayloadRoot(localPart = "ActualMeterReadsRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public ActualMeterReadsAsyncResponse getActualMeterReads(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ActualMeterReadsRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualMeterReadsQuery
        requestData =
            new org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                .ActualMeterReadsQuery(false);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.REQUEST_ACTUAL_METER_DATA)
            .withMessageType(MessageType.REQUEST_ACTUAL_METER_DATA)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.monitoringMapper.map(asyncResponse, ActualMeterReadsAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "ActualMeterReadsGasRequest",
      namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public ActualMeterReadsGasAsyncResponse getActualMeterReadsGas(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ActualMeterReadsGasRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualMeterReadsQuery
        requestData =
            new org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                .ActualMeterReadsQuery(true);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.REQUEST_ACTUAL_METER_DATA)
            .withMessageType(MessageType.REQUEST_ACTUAL_METER_DATA)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.monitoringMapper.map(asyncResponse, ActualMeterReadsGasAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "ActualMeterReadsAsyncRequest",
      namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public ActualMeterReadsResponse getActualMeterReadsResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ActualMeterReadsAsyncRequest request)
      throws OsgpException {

    log.debug(
        "Incoming ActualMeterReadsAsyncRequest for meter: {}", request.getDeviceIdentification());

    ActualMeterReadsResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), MeterReads.class, ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "retrieving the actual meter reads");

      response =
          this.monitoringMapper.map(responseData.getMessageData(), ActualMeterReadsResponse.class);
    } catch (final Exception e) {
      this.handleRetrieveException(e, request, organisationIdentification);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "ActualMeterReadsGasAsyncRequest",
      namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public ActualMeterReadsGasResponse getActualMeterReadsGasResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ActualMeterReadsGasAsyncRequest request)
      throws OsgpException {

    log.debug(
        "Incoming ActualMeterReadsGasAsyncRequest for meter: {}",
        request.getDeviceIdentification());

    ActualMeterReadsGasResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), MeterReadsGas.class, ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "retrieving the actual meter reads for gas");

      response =
          this.monitoringMapper.map(
              responseData.getMessageData(), ActualMeterReadsGasResponse.class);
    } catch (final Exception e) {
      this.handleRetrieveException(e, request, organisationIdentification);
    }
    return response;
  }

  @PayloadRoot(localPart = "ReadAlarmRegisterRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public ReadAlarmRegisterAsyncResponse readAlarmRegister(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ReadAlarmRegisterRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest
        requestData =
            this.monitoringMapper.map(
                request,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .ReadAlarmRegisterRequest.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.READ_ALARM_REGISTER)
            .withMessageType(MessageType.READ_ALARM_REGISTER)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.monitoringMapper.map(asyncResponse, ReadAlarmRegisterAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "ReadAlarmRegisterAsyncRequest",
      namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public ReadAlarmRegisterResponse getReadAlarmRegisterResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ReadAlarmRegisterAsyncRequest request)
      throws OsgpException {

    log.info(
        "Incoming RetrieveReadAlarmRegisterRequest for meter: {}",
        request.getDeviceIdentification());

    ReadAlarmRegisterResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), AlarmRegister.class, ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "retrieving the alarm register");

      response =
          this.monitoringMapper.map(responseData.getMessageData(), ReadAlarmRegisterResponse.class);

    } catch (final FunctionalException e) {
      throw e;
    } catch (final Exception e) {
      log.error(
          "Exception: {} while sending RetrieveReadAlarmRegisterRequest of device: {} for organisation {}.",
          e.getMessage(),
          request.getDeviceIdentification(),
          organisationIdentification);

      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "RetrievePushNotificationAlarmRequest",
      namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public RetrievePushNotificationAlarmResponse getRetrievePushNotificationAlarmResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final RetrievePushNotificationAlarmRequest request)
      throws OsgpException {

    log.info(
        "Incoming RetrievePushNotificationAlarmRequest for correlation UID: {}",
        request.getCorrelationUid());

    RetrievePushNotificationAlarmResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(),
              PushNotificationAlarm.class,
              ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "retrieving the push notification alarm");

      response =
          this.monitoringMapper.map(
              responseData.getMessageData(), RetrievePushNotificationAlarmResponse.class);

    } catch (final FunctionalException e) {
      throw e;
    } catch (final Exception e) {
      log.error(
          "Exception: {} while sending RetrievePushNotificationAlarmRequest for correlation UID: {} for organisation {}.",
          e.getMessage(),
          request.getCorrelationUid(),
          organisationIdentification);

      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "GetPowerQualityProfileRequest",
      namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public GetPowerQualityProfileAsyncResponse getGetPowerQualityProfile(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetPowerQualityProfileRequest request,
      @MessagePriority final String messagePriority,
      @ResponseUrl final String responseUrl,
      @ScheduleTime final String scheduleTime,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final GetPowerQualityProfileRequest requestData =
        this.monitoringMapper.map(request, GetPowerQualityProfileRequest.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_PROFILE_GENERIC_DATA)
            .withMessageType(MessageType.GET_PROFILE_GENERIC_DATA)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.monitoringMapper.map(asyncResponse, GetPowerQualityProfileAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "GetPowerQualityProfileAsyncRequest",
      namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public GetPowerQualityProfileResponse getGetPowerQualityProfileResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetPowerQualityProfileAsyncRequest request)
      throws OsgpException {

    log.debug(
        "Incoming GetPowerQualityProfileAsyncRequest for meter: {}.",
        request.getDeviceIdentification());

    GetPowerQualityProfileResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(),
              org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                  .GetPowerQualityProfileResponse.class,
              ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "retrieving power quality profile");

      response =
          this.monitoringMapper.map(
              responseData.getMessageData(), GetPowerQualityProfileResponse.class);

    } catch (final Exception e) {
      log.error(
          "Exception: {} while sending GetPowerQualityProfileAsyncRequest for correlation UID: {} for organisation {}.",
          e.getMessage(),
          request.getCorrelationUid(),
          organisationIdentification);

      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(localPart = "ClearAlarmRegisterRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public ClearAlarmRegisterAsyncResponse clearAlarmRegister(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ClearAlarmRegisterRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClearAlarmRegisterRequest
        requestData =
            this.monitoringMapper.map(
                request,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .ClearAlarmRegisterRequest.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.CLEAR_ALARM_REGISTER)
            .withMessageType(MessageType.CLEAR_ALARM_REGISTER)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.monitoringMapper.map(asyncResponse, ClearAlarmRegisterAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "ClearAlarmRegisterAsyncRequest",
      namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public ClearAlarmRegisterResponse getClearAlarmRegisterResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ClearAlarmRegisterAsyncRequest request)
      throws OsgpException {

    log.info(
        "Incoming clear alarm register request for meter: {}", request.getDeviceIdentification());

    ClearAlarmRegisterResponse response = null;
    try {
      response = new ClearAlarmRegisterResponse();

      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Retrieving clear alarm register");

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final FunctionalException e) {
      throw e;
    } catch (final Exception e) {
      log.error(
          "Exception: {} while sending clear alarm register request of device: {} for organisation {}.",
          e.getMessage(),
          request.getDeviceIdentification(),
          organisationIdentification);

      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(localPart = "ActualPowerQualityRequest", namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public ActualPowerQualityAsyncResponse getActualPowerQuality(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload
          final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                  .ActualPowerQualityRequest
              request,
      @MessagePriority final String messagePriority,
      @ResponseUrl final String responseUrl,
      @ScheduleTime final String scheduleTime,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final ActualPowerQualityRequest requestData =
        this.monitoringMapper.map(request, ActualPowerQualityRequest.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_ACTUAL_POWER_QUALITY)
            .withMessageType(MessageType.GET_ACTUAL_POWER_QUALITY)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.monitoringMapper.map(asyncResponse, ActualPowerQualityAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "ActualPowerQualityAsyncRequest",
      namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public ActualPowerQualityResponse getActualPowerQualityResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ActualPowerQualityAsyncRequest request)
      throws OsgpException {

    log.debug(
        "Incoming ActualPowerQualityAsyncRequest for meter: {}.",
        request.getDeviceIdentification());

    ActualPowerQualityResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "retrieving actual power data");

      response =
          this.monitoringMapper.map(
              responseData.getMessageData(),
              org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                  .ActualPowerQualityResponse.class);
    } catch (final Exception e) {
      this.handleRetrieveException(e, request, organisationIdentification);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "GetSystemEventAsyncRequest",
      namespace = SMARTMETER_MONITORING_NAMESPACE)
  @ResponsePayload
  public GetSystemEventResponse getSystemEventResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetSystemEventAsyncRequest request)
      throws OsgpException {

    log.debug(
        "Incoming GetSystemEventAsyncRequest for meter: {}.", request.getDeviceIdentification());

    GetSystemEventResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "retrieving system event data");

      response =
          this.monitoringMapper.map(
              responseData.getMessageData(),
              org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                  .GetSystemEventResponse.class);
    } catch (final Exception e) {
      this.handleRetrieveException(e, request, organisationIdentification);
    }
    return response;
  }
}
