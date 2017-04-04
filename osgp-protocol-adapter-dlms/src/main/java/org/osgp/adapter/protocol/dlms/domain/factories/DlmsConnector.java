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

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

public abstract class DlmsConnector {
    static final int DLMS_PUBLIC_CLIENT_ID = 16;

    public abstract DlmsConnection connect(final DlmsDevice device, final DlmsMessageListener dlmsMessageListener)
            throws TechnicalException, FunctionalException;

    protected void checkDevice(final DlmsDevice device) {
        if (device == null) {
            throw new IllegalStateException("Can not connect because no device is set.");
        }
    }

    protected void checkIpAddress(final DlmsDevice device) throws TechnicalException {
        if (device.getIpAddress() == null) {
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS, "Unable to get connection for device "
                    + device.getDeviceIdentification() + ", because the IP address is not set.");
        }
    }

    protected void setOptionalValues(final DlmsDevice device, final TcpConnectionBuilder tcpConnectionBuilder) {
        if (device.getPort() != null) {
            tcpConnectionBuilder.setTcpPort(device.getPort().intValue());
        }
        if (device.getLogicalId() != null) {
            tcpConnectionBuilder.setLogicalDeviceId(device.getLogicalId().intValue());
        }

        final Integer challengeLength = device.getChallengeLength();
        if (challengeLength != null) {
            tcpConnectionBuilder.setChallengeLength(challengeLength);
        }

    }
}
