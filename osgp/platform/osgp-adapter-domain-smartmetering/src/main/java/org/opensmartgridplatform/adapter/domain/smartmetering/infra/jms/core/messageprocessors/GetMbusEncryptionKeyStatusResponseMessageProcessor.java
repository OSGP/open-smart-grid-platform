// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class GetMbusEncryptionKeyStatusResponseMessageProcessor
    extends OsgpCoreResponseMessageProcessor {

  @Autowired private ConfigurationService configurationService;

  @Autowired
  public GetMbusEncryptionKeyStatusResponseMessageProcessor(
      final WebServiceResponseMessageSender responseMessageSender,
      @Qualifier("domainSmartMeteringInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(
        responseMessageSender,
        messageProcessorMap,
        MessageType.GET_MBUS_ENCRYPTION_KEY_STATUS,
        ComponentType.DOMAIN_SMART_METERING);
  }

  @Override
  protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
    return responseMessage.getDataObject() instanceof GetMbusEncryptionKeyStatusResponseDto;
  }

  @Override
  protected void handleMessage(
      final MessageMetadata deviceMessageMetadata,
      final ResponseMessage responseMessage,
      final OsgpException osgpException) {

    final GetMbusEncryptionKeyStatusResponseDto getMbusEncryptionKeyStatusResponseDto =
        (GetMbusEncryptionKeyStatusResponseDto) responseMessage.getDataObject();
    this.configurationService.handleGetMbusEncryptionKeyStatusResponse(
        deviceMessageMetadata,
        responseMessage.getResult(),
        osgpException,
        getMbusEncryptionKeyStatusResponseDto);
  }
}
