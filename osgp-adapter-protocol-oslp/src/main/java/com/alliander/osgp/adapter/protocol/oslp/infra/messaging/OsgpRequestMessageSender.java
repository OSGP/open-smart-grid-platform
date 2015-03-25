package com.alliander.osgp.adapter.protocol.oslp.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

public class OsgpRequestMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpRequestMessageSender.class);

    @Autowired
    @Qualifier("osgpRequestsJmsTemplate")
    private JmsTemplate osgpRequestsJmsTemplate;

    public void send(final RequestMessage requestMessage, final String messageType) {
        LOGGER.info("Sending request message to OSGP.");

        this.osgpRequestsJmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(requestMessage);
                objectMessage.setJMSType(messageType);
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        requestMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        requestMessage.getDeviceIdentification());

                return objectMessage;
            }

        });
    }

}
