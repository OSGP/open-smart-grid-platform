// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupLastGasp;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SetPushSetupLastGaspRequestMessageProcessor extends BaseRequestMessageProcessor {

  @Autowired
  @Qualifier("domainSmartMeteringConfigurationService")
  private ConfigurationService configurationService;

  @Autowired
  protected SetPushSetupLastGaspRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(messageProcessorMap, MessageType.SET_PUSH_SETUP_LAST_GASP);
  }

  @Override
  protected void handleMessage(final MessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {

    final PushSetupLastGasp pushSetupLastGasp = (PushSetupLastGasp) dataObject;

    this.configurationService.setPushSetupLastGasp(deviceMessageMetadata, pushSetupLastGasp);
  }
}
