/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.openmuc.jdlms.AuthenticationMechanism;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SecuritySuite;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.openmuc.jdlms.settings.client.ReferencingMethod;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.EncrypterException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.security.EncryptionService;

public class Lls1Connector extends DlmsConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(Lls1Connector.class);

    @Autowired
    private EncryptionService encryptionService;

    private final int clientAccessPoint;

    public Lls1Connector(final int responseTimeout, final int logicalDeviceAddress, final int clientAccessPoint) {
        super(responseTimeout, logicalDeviceAddress);
        this.clientAccessPoint = clientAccessPoint;
    }

    @Override
    public DlmsConnection connect(final DlmsDevice device, final DlmsMessageListener dlmsMessageListener)
            throws TechnicalException {

        // Make sure neither device or device.getIpAddress() is null.
        this.checkDevice(device);
        this.checkIpAddress(device);

        try {
            return this.createConnection(device, dlmsMessageListener);
        } catch (final UnknownHostException e) {
            LOGGER.warn("The IP address is not found: {}", device.getIpAddress(), e);
            // Unknown IP, unrecoverable.
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS,
                    "The IP address is not found: " + device.getIpAddress());
        } catch (final IOException e) {
            throw new ConnectionException(e);
        } catch (final EncrypterException e) {
            LOGGER.error("decryption on security keys went wrong for device: {}", device.getDeviceIdentification(), e);
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS,
                    "decryption on security keys went wrong for device: " + device.getDeviceIdentification());
        }
    }

    /**
     * Create a connection with the device.
     *
     * @return The connection.
     * @throws IOException
     *             When there are problems in connecting to or communicating
     *             with the device.
     * @throws TechnicalException
     *             When there are problems reading the security and
     *             authorisation keys.
     */
    private DlmsConnection createConnection(final DlmsDevice device, final DlmsMessageListener dlmsMessageListener)
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

    private void setSecurity(final DlmsDevice device, final TcpConnectionBuilder tcpConnectionBuilder)
            throws TechnicalException {

        final SecurityKey validPassword = this.getSecurityKey(device, SecurityKeyType.PASSWORD);

        // Decode the key final from Hexstring final to bytes
        byte[] password = null;
        try {
            password = Hex.decodeHex(validPassword.getKey().toCharArray());
        } catch (final DecoderException e) {
            throw new EncrypterException(e);
        }

        // Decrypt the key, discard ivBytes
        final byte[] decryptedPassword = this.encryptionService.decrypt(password);

        final SecuritySuite securitySuite = SecuritySuite.builder()
                .setAuthenticationMechanism(AuthenticationMechanism.LOW).setPassword(decryptedPassword).build();

        tcpConnectionBuilder.setSecuritySuite(securitySuite).setClientId(this.clientAccessPoint);
    }

    /**
     * Get the valid securityKey of a given type for the device.
     *
     * @param securityKeyType
     * @return SecurityKey
     * @throws TechnicalException
     *             when there is no valid key of the given type.
     */
    private SecurityKey getSecurityKey(final DlmsDevice device, final SecurityKeyType securityKeyType)
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
