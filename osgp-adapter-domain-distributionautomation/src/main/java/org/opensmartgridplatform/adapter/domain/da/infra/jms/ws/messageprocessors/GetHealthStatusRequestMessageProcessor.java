/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.adapter.domain.da.application.services.DeviceManagementService;
import org.opensmartgridplatform.adapter.domain.da.infra.jms.ws.AbstractWebServiceRequestMessageProcessor;
import org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

/**
 * Class for processing da get health status request messages
 */
@Component("domainDistributionAutomationGetHealthStatusRequestMessageProcessor")
public class GetHealthStatusRequestMessageProcessor extends AbstractWebServiceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GetHealthStatusRequestMessageProcessor.class);

    @Autowired
    @Qualifier("domainDistributionAutomationDeviceManagementService")
    private DeviceManagementService deviceManagementService;

    public GetHealthStatusRequestMessageProcessor() {
        super(DeviceFunction.GET_HEALTH_STATUS);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.info("Processing DA Get Health Status request message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        GetHealthStatusRequest getHealthStatusRequest = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

            if (message.getObject() instanceof GetHealthStatusRequest) {
                getHealthStatusRequest = (GetHealthStatusRequest) message.getObject();
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

            this.deviceManagementService
                    .getHealthStatus(organisationIdentification, deviceIdentification, correlationUid, messageType, getHealthStatusRequest);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType);
        }
    }
}
