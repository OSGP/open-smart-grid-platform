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
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AssociationLnListType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ScanMbusChannelsResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequest;
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
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
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
  private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

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
      final MessageMetadata deviceMessageMetadata,
      final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
              .SynchronizeTimeRequestData
          synchronizeTimeRequestDataValueObject)
      throws FunctionalException {

    LOGGER.debug(
        "synchronizeTime for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final SynchronizeTimeRequestDto synchronizeTimeRequestDto =
        this.mapperFactory
            .getMapperFacade()
            .map(synchronizeTimeRequestDataValueObject, SynchronizeTimeRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            synchronizeTimeRequestDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.isBypassRetry());
  }

  public void handleSynchronizeTimeResponse(
      final MessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    LOGGER.debug(
        "handleSynchronizeTimeResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      LOGGER.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
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

  public void getAllAttributeValues(final MessageMetadata deviceMessageMetadata)
      throws FunctionalException {

    LOGGER.debug(
        "retrieveAllAttributeValues for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final GetAllAttributeValuesRequestDto requestDto = new GetAllAttributeValuesRequestDto();

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
        deviceMessageMetadata.isBypassRetry());
  }

  public void handleGetAllAttributeValuesResponse(
      final MessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final String resultData) {

    LOGGER.debug(
        "handleGetAllAttributeValuesResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      LOGGER.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(resultData)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void getAssociationLnObjects(final MessageMetadata deviceMessageMetadata)
      throws FunctionalException {
    LOGGER.debug(
        "getAssociationLnObjects for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final GetAssociationLnObjectsRequestDto requestDto = new GetAssociationLnObjectsRequestDto();

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
        deviceMessageMetadata.isBypassRetry());
  }

  public void handleGetAssocationLnObjectsResponse(
      final MessageMetadata deviceMessageMetadata,
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
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(associationLnListValueDomain)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void getSpecificAttributeValue(
      final MessageMetadata deviceMessageMetadata, final SpecificAttributeValueRequest request)
      throws FunctionalException {

    LOGGER.debug(
        "getSpecificAttributeValue for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final SpecificAttributeValueRequestDto requestDto =
        new SpecificAttributeValueRequestDto(
            request.getClassId(),
            request.getAttribute(),
            this.mapperFactory
                .getMapperFacade()
                .map(request.getObisCode(), ObisCodeValuesDto.class));

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
        deviceMessageMetadata.isBypassRetry());
  }

  public void handleGetSpecificAttributeValueResponse(
      final MessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final String resultData) {
    LOGGER.debug(
        "handleGetSpecificAttributeValueResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      LOGGER.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(resultData)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void scanMbusChannels(final MessageMetadata deviceMessageMetadata)
      throws FunctionalException {

    LOGGER.debug(
        "ScanMbusChannels for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final ScanMbusChannelsRequestDataDto requestDto = new ScanMbusChannelsRequestDataDto();

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
        deviceMessageMetadata.isBypassRetry());
  }

  public void handleScanMbusChannelsResponse(
      final MessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final ScanMbusChannelsResponseDto resultData) {
    LOGGER.debug(
        "handleScanMbusChannelsResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      LOGGER.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ScanMbusChannelsResponseData scanMbusChannelsResponseData =
        this.mapperFactory.getMapperFacade().map(resultData, ScanMbusChannelsResponseData.class);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(scanMbusChannelsResponseData)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }
}
