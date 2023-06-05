// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.endpoints;

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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMBusDeviceAdministrativeAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMBusDeviceAdministrativeRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceAdministrativeAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceAdministrativeResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceResponse;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.InstallationMapper;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.RequestService;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceModel;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AddSmartMeterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceAdministrativeRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Slf4j
@Endpoint
public class SmartMeteringInstallationEndpoint extends SmartMeteringEndpoint {

  private static final String SMARTMETER_INSTALLATION_NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/smartmetering/sm-installation/2014/10";

  @Autowired private RequestService requestService;

  @Autowired private InstallationMapper installationMapper;

  public SmartMeteringInstallationEndpoint() {
    // Empty constructor
  }

  @PayloadRoot(localPart = "AddDeviceRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public AddDeviceAsyncResponse addDevice(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final AddDeviceRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    log.info(
        "Incoming AddDeviceRequest for meter: {}.", request.getDevice().getDeviceIdentification());

    AsyncResponse asyncResponse = null;
    try {
      final SmartMeteringDevice device =
          this.installationMapper.map(request.getDevice(), SmartMeteringDevice.class);
      final DeviceModel deviceModel =
          new DeviceModel(
              request.getDeviceModel().getManufacturer(),
              request.getDeviceModel().getModelCode(),
              "");
      final AddSmartMeterRequest addSmartMeterRequest =
          new AddSmartMeterRequest(device, deviceModel);

      final RequestMessageMetadata requestMessageMetadata =
          RequestMessageMetadata.newBuilder()
              .withOrganisationIdentification(organisationIdentification)
              .withDeviceIdentification(device.getDeviceIdentification())
              .withDeviceFunction(null)
              .withMessageType(MessageType.ADD_METER)
              .withMessagePriority(messagePriority)
              .withScheduleTime(scheduleTime)
              .withBypassRetry(bypassRetry)
              .build();

      asyncResponse =
          this.requestService.enqueueAndSendRequest(requestMessageMetadata, addSmartMeterRequest);

      this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);
    } catch (final ConstraintViolationException e) {

      log.error(
          "Exception: {} while adding device: {} for organisation {}.",
          e.getMessage(),
          request.getDevice().getDeviceIdentification(),
          organisationIdentification,
          e);

      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_CORE,
          new ValidationException(e.getConstraintViolations()));

    } catch (final Exception e) {

      log.error(
          "Exception: {} while adding device: {} for organisation {}.",
          e.getMessage(),
          request.getDevice().getDeviceIdentification(),
          organisationIdentification,
          e);

      this.handleException(e);
    }
    return this.installationMapper.map(asyncResponse, AddDeviceAsyncResponse.class);
  }

  @PayloadRoot(localPart = "AddDeviceAsyncRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public AddDeviceResponse getAddDeviceResponse(@RequestPayload final AddDeviceAsyncRequest request)
      throws OsgpException {

    AddDeviceResponse response = null;
    try {
      response = new AddDeviceResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Add Device");

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String messageData) {
        response.setDescription(messageData);
      }

    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  /**
   * @param organisationIdentification the organisation requesting the coupling of devices
   * @param request the CoupleMbusDeviceRequest containing the deviceIdentification,
   *     mbusDeviceIdentification and channel
   * @param messagePriority the priority of the message
   * @param scheduleTime the time the request is scheduled for
   * @return a response containing a correlationUid and the deviceIdentification
   * @throws OsgpException
   */
  @PayloadRoot(localPart = "CoupleMbusDeviceRequest", namespace = SMARTMETER_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public CoupleMbusDeviceAsyncResponse coupleMbusDevice(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final CoupleMbusDeviceRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final CoupleMbusDeviceRequestData requestData =
        new CoupleMbusDeviceRequestData(request.getMbusDeviceIdentification(), request.isForce());

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.COUPLE_MBUS_DEVICE)
            .withMessageType(MessageType.COUPLE_MBUS_DEVICE)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.installationMapper.map(asyncResponse, CoupleMbusDeviceAsyncResponse.class);
  }

  /**
   * @param request the request message containing the correlationUid
   * @return the response message containing the OsgpResultType and optional a message
   * @throws OsgpException
   */
  @PayloadRoot(
      localPart = "CoupleMbusDeviceAsyncRequest",
      namespace = SMARTMETER_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public CoupleMbusDeviceResponse getCoupleMbusDeviceResponse(
      @RequestPayload final CoupleMbusDeviceAsyncRequest request) throws OsgpException {

    CoupleMbusDeviceResponse response = null;
    try {
      response = new CoupleMbusDeviceResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Couple Mbus Device");

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      populateResponseMessageData(response, responseData);

    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  private static void populateResponseMessageData(
      final CoupleMbusDeviceResponse response, final ResponseData responseData) {
    if (responseData.getMessageData() instanceof String messageData) {
      response.setDescription(messageData);
    }
  }

  /**
   * @param organisationIdentification the organisation requesting the coupling of devices
   * @param request the DecoupleMbusDeviceRequest containing the deviceIdentification,
   *     mbusDeviceIdentification and channel
   * @param messagePriority the priority of the message
   * @param scheduleTime the time the request is scheduled for
   * @return a response containing a correlationUid and the deviceIdentification
   * @throws OsgpException
   */
  @PayloadRoot(
      localPart = "DecoupleMbusDeviceRequest",
      namespace = SMARTMETER_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public DecoupleMbusDeviceAsyncResponse decoupleMbusDevice(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final DecoupleMbusDeviceRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final DecoupleMbusDeviceRequestData requestData =
        new DecoupleMbusDeviceRequestData(request.getMbusDeviceIdentification());

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.DECOUPLE_MBUS_DEVICE)
            .withMessageType(MessageType.DECOUPLE_MBUS_DEVICE)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.installationMapper.map(asyncResponse, DecoupleMbusDeviceAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "DecoupleMBusDeviceAdministrativeRequest",
      namespace = SMARTMETER_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public DecoupleMBusDeviceAdministrativeAsyncResponse decoupleMbusDeviceAdministrative(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final DecoupleMBusDeviceAdministrativeRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final DecoupleMbusDeviceAdministrativeRequestData requestData =
        new DecoupleMbusDeviceAdministrativeRequestData(request.getMbusDeviceIdentification());

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getMbusDeviceIdentification())
            .withDeviceFunction(DeviceFunction.DECOUPLE_MBUS_DEVICE_ADMINISTRATIVE)
            .withMessageType(MessageType.DECOUPLE_MBUS_DEVICE_ADMINISTRATIVE)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.installationMapper.map(
        asyncResponse, DecoupleMBusDeviceAdministrativeAsyncResponse.class);
  }

  /**
   * @param request the request message containing the correlationUid
   * @return the response message containing the OsgpResultType and optional a message
   * @throws OsgpException
   */
  @PayloadRoot(
      localPart = "DecoupleMbusDeviceAsyncRequest",
      namespace = SMARTMETER_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public DecoupleMbusDeviceResponse getDecoupleMbusDeviceResponse(
      @RequestPayload final DecoupleMbusDeviceAsyncRequest request) throws OsgpException {

    DecoupleMbusDeviceResponse response = null;
    try {
      response = new DecoupleMbusDeviceResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Decouple Mbus Device");

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String messageData) {
        response.setDescription(messageData);
      }

    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  /**
   * @param request the request message containing the correlationUid
   * @return the response message containing the OsgpResultType and optional a message
   * @throws OsgpException
   */
  @PayloadRoot(
      localPart = "DecoupleMbusDeviceAdministrativeAsyncRequest",
      namespace = SMARTMETER_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public DecoupleMbusDeviceAdministrativeResponse getDecoupleMbusDeviceAdministrativeResponse(
      @RequestPayload final DecoupleMbusDeviceAdministrativeAsyncRequest request)
      throws OsgpException {

    DecoupleMbusDeviceAdministrativeResponse response = null;
    try {
      response = new DecoupleMbusDeviceAdministrativeResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Decouple Mbus Device Administrative");

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String messageData) {
        response.setDescription(messageData);
      }

    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  /**
   * @param organisationIdentification the organization requesting the coupling of devices
   * @param request the CoupleMbusDeviceByChannelRequest containing the gatewayDeviceIdentification
   *     and channel
   * @param messagePriority the priority of the message
   * @param scheduleTime the time the request is scheduled for
   * @return a response containing a correlationUid and the deviceIdentification
   * @throws OsgpException
   */
  @PayloadRoot(
      localPart = "CoupleMbusDeviceByChannelRequest",
      namespace = SMARTMETER_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public CoupleMbusDeviceByChannelAsyncResponse coupleMbusDeviceByChannel(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final CoupleMbusDeviceByChannelRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final CoupleMbusDeviceByChannelRequestData requestData =
        new CoupleMbusDeviceByChannelRequestData(
            request.getCoupleMbusDeviceByChannelRequestData().getChannel());

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.COUPLE_MBUS_DEVICE_BY_CHANNEL)
            .withMessageType(MessageType.COUPLE_MBUS_DEVICE_BY_CHANNEL)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.installationMapper.map(asyncResponse, CoupleMbusDeviceByChannelAsyncResponse.class);
  }

  /**
   * @param request the request message containing the correlationUid
   * @return the response message containing the OsgpResultType and optional a message
   * @throws OsgpException
   */
  @PayloadRoot(
      localPart = "CoupleMbusDeviceByChannelAsyncRequest",
      namespace = SMARTMETER_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public CoupleMbusDeviceByChannelResponse getCoupleMbusDeviceByChannelResponse(
      @RequestPayload final CoupleMbusDeviceByChannelAsyncRequest request) throws OsgpException {

    CoupleMbusDeviceByChannelResponse response = null;
    try {
      response = new CoupleMbusDeviceByChannelResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Couple Mbus Device By Channel");

      if (responseData.getMessageData() instanceof String messageData) {
        response.setResultString(messageData);
      }
      response =
          this.installationMapper.map(
              responseData.getMessageData(), CoupleMbusDeviceByChannelResponse.class);

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));

    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  /**
   * @param organisationIdentification the organization requesting the coupling of devices
   * @param request the DecoupleMbusDeviceByChannelRequest containing the
   *     gatewayDeviceIdentification and channel
   * @param messagePriority the priority of the message
   * @param scheduleTime the time the request is scheduled for
   * @return a response containing a correlationUid and the deviceIdentification
   * @throws OsgpException
   */
  @PayloadRoot(
      localPart = "DecoupleMbusDeviceByChannelRequest",
      namespace = SMARTMETER_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public DecoupleMbusDeviceByChannelAsyncResponse decoupleMbusDeviceByChannel(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final DecoupleMbusDeviceByChannelRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final DecoupleMbusDeviceByChannelRequestData requestData =
        new DecoupleMbusDeviceByChannelRequestData(
            request.getDecoupleMbusDeviceByChannelRequestData().getChannel());

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.DECOUPLE_MBUS_DEVICE_BY_CHANNEL)
            .withMessageType(MessageType.DECOUPLE_MBUS_DEVICE_BY_CHANNEL)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.installationMapper.map(
        asyncResponse, DecoupleMbusDeviceByChannelAsyncResponse.class);
  }

  /**
   * @param request the request message containing the correlationUid
   * @return the response message containing the OsgpResultType and optional a message
   * @throws OsgpException
   */
  @PayloadRoot(
      localPart = "DecoupleMbusDeviceByChannelAsyncRequest",
      namespace = SMARTMETER_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public DecoupleMbusDeviceByChannelResponse getDecoupleMbusDeviceByChannelResponse(
      @RequestPayload final DecoupleMbusDeviceByChannelAsyncRequest request) throws OsgpException {

    DecoupleMbusDeviceByChannelResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "Decouple Mbus Device By Channel");

      response =
          this.installationMapper.map(
              responseData.getMessageData(), DecoupleMbusDeviceByChannelResponse.class);

    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }
}
