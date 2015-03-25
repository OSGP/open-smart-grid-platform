package com.alliander.osgp.shared.infra.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseMessageProcessorMap implements MessageProcessorMap {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseMessageProcessorMap.class);

    protected Map<Integer, MessageProcessor> messageProcessors = new HashMap<>();

    protected final String messageProcessorMapName;

    public BaseMessageProcessorMap(final String messageProcessorMapName) {
        super();
        this.messageProcessorMapName = messageProcessorMapName;
    }

    @Override
    public void setMessageProcessors(final Map<Integer, MessageProcessor> messageProcessors) {
        this.messageProcessors = messageProcessors;
    }

    @Override
    public void addMessageProcessor(final Integer key, final String messageType, final MessageProcessor messageProcessor) {

        LOGGER.info("Putting MessageProcessor in {} with key: {} for MessageType: {}", this.messageProcessorMapName,
                key, messageType);

        this.messageProcessors.put(key, messageProcessor);
    }

    @Override
    public abstract MessageProcessor getMessageProcessor(ObjectMessage message) throws JMSException;
}