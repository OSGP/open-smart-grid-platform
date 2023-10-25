// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.JmsMessageSender;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AssociationLnListType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ScanMbusChannelsResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SynchronizeTimeRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.TestAlarmSchedulerRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnListTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAllAttributeValuesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAssociationLnObjectsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ScanMbusChannelsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ScanMbusChannelsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TestAlarmSchedulerRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service(value = "domainSmartMeteringAdhocService")
@Transactional(value = "transactionManager")
public class AdhocService {

  private static final String DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION =
      "Device Response not ok. Unexpected Exception";

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

    log.debug(
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
            .withNetworkAddress(smartMeter.getNetworkAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .build());
  }

  public void scheduleTestAlarm(
      final MessageMetadata messageMetadata,
      final TestAlarmSchedulerRequestData testAlarmSchedulerRequestData)
      throws FunctionalException {

    log.debug(
        "scheduleTestAlarm for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());

    final TestAlarmSchedulerRequestDto requestDto =
        this.mapperFactory
            .getMapperFacade()
            .map(testAlarmSchedulerRequestData, TestAlarmSchedulerRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withNetworkAddress(smartMeter.getNetworkAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .build());
  }

  public void handleSynchronizeTimeResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    log.debug(
        "handleSynchronizeTimeResponse for MessageType: {}", messageMetadata.getMessageType());

    final ResponseMessage responseMessage =
        this.createMetadataOnlyResponseMessage(messageMetadata, deviceResult, exception);
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  public void getAllAttributeValues(final MessageMetadata messageMetadata)
      throws FunctionalException {

    log.debug(
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
            .withNetworkAddress(smartMeter.getNetworkAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .withDeviceModelCode(smartMeter.getDeviceModel().getModelCode())
            .build());
  }

  public void handleGetAllAttributeValuesResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final String resultData) {

    log.debug(
        "handleGetAllAttributeValuesResponse for MessageType: {}",
        messageMetadata.getMessageType());

    final ResponseMessage responseMessage =
        this.createResponseMessageWithDataObject(
            deviceResult, exception, messageMetadata, resultData);
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  public void getAssociationLnObjects(final MessageMetadata messageMetadata)
      throws FunctionalException {

    log.debug(
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
            .withNetworkAddress(smartMeter.getNetworkAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .withDeviceModelCode(smartMeter.getDeviceModel().getModelCode())
            .build());
  }

  public void handleGetAssocationLnObjectsResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final AssociationLnListTypeDto resultData) {

    final AssociationLnListType associationLnListValueDomain =
        this.configurationMapper.map(resultData, AssociationLnListType.class);

    final ResponseMessage responseMessage =
        this.createResponseMessageWithDataObject(
            deviceResult, exception, messageMetadata, associationLnListValueDomain);
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  public void getSpecificAttributeValue(
      final MessageMetadata messageMetadata, final SpecificAttributeValueRequest request)
      throws FunctionalException {

    log.debug(
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
            .withNetworkAddress(smartMeter.getNetworkAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .withDeviceModelCode(smartMeter.getDeviceModel().getModelCode())
            .build());
  }

  public void handleGetSpecificAttributeValueResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final String resultData) {

    log.debug(
        "handleGetSpecificAttributeValueResponse for MessageType: {}",
        messageMetadata.getMessageType());

    final ResponseMessage responseMessage =
        this.createResponseMessageWithDataObject(
            deviceResult, exception, messageMetadata, resultData);
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  public void scanMbusChannels(final MessageMetadata messageMetadata) throws FunctionalException {

    log.debug(
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
            .withNetworkAddress(smartMeter.getNetworkAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .build());
  }

  public void handleScanMbusChannelsResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final ScanMbusChannelsResponseDto resultData) {

    log.debug(
        "handleScanMbusChannelsResponse for MessageType: {}", messageMetadata.getMessageType());

    final ScanMbusChannelsResponseData scanMbusChannelsResponseData =
        this.mapperFactory.getMapperFacade().map(resultData, ScanMbusChannelsResponseData.class);

    final ResponseMessage responseMessage =
        this.createResponseMessageWithDataObject(
            deviceResult, exception, messageMetadata, scanMbusChannelsResponseData);
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  public void handleTestAlarmSchedulerResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    log.debug(
        "handleTestAlarmSchedulerResponse for MessageType: {}", messageMetadata.getMessageType());

    final ResponseMessage responseMessage =
        this.createMetadataOnlyResponseMessage(messageMetadata, deviceResult, exception);
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
  }

  private ResponseMessage createMetadataOnlyResponseMessage(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    final ResponseMessageResultType result = this.determineResult(deviceResult, exception);

    return ResponseMessage.newResponseMessageBuilder()
        .withMessageMetadata(messageMetadata)
        .withResult(result)
        .withOsgpException(exception)
        .build();
  }

  private ResponseMessage createResponseMessageWithDataObject(
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final MessageMetadata messageMetadata,
      final Serializable resultData) {

    final ResponseMessageResultType result = this.determineResult(deviceResult, exception);

    return ResponseMessage.newResponseMessageBuilder()
        .withMessageMetadata(messageMetadata)
        .withResult(result)
        .withOsgpException(exception)
        .withDataObject(resultData)
        .build();
  }

  private ResponseMessageResultType determineResult(
      final ResponseMessageResultType deviceResult, final OsgpException exception) {

    if (exception == null) {
      return deviceResult;

    } else {
      log.error(DEVICE_RESPONSE_NOT_OK_UNEXPECTED_EXCEPTION, exception);
      return ResponseMessageResultType.NOT_OK;
    }
  }
}
