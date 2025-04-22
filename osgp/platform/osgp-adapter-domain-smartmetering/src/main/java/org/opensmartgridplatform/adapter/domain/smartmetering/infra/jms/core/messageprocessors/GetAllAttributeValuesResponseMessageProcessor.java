// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.AdhocService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class GetAllAttributeValuesResponseMessageProcessor
    extends OsgpCoreResponseMessageProcessor {

  private final AdhocService adhocService;

  public GetAllAttributeValuesResponseMessageProcessor(
      final WebServiceResponseMessageSender responseMessageSender,
      @Qualifier("domainSmartMeteringInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap,
      @Qualifier("domainSmartMeteringAdhocService") final AdhocService adhocService) {
    super(
        responseMessageSender,
        messageProcessorMap,
        MessageType.GET_ALL_ATTRIBUTE_VALUES,
        ComponentType.DOMAIN_SMART_METERING);
    this.adhocService = adhocService;
  }

  @Override
  protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
    return responseMessage.getDataObject() instanceof String;
  }

  @Override
  protected void handleMessage(
      final MessageMetadata deviceMessageMetadata,
      final ResponseMessage responseMessage,
      final OsgpException osgpException) {
    this.adhocService.handleGetAllAttributeValuesResponse(
        deviceMessageMetadata,
        responseMessage.getResult(),
        osgpException,
        (String) responseMessage.getDataObject());
  }
}
