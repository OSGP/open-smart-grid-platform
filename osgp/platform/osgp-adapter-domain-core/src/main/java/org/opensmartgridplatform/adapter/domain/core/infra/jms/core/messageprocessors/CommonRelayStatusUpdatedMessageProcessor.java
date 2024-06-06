// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.core.infra.jms.core.messageprocessors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.DomainCoreDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceRequestMessageSender;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CommonRelayStatusUpdatedMessageProcessor
    extends DomainCoreDeviceRequestMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonRelayStatusUpdatedMessageProcessor.class);

  @Qualifier("domainCoreOutboundWebServiceRequestsMessageSender")
  @Autowired
  private WebServiceRequestMessageSender webServiceRequestMessageSender;

  public CommonRelayStatusUpdatedMessageProcessor() {
    super(MessageType.RELAY_STATUS_UPDATED_EVENTS);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {

    final MessageMetadata messageMetadata;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      return;
    }

    final RequestMessage requestMsg =
        new RequestMessage(
            messageMetadata.getCorrelationUid(),
            messageMetadata.getOrganisationIdentification(),
            messageMetadata.getDeviceIdentification(),
            null);
    this.webServiceRequestMessageSender.send(requestMsg, messageMetadata.getMessageType());
  }
}
