/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.factories;

import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

public abstract class DlmsConnector {
    static final int DLMS_PUBLIC_CLIENT_ID = 16;

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsConnector.class);

    public abstract DlmsConnection connect(final DlmsDevice device, final DlmsMessageListener dlmsMessageListener)
            throws TechnicalException, FunctionalException;

    protected void checkDevice(final DlmsDevice device) {
        if (device == null) {
            throw new IllegalStateException("Can not connect because no device is set.");
        }
    }

    protected void checkIpAddress(final DlmsDevice device) throws TechnicalException, FunctionalException {
        if (device.getIpAddress() == null) {
            final String errorMessage = String.format(
                    "Unable to get connection for device %s, because the IP address:%s is not valid or empty",
                    device.getDeviceIdentification(), device.getIpAddress());
            LOGGER.error(errorMessage);

            throw new FunctionalException(FunctionalExceptionType.INVALID_IP_ADDRESS, ComponentType.PROTOCOL_DLMS);
        }
    }

    protected void setOptionalValues(final DlmsDevice device, final TcpConnectionBuilder tcpConnectionBuilder)
            throws FunctionalException {
        if (device.getPort() != null) {
            tcpConnectionBuilder.setPort(device.getPort().intValue());
        }
        if (device.getLogicalId() != null) {
            tcpConnectionBuilder.setLogicalDeviceId(device.getLogicalId().intValue());
        }

        final Integer challengeLength = device.getChallengeLength();

        try {
            if (challengeLength != null) {
                tcpConnectionBuilder.setChallengeLength(challengeLength);
            }
        } catch (final IllegalArgumentException e) {
            final String errorMessage = String.format("Challenge length has to be between 8 and 64 for device %s",
                    device.getDeviceIdentification());
            LOGGER.error(errorMessage);

            throw new FunctionalException(FunctionalExceptionType.CHALLENGE_LENGTH_OUT_OF_RANGE,
                    ComponentType.PROTOCOL_DLMS, e);
        }

    }
}
