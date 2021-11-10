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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DlmsLogItemRequestJsonMessageCreatorTest {

  private Session session;

  private DlmsLogItemRequestMessage dlmsLogItemRequestMessage;
  private DlmsLogItemRequestJsonMessageCreator dlmsLogItemRequestJsonMessageCreator;

  @BeforeEach
  public void setUp() {
    this.dlmsLogItemRequestMessage = this.getDlmsLogItemRequestMessage();
    this.dlmsLogItemRequestJsonMessageCreator =
        new DlmsLogItemRequestJsonMessageCreator(this.dlmsLogItemRequestMessage);
    this.session = mock(Session.class);
  }

  @Test
  void createJsonMessage() throws JMSException {
    final TextMessage expectedTextMessage = mock(TextMessage.class);
    when(this.session.createTextMessage(any())).thenReturn(expectedTextMessage);

    final Message actualMessage =
        this.dlmsLogItemRequestJsonMessageCreator.getJsonMessage(this.session);

    assertThat(actualMessage).isSameAs(expectedTextMessage);
  }

  @Test
  void jsonMessageIsEmptyWhenThereIsNoInputMessage() {
    final Message actualMessage =
        this.dlmsLogItemRequestJsonMessageCreator.getJsonMessage(this.session);

    assertThat(actualMessage).isNull();
  }

  @Test
  void jsonMessageIsEmptyWhenExceptionIsThrown() throws JMSException {
    when(this.session.createTextMessage(any())).thenThrow(new JMSException("test jms exception"));

    final Message actualMessage =
        this.dlmsLogItemRequestJsonMessageCreator.getJsonMessage(this.session);

    assertThat(actualMessage).isNull();
  }

  private DlmsLogItemRequestMessage getDlmsLogItemRequestMessage() {
    return new DlmsLogItemRequestMessage(
        "deviceIdentification",
        "organisationIdentification",
        true,
        "encoded message".getBytes(),
        "decoded message");
  }
}
