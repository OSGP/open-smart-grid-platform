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

    private static final int AES_GMC_128 = 128;

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

            final String errorMessage = String.format("Error creating connection for %s with IP adress: %s and with device port: %d",
                    device.getDeviceIdentification(), device.getIpAddress(), device.getPort());
            LOGGER.error(errorMessage);
            throw new FunctionalException(FunctionalExceptionType.CONNECTION_ERROR, ComponentType.PROTOCOL_DLMS, e);
        } catch (final EncrypterException e) {
            LOGGER.error("decryption on security keys went wrong for device: {}", device.getDeviceIdentification(), e);
            throw new FunctionalException(FunctionalExceptionType.INVALID_DLMS_KEY_FORMAT, ComponentType.PROTOCOL_DLMS, e);
        }
    }

    @Override
    protected void setSecurity(final DlmsDevice device, final TcpConnectionBuilder tcpConnectionBuilder)
            throws TechnicalException, FunctionalException {
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

        // Validate keys before JDLMS does and throw a FunctionalException if necessary
        this.validateKeys(decryptedAuthentication, decryptedEncryption);

        final SecuritySuite securitySuite = SecuritySuite.builder().setAuthenticationKey(decryptedAuthentication)
                .setAuthenticationMechanism(AuthenticationMechanism.HLS5_GMAC)
                .setGlobalUnicastEncryptionKey(decryptedEncryption)
                .setEncryptionMechanism(EncryptionMechanism.AES_GMC_128).build();

        tcpConnectionBuilder.setSecuritySuite(securitySuite).setClientId(this.clientAccessPoint);
    }

    private void validateKeys(final byte[] encryptionKey, final byte[] authenticationKey)
            throws FunctionalException {
        if (this.checkEmptyKey(encryptionKey)) {
            this.throwFunctionException("The encryption key is empty", FunctionalExceptionType.INVALID_DLMS_KEY_ENCRYPTION);
        }

        if (this.checkEmptyKey(authenticationKey)) {
            this.throwFunctionException("The authentication key is empty", FunctionalExceptionType.INVALID_DLMS_KEY_ENCRYPTION);
        }

        if (this.checkLenghtKey(encryptionKey)) {
            this.throwFunctionException("The encryption key has an invalid length", FunctionalExceptionType.INVALID_DLMS_KEY_FORMAT);
        }

        if (this.checkLenghtKey(authenticationKey)) {
            this.throwFunctionException("The authentication key has an invalid length", FunctionalExceptionType.INVALID_DLMS_KEY_FORMAT);
        }
    }

    private boolean checkEmptyKey(final byte[] key) {
        if (key == null) {
            return true;
        }
        return false;
    }

    private boolean checkLenghtKey(final byte[] key) {
        if (key.length * 8 != AES_GMC_128) {
            return true;
        }
        return false;
    }

    private void throwFunctionException(final String msg, final FunctionalExceptionType type) throws FunctionalException {
        LOGGER.error(msg);
        throw new FunctionalException(type, ComponentType.PROTOCOL_DLMS);
    }
}
