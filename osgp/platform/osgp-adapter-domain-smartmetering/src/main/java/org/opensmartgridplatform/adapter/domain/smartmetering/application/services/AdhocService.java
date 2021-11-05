/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import ma.glasnost.orika.MapperFactory;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.JmsMessageSender;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AssociationLnListType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ScanMbusChannelsResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SynchronizeTimeRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnListTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAllAttributeValuesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAssociationLnObjectsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ScanMbusChannelsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ScanMbusChannelsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;
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

@Service(value = "domainSmartMeteringAdhocService")
@Transactional(value = "transactionManager")
public class AdhocService {

  private static final String DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION =
      "Device Response not ok. Unexpected " + "Exception";

  private static final Logger LOGGER = LoggerFactory.getLogger(AdhocService.class);

  @Autowired
  @Qualifier(value = "domainSmartMeteringOutboundOsgpCoreRequestsMessageSender")
  private JmsMessageSender osgpCoreRequestMessageSender;

  @Autowired
  @Qualifier(value = "domainSmartMeteringOutboundWebServiceResponsesMessageSender")
  private WebServiceResponseMessageSender webServiceResponseMessageSender;

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private MapperFactory mapperFactory;

  @Autowired private ConfigurationMapper configurationMapper;

  public AdhocService() {
    // Parameterless constructor required for transactions...
  }

  public void synchronizeTime(
      final MessageMetadata messageMetadata,
      final SynchronizeTimeRequestData synchronizeTimeRequestData)
      throws FunctionalException {

    LOGGER.debug(
        "synchronizeTime for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());

    final SynchronizeTimeRequestDto requestDto =
        this.mapperFactory
            .getMapperFacade()
            .map(synchronizeTimeRequestData, SynchronizeTimeRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withIpAddress(smartMeter.getIpAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .build());
  }

  public void handleSynchronizeTimeResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    LOGGER.debug(
        "handleSynchronizeTimeResponse for MessageType: {}", messageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      LOGGER.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
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

  public void getAllAttributeValues(final MessageMetadata messageMetadata)
      throws FunctionalException {

    LOGGER.debug(
        "retrieveAllAttributeValues for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());

    final GetAllAttributeValuesRequestDto requestDto = new GetAllAttributeValuesRequestDto();

    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withIpAddress(smartMeter.getIpAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .build());
  }

  public void handleGetAllAttributeValuesResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final String resultData) {

    LOGGER.debug(
        "handleGetAllAttributeValuesResponse for MessageType: {}",
        messageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      LOGGER.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(resultData)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  public void getAssociationLnObjects(final MessageMetadata messageMetadata)
      throws FunctionalException {
    LOGGER.debug(
        "getAssociationLnObjects for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());

    final GetAssociationLnObjectsRequestDto requestDto = new GetAssociationLnObjectsRequestDto();

    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withIpAddress(smartMeter.getIpAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .build());
  }

  public void handleGetAssocationLnObjectsResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final AssociationLnListTypeDto resultData) {

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      LOGGER.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final AssociationLnListType associationLnListValueDomain =
        this.configurationMapper.map(resultData, AssociationLnListType.class);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(associationLnListValueDomain)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  public void getSpecificAttributeValue(
      final MessageMetadata messageMetadata, final SpecificAttributeValueRequest request)
      throws FunctionalException {

    LOGGER.debug(
        "getSpecificAttributeValue for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());

    final SpecificAttributeValueRequestDto requestDto =
        new SpecificAttributeValueRequestDto(
            request.getClassId(),
            request.getAttribute(),
            this.mapperFactory
                .getMapperFacade()
                .map(request.getObisCode(), ObisCodeValuesDto.class));

    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withIpAddress(smartMeter.getIpAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .build());
  }

  public void handleGetSpecificAttributeValueResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final String resultData) {
    LOGGER.debug(
        "handleGetSpecificAttributeValueResponse for MessageType: {}",
        messageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      LOGGER.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(resultData)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  public void scanMbusChannels(final MessageMetadata messageMetadata) throws FunctionalException {

    LOGGER.debug(
        "ScanMbusChannels for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());

    final ScanMbusChannelsRequestDataDto requestDto = new ScanMbusChannelsRequestDataDto();

    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withIpAddress(smartMeter.getIpAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .build());
  }

  public void handleScanMbusChannelsResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final ScanMbusChannelsResponseDto resultData) {
    LOGGER.debug(
        "handleScanMbusChannelsResponse for MessageType: {}", messageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      LOGGER.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ScanMbusChannelsResponseData scanMbusChannelsResponseData =
        this.mapperFactory.getMapperFacade().map(resultData, ScanMbusChannelsResponseData.class);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(scanMbusChannelsResponseData)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }
}
