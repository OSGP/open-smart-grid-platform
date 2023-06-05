// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.infra.jms.Constants;

@ExtendWith(MockitoExtension.class)
class CoreLogItemJsonMessageCreatorTest {
  private Session session;

  private CoreLogItemRequestMessage coreLogItemRequestMessage;
  private CoreLogItemJsonMessageCreator jsonMessageCreator;

  @BeforeEach
  public void setUp() {
    this.coreLogItemRequestMessage = this.getCoreLogItemRequestMessage();
    this.jsonMessageCreator = new CoreLogItemJsonMessageCreator(this.coreLogItemRequestMessage);
    this.session = mock(Session.class);
  }

  @Test
  void createJsonMessage() throws JMSException {
    final TextMessage expectedTextMessage = mock(TextMessage.class);
    when(this.session.createTextMessage(any())).thenReturn(expectedTextMessage);

    final Message actualMessage = this.jsonMessageCreator.getJsonMessage(this.session);

    assertThat(actualMessage).isSameAs(expectedTextMessage);
    verify(actualMessage).setJMSType(Constants.CORE_LOG_ITEM_REQUEST);
  }

  private CoreLogItemRequestMessage getCoreLogItemRequestMessage() {
    return new CoreLogItemRequestMessage(
        "deviceIdentification", "organisationIdentification", "message");
  }
}
