package com.alliander.osgp.shared.infra.jms;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

public interface MessageProcessorMap {

    void setMessageProcessors(Map<Integer, MessageProcessor> messageProcessors);

    void addMessageProcessor(Integer key, String messageType, MessageProcessor messageProcessor);

    MessageProcessor getMessageProcessor(ObjectMessage message) throws JMSException;

}