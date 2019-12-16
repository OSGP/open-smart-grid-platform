/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import java.io.IOException;
import java.net.InetAddress;

import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.openmuc.jdlms.settings.client.ReferencingMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.InvocationCountingDlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SecureDlmsConnector extends Lls0Connector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureDlmsConnector.class);

    public SecureDlmsConnector(final int responseTimeout, final int logicalDeviceAddress,
            final DlmsDeviceAssociation deviceAssociation) {
        super(responseTimeout, logicalDeviceAddress, deviceAssociation);
    }

    /**
     * Set the correct security attributes on the tcpConnectionBuilder.
     *
     * @param device
     *         The device to connect with.
     * @param tcpConnectionBuilder
     *         The connection builder instance.
     */
    protected abstract void setSecurity(final DlmsDevice device, final TcpConnectionBuilder tcpConnectionBuilder)
            throws OsgpException;

    /**
     * Create a connection with the device.
     *
     * @param device
     *         The device to connect with.
     * @param dlmsMessageListener
     *         Listener to set on the connection.
     *
     * @return The connection.
     *
     * @throws IOException
     *         When there are problems in connecting to or communicating
     *         with the device.
     * @throws OsgpException
     *         When there are problems reading the security and
     *         authorization keys.
     */
    DlmsConnection createConnection(final DlmsDevice device, final DlmsMessageListener dlmsMessageListener)
            throws IOException, OsgpException {

        // Setup connection to device
        final TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(
                InetAddress.getByName(device.getIpAddress())).setResponseTimeout(
                this.responseTimeout).setLogicalDeviceId(this.logicalDeviceAddress);
        tcpConnectionBuilder.setClientId(this.clientId).setReferencingMethod(
                device.isUseSn() ? ReferencingMethod.SHORT : ReferencingMethod.LOGICAL);

        if (device.isUseHdlc()) {
            tcpConnectionBuilder.useHdlc();
        }

        this.setSecurity(device, tcpConnectionBuilder);
        this.setOptionalValues(device, tcpConnectionBuilder);

        if (device.isInDebugMode() || dlmsMessageListener instanceof InvocationCountingDlmsMessageListener) {
            tcpConnectionBuilder.setRawMessageListener(dlmsMessageListener);
        }

        return tcpConnectionBuilder.build();
    }

    /**
     * Get the valid securityKey of a given type for the device.
     *
     * @param device
     *         The device.
     * @param securityKeyType
     *         The type of key to return.
     *
     * @return SecurityKey
     *
     * @throws FunctionalException
     *         when there is no valid key of the given type.
     */
    protected SecurityKey getSecurityKey(final DlmsDevice device, final SecurityKeyType securityKeyType)
            throws FunctionalException {
        final SecurityKey securityKey = device.getValidSecurityKey(securityKeyType);
        if (securityKey == null) {
            final String errorMessage = String.format("There is no valid key for device %s of type %s",
                    device.getDeviceIdentification(), securityKeyType.name());
            LOGGER.error(errorMessage);

            throw new FunctionalException(FunctionalExceptionType.INVALID_DLMS_KEY_ENCRYPTION,
                    ComponentType.PROTOCOL_DLMS);
        }

        return securityKey;
    }
}
