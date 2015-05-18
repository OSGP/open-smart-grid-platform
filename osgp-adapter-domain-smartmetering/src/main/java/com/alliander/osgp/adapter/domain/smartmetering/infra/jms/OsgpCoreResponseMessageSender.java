package com.alliander.osgp.adapter.domain.smartmetering.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

// This class should send response messages to OSGP Core.
@Component(value = "domainSmartMeteringOutgoingOsgpCoreResponseMessageSender")
public class OsgpCoreResponseMessageSender {

    @Autowired
    @Qualifier("domainSmartMeteringOutgoingOsgpCoreResponsesJmsTemplate")
    private JmsTemplate osgpCoreResponsesJmsTemplate;

    public void send(final ResponseMessage responseMessage, final String messageType) {

        this.osgpCoreResponsesJmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage();

                objectMessage.setJMSType(messageType);
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        responseMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        responseMessage.getDeviceIdentification());
                objectMessage.setObject(responseMessage);

                return objectMessage;
            }
        });
    }
}
