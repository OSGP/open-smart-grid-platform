package com.alliander.osgp.adapter.ws.core.infra.jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.schema.core.notification.NotificationType;
import com.alliander.osgp.adapter.ws.shared.services.NotificationService;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.UnknownMessageTypeException;

//Fetch incoming messages from the domain core to web service core requests queue.
@Component(value = "domainCoreToWsIncomingWebServiceRequestsMessageListener")
public class CommonRequestMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonRequestMessageListener.class);

    @Autowired
    private NotificationService notificationService;

    @Override
    public void onMessage(final Message message) {
        try {
            LOGGER.info("Received message of type: {}", message.getJMSType());
            final ObjectMessage objectMessage = (ObjectMessage) message;
            final String correlationUid = objectMessage.getJMSCorrelationID();
            final String messageType = objectMessage.getJMSType();
            final String organisationIdentification = objectMessage
                    .getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            final String deviceIdentification = objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            final Serializable dataObject = objectMessage.getObject();

            if ("RELAY_STATUS_UPDATED".equals(messageType)) {
                this.notificationService.sendNotification(organisationIdentification, deviceIdentification, null,
                        correlationUid, null, NotificationType.DEVICE_UPDATED);
            } else {
                LOGGER.debug(
                        "Unknown message received. MessageType: {}, correlationUID: {}, organisation: {}, deviceIdentification: {}, dataObject: {}",
                        messageType, correlationUid, organisationIdentification, deviceIdentification,
                        dataObject == null ? "null" : dataObject.getClass().getCanonicalName());
                throw new UnknownMessageTypeException("Unknown JMSType: " + messageType);
            }
        } catch (final JMSException ex) {
            LOGGER.error("Caught JMSException: {} ", ex);
        } catch (final UnknownMessageTypeException e) {
            LOGGER.error("Caught UnknownMessageTypeException", e);
        }
    }
}
