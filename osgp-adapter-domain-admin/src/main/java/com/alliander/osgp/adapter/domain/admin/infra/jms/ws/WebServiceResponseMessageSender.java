package com.alliander.osgp.adapter.domain.admin.infra.jms.ws;

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
import com.alliander.osgp.shared.infra.jms.ResponseMessageSender;

// Send response message to the web service adapter.
@Component(value = "domainAdminOutgoingWebServiceResponseMessageSender")
public class WebServiceResponseMessageSender implements ResponseMessageSender {

    @Autowired
    @Qualifier("domainAdminOutgoingWebServiceResponsesJmsTemplate")
    private JmsTemplate outgoingWebServiceResponsesJmsTemplate;

    @Override
    public void send(final ResponseMessage responseMessage) {

        this.outgoingWebServiceResponsesJmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(responseMessage);
                objectMessage.setJMSCorrelationID(responseMessage.getCorrelationUid());
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        responseMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        responseMessage.getDeviceIdentification());
                objectMessage.setStringProperty(Constants.RESULT, responseMessage.getResult().toString());
                if (responseMessage.getOsgpException() !=null){
                    objectMessage.setStringProperty(Constants.DESCRIPTION, responseMessage.getOsgpException().getMessage());                	
                }
                return objectMessage;
            }
        });
    }
}
