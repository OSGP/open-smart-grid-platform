// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import java.util.Map;

public interface MessageProcessorMap {

  void setMessageProcessors(Map<MessageType, MessageProcessor> messageProcessors);

  void addMessageProcessor(MessageType messageType, MessageProcessor messageProcessor);

  MessageProcessor getMessageProcessor(ObjectMessage message) throws JMSException;
}
