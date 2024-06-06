// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

import static org.apache.activemq.ScheduledMessage.AMQ_SCHEDULED_DELAY;
import static org.apache.activemq.artemis.api.core.Message.HDR_SCHEDULED_DELIVERY_TIME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import java.io.Serializable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.application.config.messaging.JmsBrokerType;

@ExtendWith(MockitoExtension.class)
class JmsMessageCreatorTest {

  @Mock private Session session;
  @Spy private ObjectMessage message;

  @Captor ArgumentCaptor<String> nameCaptor;
  @Captor ArgumentCaptor<Long> valueCaptor;

  @ParameterizedTest
  @EnumSource(JmsBrokerType.class)
  void shouldNotSetDelayOnNullValue(final JmsBrokerType jmsBrokerType) throws JMSException {
    final JmsMessageCreator jmsMessageCreator = new JmsMessageCreator(jmsBrokerType);

    when(this.session.createObjectMessage()).thenReturn(this.message);

    final ObjectMessage createdMessage = jmsMessageCreator.createObjectMessage(this.session, null);
    assertThat(createdMessage).isEqualTo(this.message);

    verifyNoInteractions(this.message);
  }

  @ParameterizedTest
  @EnumSource(JmsBrokerType.class)
  void shouldUseCorrectProperty(final JmsBrokerType jmsBrokerType) throws JMSException {
    final Long delay = 123L;
    final JmsMessageCreator jmsMessageCreator = new JmsMessageCreator(jmsBrokerType);

    when(this.session.createObjectMessage()).thenReturn(this.message);

    final ObjectMessage createdMessage = jmsMessageCreator.createObjectMessage(this.session, delay);

    this.validateDelayProperty(delay, jmsBrokerType);
    assertThat(createdMessage).isEqualTo(this.message);
  }

  @ParameterizedTest
  @EnumSource(JmsBrokerType.class)
  void shouldUseCorrectPropertyWithData(final JmsBrokerType jmsBrokerType) throws JMSException {
    final Serializable payload = "1234567789";
    final Long delay = 123L;
    final JmsMessageCreator jmsMessageCreator = new JmsMessageCreator(jmsBrokerType);

    when(this.session.createObjectMessage(payload)).thenReturn(this.message);

    final ObjectMessage createdMessage =
        jmsMessageCreator.createObjectMessage(this.session, payload, delay);

    this.validateDelayProperty(delay, jmsBrokerType);
    assertThat(createdMessage).isEqualTo(this.message);
  }

  @ParameterizedTest
  @EnumSource(JmsBrokerType.class)
  void shouldCreateMessageWithCorrectPropertyWithData(final JmsBrokerType jmsBrokerType)
      throws JMSException {
    final Long delay = 12345678L;
    final JmsMessageCreator jmsMessageCreator = new JmsMessageCreator(jmsBrokerType);

    when(this.session.createMessage()).thenReturn(this.message);

    final Message createdMessage = jmsMessageCreator.createMessage(this.session, delay);

    this.validateDelayProperty(delay, jmsBrokerType);
    assertThat(createdMessage).isEqualTo(this.message);
  }

  private void validateDelayProperty(final Long delay, final JmsBrokerType jmsBrokerType)
      throws JMSException {

    verify(this.message).setLongProperty(this.nameCaptor.capture(), this.valueCaptor.capture());

    if (jmsBrokerType == JmsBrokerType.ACTIVE_MQ) {
      assertThat(this.nameCaptor.getValue()).isEqualTo(AMQ_SCHEDULED_DELAY);
      assertThat(this.valueCaptor.getValue()).isEqualTo(delay);
      verify(this.message, never())
          .setLongProperty(eq(HDR_SCHEDULED_DELIVERY_TIME.toString()), any(Long.class));
    } else {
      final long expected = System.currentTimeMillis() + delay;
      assertThat(this.nameCaptor.getValue()).isEqualTo(HDR_SCHEDULED_DELIVERY_TIME.toString());
      assertThat(this.valueCaptor.getValue()).isBetween(expected - 1000, expected);
      verify(this.message, never()).setLongProperty(eq(AMQ_SCHEDULED_DELAY), any(Long.class));
    }
  }
}
