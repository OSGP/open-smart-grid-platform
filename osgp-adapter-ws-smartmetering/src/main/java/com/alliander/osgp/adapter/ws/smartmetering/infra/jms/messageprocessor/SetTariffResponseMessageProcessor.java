/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.Constants;

@Component("domainSmartMeteringSetTariffResponseMessageProcessor")
public class SetTariffResponseMessageProcessor extends DomainResponseMessageProcessor {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SetTariffResponseMessageProcessor.class);

    @Autowired
    private MeterResponseDataRepository meterResponseDataRepository;

    @Autowired
    private NotificationService notificationService;

    protected SetTariffResponseMessageProcessor() {
        super(DeviceFunction.SET_TARIFF);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing smart metering set tariff response message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        final OsgpException osgpException = null;

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
            LOGGER.info("correlationUid: {}", correlationUid);
            LOGGER.info("messageType: {}", messageType);
            LOGGER.info("organisationIdentification: {}", organisationIdentification);
            LOGGER.info("deviceIdentification: {}", deviceIdentification);
            LOGGER.info("osgpException: {}", osgpException);
            return;
        }

        try {
            LOGGER.info("Calling application service function to handle response: {}", messageType);

            final String resultString = (String) message.getObject();

            // Convert the events to entity and save the Set Tariff result
            final MeterResponseData meterResponseData = new MeterResponseData(organisationIdentification, messageType,
                    deviceIdentification, correlationUid, resultString);
            this.meterResponseDataRepository.save(meterResponseData);

            // Notifying
            this.notificationService.sendNotification(organisationIdentification, deviceIdentification, result,
                    correlationUid, notificationMessage, notificationType);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, notificationType);
        }
    }
}
