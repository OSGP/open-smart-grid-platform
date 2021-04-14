/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.jms;

import java.util.Map;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

public interface MessageProcessorMap {

  void setMessageProcessors(Map<MessageType, MessageProcessor> messageProcessors);

  void addMessageProcessor(MessageType messageType, MessageProcessor messageProcessor);

  MessageProcessor getMessageProcessor(ObjectMessage message) throws JMSException;
}
