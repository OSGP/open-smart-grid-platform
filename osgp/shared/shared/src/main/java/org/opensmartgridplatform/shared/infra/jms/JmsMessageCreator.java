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

import static org.apache.activemq.artemis.api.core.Message.HDR_SCHEDULED_DELIVERY_TIME;

import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.activemq.ScheduledMessage;
import org.opensmartgridplatform.shared.application.config.messaging.JmsBrokerType;

public class JmsMessageCreator {
  private final JmsBrokerType jmsBrokerType;

  public JmsMessageCreator(final JmsBrokerType jmsBrokerType) {
    this.jmsBrokerType = jmsBrokerType;
  }

  public ObjectMessage createObjectMessage(
      final Session session, final Serializable payload, final Long delay) throws JMSException {
    final ObjectMessage objectMessage = session.createObjectMessage(payload);
    this.addDelayProperty(objectMessage, delay);
    return objectMessage;
  }

  public ObjectMessage createObjectMessage(final Session session, final Long delay)
      throws JMSException {
    final ObjectMessage objectMessage = session.createObjectMessage();
    this.addDelayProperty(objectMessage, delay);
    return objectMessage;
  }

  private void addDelayProperty(final ObjectMessage objectMessage, final Long delay)
      throws JMSException {
    if (delay == null || delay <= 0) {
      return;
    }

    if (this.jmsBrokerType == JmsBrokerType.ACTIVE_MQ) {
      // Active MQ requires property AMQ_SCHEDULED_DELAY
      objectMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
    } else {
      // Artemis requires property _AMQ_SCHED_DELIVERY
      objectMessage.setLongProperty(
          HDR_SCHEDULED_DELIVERY_TIME.toString(), System.currentTimeMillis() + delay);
    }
  }
}
