/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms;

import org.opensmartgridplatform.adapter.domain.publiclighting.application.services.AdHocManagementService;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.UnknownMessageTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value = "domainPublicLightingInboundOsgpCoreRequestsMessageProcessor")
public class OsgpCoreRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpCoreRequestMessageProcessor.class);

    @Autowired
    @Qualifier("domainPublicLightingAdHocManagementService")
    private AdHocManagementService adHocManagementService;

    public void processMessage(final RequestMessage requestMessage, final String messageType)
            throws UnknownMessageTypeException {

        final String organisationIdentification = requestMessage.getOrganisationIdentification();
        final String deviceIdentification = requestMessage.getDeviceIdentification();
        final String correlationUid = requestMessage.getCorrelationUid();
        final Object dataObject = requestMessage.getRequest();

        LOGGER.info(
                "Received request message from OSGP-CORE with messageType: {} deviceIdentification: {}, organisationIdentification: {}, correlationUid: {}, className: {}",
                messageType, deviceIdentification, organisationIdentification, correlationUid,
                dataObject.getClass().getCanonicalName());

        if (MessageType.EVENT_NOTIFICATION == MessageType.valueOf(messageType)) {
            final Event event = (Event) dataObject;
            this.handleLightMeasurementDeviceTransition(organisationIdentification, correlationUid, event);
        } else {
            throw new UnknownMessageTypeException("Unknown JMSType: " + messageType);
        }
    }

    private void handleLightMeasurementDeviceTransition(final String organisationIdentification,
            final String correlationUid, final Event event) {
        LOGGER.info("Received transition message of light measurement device: {}", event.getDeviceIdentification());

        this.adHocManagementService.handleLightMeasurementDeviceTransition(organisationIdentification, correlationUid,
                event);
    }
}
