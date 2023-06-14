// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

import static org.apache.activemq.artemis.api.core.Message.HDR_SCHEDULED_DELIVERY_TIME;

import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.activemq.ScheduledMessage;
import org.opensmartgridplatform.shared.application.config.messaging.JmsBrokerType;

public class JmsMessageCreator {
  private final JmsBrokerType jmsBrokerType;

  public JmsMessageCreator(final JmsBrokerType jmsBrokerType) {
    this.jmsBrokerType = jmsBrokerType;
  }

  public Message createMessage(final Session session, final Long delay) throws JMSException {
    final Message message = session.createMessage();
    this.addDelayProperty(message, delay);
    return message;
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

  private void addDelayProperty(final Message message, final Long delay) throws JMSException {
    if (delay == null || delay <= 0) {
      return;
    }

    if (this.jmsBrokerType == JmsBrokerType.ACTIVE_MQ) {
      // Active MQ requires property AMQ_SCHEDULED_DELAY
      message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
    } else {
      // Artemis requires property _AMQ_SCHED_DELIVERY
      message.setLongProperty(
          HDR_SCHEDULED_DELIVERY_TIME.toString(), System.currentTimeMillis() + delay);
    }
  }
}
