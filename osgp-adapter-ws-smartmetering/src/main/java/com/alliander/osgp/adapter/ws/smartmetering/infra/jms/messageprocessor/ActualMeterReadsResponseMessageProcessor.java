/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas;
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
            return;
        }

        try {
            LOGGER.info("Calling application service function to handle response: {}", messageType);

            if (message.getObject() instanceof MeterReads) {
                // Convert and Persist data
                final MeterReads data = (MeterReads) message.getObject();
                final MeterResponseData meterResponseData = new MeterResponseData(organisationIdentification,
                        messageType, deviceIdentification, correlationUid, data);
                this.meterResponseDataRepository.save(meterResponseData);

            } else {
                MeterReadsGas meterReadsGas = (MeterReadsGas) message.getObject();
                final MeterResponseData meterResponseData = new MeterResponseData(organisationIdentification,
                        messageType, deviceIdentification, correlationUid, meterReadsGas);
                this.meterResponseDataRepository.save(meterResponseData);
            }
            // Send notification indicating data is available.
            this.notificationService.sendNotification(organisationIdentification, deviceIdentification, result,
                    correlationUid, notificationMessage, notificationType);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, notificationType);
        }
    }
}
