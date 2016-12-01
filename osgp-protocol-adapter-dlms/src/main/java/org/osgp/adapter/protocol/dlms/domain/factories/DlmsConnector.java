/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

public class DlmsConnector {

    private static final int DLMS_PUBLIC_CLIENT_ID = 16;

    protected final int responseTimeout;

    protected final int logicalDeviceAddress;

    protected final int clientAccessPoint;

    public DlmsConnector(final int responseTimeout, final int logicalDeviceAddress) {
        this.responseTimeout = responseTimeout;
        this.logicalDeviceAddress = logicalDeviceAddress;
        this.clientAccessPoint = DLMS_PUBLIC_CLIENT_ID;
    }

    public DlmsConnection connect(final DlmsDevice device, final DlmsMessageListener dlmsMessageListener)
            throws TechnicalException {

        // Make sure neither device or device.getIpAddress() is null.
        this.checkDevice(device);
        this.checkIpAddress(device);

        // Setup connection to device
        TcpConnectionBuilder tcpConnectionBuilder;
        try {
            tcpConnectionBuilder = new TcpConnectionBuilder(InetAddress.getByName(device.getIpAddress()))
            .setResponseTimeout(this.responseTimeout).setLogicalDeviceId(this.logicalDeviceAddress);
        } catch (final UnknownHostException e) {
            throw new ConnectionException(e);
        }

        this.setOptionalValues(device, tcpConnectionBuilder);

        if (device.isInDebugMode()) {
            tcpConnectionBuilder.setRawMessageListener(dlmsMessageListener);
        }

        try {
            return tcpConnectionBuilder.build();
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

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
