package com.alliander.osgp.adapter.ws.smartmetering.infra.jms.messageprocessor;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.NotificationService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.SynchronizeTimeDataRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeReads;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing smart metering default response messages
 */
@Component("domainSmartMeteringSynchronizeTimeResponseMessageProcessor")
public class SynchronizeTimeResponseMessageProcessor extends DomainResponseMessageProcessor {

	/**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizeTimeResponseMessageProcessor.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SynchronizeTimeDataRepository synchronizeTimeDataRepository;

    @Autowired
    private AdhocMapper adhocMapper;

    protected SynchronizeTimeResponseMessageProcessor() {
        super(DeviceFunction.REQUEST_SYNCHRONIZE_TIME);
    }

    @Override
    public void processMessage(final ObjectMessage objectMessage) throws JMSException {
        LOGGER.debug("Processing smart metering synchronize time data response message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        final OsgpException osgpException = null;

        String result = null;
        String message = null;
        NotificationType notificationType = null;

        SynchronizeTimeReads synchronizeTimeReads = null;

        try {
            correlationUid = objectMessage.getJMSCorrelationID();
            messageType = objectMessage.getJMSType();
            organisationIdentification = objectMessage.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);

            result = objectMessage.getStringProperty(Constants.RESULT);
            message = objectMessage.getStringProperty(Constants.DESCRIPTION);
            notificationType = NotificationType.valueOf(messageType);

            synchronizeTimeReads = (SynchronizeTimeReads) objectMessage.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("osgpException: {}", osgpException);
            return;
        }

        try {
            LOGGER.info("Calling application service function to handle response: {}", messageType);

            // convert and Save the synchronizeTimeReads
            final com.alliander.osgp.adapter.ws.smartmetering.domain.entities.SynchronizeTimeReads data = this.adhocMapper
                    .map(synchronizeTimeReads,
                    		com.alliander.osgp.adapter.ws.smartmetering.domain.entities.SynchronizeTimeReads.class);

            data.setCorrelationUid(correlationUid);
            this.synchronizeTimeDataRepository.save(data);
            
            // Notifying
            this.notificationService.sendNotification(organisationIdentification, deviceIdentification, result,
                    correlationUid, message, notificationType);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, notificationType);
        }
    }	
}
