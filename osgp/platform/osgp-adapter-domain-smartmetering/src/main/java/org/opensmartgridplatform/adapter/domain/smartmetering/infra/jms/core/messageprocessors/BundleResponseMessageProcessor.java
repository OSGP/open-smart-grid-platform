// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.BundleService;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.util.FaultResponseFactory;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParameterDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BundleResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

  @Autowired
  @Qualifier("domainSmartMeteringBundleService")
  private BundleService bundleService;

  private final FaultResponseFactory faultResponseFactory;

  @Autowired
  public BundleResponseMessageProcessor(
      final WebServiceResponseMessageSender responseMessageSender,
      @Qualifier("domainSmartMeteringInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(
        responseMessageSender,
        messageProcessorMap,
        MessageType.HANDLE_BUNDLED_ACTIONS,
        ComponentType.DOMAIN_SMART_METERING);
    this.faultResponseFactory = new FaultResponseFactory();
  }

  @Override
  protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
    return responseMessage.getDataObject() instanceof BundleMessagesRequestDto;
  }

  @Override
  protected void handleMessage(
      final MessageMetadata deviceMessageMetadata,
      final ResponseMessage responseMessage,
      final OsgpException osgpException)
      throws FunctionalException {

    final BundleMessagesRequestDto bundleMessagesResponseDto =
        (BundleMessagesRequestDto) responseMessage.getDataObject();

    this.bundleService.handleBundleResponse(
        deviceMessageMetadata,
        responseMessage.getResult(),
        osgpException,
        bundleMessagesResponseDto);
  }

  @Override
  protected void handleError(
      final Exception e,
      final MessageMetadata deviceMessageMetadata,
      final ResponseMessage responseMessage)
      throws FunctionalException {

    log.info(
        "handling error: {} for correlationUid: {}",
        e.getMessage(),
        deviceMessageMetadata.getCorrelationUid(),
        e);

    final OsgpException osgpException = this.ensureOsgpException(e);

    final BundleMessagesRequestDto bundleMessagesResponseDto =
        (BundleMessagesRequestDto) responseMessage.getDataObject();

    final List<ActionDto> actionList = bundleMessagesResponseDto.getActionList();
    for (final ActionDto action : actionList) {
      if (action.getResponse() == null) {
        final List<FaultResponseParameterDto> parameterList = new ArrayList<>();
        final FaultResponseParameterDto deviceIdentificationParameter =
            new FaultResponseParameterDto(
                "deviceIdentification", deviceMessageMetadata.getDeviceIdentification());
        parameterList.add(deviceIdentificationParameter);
        action.setResponse(
            this.faultResponseFactory.faultResponseForException(
                e, parameterList, "Unable to handle request"));
      }
    }

    this.bundleService.handleBundleResponse(
        deviceMessageMetadata,
        responseMessage.getResult(),
        osgpException,
        bundleMessagesResponseDto);
  }
}
