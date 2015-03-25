package com.alliander.osgp.core.infra.jms.protocol.in;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alliander.osgp.core.domain.model.protocol.ProtocolResponseService;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

// This class sends response messages to the protocol incoming responses queue.
public class ProtocolResponseMessageSender implements ProtocolResponseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolResponseMessageSender.class);

    @Autowired
    private ProtocolInfoRepository protocolInfoRepository;

    @Autowired
    private ProtocolResponseMessageJmsTemplateFactory factory;

    @Override
    public void send(final ResponseMessage responseMessage, final String messageType, final ProtocolInfo protocolInfo) {

        final String key = protocolInfo.getKey();

        final JmsTemplate jmsTemplate = this.factory.getJmsTemplate(key);

        this.send(responseMessage, messageType, jmsTemplate);
    }

    public void send(final ResponseMessage responseMessage, final String messageType, final JmsTemplate jmsTemplate) {
        LOGGER.info("Sending response message to protocol responses incoming queue");

        jmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(responseMessage);
                objectMessage.setJMSCorrelationID(responseMessage.getCorrelationUid());
                objectMessage.setJMSType(messageType);
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        responseMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        responseMessage.getDeviceIdentification());
                return objectMessage;
            }

        });
    }
}
