// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.infra.jms.Constants;

@ExtendWith(MockitoExtension.class)
class DlmsLogItemRequestObjectMessageCreatorTest {
  private Session session;

  private DlmsLogItemRequestMessage dlmsLogItemRequestMessage;
  private DlmsLogItemRequestObjectMessageCreator dlmsLogItemRequestObjectMessageCreator;

  @BeforeEach
  public void setUp() {
    this.dlmsLogItemRequestMessage = this.getDlmsLogItemRequestMessage();
    this.dlmsLogItemRequestObjectMessageCreator =
        new DlmsLogItemRequestObjectMessageCreator(this.dlmsLogItemRequestMessage);
    this.session = mock(Session.class);
  }

  @Test
  void createObjectMessage() throws JMSException {
    final ObjectMessage expectedObjectMessage = mock(ObjectMessage.class);
    when(this.session.createObjectMessage()).thenReturn(expectedObjectMessage);

    final Message actualMessage =
        this.dlmsLogItemRequestObjectMessageCreator.getObjectMessage(this.session);

    assertThat(actualMessage).isSameAs(expectedObjectMessage);
    verify(actualMessage).setJMSType(Constants.DLMS_LOG_ITEM_REQUEST);
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
