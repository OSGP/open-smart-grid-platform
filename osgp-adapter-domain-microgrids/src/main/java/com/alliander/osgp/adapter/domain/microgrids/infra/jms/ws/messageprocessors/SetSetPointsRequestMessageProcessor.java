/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.microgrids.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.microgrids.application.services.AdHocManagementService;
import com.alliander.osgp.adapter.domain.microgrids.infra.jms.ws.WebServiceRequestMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.microgrids.valueobjects.SetPointsRequest;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing microgrids set setpoints request messages
 */
@Component("domainMicrogridsSetSetPointsRequestMessageProcessor")
public class SetSetPointsRequestMessageProcessor extends WebServiceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SetSetPointsRequestMessageProcessor.class);

    @Autowired
    @Qualifier("domainMicrogridsAdHocManagementService")
    private AdHocManagementService adHocManagementService;

    public SetSetPointsRequestMessageProcessor() {
        super(DeviceFunction.SET_SETPOINT);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.info("Processing public lighting get status request message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        SetPointsRequest setPointsRequest = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

            if (message.getObject() != null && message.getObject() instanceof SetPointsRequest) {
                setPointsRequest = (SetPointsRequest) message.getObject();
            }

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

            this.adHocManagementService.handleSetPointsRequest(organisationIdentification, deviceIdentification,
                    correlationUid, messageType, setPointsRequest);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType);
        }
    }
}
