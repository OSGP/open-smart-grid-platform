package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.osgp.adapter.protocol.dlms.application.services.ManagementService;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryMessageDataContainer;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing add meter request messages
 */
@Component("dlmsFindEventsRequestMessageProcessor")
public class FindEventsRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddMeterRequestMessageProcessor.class);

    @Autowired
    private ManagementService managementService;

    public FindEventsRequestMessageProcessor() {
        super(DeviceRequestMessageType.FIND_EVENTS);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing find events request message");

        String correlationUid = null;
        String domain = null;
        String domainVersion = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        Object data = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            domain = message.getStringProperty(Constants.DOMAIN);
            domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            data = message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("domain: {}", domain);
            LOGGER.debug("domainVersion: {}", domainVersion);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            return;
        }

        this.managementService.findEvents(organisationIdentification, deviceIdentification, correlationUid,
                this.responseMessageSender, domain, domainVersion, messageType,
                (FindEventsQueryMessageDataContainer) data);
    }
}
