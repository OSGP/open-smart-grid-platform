// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetKeysRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class GetKeysRequestMessageProcessor extends BaseRequestMessageProcessor {

  private final ConfigurationService configurationService;

  protected GetKeysRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap,
      @Qualifier("domainSmartMeteringConfigurationService")
          final ConfigurationService configurationService) {
    super(messageProcessorMap, MessageType.GET_KEYS);
    this.configurationService = configurationService;
  }

  @Override
  protected void handleMessage(final MessageMetadata messageMetadata, final Object dataObject)
      throws FunctionalException {

    final GetKeysRequestData getKeysRequestData = (GetKeysRequestData) dataObject;

    this.configurationService.getKeys(messageMetadata, getKeysRequestData);
  }
}
