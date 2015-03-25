package com.alliander.osgp.adapter.domain.publiclighting.infra.jms.ws;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.infra.jms.MessageProcessor;
import com.alliander.osgp.shared.infra.jms.MessageProcessorMap;

// Fetch incoming messages from the requests queue of web service adapter.
@Component(value = "domainPublicLightingIncomingWebServiceRequestMessageListener")
public class WebServiceRequestMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceRequestMessageListener.class);

    @Autowired
    @Qualifier("domainPublicLightingWebServiceRequestMessageProcessorMap")
    private MessageProcessorMap webServiceRequestMessageProcessorMap;

    public WebServiceRequestMessageListener() {
        // empty constructor
    }

    @Override
    public void onMessage(final Message message) {
        try {
            LOGGER.info("Received message of type: {}", message.getJMSType());

            final ObjectMessage objectMessage = (ObjectMessage) message;

            final MessageProcessor processor = this.webServiceRequestMessageProcessorMap
                    .getMessageProcessor(objectMessage);

            processor.processMessage(objectMessage);

        } catch (final JMSException ex) {
            LOGGER.error("Exception: {} ", ex.getMessage(), ex);
        }
    }
}
