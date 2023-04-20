/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import java.io.Serializable;
import java.time.Duration;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.JmsMessageCreator;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeviceRequestMessageSender {

  @Autowired
  @Qualifier("protocolDlmsDeviceRequestMessageSenderJmsTemplate")
  private JmsTemplate jmsTemplate;

  @Autowired private JmsMessageCreator jmsMessageCreator;

  public void send(
      final Serializable payload, final MessageMetadata messageMetadata, final Duration delay) {

    this.jmsTemplate.send(
        session -> {
          final ObjectMessage objectMessage =
              this.jmsMessageCreator.createObjectMessage(session, payload, delay.toMillis());
          messageMetadata.applyTo(objectMessage);
          return objectMessage;
        });
  }
}
