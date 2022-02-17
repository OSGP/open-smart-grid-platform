/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.infra.jms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
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
