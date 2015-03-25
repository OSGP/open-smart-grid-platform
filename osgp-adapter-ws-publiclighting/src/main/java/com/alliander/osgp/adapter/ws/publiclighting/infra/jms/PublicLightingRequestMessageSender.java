package com.alliander.osgp.adapter.ws.publiclighting.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for sending public lighting request messages to a queue
 *
 * @author CGI
 *
 */
public class PublicLightingRequestMessageSender {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicLightingRequestMessageSender.class);

    /**
     * Autowired field for public lighting requests jms template
     */
    @Autowired
    @Qualifier("wsPublicLightingOutgoingRequestsJmsTemplate")
    private JmsTemplate publicLightingRequestsJmsTemplate;

    /**
     * Method for sending a request message to the queue
     *
     * @param requestMessage
     *            The PublicLightingRequestMessage request message to send.
     */
    public void send(final PublicLightingRequestMessage requestMessage) {
        LOGGER.debug("Sending public lighting request message to the queue");

        if (requestMessage.getMessageType() == null) {
            LOGGER.error("MessageType is null");
            return;
        }
        if (StringUtils.isBlank(requestMessage.getOrganisationIdentification())) {
            LOGGER.error("OrganisationIdentification is blank");
            return;
        }
        if (StringUtils.isBlank(requestMessage.getDeviceIdentification())) {
            LOGGER.error("DeviceIdentification is blank");
            return;
        }
        if (StringUtils.isBlank(requestMessage.getCorrelationUid())) {
            LOGGER.error("CorrelationUid is blank");
            return;
        }

        this.sendMessage(requestMessage);
    }

    /**
     * Method for sending a request message to the public lighting requests
     * queue
     *
     * @param requestMessage
     *            The PublicLightingRequestMessage request message to send.
     */
    private void sendMessage(final PublicLightingRequestMessage requestMessage) {
        LOGGER.info("Sending message to the public lighting requests queue");

        this.publicLightingRequestsJmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(requestMessage.getRequest());
                objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
                objectMessage.setJMSType(requestMessage.getMessageType().toString());
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        requestMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        requestMessage.getDeviceIdentification());
                if (requestMessage.getScheduleTime() != null) {
                    objectMessage
                            .setLongProperty(Constants.SCHEDULE_TIME, requestMessage.getScheduleTime().getMillis());
                }
                return objectMessage;
            }

        });
    }
}
