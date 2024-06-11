// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.Optional;
import ma.glasnost.orika.MapperFactory;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.CommonMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.InstallationMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.JmsMessageSender;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AddSmartMeterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceAdministrativeRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceAdministrativeResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
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

@Service(value = "domainSmartMeteringInstallationService")
public class InstallationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(InstallationService.class);

  @Autowired
  @Qualifier(value = "domainSmartMeteringOutboundOsgpCoreRequestsMessageSender")
  private JmsMessageSender osgpCoreRequestMessageSender;

  @Autowired private MapperFactory mapperFactory;

  @Autowired
  @Qualifier(value = "domainSmartMeteringOutboundWebServiceResponsesMessageSender")
  private WebServiceResponseMessageSender webServiceResponseMessageSender;

  @Autowired
  @Qualifier(value = "domainSmartMeteringSmartMeterService")
  private SmartMeterService smartMeterService;

  @Autowired private MBusGatewayService mBusGatewayService;

  @Autowired private CommonMapper commonMapper;

  @Autowired private InstallationMapper installationMapper;

  public InstallationService() {
    // No-args constructor required for transactions...
  }

  public void addMeter(
      final MessageMetadata messageMetadata, final AddSmartMeterRequest addSmartMeterRequest)
      throws FunctionalException {
    this.storeMeter(messageMetadata, addSmartMeterRequest);
    this.sendRequestToOsgpCore(messageMetadata, addSmartMeterRequest);
  }

  private void storeMeter(
      final MessageMetadata messageMetadata, final AddSmartMeterRequest addSmartMeterRequest)
      throws FunctionalException {
    final String organisationId = messageMetadata.getOrganisationIdentification();
    final String deviceId = messageMetadata.getDeviceIdentification();
    LOGGER.debug(
        "addMeter for organisationIdentification: {} for deviceIdentification: {}",
        organisationId,
        deviceId);
    final SmartMeteringDevice smartMeteringDevice = addSmartMeterRequest.getDevice();

    final String deviceIdentification = messageMetadata.getDeviceIdentification();
    final Optional<SmartMeter> existingSmartMeter =
        this.smartMeterService.validateSmartMeterDoesNotExist(
            deviceIdentification, addSmartMeterRequest.getOverwrite());

    final SmartMeter smartMeter = this.smartMeterService.convertSmartMeter(smartMeteringDevice);
    if (existingSmartMeter.isPresent()) {
      if (addSmartMeterRequest.getOverwrite()) { // overwrite existing device
        LOGGER.info(
            "UPDATE SmartMeter !! Update existing smart meter with device identification: {}",
            messageMetadata.getDeviceIdentification());
        this.smartMeterService.updateMeter(
            addSmartMeterRequest,
            this.smartMeterService.convertToExistingSmartMeter(
                smartMeter, existingSmartMeter.get()));
      } else {
        LOGGER.error(
            "ERROR: SmartMeter with device identification {} already exists and overwrite is not enabled",
            messageMetadata.getDeviceIdentification());
      }
    } else {
      this.smartMeterService.storeMeter(organisationId, addSmartMeterRequest, smartMeter);
    }
  }

  private void sendRequestToOsgpCore(
      final MessageMetadata messageMetadata, final AddSmartMeterRequest addSmartMeterRequest) {
    final SmartMeteringDeviceDto requestDto =
        this.mapperFactory
            .getMapperFacade()
            .map(addSmartMeterRequest.getDevice(), SmartMeteringDeviceDto.class);
    this.osgpCoreRequestMessageSender.send(requestDto, messageMetadata); // en dan??
  }

  /**
   * In case of errors that prevented adding the meter to the protocol database, the meter should be
   * removed from the core database as well.
   */
  public void removeMeter(final MessageMetadata messageMetadata) {
    LOGGER.warn(
        "Removing meter {} for organization {}, because adding it to the protocol database failed with "
            + "correlation UID {}",
        messageMetadata.getDeviceIdentification(),
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getCorrelationUid());

    this.smartMeterService.removeMeter(messageMetadata);
  }

  @Transactional(value = "transactionManager")
  public void handleAddMeterResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {
    this.doHandleResponse("handleDefaultDeviceResponse", messageMetadata, deviceResult, exception);
  }

  public void coupleMbusDevice(
      final MessageMetadata messageMetadata, final CoupleMbusDeviceRequestData requestData)
      throws FunctionalException {
    this.mBusGatewayService.coupleMbusDevice(messageMetadata, requestData);
  }

  @Transactional(value = "transactionManager")
  public void decoupleMbusDevice(
      final MessageMetadata messageMetadata, final DecoupleMbusDeviceRequestData requestData)
      throws FunctionalException {
    this.mBusGatewayService.decoupleMbusDevice(messageMetadata, requestData);
  }

  @Transactional(value = "transactionManager")
  public void decoupleMbusDeviceAdministrative(
      final MessageMetadata messageMetadata,
      final DecoupleMbusDeviceAdministrativeRequestData requestData)
      throws FunctionalException {
    this.mBusGatewayService.decoupleMbusDeviceAdministrative(messageMetadata, requestData);

    final DecoupleMbusDeviceAdministrativeResponse response =
        new DecoupleMbusDeviceAdministrativeResponse(requestData.getMbusDeviceIdentification());

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(ResponseMessageResultType.OK)
            .withOsgpException(null)
            .withDataObject(response)
            .build();

    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  @Transactional(value = "transactionManager")
  public void coupleMbusDeviceByChannel(
      final MessageMetadata messageMetadata, final CoupleMbusDeviceByChannelRequestData requestData)
      throws FunctionalException {
    this.mBusGatewayService.coupleMbusDeviceByChannel(messageMetadata, requestData);
  }

  @Transactional(value = "transactionManager")
  public void handleCoupleMbusDeviceResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException,
      final CoupleMbusDeviceResponseDto dataObject)
      throws FunctionalException {

    if (osgpException == null) {
      this.mBusGatewayService.handleCoupleMbusDeviceResponse(messageMetadata, dataObject);
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(responseMessageResultType)
            .withOsgpException(osgpException)
            .withDataObject(this.commonMapper.map(dataObject, CoupleMbusDeviceResponse.class))
            .build();

    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  @Transactional(value = "transactionManager")
  public void decoupleMbusDeviceByChannel(
      final MessageMetadata messageMetadata,
      final DecoupleMbusDeviceByChannelRequestData requestData)
      throws FunctionalException {
    this.mBusGatewayService.decoupleMbusDeviceByChannel(messageMetadata, requestData);
  }

  @Transactional(value = "transactionManager")
  public void handleDecoupleMbusDeviceResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType result,
      final OsgpException exception,
      final DecoupleMbusDeviceResponseDto decoupleMbusDeviceResponseDto)
      throws FunctionalException {
    if (exception == null) {
      this.mBusGatewayService.handleDecoupleMbusDeviceResponse(
          messageMetadata, decoupleMbusDeviceResponseDto);
    }
    this.doHandleResponse("decoupleMbusDevice", messageMetadata, result, exception);
  }

  @Transactional(value = "transactionManager")
  public void handleDecoupleMbusDeviceByChannelResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException,
      final DecoupleMbusDeviceResponseDto decoupleMbusDeviceResponseDto)
      throws FunctionalException {

    if (osgpException == null) {
      this.mBusGatewayService.handleDecoupleMbusDeviceResponse(
          messageMetadata, decoupleMbusDeviceResponseDto);
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(responseMessageResultType)
            .withOsgpException(osgpException)
            .withDataObject(
                this.installationMapper.map(
                    decoupleMbusDeviceResponseDto, DecoupleMbusDeviceByChannelResponse.class))
            .build();

    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  @Transactional(value = "transactionManager")
  public void handleCoupleMbusDeviceByChannelResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException,
      final CoupleMbusDeviceByChannelResponseDto dataObject)
      throws FunctionalException {

    this.mBusGatewayService.handleCoupleMbusDeviceByChannelResponse(messageMetadata, dataObject);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(responseMessageResultType)
            .withOsgpException(osgpException)
            .withDataObject(
                this.commonMapper.map(dataObject, CoupleMbusDeviceByChannelResponse.class))
            .build();

    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  @Transactional(value = "transactionManager")
  public void handleResponse(
      final String methodName,
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    this.doHandleResponse(methodName, messageMetadata, deviceResult, exception);
  }

  private void doHandleResponse(
      final String methodName,
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {
    LOGGER.debug("{} for MessageType: {}", methodName, messageMetadata.getMessageType());

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(this.getResponseMessageResultType(deviceResult, exception))
            .withOsgpException(exception)
            .build();

    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
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
