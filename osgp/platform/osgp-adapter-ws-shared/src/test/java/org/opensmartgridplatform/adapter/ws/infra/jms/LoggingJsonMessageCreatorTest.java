// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.infra.jms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;

@ExtendWith(MockitoExtension.class)
class LoggingJsonMessageCreatorTest {
  private LoggingJsonMessageCreator loggingJsonMessageCreator;
  private Session session;

  private LoggingRequestMessage loggingMessage;

  @BeforeEach
  public void setUp() {
    this.loggingMessage = this.getLoggingMessage();
    this.loggingJsonMessageCreator = new LoggingJsonMessageCreator(this.loggingMessage);
    this.session = mock(Session.class);
  }

  @Test
  void createJsonMessage() throws JMSException {
    final TextMessage expectedTextMessage = mock(TextMessage.class);
    when(this.session.createTextMessage(any())).thenReturn(expectedTextMessage);

    final Message actualMessage = this.loggingJsonMessageCreator.getJsonMessage(this.session);

    assertThat(actualMessage).isSameAs(expectedTextMessage);
    verify(actualMessage).setJMSType(Constants.LOG_ITEM_REQUEST);
    verify(actualMessage).setJMSCorrelationID(this.loggingMessage.getCorrelationUid());
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
