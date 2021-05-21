/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ManagementMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventMessagesResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FindEventsRequestDataList;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetGsmDiagnosticRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetGsmDiagnosticResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventMessageDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FindEventsRequestList;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceCommunicationSettingsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainSmartMeteringManagementService")
@Transactional(value = "transactionManager")
public class ManagementService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ManagementService.class);
  private static final String DEVICE_RESPONSE_NOT_OK_LOG_MSG =
      "Device Response not ok. Unexpected Exception";
  private static final String SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG =
      "Sending request message to core.";

  @Autowired
  @Qualifier(value = "domainSmartMeteringOutboundOsgpCoreRequestsMessageSender")
  private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

  @Autowired
  @Qualifier(value = "domainSmartMeteringOutboundWebServiceResponsesMessageSender")
  private WebServiceResponseMessageSender webServiceResponseMessageSender;

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private ManagementMapper managementMapper;

  @Autowired private SmartMeterRepository smartMeterRepository;

  @Autowired private EventService eventService;

  public ManagementService() {
    // Parameterless constructor required for transactions...
  }

  public void findEvents(
      final DeviceMessageMetadata deviceMessageMetadata,
      final FindEventsRequestDataList findEventsQueryMessageDataContainer)
      throws FunctionalException {

    LOGGER.info(
        "findEvents for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    LOGGER.info(SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG);
    final RequestMessage requestMessage =
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeter.getIpAddress(),
            this.managementMapper.map(
                findEventsQueryMessageDataContainer, FindEventsRequestList.class));
    this.osgpCoreRequestMessageSender.send(
        requestMessage,
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleFindEventsResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException,
      final EventMessageDataResponseDto eventMessageDataContainerDto)
      throws FunctionalException {

    this.eventService.addEventTypeToEvents(deviceMessageMetadata, eventMessageDataContainerDto);

    final EventMessagesResponse eventMessageDataContainer =
        this.managementMapper.map(eventMessageDataContainerDto, EventMessagesResponse.class);

    // Send the response containing the events to the webservice-adapter
    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(ResponseMessageResultType.NOT_OK)
            .withOsgpException(osgpException)
            .withDataObject(eventMessageDataContainer)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void enableDebugging(final DeviceMessageMetadata deviceMessageMetadata)
      throws FunctionalException {
    LOGGER.info(
        "EnableDebugging for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    this.sendMetadataOnlyRequestMessage(deviceMessageMetadata);
  }

  public void handleEnableDebuggingResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    LOGGER.info(
        "handleEnableDebuggingResponse for MessageType: {}, with result: {}",
        deviceMessageMetadata.getMessageType(),
        deviceResult);

    this.handleMetadataOnlyResponseMessage(deviceMessageMetadata, deviceResult, exception);
  }

  public void disableDebugging(final DeviceMessageMetadata deviceMessageMetadata)
      throws FunctionalException {
    LOGGER.info(
        "DisableDebugging for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    this.sendMetadataOnlyRequestMessage(deviceMessageMetadata);
  }

  public void handleDisableDebuggingResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    LOGGER.info(
        "handleDisableDebuggingResponse for MessageType: {}, with result: {}",
        deviceMessageMetadata.getMessageType(),
        deviceResult);

    this.handleMetadataOnlyResponseMessage(deviceMessageMetadata, deviceResult, exception);
  }

  public void setDeviceCommunicationSettings(
      final DeviceMessageMetadata deviceMessageMetadata,
      final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
              .SetDeviceCommunicationSettingsRequest
          setDeviceCommunicationSettingsRequest)
      throws FunctionalException {
    LOGGER.info(
        "Set device communication settings for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SetDeviceCommunicationSettingsRequestDto setDeviceCommunicationSettingsRequestDto =
        this.managementMapper.map(
            setDeviceCommunicationSettingsRequest, SetDeviceCommunicationSettingsRequestDto.class);

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            setDeviceCommunicationSettingsRequestDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleSetDeviceCommunicationSettingsResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {
    LOGGER.info(
        "Set device communication settings for messageType: {}, with result: {}",
        deviceMessageMetadata.getMessageType(),
        deviceResult);

    this.handleMetadataOnlyResponseMessage(deviceMessageMetadata, deviceResult, exception);
  }

  public void setDeviceLifecycleStatusByChannel(
      final DeviceMessageMetadata deviceMessageMetadata,
      final SetDeviceLifecycleStatusByChannelRequestData request)
      throws FunctionalException {

    LOGGER.info(
        "Set device communication settings for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SetDeviceLifecycleStatusByChannelRequestDataDto requestDto =
        this.managementMapper.map(request, SetDeviceLifecycleStatusByChannelRequestDataDto.class);
    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            requestDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleSetDeviceLifecycleStatusByChannelResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType result,
      final OsgpException osgpException,
      final SetDeviceLifecycleStatusByChannelResponseDto responseDto) {

    LOGGER.info(
        "handleSetDeviceLifecycleStatusByChannelResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    this.setDeviceLifecycleStatusByChannel(responseDto);

    final String gatewayDeviceIdentification = deviceMessageMetadata.getDeviceIdentification();

    final SetDeviceLifecycleStatusByChannelResponseData responseData =
        this.managementMapper.map(responseDto, SetDeviceLifecycleStatusByChannelResponseData.class);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(gatewayDeviceIdentification)
            .withResult(result)
            .withOsgpException(osgpException)
            .withDataObject(responseData)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void setDeviceLifecycleStatusByChannel(
      final SetDeviceLifecycleStatusByChannelResponseDto responseDto) {

    final SmartMeter mbusDevice =
        this.smartMeterRepository.findByDeviceIdentification(
            responseDto.getMbusDeviceIdentification());
    mbusDevice.setDeviceLifecycleStatus(
        DeviceLifecycleStatus.valueOf(responseDto.getDeviceLifecycleStatus().name()));
    this.smartMeterRepository.save(mbusDevice);
  }

  public void getGsmDiagnostic(
      final DeviceMessageMetadata deviceMessageMetadata, final GetGsmDiagnosticRequestData request)
      throws FunctionalException {

    LOGGER.info(
        "Get gsm diagnostic for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final GetGsmDiagnosticRequestDto requestDto =
        this.managementMapper.map(request, GetGsmDiagnosticRequestDto.class);
    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            requestDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleGetGsmDiagnosticResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType result,
      final OsgpException osgpException,
      final GetGsmDiagnosticResponseDto responseDto) {

    LOGGER.info(
        "handleGetGsmDiagnosticResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    final String deviceIdentification = deviceMessageMetadata.getDeviceIdentification();

    final GetGsmDiagnosticResponseData responseData =
        this.managementMapper.map(responseDto, GetGsmDiagnosticResponseData.class);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceIdentification)
            .withResult(result)
            .withOsgpException(osgpException)
            .withDataObject(responseData)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  private void sendMetadataOnlyRequestMessage(final DeviceMessageMetadata deviceMessageMetadata)
      throws FunctionalException {
    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    LOGGER.info(SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG);
    final RequestMessage requestMessage =
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress());
    this.osgpCoreRequestMessageSender.send(
        requestMessage,
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  private void handleMetadataOnlyResponseMessage(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }
}
