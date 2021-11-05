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
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.JmsMessageSender;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClearMBusStatusOnAllChannelsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventMessagesResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FindEventsRequestDataList;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetGsmDiagnosticRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetGsmDiagnosticResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearMBusStatusOnAllChannelsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventMessageDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FindEventsRequestList;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceCommunicationSettingsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
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
  private JmsMessageSender osgpCoreRequestMessageSender;

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
      final MessageMetadata messageMetadata,
      final FindEventsRequestDataList findEventsQueryMessageDataContainer)
      throws FunctionalException {

    LOGGER.info(
        "findEvents for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());

    LOGGER.info(SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG);
    final FindEventsRequestList requestDto =
        this.managementMapper.map(findEventsQueryMessageDataContainer, FindEventsRequestList.class);
    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withIpAddress(smartMeter.getIpAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .build());
  }

  public void handleFindEventsResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType resultType,
      final OsgpException osgpException,
      final EventMessageDataResponseDto eventMessageDataContainerDto)
      throws FunctionalException {

    this.eventService.enrichEvents(messageMetadata, eventMessageDataContainerDto);

    final EventMessagesResponse eventMessageDataContainer =
        this.managementMapper.map(eventMessageDataContainerDto, EventMessagesResponse.class);

    // Send the response containing the events to the webservice-adapter
    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(resultType)
            .withOsgpException(osgpException)
            .withDataObject(eventMessageDataContainer)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  public void enableDebugging(final MessageMetadata messageMetadata) throws FunctionalException {
    LOGGER.info(
        "EnableDebugging for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    this.sendMetadataOnlyRequestMessage(messageMetadata);
  }

  public void handleEnableDebuggingResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    LOGGER.info(
        "handleEnableDebuggingResponse for MessageType: {}, with result: {}",
        messageMetadata.getMessageType(),
        deviceResult);

    this.handleMetadataOnlyResponseMessage(messageMetadata, deviceResult, exception);
  }

  public void disableDebugging(final MessageMetadata messageMetadata) throws FunctionalException {
    LOGGER.info(
        "DisableDebugging for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    this.sendMetadataOnlyRequestMessage(messageMetadata);
  }

  public void handleDisableDebuggingResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    LOGGER.info(
        "handleDisableDebuggingResponse for MessageType: {}, with result: {}",
        messageMetadata.getMessageType(),
        deviceResult);

    this.handleMetadataOnlyResponseMessage(messageMetadata, deviceResult, exception);
  }

  public void setDeviceCommunicationSettings(
      final MessageMetadata messageMetadata,
      final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
              .SetDeviceCommunicationSettingsRequest
          setDeviceCommunicationSettingsRequest)
      throws FunctionalException {
    LOGGER.info(
        "Set device communication settings for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    final SetDeviceCommunicationSettingsRequestDto requestDto =
        this.managementMapper.map(
            setDeviceCommunicationSettingsRequest, SetDeviceCommunicationSettingsRequestDto.class);

    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());

    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withIpAddress(smartMeter.getIpAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .build());
  }

  public void handleSetDeviceCommunicationSettingsResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {
    LOGGER.info(
        "Set device communication settings for messageType: {}, with result: {}",
        messageMetadata.getMessageType(),
        deviceResult);

    this.handleMetadataOnlyResponseMessage(messageMetadata, deviceResult, exception);
  }

  public void setDeviceLifecycleStatusByChannel(
      final MessageMetadata messageMetadata,
      final SetDeviceLifecycleStatusByChannelRequestData request)
      throws FunctionalException {

    LOGGER.info(
        "Set device communication settings for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    final SetDeviceLifecycleStatusByChannelRequestDataDto requestDto =
        this.managementMapper.map(request, SetDeviceLifecycleStatusByChannelRequestDataDto.class);
    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());

    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withIpAddress(smartMeter.getIpAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .build());
  }

  public void handleSetDeviceLifecycleStatusByChannelResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType result,
      final OsgpException osgpException,
      final SetDeviceLifecycleStatusByChannelResponseDto responseDto) {

    LOGGER.info(
        "handleSetDeviceLifecycleStatusByChannelResponse for MessageType: {}",
        messageMetadata.getMessageType());

    this.setDeviceLifecycleStatusByChannel(responseDto);

    final SetDeviceLifecycleStatusByChannelResponseData responseData =
        this.managementMapper.map(responseDto, SetDeviceLifecycleStatusByChannelResponseData.class);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(result)
            .withOsgpException(osgpException)
            .withDataObject(responseData)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
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
      final MessageMetadata messageMetadata, final GetGsmDiagnosticRequestData request)
      throws FunctionalException {

    LOGGER.info(
        "Get gsm diagnostic for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    final GetGsmDiagnosticRequestDto requestDto =
        this.managementMapper.map(request, GetGsmDiagnosticRequestDto.class);
    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());

    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withIpAddress(smartMeter.getIpAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .build());
  }

  public void handleGetGsmDiagnosticResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType result,
      final OsgpException osgpException,
      final GetGsmDiagnosticResponseDto responseDto) {

    LOGGER.info(
        "handleGetGsmDiagnosticResponse for MessageType: {}", messageMetadata.getMessageType());

    final GetGsmDiagnosticResponseData responseData =
        this.managementMapper.map(responseDto, GetGsmDiagnosticResponseData.class);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(result)
            .withOsgpException(osgpException)
            .withDataObject(responseData)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  public void clearMBusStatusOnAllChannels(
      final MessageMetadata messageMetadata, final ClearMBusStatusOnAllChannelsRequestData request)
      throws FunctionalException {

    LOGGER.info(
        "Clear M-Bus status on all channels for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    final ClearMBusStatusOnAllChannelsRequestDto requestDto =
        this.managementMapper.map(request, ClearMBusStatusOnAllChannelsRequestDto.class);
    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());

    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withIpAddress(smartMeter.getIpAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .build());
  }

  public void handleClearMBusStatusOnAllChannelsResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    LOGGER.info(
        "handleClearMBusStatusOnAllChannelsResponse for MessageType: {}, with result: {}",
        messageMetadata.getMessageType(),
        deviceResult);

    this.handleMetadataOnlyResponseMessage(messageMetadata, deviceResult, exception);
  }

  private void sendMetadataOnlyRequestMessage(final MessageMetadata messageMetadata)
      throws FunctionalException {
    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());

    LOGGER.info(SENDING_REQUEST_MESSAGE_TO_CORE_LOG_MSG);
    this.osgpCoreRequestMessageSender.send(smartMeter.getIpAddress(), messageMetadata);
  }

  private void handleMetadataOnlyResponseMessage(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      LOGGER.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(result)
            .withOsgpException(exception)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }
}
