package com.alliander.osgp.core.infra.jms.protocol.in;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.infra.jms.BaseMessageProcessorMap;
import com.alliander.osgp.shared.infra.jms.MessageProcessor;

@Component("osgpCoreIncomingProtocolRequestMessageProcessorMap")
public class ProtocolRequestMessageProcessorMap extends BaseMessageProcessorMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolRequestMessageProcessorMap.class);

    public ProtocolRequestMessageProcessorMap() {
        super("ProtocolRequestMessageProcessorMap");
    }

    @Override
    public MessageProcessor getMessageProcessor(final ObjectMessage message) throws JMSException {

        if (message.getJMSType() == null) {
            LOGGER.error("Unknown message type: {}", message.getJMSType());
            throw new JMSException("Unknown message type");
        }

        final DeviceFunction messageType = DeviceFunction.valueOf(message.getJMSType());

        if (messageType.name() == null) {
            LOGGER.error("No message processor found for message type: {}", message.getJMSType());
            throw new JMSException("Unknown message processor");
        }

        return this.messageProcessors.get(messageType.ordinal());
    }
}
