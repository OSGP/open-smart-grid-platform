// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.infra.jms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;

@ExtendWith(MockitoExtension.class)
class LoggingObjectMessageCreatorTest {
  private LoggingObjectMessageCreator loggingObjectMessageCreator;
  private Session session;

  private LoggingRequestMessage loggingMessage;

  @BeforeEach
  public void setUp() {
    this.loggingMessage = this.getLoggingMessage();
    this.loggingObjectMessageCreator = new LoggingObjectMessageCreator(this.loggingMessage);
    this.session = mock(Session.class);
  }

  @Test
  void createObjectMessage() throws JMSException {
    final ObjectMessage expectedObjectMessage = mock(ObjectMessage.class);
    when(this.session.createObjectMessage()).thenReturn(expectedObjectMessage);

    final Message actualMessage =
        this.loggingObjectMessageCreator.getObjectMessage(this.session, this.loggingMessage);

    assertThat(actualMessage).isSameAs(expectedObjectMessage);
    verify(actualMessage).setJMSCorrelationID(this.loggingMessage.getCorrelationUid());
    verify(actualMessage).setJMSType(Constants.LOG_ITEM_REQUEST);
  }

  private LoggingRequestMessage getLoggingMessage() {
    return new LoggingRequestMessage(
        new Date(),
        new CorrelationIds("organisationIdentification", "device Identification", "uid"),
        "my username",
        "app name",
        new EndpointClassAndMethod("a", "b"),
        new ResponseResultAndDataSize("c", 1));
  }
}
