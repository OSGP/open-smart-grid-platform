package com.alliander.osgp.adapter.ws.smartmetering.infra.jms.messageprocessor;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.NotificationService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.EventMessageDataContainer;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing smart metering find events response messages
 */
@Component("domainSmartMeteringFindEventsResponseMessageProcessor")
public class FindEventsResponseMessageProcessor extends DomainResponseMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FindEventsResponseMessageProcessor.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ManagementMapper managementMapper;

    @Autowired
    private MeterResponseDataRepository meterResponseDataRepository;

    public FindEventsResponseMessageProcessor() {
        super(DeviceFunction.FIND_EVENTS);
    }

    @Override
    public void processMessage(final ObjectMessage objectMessage) throws JMSException {
        LOGGER.debug("Processing smart metering find events response message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        String result = null;
        String message = null;
        NotificationType notificationType = null;
        Serializable data = null;

        try {
            correlationUid = objectMessage.getJMSCorrelationID();
            messageType = objectMessage.getJMSType();
            organisationIdentification = objectMessage.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            result = objectMessage.getStringProperty(Constants.RESULT);
            message = objectMessage.getStringProperty(Constants.DESCRIPTION);
            notificationType = NotificationType.valueOf(messageType);
            data = objectMessage.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            return;
        }

        try {
            LOGGER.info("Calling application service function to handle response: {}", messageType);

            final EventMessageDataContainer eventMessageDataContainer = (EventMessageDataContainer) data;

            // Test print below, remove this line of code after saving the data:
            LOGGER.info("Number of events in EventMessageDataContainer: {}", eventMessageDataContainer.getEvents()
                    .size());

            // Convert the events to entity and save the events
            final MeterResponseData meterResponseData = new MeterResponseData(organisationIdentification, messageType,
                    deviceIdentification, correlationUid, data);
            this.meterResponseDataRepository.save(meterResponseData);

            // Notifying
            this.notificationService.sendNotification(organisationIdentification, deviceIdentification, result,
                    correlationUid, message, notificationType);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, notificationType);
        }
    }
}
