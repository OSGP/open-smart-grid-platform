/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.services.MonitoringService;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceRequestMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequest;
import com.alliander.osgp.shared.infra.jms.Constants;

@Component("domainSmartmeteringActualMeterReadsRequestMessageProcessor")
public class ActualMeterReadsRequestMessageProcessor extends WebServiceRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsRequestMessageProcessor.class);

    @Autowired
    @Qualifier("domainSmartMeteringMonitoringService")
    private MonitoringService monitoringService;

    public ActualMeterReadsRequestMessageProcessor() {
        super(DeviceFunction.REQUEST_ACTUAL_METER_DATA);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        Object dataObject = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            dataObject = message.getObject();

        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            return;
        }

        try {
            LOGGER.info("Calling application service function: {}", messageType);

            final ActualMeterReadsRequest actualMeterReadsRequest = (ActualMeterReadsRequest) dataObject;

            this.monitoringService.requestActualMeterReads(organisationIdentification, deviceIdentification,
                    correlationUid, actualMeterReadsRequest, messageType);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType);
        }
    }

}
