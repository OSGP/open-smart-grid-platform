/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.infra.jms;

import java.io.Serializable;

import org.opensmartgridplatform.adapter.domain.core.application.services.FirmwareManagementService;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceRequestMessageSender;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.UnknownMessageTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value = "domainCoreIncomingOsgpCoreRequestsMessageProcessor")
public class OsgpCoreRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpCoreRequestMessageProcessor.class);

    private static final String RELAY_STATUS_UPDATED_EVENTS = "RELAY_STATUS_UPDATED";

    private static final String SSLD_PENDING_FIRMWARE_UPDATE = "SSLD_PENDING_FIRMWARE_UPDATE";

    @Autowired
    private FirmwareManagementService firmwareManagementService;

    @Qualifier("domainCoreWebServiceRequestsMessageSender")
    @Autowired
    private WebServiceRequestMessageSender webServiceRequestMessageSender;

    public void processMessage(final RequestMessage requestMessage, final String messageType)
            throws UnknownMessageTypeException {

        final String organisationIdentification = requestMessage.getOrganisationIdentification();
        final String deviceIdentification = requestMessage.getDeviceIdentification();
        final String correlationUid = requestMessage.getCorrelationUid();
        final Serializable dataObject = requestMessage.getRequest();

        LOGGER.info(
                "Received request message from OSGP-CORE messageType: {} deviceIdentification: {}, organisationIdentification: {}, correlationUid: {}, dataObject.className: {}",
                messageType, deviceIdentification, organisationIdentification, correlationUid,
                dataObject == null ? "null" : dataObject.getClass().getCanonicalName());

        if (RELAY_STATUS_UPDATED_EVENTS.equals(messageType)) {
            this.sendMessageToWebServiceComponent(messageType, organisationIdentification, deviceIdentification,
                    correlationUid, dataObject);
        } else if (SSLD_PENDING_FIRMWARE_UPDATE.equals(messageType)) {
            this.firmwareManagementService.handleSsldPendingFirmwareUpdate(organisationIdentification,
                    deviceIdentification, correlationUid);
        } else {
            throw new UnknownMessageTypeException("Unknown JMSType: " + messageType);
        }
    }

    private void sendMessageToWebServiceComponent(final String messageType, final String organisationIdentification,
            final String deviceIdentification, final String correlationUid, final Serializable dataObject) {
        final RequestMessage requestMsg = new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, dataObject);
        this.webServiceRequestMessageSender.send(requestMsg, messageType);
    }
}
