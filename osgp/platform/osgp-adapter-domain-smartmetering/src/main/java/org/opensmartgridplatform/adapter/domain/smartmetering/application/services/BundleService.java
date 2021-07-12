/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.Arrays;
import java.util.List;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BundleMessageRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BundleMessagesResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventMessageDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FirmwareVersionGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FirmwareVersionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainSmartMeteringBundleService")
@Transactional(value = "transactionManager")
public class BundleService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BundleService.class);

  @Autowired
  @Qualifier(value = "domainSmartMeteringOutboundOsgpCoreRequestsMessageSender")
  private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

  @Autowired
  @Qualifier(value = "domainSmartMeteringOutboundWebServiceResponsesMessageSender")
  private WebServiceResponseMessageSender webServiceResponseMessageSender;

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private ActionMapperService actionMapperService;

  @Autowired private ActionMapperResponseService actionMapperResponseService;

  @Autowired private MBusGatewayService mBusGatewayService;

  @Autowired private ManagementService managementService;

  @Autowired private ConfigurationMapper configurationMapper;

  @Autowired private FirmwareService firmwareService;

  @Autowired private EventService eventService;

  public BundleService() {

    // Parameterless constructor required for transactions...
  }

  @Transactional(value = "transactionManager")
  public void handleBundle(
      final MessageMetadata deviceMessageMetadata,
      final BundleMessageRequest bundleMessageDataContainer)
      throws FunctionalException {

    LOGGER.info(
        "handleBundle request for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final BundleMessagesRequestDto bundleMessageDataContainerDto =
        this.actionMapperService.mapAllActions(bundleMessageDataContainer, smartMeter);

    LOGGER.info("Sending request message to core.");
    final RequestMessage requestMessage =
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeter.getIpAddress(),
            bundleMessageDataContainerDto);
    this.osgpCoreRequestMessageSender.send(
        requestMessage,
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.isBypassRetry());
  }

  @Transactional(value = "transactionManager")
  public void handleBundleResponse(
      final MessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException,
      final BundleMessagesRequestDto bundleResponseMessageDataContainerDto)
      throws FunctionalException {

    LOGGER.info(
        "handleBundle response for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    this.checkIfAdditionalActionIsNeeded(
        deviceMessageMetadata, bundleResponseMessageDataContainerDto);

    // convert bundleResponseMessageDataContainerDto back to core object
    final BundleMessagesResponse bundleResponseMessageDataContainer =
        this.actionMapperResponseService.mapAllActions(bundleResponseMessageDataContainerDto);

    // Send the response final containing the events final to the
    // webservice-adapter

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(responseMessageResultType)
            .withOsgpException(osgpException)
            .withDataObject(bundleResponseMessageDataContainer)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();

    LOGGER.info("Send response for CorrelationUID: {}", deviceMessageMetadata.getCorrelationUid());
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
    LOGGER.info("Response sent for CorrelationUID: {}", deviceMessageMetadata.getCorrelationUid());
  }

  private void checkIfAdditionalActionIsNeeded(
      final MessageMetadata deviceMessageMetadata,
      final BundleMessagesRequestDto bundleResponseMessageDataContainerDto)
      throws FunctionalException {

    for (final ActionResponseDto action : bundleResponseMessageDataContainerDto.getAllResponses()) {
      if (action instanceof CoupleMbusDeviceByChannelResponseDto) {
        this.mBusGatewayService.handleCoupleMbusDeviceByChannelResponse(
            deviceMessageMetadata, (CoupleMbusDeviceByChannelResponseDto) action);
      } else if (action instanceof DecoupleMbusDeviceResponseDto) {
        this.mBusGatewayService.handleDecoupleMbusDeviceResponse(
            deviceMessageMetadata, (DecoupleMbusDeviceResponseDto) action);
      } else if (action instanceof SetDeviceLifecycleStatusByChannelResponseDto) {
        this.managementService.setDeviceLifecycleStatusByChannel(
            (SetDeviceLifecycleStatusByChannelResponseDto) action);
      } else if (action instanceof EventMessageDataResponseDto) {
        this.eventService.addEventTypeToEvents(
            deviceMessageMetadata, (EventMessageDataResponseDto) action);
      } else if (action instanceof FirmwareVersionResponseDto) {
        final List<FirmwareVersion> firmwareVersions =
            this.configurationMapper.mapAsList(
                ((FirmwareVersionResponseDto) action).getFirmwareVersions(), FirmwareVersion.class);
        this.firmwareService.saveFirmwareVersionsReturnedFromDevice(
            deviceMessageMetadata.getDeviceIdentification(), firmwareVersions);
      } else if (action instanceof FirmwareVersionGasResponseDto) {
        final FirmwareVersionGasResponseDto firmwareVersionGasResponseDto =
            (FirmwareVersionGasResponseDto) action;
        final FirmwareVersion firmwareVersion =
            this.configurationMapper.map(
                firmwareVersionGasResponseDto.getFirmwareVersion(), FirmwareVersion.class);
        this.firmwareService.saveFirmwareVersionsReturnedFromDevice(
            firmwareVersionGasResponseDto.getFirmwareVersion().getMbusDeviceIdentification(),
            Arrays.asList(firmwareVersion));
      }
    }
  }
}
