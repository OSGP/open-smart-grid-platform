package com.alliander.osgp.adapter.domain.core.infra.jms.ws;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageSender;

// Send response message to the responses queue of web service adapter.
public class WebServiceResponseMessageSender implements ResponseMessageSender {

    @Autowired
    @Qualifier("domainCoreOutgoingWebServiceResponsesJmsTemplate")
    private JmsTemplate commonWsResponsesJmsTemplate;

    @Override
    public void send(final ResponseMessage responseMessage) {

        this.commonWsResponsesJmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(responseMessage);
                objectMessage.setJMSCorrelationID(responseMessage.getCorrelationUid());
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        responseMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        responseMessage.getDeviceIdentification());
                objectMessage.setStringProperty(Constants.RESULT, responseMessage.getResult().toString());
                objectMessage.setStringProperty(Constants.DESCRIPTION, responseMessage.getOsgpException().getMessage());
                return objectMessage;
            }
        });
    }
}
