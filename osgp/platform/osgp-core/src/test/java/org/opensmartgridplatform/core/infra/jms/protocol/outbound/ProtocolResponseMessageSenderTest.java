//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.core.infra.jms.protocol.outbound;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.activemq.command.ActiveMQDestination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.jms.core.JmsTemplate;

@ExtendWith(MockitoExtension.class)
class ProtocolResponseMessageSenderTest {

  private static final String MESSAGE_TYPE = "type";

  @Mock private ProtocolResponseMessageJmsTemplateFactory templateFactory;

  @Mock private JmsTemplate jmsTemplate;

  @Mock private ResponseMessage responseMessage;

  @Mock private ProtocolInfo protocolInfo;

  @Mock private MessageMetadata messageMetadata;

  @InjectMocks private ProtocolResponseMessageSender messageSender;

  @Test
  void testSendWithoutDestination() {
    final ActiveMQDestination defaultDestination = mock(ActiveMQDestination.class);
    when(this.templateFactory.getJmsTemplate(any())).thenReturn(this.jmsTemplate);
    when(this.jmsTemplate.getDefaultDestination()).thenReturn(defaultDestination);

    this.messageSender.send(
        this.responseMessage, MESSAGE_TYPE, this.protocolInfo, this.messageMetadata);

    verify(this.jmsTemplate).send(eq(defaultDestination), any());
  }

  @Test
  void testSendWithDestination() {
    final ActiveMQDestination destination = mock(ActiveMQDestination.class);
    when(this.templateFactory.getJmsTemplate(any())).thenReturn(this.jmsTemplate);

    this.messageSender.sendWithDestination(
        this.responseMessage, MESSAGE_TYPE, this.protocolInfo, this.messageMetadata, destination);

    verify(this.jmsTemplate).send(eq(destination), any());
  }
}
