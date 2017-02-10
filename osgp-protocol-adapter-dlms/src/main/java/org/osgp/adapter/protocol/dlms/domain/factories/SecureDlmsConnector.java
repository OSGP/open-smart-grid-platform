/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;
import java.net.InetAddress;

import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.openmuc.jdlms.settings.client.ReferencingMethod;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

abstract public class SecureDlmsConnector extends Lls0Connector {
    
    public SecureDlmsConnector(int responseTimeout, int logicalDeviceAddress, int clientAccessPoint) {
        super(responseTimeout, logicalDeviceAddress, clientAccessPoint);
    }

    /**
     * Set the correct security attributes on the tcpConnectionBuilder. 
     * 
     * @param device The device to connect with. 
     * @param tcpConnectionBuilder The connection builder instance. 
     * @throws TechnicalException
     */
    abstract protected void setSecurity(final DlmsDevice device, final TcpConnectionBuilder tcpConnectionBuilder) throws TechnicalException;
    
    /**
     * Create a connection with the device.
     *
     *
     * @param device The device to connect with.
     * @param dlmsMessageListener Listener to set on the connection. 
     * @return The connection.
     * @throws IOException
     *             When there are problems in connecting to or communicating
     *             with the device.
     * @throws TechnicalException
     *             When there are problems reading the security and
     *             authorization keys.
     */
    protected DlmsConnection createConnection(final DlmsDevice device, final DlmsMessageListener dlmsMessageListener)
            throws IOException, TechnicalException {

        // Setup connection to device
        final TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(
                InetAddress.getByName(device.getIpAddress())).setResponseTimeout(this.responseTimeout)
                        .setLogicalDeviceId(this.logicalDeviceAddress);
        tcpConnectionBuilder.setClientId(this.clientAccessPoint)
                .setReferencingMethod(device.isUseSn() ? ReferencingMethod.SHORT : ReferencingMethod.LOGICAL);

        if (device.isUseHdlc()) {
            tcpConnectionBuilder.useHdlc();
        }

        this.setSecurity(device, tcpConnectionBuilder);
        this.setOptionalValues(device, tcpConnectionBuilder);

        if (device.isInDebugMode()) {
            tcpConnectionBuilder.setRawMessageListener(dlmsMessageListener);
        }

        return tcpConnectionBuilder.build();
    }
    
    /**
     * Get the valid securityKey of a given type for the device.
     * 
     * @param device The device.
     * @param securityKeyType The type of key to return.
     * @return SecurityKey 
     * @throws TechnicalException
     *             when there is no valid key of the given type.
     */
    protected SecurityKey getSecurityKey(final DlmsDevice device, final SecurityKeyType securityKeyType)
            throws TechnicalException {
        final SecurityKey securityKey = device.getValidSecurityKey(securityKeyType);
        if (securityKey == null) {
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS,
                    String.format("There is no valid key for device '%s' of type '%s'.",
                            device.getDeviceIdentification(), securityKeyType.name()));
        }

        return securityKey;
    }
}
