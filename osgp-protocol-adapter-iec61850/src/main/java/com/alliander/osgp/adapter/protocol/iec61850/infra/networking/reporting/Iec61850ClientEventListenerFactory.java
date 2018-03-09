/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.protocol.iec61850.domain.repositories.Iec61850ReportEntryRepository;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.IED;

@Component
public class Iec61850ClientEventListenerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850ClientEventListenerFactory.class);

    @Autowired
    private Iec61850ReportEntryRepository iec61850ReportEntryRepository;

    public Iec61850ClientBaseEventListener getEventListener(final IED ied, final String deviceIdentification,
            final DeviceManagementService deviceManagementService) throws ProtocolAdapterException {
        switch (ied) {
        case FLEX_OVL:
            return new Iec61850ClientSSLDEventListener(deviceIdentification, deviceManagementService);
        case ABB_RTU:
            return new Iec61850ClientLMDEventListener(deviceIdentification, deviceManagementService);
        case ZOWN_RTU:
            return new Iec61850ClientRTUEventListener(deviceIdentification, deviceManagementService,
                    this.iec61850ReportEntryRepository);
        case DA_RTU:
            return new Iec61850ClientDaRTUEventListener(deviceIdentification, deviceManagementService);
        default:
            LOGGER.warn("Unknown IED {}, could not create event listener for device {}", ied, deviceIdentification);
            return null;
        }
    }
}
