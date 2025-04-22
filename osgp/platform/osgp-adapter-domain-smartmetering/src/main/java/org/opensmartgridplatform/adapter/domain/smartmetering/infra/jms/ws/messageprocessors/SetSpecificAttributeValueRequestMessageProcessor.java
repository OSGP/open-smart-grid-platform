// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.AdhocService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetSpecificAttributeValueRequest;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SetSpecificAttributeValueRequestMessageProcessor extends BaseRequestMessageProcessor {

  private final AdhocService adhocService;

  protected SetSpecificAttributeValueRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap,
      @Qualifier("domainSmartMeteringAdhocService") final AdhocService adhocService) {
    super(messageProcessorMap, MessageType.SET_SPECIFIC_ATTRIBUTE_VALUE);
    this.adhocService = adhocService;
  }

  @Override
  protected void handleMessage(final MessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {

    final SetSpecificAttributeValueRequest request = (SetSpecificAttributeValueRequest) dataObject;

    this.adhocService.setSpecificAttributeValue(deviceMessageMetadata, request);
  }
}
