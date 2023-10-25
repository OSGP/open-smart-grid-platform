// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.Arrays;
import java.util.List;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.JmsMessageSender;
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
  private JmsMessageSender osgpCoreRequestMessageSender;

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
      final MessageMetadata messageMetadata, final BundleMessageRequest bundleMessageRequest)
      throws FunctionalException {

    LOGGER.info(
        "handleBundle request for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());

    final BundleMessagesRequestDto requestDto =
        this.actionMapperService.mapAllActions(bundleMessageRequest, smartMeter);

    LOGGER.info("Sending request message to core.");

    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withNetworkAddress(smartMeter.getNetworkAddress())
            .withNetworkSegmentIds(smartMeter.getBtsId(), smartMeter.getCellId())
            .withDeviceModelCode(smartMeter.getDeviceModel().getModelCode())
            .build());
  }

  @Transactional(value = "transactionManager")
  public void handleBundleResponse(
      final MessageMetadata messageMetadata,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException,
      final BundleMessagesRequestDto bundleMessagesRequestDto)
      throws FunctionalException {

    LOGGER.info(
        "handleBundle response for organisationIdentification: {} for deviceIdentification: {}",
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification());

    this.checkIfAdditionalActionIsNeeded(messageMetadata, bundleMessagesRequestDto);

    // Convert bundleMessagesRequestDto (containing the list of actions from the request, along with
    // their respective responses) back to core object.
    final BundleMessagesResponse bundleMessagesResponse =
        this.actionMapperResponseService.mapAllActions(bundleMessagesRequestDto);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withMessageMetadata(messageMetadata)
            .withResult(responseMessageResultType)
            .withOsgpException(osgpException)
            .withDataObject(bundleMessagesResponse)
            .build();

    LOGGER.info("Send response for CorrelationUID: {}", messageMetadata.getCorrelationUid());
    this.webServiceResponseMessageSender.send(responseMessage, messageMetadata.getMessageType());
    LOGGER.info("Response sent for CorrelationUID: {}", messageMetadata.getCorrelationUid());
  }

  private void checkIfAdditionalActionIsNeeded(
      final MessageMetadata messageMetadata,
      final BundleMessagesRequestDto bundleMessagesRequestDto)
      throws FunctionalException {

    for (final ActionResponseDto action : bundleMessagesRequestDto.getAllResponses()) {
      if (action instanceof CoupleMbusDeviceByChannelResponseDto) {
        this.mBusGatewayService.handleCoupleMbusDeviceByChannelResponse(
            messageMetadata, (CoupleMbusDeviceByChannelResponseDto) action);
      } else if (action instanceof DecoupleMbusDeviceResponseDto) {
        this.mBusGatewayService.handleDecoupleMbusDeviceResponse(
            messageMetadata, (DecoupleMbusDeviceResponseDto) action);
      } else if (action instanceof SetDeviceLifecycleStatusByChannelResponseDto) {
        this.managementService.setDeviceLifecycleStatusByChannel(
            (SetDeviceLifecycleStatusByChannelResponseDto) action);
      } else if (action instanceof EventMessageDataResponseDto) {
        this.eventService.enrichEvents(messageMetadata, (EventMessageDataResponseDto) action);
      } else if (action instanceof FirmwareVersionResponseDto) {
        final List<FirmwareVersion> firmwareVersions =
            this.configurationMapper.mapAsList(
                ((FirmwareVersionResponseDto) action).getFirmwareVersions(), FirmwareVersion.class);
        this.firmwareService.saveFirmwareVersionsReturnedFromDevice(
            messageMetadata.getDeviceIdentification(), firmwareVersions);
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
