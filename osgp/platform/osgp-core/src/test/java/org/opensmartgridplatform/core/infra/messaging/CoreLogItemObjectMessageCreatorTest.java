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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.infra.jms.Constants;

@ExtendWith(MockitoExtension.class)
class CoreLogItemObjectMessageCreatorTest {
  private Session session;

  private CoreLogItemRequestMessage coreLogItemRequestMessage;
  private CoreLogItemObjectMessageCreator objectMessageCreator;

  @BeforeEach
  public void setUp() {
    this.coreLogItemRequestMessage = this.getCoreLogItemRequestMessage();
    this.objectMessageCreator = new CoreLogItemObjectMessageCreator(this.coreLogItemRequestMessage);
    this.session = mock(Session.class);
  }

  @Test
  void createObjectMessage() throws JMSException {
    final ObjectMessage expectedObjectMessage = mock(ObjectMessage.class);
    when(this.session.createObjectMessage()).thenReturn(expectedObjectMessage);

    final Message actualMessage = this.objectMessageCreator.getObjectMessage(this.session);

    assertThat(actualMessage).isSameAs(expectedObjectMessage);
    verify(actualMessage).setJMSType(Constants.CORE_LOG_ITEM_REQUEST);
  }

  private CoreLogItemRequestMessage getCoreLogItemRequestMessage() {
    return new CoreLogItemRequestMessage(
        "deviceIdentification", "organisationIdentification", "message");
  }
}
