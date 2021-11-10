/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.core.infra.messaging;

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
class CoreLogItemJsonMessageCreatorTest {
  private Session session;

  private CoreLogItemRequestMessage coreLogItemRequestMessage;
  private CoreLogItemJsonMessageCreator jsonMessageCreator;

  @BeforeEach
  public void setUp() {
    this.jsonMessageCreator = new CoreLogItemJsonMessageCreator();
    this.coreLogItemRequestMessage = this.getCoreLogItemRequestMessage();
    this.session = mock(Session.class);
  }

  @Test
  void createJsonMessage() throws JMSException {
    final TextMessage expectedTextMessage = mock(TextMessage.class);
    when(this.session.createTextMessage(any())).thenReturn(expectedTextMessage);

    final Message actualMessage =
        this.jsonMessageCreator.getJsonMessage(this.coreLogItemRequestMessage, this.session);

    assertThat(actualMessage).isSameAs(expectedTextMessage);
  }

  @Test
  void jsonMessageIsEmptyWhenThereIsNoInputMessage() {
    final Message actualMessage = this.jsonMessageCreator.getJsonMessage(null, this.session);

    assertThat(actualMessage).isNull();
  }

  @Test
  void jsonMessageIsEmptyWhenInputMessageHasNoValues() {
    final Message actualMessage =
        this.jsonMessageCreator.getJsonMessage(
            new CoreLogItemRequestMessage(null, null, null), this.session);

    assertThat(actualMessage).isNull();
  }

  @Test
  void jsonMessageIsEmptyWhenExceptionIsThrown() throws JMSException {
    when(this.session.createTextMessage(any())).thenThrow(new JMSException("test jsm exception"));

    final Message actualMessage =
        this.jsonMessageCreator.getJsonMessage(this.coreLogItemRequestMessage, this.session);

    assertThat(actualMessage).isNull();
  }

  private CoreLogItemRequestMessage getCoreLogItemRequestMessage() {
    return new CoreLogItemRequestMessage(
        "deviceIdentification", "organisationIdentification", "message");
  }
}
