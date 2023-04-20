/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

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

import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
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

    this.validateMessage(createdMessage, delay, jmsBrokerType);
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

    this.validateMessage(createdMessage, delay, jmsBrokerType);
    assertThat(createdMessage).isEqualTo(this.message);
  }

  private void validateMessage(
      final ObjectMessage createdMessage, final Long delay, final JmsBrokerType jmsBrokerType)
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
      assertThat(this.valueCaptor.getValue()).isBetween(expected - 10, expected);
      verify(this.message, never())
          .setLongProperty(eq(AMQ_SCHEDULED_DELAY.toString()), any(Long.class));
    }
  }
}
