/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.publiclighting.infra.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.publiclighting.application.services.AdHocManagementService;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.EventMessageDataContainer;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.UnknownMessageTypeException;

@Component(value = "domainPublicLightingIncomingOsgpCoreRequestMessageProcessor")
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
                "Received request message from OSGP-CORE messageType: {} deviceIdentification: {}, organisationIdentification: {}, correlationUid: {}, className: {}",
                messageType, deviceIdentification, organisationIdentification, correlationUid, dataObject.getClass()
                        .getCanonicalName());

        if (DeviceFunction.SET_TRANSITION.name().equals(messageType)) {
            final EventMessageDataContainer dataContainer = (EventMessageDataContainer) dataObject;
            this.handleLightMeasurementDeviceTransition(organisationIdentification, deviceIdentification,
                    correlationUid, dataContainer);
        } else {
            throw new UnknownMessageTypeException("Unknown JMSType: " + messageType);
        }
    }

    private void handleLightMeasurementDeviceTransition(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid,
            final EventMessageDataContainer eventMessageDataContainer) {
        LOGGER.info("Received transition message of light measurement device: {}", deviceIdentification);

        this.adHocManagementService.handleLightMeasurementDeviceTransition(organisationIdentification,
                deviceIdentification, correlationUid, eventMessageDataContainer);
    }
}
