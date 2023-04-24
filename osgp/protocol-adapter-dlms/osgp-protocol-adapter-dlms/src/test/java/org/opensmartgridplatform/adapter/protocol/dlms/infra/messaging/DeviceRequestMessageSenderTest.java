/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.time.Duration;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.opensmartgridplatform.shared.infra.jms.JmsMessageCreator;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

@ExtendWith(MockitoExtension.class)
class DeviceRequestMessageSenderTest {

  @Mock private JmsTemplate jmsTemplate;
  @Mock private Session session;
  @Mock private ObjectMessage objectMessage;

  @Mock private JmsMessageCreator jmsMessageCreator;

  @InjectMocks private DeviceRequestMessageSender deviceRequestMessageSender;

  @Test
  void testSend() throws JMSException {
    final Serializable payload = "1234567789";
    final MessageMetadata messageMetadata = MessageMetadata.newBuilder().build();
    final Duration delay = Duration.ofSeconds(5);

    when(this.jmsMessageCreator.createObjectMessage(this.session, payload, delay.toMillis()))
        .thenReturn(this.objectMessage);

    doAnswer(
            (Answer<Message>)
                invocation -> {
                  final Object[] args = invocation.getArguments();
                  final MessageCreator arg = (MessageCreator) args[0];
                  return arg.createMessage(this.session);
                })
        .when(this.jmsTemplate)
        .send(any(MessageCreator.class));

    this.deviceRequestMessageSender.send(payload, messageMetadata, delay);

    verify(this.jmsTemplate).send(any(MessageCreator.class));
  }
}
