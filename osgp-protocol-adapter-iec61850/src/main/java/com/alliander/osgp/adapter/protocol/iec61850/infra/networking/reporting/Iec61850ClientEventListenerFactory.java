package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.IED;

public class Iec61850ClientEventListenerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850ClientEventListenerFactory.class);

    private static Iec61850ClientEventListenerFactory instance;

    private Iec61850ClientEventListenerFactory() {
    }

    public static Iec61850ClientEventListenerFactory getInstance() {
        if (instance == null) {
            instance = new Iec61850ClientEventListenerFactory();
        }
        return instance;
    }

    public Iec61850ClientBaseEventListener getEventListener(final IED ied, final String deviceIdentification,
            final DeviceManagementService deviceManagementService) throws ProtocolAdapterException {
        switch (ied) {
        case FLEX_OVL:
            return new Iec61850ClientSSLDEventListener(deviceIdentification, deviceManagementService);
        case ZOWN_RTU:
            return new Iec61850ClientRTUEventListener(deviceIdentification, deviceManagementService);
        default:
            LOGGER.warn("Unknown IED {}, could not create event listener for device {}", ied, deviceIdentification);
            return null;
        }
    }
}
