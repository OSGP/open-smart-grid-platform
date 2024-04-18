// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetThdConfigurationRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SetThdConfigurationRequestMessageProcessor extends BaseRequestMessageProcessor {

  private final ConfigurationService configurationService;

  protected SetThdConfigurationRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap,
      @Qualifier("domainSmartMeteringConfigurationService")
          final ConfigurationService configurationService) {
    super(messageProcessorMap, MessageType.SET_THD_CONFIGURATION);
    this.configurationService = configurationService;
  }

  @Override
  protected void handleMessage(final MessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {

    this.configurationService.setThdConfiguration(
        deviceMessageMetadata, (SetThdConfigurationRequestData) dataObject);
  }
}
