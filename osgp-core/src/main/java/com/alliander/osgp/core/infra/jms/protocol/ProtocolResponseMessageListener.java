package com.alliander.osgp.core.infra.jms.protocol;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.core.application.services.DeviceResponseMessageService;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

// This class should fetch incoming messages from a responses queue.
public class ProtocolResponseMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolResponseMessageListener.class);

    private final DeviceResponseMessageService deviceResponseMessageService;

    public ProtocolResponseMessageListener(final DeviceResponseMessageService deviceResponseMessageService) {
        this.deviceResponseMessageService = deviceResponseMessageService;
    }

    @Override
    public void onMessage(final Message message) {
        try {
            LOGGER.info("Received protocol response message with correlationUid [{}] and type [{}]",
                    message.getJMSCorrelationID(), message.getJMSType());

            final ProtocolResponseMessage protocolResponseMessage = this.createResponseMessage(message);

            LOGGER.debug("OrganisationIdentification: [{}]", protocolResponseMessage.getOrganisationIdentification());
            LOGGER.debug("DeviceIdentification      : [{}]", protocolResponseMessage.getDeviceIdentification());
            LOGGER.debug("Domain                    : [{}]", protocolResponseMessage.getDomain());
            LOGGER.debug("DomainVersion             : [{}]", protocolResponseMessage.getDomainVersion());
            LOGGER.debug("Result                    : [{}]", protocolResponseMessage.getResult());
            LOGGER.debug("Description               : [{}]", protocolResponseMessage.getDescription());

            this.deviceResponseMessageService.processMessage(protocolResponseMessage);

        } catch (final JMSException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
        }
    }

    private ProtocolResponseMessage createResponseMessage(final Message message) throws JMSException {
        final String correlationUid = message.getJMSCorrelationID();
        final String messageType = message.getJMSType();
        final String domain = message.getStringProperty(Constants.DOMAIN);
        final String domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
        final String organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
        final String deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
        final ResponseMessageResultType responseMessageResultType = ResponseMessageResultType.valueOf(message
                .getStringProperty(Constants.RESULT));
        final String description = message.getStringProperty(Constants.DESCRIPTION);
        final Serializable dataObject = ((ObjectMessage) message).getObject();
        final boolean scheduled = message.propertyExists(Constants.IS_SCHEDULED) ? message
                .getBooleanProperty(Constants.IS_SCHEDULED) : false;
        final int retryCount = message.getIntProperty(Constants.RETRY_COUNT);
        return new ProtocolResponseMessage(domain, domainVersion, messageType, correlationUid,
                organisationIdentification, deviceIdentification, responseMessageResultType, description, dataObject,
                scheduled, retryCount);
    }
}
