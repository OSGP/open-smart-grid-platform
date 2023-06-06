// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

import java.util.Map;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

public interface MessageProcessorMap {

  void setMessageProcessors(Map<MessageType, MessageProcessor> messageProcessors);

  void addMessageProcessor(MessageType messageType, MessageProcessor messageProcessor);

  MessageProcessor getMessageProcessor(ObjectMessage message) throws JMSException;
}
