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
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.CommonMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AddSmartMeterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
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

@Service(value = "domainSmartMeteringInstallationService")
public class InstallationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(InstallationService.class);

  @Autowired
  @Qualifier(value = "domainSmartMeteringOutboundOsgpCoreRequestsMessageSender")
  private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

  @Autowired private MapperFactory mapperFactory;

  @Autowired
  @Qualifier(value = "domainSmartMeteringOutboundWebServiceResponsesMessageSender")
  private WebServiceResponseMessageSender webServiceResponseMessageSender;

  @Autowired
  @Qualifier(value = "domainSmartMeteringSmartMeterService")
  private SmartMeterService smartMeterService;

  @Autowired private MBusGatewayService mBusGatewayService;

  @Autowired private CommonMapper commonMapper;

  public InstallationService() {
    // No-args constructor required for transactions...
  }

  public void addMeter(
      final DeviceMessageMetadata deviceMessageMetadata,
      final AddSmartMeterRequest addSmartMeterRequest)
      throws FunctionalException {
    final String organisationId = deviceMessageMetadata.getOrganisationIdentification();
    final String deviceId = deviceMessageMetadata.getDeviceIdentification();
    LOGGER.debug(
        "addMeter for organisationIdentification: {} for deviceIdentification: {}",
        organisationId,
        deviceId);
    final SmartMeteringDevice smartMeteringDevice = addSmartMeterRequest.getDevice();

    final String deviceIdentification = deviceMessageMetadata.getDeviceIdentification();

    this.smartMeterService.validateSmartMeterDoesNotExist(deviceIdentification);

    final SmartMeter smartMeter = this.smartMeterService.convertSmartMeter(smartMeteringDevice);
    this.smartMeterService.storeMeter(organisationId, addSmartMeterRequest, smartMeter);
    this.osgpCoreRequestMessageSender.send(
        this.getRequestMessage(deviceMessageMetadata, smartMeteringDevice),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  /**
   * In case of errors that prevented adding the meter to the protocol database, the meter should be
   * removed from the core database as well.
   */
  public void removeMeter(final DeviceMessageMetadata deviceMessageMetadata) {
    LOGGER.warn(
        "Removing meter {} for organization {}, because adding it to the protocol database failed with "
            + "correlation UID {}",
        deviceMessageMetadata.getDeviceIdentification(),
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getCorrelationUid());

    this.smartMeterService.removeMeter(deviceMessageMetadata);
  }

  private RequestMessage getRequestMessage(
      final DeviceMessageMetadata deviceMessageMetadata,
      final SmartMeteringDevice smartMeteringDevice) {
    return new RequestMessage(
        deviceMessageMetadata.getCorrelationUid(),
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification(),
        this.mapperFactory
            .getMapperFacade()
            .map(smartMeteringDevice, SmartMeteringDeviceDto.class));
  }

  @Transactional(value = "transactionManager")
  public void handleAddMeterResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {
    this.handleResponse(
        "handleDefaultDeviceResponse", deviceMessageMetadata, deviceResult, exception);
  }

  public void coupleMbusDevice(
      final DeviceMessageMetadata deviceMessageMetadata,
      final CoupleMbusDeviceRequestData requestData)
      throws FunctionalException {
    this.mBusGatewayService.coupleMbusDevice(deviceMessageMetadata, requestData);
  }

  @Transactional(value = "transactionManager")
  public void decoupleMbusDevice(
      final DeviceMessageMetadata deviceMessageMetadata,
      final DecoupleMbusDeviceRequestData requestData)
      throws FunctionalException {
    this.mBusGatewayService.decoupleMbusDevice(deviceMessageMetadata, requestData);
  }

  @Transactional(value = "transactionManager")
  public void coupleMbusDeviceByChannel(
      final DeviceMessageMetadata deviceMessageMetadata,
      final CoupleMbusDeviceByChannelRequestData requestData)
      throws FunctionalException {
    this.mBusGatewayService.coupleMbusDeviceByChannel(deviceMessageMetadata, requestData);
  }

  @Transactional(value = "transactionManager")
  public void handleCoupleMbusDeviceResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType result,
      final OsgpException exception,
      final MbusChannelElementsResponseDto dataObject)
      throws FunctionalException {
    if (exception == null) {
      this.mBusGatewayService.handleCoupleMbusDeviceResponse(deviceMessageMetadata, dataObject);
    }
    this.handleResponse("coupleMbusDevice", deviceMessageMetadata, result, exception);
  }

  @Transactional(value = "transactionManager")
  public void decoupleMbusDeviceByChannel(
      final DeviceMessageMetadata deviceMessageMetadata,
      final DecoupleMbusDeviceByChannelRequestData requestData)
      throws FunctionalException {
    this.mBusGatewayService.decoupleMbusDeviceByChannel(deviceMessageMetadata, requestData);
  }

  @Transactional(value = "transactionManager")
  public void handleDecoupleMbusDeviceResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType result,
      final OsgpException exception,
      final DecoupleMbusDeviceResponseDto decoupleMbusDeviceResponseDto)
      throws FunctionalException {
    if (exception == null) {
      this.mBusGatewayService.handleDecoupleMbusDeviceResponse(
          deviceMessageMetadata, decoupleMbusDeviceResponseDto);
    }
    this.handleResponse("decoupleMbusDevice", deviceMessageMetadata, result, exception);
  }

  @Transactional(value = "transactionManager")
  public void handleDecoupleMbusDeviceByChannelResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException,
      final DecoupleMbusDeviceResponseDto decoupleMbusDeviceResponseDto)
      throws FunctionalException {

    if (osgpException == null) {
      this.mBusGatewayService.handleDecoupleMbusDeviceResponse(
          deviceMessageMetadata, decoupleMbusDeviceResponseDto);
    }

    final DecoupleMbusDeviceByChannelResponse response =
        new DecoupleMbusDeviceByChannelResponse(
            decoupleMbusDeviceResponseDto.getMbusDeviceIdentification(),
            decoupleMbusDeviceResponseDto.getChannelElementValues().getChannel());

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(responseMessageResultType)
            .withOsgpException(osgpException)
            .withDataObject(response)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();

    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  @Transactional(value = "transactionManager")
  public void handleCoupleMbusDeviceByChannelResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException,
      final CoupleMbusDeviceByChannelResponseDto dataObject)
      throws FunctionalException {

    this.mBusGatewayService.handleCoupleMbusDeviceByChannelResponse(
        deviceMessageMetadata, dataObject);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(responseMessageResultType)
            .withOsgpException(osgpException)
            .withDataObject(
                this.commonMapper.map(dataObject, CoupleMbusDeviceByChannelResponse.class))
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();

    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  @Transactional(value = "transactionManager")
  public void handleResponse(
      final String methodName,
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    LOGGER.debug("{} for MessageType: {}", methodName, deviceMessageMetadata.getMessageType());

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(this.getResponseMessageResultType(deviceResult, exception))
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();

    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  private ResponseMessageResultType getResponseMessageResultType(
      final ResponseMessageResultType deviceResult, final OsgpException exception) {
    if (exception != null) {
      LOGGER.error("Device Response not ok. Unexpected Exception", exception);
      return ResponseMessageResultType.NOT_OK;
    }
    return deviceResult;
  }
}
