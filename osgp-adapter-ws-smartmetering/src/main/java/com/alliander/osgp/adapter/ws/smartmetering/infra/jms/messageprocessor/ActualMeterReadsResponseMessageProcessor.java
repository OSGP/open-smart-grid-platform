package com.alliander.osgp.adapter.ws.smartmetering.infra.jms.messageprocessor;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.NotificationService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReads;
import com.alliander.osgp.shared.infra.jms.Constants;

@Component("domainSmartMeteringActualMeterReadslResponseMessageProcessor")
public class ActualMeterReadsResponseMessageProcessor extends DomainResponseMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsResponseMessageProcessor.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MeterResponseDataRepository meterResponseDataRepository;

    protected ActualMeterReadsResponseMessageProcessor() {
        super(DeviceFunction.REQUEST_ACTUAL_METER_DATA);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing smart metering actual meter reads response message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        String result = null;
        String notificationMessage = null;
        NotificationType notificationType = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

            result = message.getStringProperty(Constants.RESULT);
            notificationMessage = message.getStringProperty(Constants.DESCRIPTION);
            notificationType = NotificationType.valueOf(messageType);

        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            return;
        }

        try {
            LOGGER.info("Calling application service function to handle response: {}", messageType);

            // Convert and Persist data
            final ActualMeterReads data = (ActualMeterReads) message.getObject();

            // Convert the events to entity and save the periodicMeterReads
            final MeterResponseData meterResponseData = new MeterResponseData(organisationIdentification, messageType,
                    deviceIdentification, correlationUid, data);
            this.meterResponseDataRepository.save(meterResponseData);

            // Send notification indicating data is available.
            this.notificationService.sendNotification(organisationIdentification, deviceIdentification, result,
                    correlationUid, notificationMessage, notificationType);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, notificationType);
        }
    }
}
