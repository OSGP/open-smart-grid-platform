// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.core.infra.jms.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.JmsMessageCreator;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

@ExtendWith(MockitoExtension.class)
class OsgpCoreRequestMessageSenderTest {

  @Mock private JmsTemplate jmsTemplate;
  @Mock private Session session;
  @Mock private ObjectMessage objectMessage;

  @Mock private JmsMessageCreator jmsMessageCreator;

  @InjectMocks private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

  @Test
  void testSend() throws JMSException {
    final RequestMessage requestMessage = mock(RequestMessage.class);
    final String messageType = "MESSAGE_TYPE";
    final int messagePriority = 8;
    final String ipAddress = "127.0.0.1";
    final Long delay = 123L;
    final String correlationUid = "CORR_UID";
    final String organisationIdentification = "ORG_ID";
    final String deviceIdentification = "DEVICE_ID";

    when(requestMessage.getCorrelationUid()).thenReturn(correlationUid);
    when(requestMessage.getOrganisationIdentification()).thenReturn(organisationIdentification);
    when(requestMessage.getDeviceIdentification()).thenReturn(deviceIdentification);

    when(this.jmsMessageCreator.createObjectMessage(this.session, delay))
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

    this.osgpCoreRequestMessageSender.sendWithDelay(
        requestMessage, messageType, messagePriority, ipAddress, delay);

    verify(this.jmsTemplate).send(any(MessageCreator.class));
    verify(this.objectMessage).setJMSType(messageType);
    verify(this.objectMessage).setJMSPriority(messagePriority);
    verify(this.objectMessage).setJMSCorrelationID(correlationUid);
    verify(this.objectMessage)
        .setStringProperty(Constants.ORGANISATION_IDENTIFICATION, organisationIdentification);
    verify(this.objectMessage)
        .setStringProperty(Constants.DEVICE_IDENTIFICATION, deviceIdentification);
    verify(this.objectMessage).setStringProperty(Constants.IP_ADDRESS, ipAddress);
    verify(this.objectMessage).setObject(requestMessage.getRequest());
  }
}
