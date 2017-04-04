/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.openmuc.jdlms.AuthenticationMechanism;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SecuritySuite;
import org.openmuc.jdlms.SecuritySuite.EncryptionMechanism;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.osgp.adapter.protocol.dlms.application.threads.RecoverKeyProcessInitiator;
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
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.security.EncryptionService;

public class Hls5Connector extends SecureDlmsConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hls5Connector.class);

    private final RecoverKeyProcessInitiator recoverKeyProcessInitiator;

    @Autowired
    private EncryptionService encryptionService;

    public Hls5Connector(final RecoverKeyProcessInitiator recoverKeyProcessInitiator, final int responseTimeout,
            final int logicalDeviceAddress, final int clientAccessPoint) {
        super(responseTimeout, logicalDeviceAddress, clientAccessPoint);
        this.recoverKeyProcessInitiator = recoverKeyProcessInitiator;
    }

    @Override
    public DlmsConnection connect(final DlmsDevice device, final DlmsMessageListener dlmsMessageListener)
            throws TechnicalException, FunctionalException {

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
            if (device.hasNewSecurityKey()) {
                // Queue key recovery process.
                this.recoverKeyProcessInitiator.initiate(device.getDeviceIdentification(), device.getIpAddress());
            }
            final String msg = "Error creating connection for " + device.getDeviceIdentification() + " "
                    + device.getIpAddress() + ":" + device.getPort() + ", " + device.isUseHdlc() + ", "
                    + device.isUseSn() + ": " + e.getMessage();
            throw new FunctionalException(FunctionalExceptionType.CONNECTION_ERROR, ComponentType.PROTOCOL_DLMS,
                    (new ConnectionException(msg, e)));
        } catch (final EncrypterException e) {
            LOGGER.error("decryption on security keys went wrong for device: {}", device.getDeviceIdentification(), e);
            throw new FunctionalException(FunctionalExceptionType.WRONG_KEY_FORMAT, ComponentType.PROTOCOL_DLMS, e);
        }
    }

    @Override
    protected void setSecurity(final DlmsDevice device, final TcpConnectionBuilder tcpConnectionBuilder)
            throws TechnicalException {
        final SecurityKey validAuthenticationKey = this.getSecurityKey(device, SecurityKeyType.E_METER_AUTHENTICATION);
        final SecurityKey validEncryptionKey = this.getSecurityKey(device, SecurityKeyType.E_METER_ENCRYPTION);

        // Decode the key from Hexstring to bytes
        byte[] authenticationKey = null;
        byte[] encryptionKey = null;
        try {
            authenticationKey = Hex.decodeHex(validAuthenticationKey.getKey().toCharArray());
            encryptionKey = Hex.decodeHex(validEncryptionKey.getKey().toCharArray());
        } catch (final DecoderException e) {
            throw new EncrypterException(e);
        }

        // Decrypt the key, discard ivBytes
        final byte[] decryptedAuthentication = this.encryptionService.decrypt(authenticationKey);
        final byte[] decryptedEncryption = this.encryptionService.decrypt(encryptionKey);

        final SecuritySuite securitySuite = SecuritySuite.builder().setAuthenticationKey(decryptedAuthentication)
                .setAuthenticationMechanism(AuthenticationMechanism.HLS5_GMAC)
                .setGlobalUnicastEncryptionKey(decryptedEncryption)
                .setEncryptionMechanism(EncryptionMechanism.AES_GMC_128).build();

        tcpConnectionBuilder.setSecuritySuite(securitySuite).setClientId(this.clientAccessPoint);
    }

}
