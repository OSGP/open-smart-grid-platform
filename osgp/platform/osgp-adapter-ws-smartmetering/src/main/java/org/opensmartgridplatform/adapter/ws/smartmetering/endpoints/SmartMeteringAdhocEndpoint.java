// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.endpoints;

import java.util.List;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.BypassRetry;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ResponseUrl;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ScheduleTime;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.MbusChannelShortEquipmentIdentifier;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmSchedulerAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmSchedulerAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmSchedulerRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmSchedulerResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.AdhocMapper;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.RequestService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AssociationLnListType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ScanMbusChannelsResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.TestAlarmSchedulerRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class SmartMeteringAdhocEndpoint extends SmartMeteringEndpoint {

  private static final String SMARTMETER_ADHOC_NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/smartmetering/sm-adhoc/2014/10";

  @Autowired private RequestService requestService;

  @Autowired private AdhocMapper adhocMapper;

  @PayloadRoot(localPart = "SynchronizeTimeRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
  @ResponsePayload
  public SynchronizeTimeAsyncResponse synchronizeTime(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SynchronizeTimeRequest request,
      @MessagePriority final String messagePriority,
      @ResponseUrl final String responseUrl,
      @ScheduleTime final String scheduleTime,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .SynchronizeTimeRequestData
        synchronizeTimeRequestData =
            this.adhocMapper.map(
                request.getSynchronizeTimeRequestData(),
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .SynchronizeTimeRequestData.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SYNCHRONIZE_TIME)
            .withMessageType(MessageType.SYNCHRONIZE_TIME)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(
            requestMessageMetadata, synchronizeTimeRequestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.adhocMapper.map(asyncResponse, SynchronizeTimeAsyncResponse.class);
  }

  @PayloadRoot(localPart = "SynchronizeTimeAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
  @ResponsePayload
  public SynchronizeTimeResponse getSynchronizeTimeResponse(
      @RequestPayload final SynchronizeTimeAsyncRequest request) throws OsgpException {

    SynchronizeTimeResponse response = null;
    try {
      response = new SynchronizeTimeResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }

    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(localPart = "TestAlarmSchedulerRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
  @ResponsePayload
  public TestAlarmSchedulerAsyncResponse scheduleTestAlarm(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final TestAlarmSchedulerRequest request,
      @MessagePriority final String messagePriority,
      @ResponseUrl final String responseUrl,
      @ScheduleTime final String scheduleTime,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final TestAlarmSchedulerRequestData data =
        this.adhocMapper.map(
            request.getTestAlarmSchedulerRequestData(), TestAlarmSchedulerRequestData.class);

    data.validate();

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SCHEDULE_TEST_ALARM)
            .withMessageType(MessageType.SCHEDULE_TEST_ALARM)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, data);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.adhocMapper.map(asyncResponse, TestAlarmSchedulerAsyncResponse.class);
  }

  @PayloadRoot(localPart = "TestAlarmSchedulerAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
  @ResponsePayload
  public TestAlarmSchedulerResponse getTestAlarmSchedulerResponse(
      @RequestPayload final TestAlarmSchedulerAsyncRequest request) throws OsgpException {

    TestAlarmSchedulerResponse response = null;
    try {

      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);
      response = this.adhocMapper.map(responseData, TestAlarmSchedulerResponse.class);
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "GetAllAttributeValuesRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
  @ResponsePayload
  public GetAllAttributeValuesAsyncResponse getAllAttributeValues(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetAllAttributeValuesRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .GetAllAttributeValuesRequest
        getAllAttributeValuesRequest =
            new org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                .GetAllAttributeValuesRequest(request.getDeviceIdentification());

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_ALL_ATTRIBUTE_VALUES)
            .withMessageType(MessageType.GET_ALL_ATTRIBUTE_VALUES)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(
            requestMessageMetadata, getAllAttributeValuesRequest);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.adhocMapper.map(asyncResponse, GetAllAttributeValuesAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "GetAllAttributeValuesAsyncRequest",
      namespace = SMARTMETER_ADHOC_NAMESPACE)
  @ResponsePayload
  public GetAllAttributeValuesResponse getAllAttributeValuesResponse(
      @RequestPayload final GetAllAttributeValuesAsyncRequest request) throws OsgpException {

    GetAllAttributeValuesResponse response = null;
    try {
      response = new GetAllAttributeValuesResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setOutput((String) responseData.getMessageData());
      }

    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "GetSpecificAttributeValueRequest",
      namespace = SMARTMETER_ADHOC_NAMESPACE)
  @ResponsePayload
  public GetSpecificAttributeValueAsyncResponse getSpecificAttributeValue(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetSpecificAttributeValueRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .SpecificAttributeValueRequest
        getSpecificAttributeValueRequest =
            this.adhocMapper.map(
                request,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .SpecificAttributeValueRequest.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_SPECIFIC_ATTRIBUTE_VALUE)
            .withMessageType(MessageType.GET_SPECIFIC_ATTRIBUTE_VALUE)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(
            requestMessageMetadata, getSpecificAttributeValueRequest);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.adhocMapper.map(asyncResponse, GetSpecificAttributeValueAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "GetSpecificAttributeValueAsyncRequest",
      namespace = SMARTMETER_ADHOC_NAMESPACE)
  @ResponsePayload
  public GetSpecificAttributeValueResponse getSpecificAttributeValueResponse(
      @RequestPayload final GetSpecificAttributeValueAsyncRequest request) throws OsgpException {

    GetSpecificAttributeValueResponse response = null;
    try {
      response = new GetSpecificAttributeValueResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (ResponseMessageResultType.OK == responseData.getResultType()) {
        response.setAttributeValueData((String) responseData.getMessageData());
      } else if (responseData.getMessageData() instanceof OsgpException) {
        throw (OsgpException) responseData.getMessageData();
      } else if (responseData.getMessageData() instanceof Exception) {
        throw new TechnicalException(
            ComponentType.WS_SMART_METERING,
            "An exception occurred: Get specific attribute value",
            (Exception) responseData.getMessageData());
      } else {
        throw new TechnicalException(
            ComponentType.WS_SMART_METERING,
            "An exception occurred: Get specific attribute value",
            null);
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "GetAssociationLnObjectsRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
  @ResponsePayload
  public GetAssociationLnObjectsAsyncResponse getAssociationLnObjects(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetAssociationLnObjectsRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .GetAssociationLnObjectsRequest
        getAssociationLnObjectsRequest =
            new org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                .GetAssociationLnObjectsRequest(request.getDeviceIdentification());

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_ASSOCIATION_LN_OBJECTS)
            .withMessageType(MessageType.GET_ASSOCIATION_LN_OBJECTS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(
            requestMessageMetadata, getAssociationLnObjectsRequest);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.adhocMapper.map(asyncResponse, GetAssociationLnObjectsAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "GetAssociationLnObjectsAsyncRequest",
      namespace = SMARTMETER_ADHOC_NAMESPACE)
  @ResponsePayload
  public GetAssociationLnObjectsResponse getAssociationLnObjectsResponse(
      @RequestPayload final GetAssociationLnObjectsAsyncRequest request) throws OsgpException {

    GetAssociationLnObjectsResponse response = null;
    try {
      response = new GetAssociationLnObjectsResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof AssociationLnListType) {
        response.setAssociationLnList(
            this.adhocMapper.map(
                responseData.getMessageData(),
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc
                    .AssociationLnListType.class));
      }

    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(localPart = "ScanMbusChannelsRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
  @ResponsePayload
  public ScanMbusChannelsAsyncResponse scanMbusChannels(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ScanMbusChannelsRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ScanMbusChannelsRequest
        scanMbusChannelsRequest =
            this.adhocMapper.map(
                request,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .ScanMbusChannelsRequest.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SCAN_MBUS_CHANNELS)
            .withMessageType(MessageType.SCAN_MBUS_CHANNELS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, scanMbusChannelsRequest);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.adhocMapper.map(asyncResponse, ScanMbusChannelsAsyncResponse.class);
  }

  @PayloadRoot(localPart = "ScanMbusChannelsAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
  @ResponsePayload
  public ScanMbusChannelsResponse getScanMbusChannelsResponse(
      @RequestPayload final ScanMbusChannelsAsyncRequest request) throws OsgpException {

    ScanMbusChannelsResponse response = null;
    try {
      response = new ScanMbusChannelsResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "retrieving the scan m-bus channels response");

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));

      final ScanMbusChannelsResponseData scanMbusChannelsResponse =
          (ScanMbusChannelsResponseData) responseData.getMessageData();

      if (ResponseMessageResultType.OK == responseData.getResultType()) {
        final List<MbusChannelShortEquipmentIdentifier> channelShortIds =
            response.getChannelShortIds();
        channelShortIds.addAll(
            this.adhocMapper.mapAsList(
                scanMbusChannelsResponse.getChannelShortIds(),
                MbusChannelShortEquipmentIdentifier.class));
      } else if (responseData.getMessageData() instanceof OsgpException) {
        throw (OsgpException) responseData.getMessageData();
      } else if (responseData.getMessageData() instanceof Exception) {
        throw new TechnicalException(
            ComponentType.WS_SMART_METERING,
            "An exception occurred: Scan M-Bus Channels",
            (Exception) responseData.getMessageData());
      } else {
        throw new TechnicalException(
            ComponentType.WS_SMART_METERING, "An exception occurred: Scan M-Bus Channels", null);
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }
}
