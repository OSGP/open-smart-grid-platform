// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import jakarta.jms.ObjectMessage;
import java.io.Serializable;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.shared.infra.jms.JmsMessageCreator;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeviceRequestMessageSender {

  @Autowired
  @Qualifier("protocolDlmsDeviceRequestMessageSenderJmsTemplate")
  private JmsTemplate jmsTemplate;

  @Autowired private JmsMessageCreator jmsMessageCreator;

  public void send(
      final Serializable payload, final MessageMetadata messageMetadata, final Duration delay) {

    log.info(
        "Sending RequestMessage of type {} from DLMS protocol adapter with correlationUid {}",
        messageMetadata.getMessageType(),
        messageMetadata.getCorrelationUid());

    this.jmsTemplate.send(
        session -> {
          final ObjectMessage objectMessage =
              this.jmsMessageCreator.createObjectMessage(session, payload, delay.toMillis());
          messageMetadata.applyTo(objectMessage);
          return objectMessage;
        });
  }
}
