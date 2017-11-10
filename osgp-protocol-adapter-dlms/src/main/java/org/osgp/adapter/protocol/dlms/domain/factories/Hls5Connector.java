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

import org.openmuc.jdlms.AuthenticationMechanism;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SecuritySuite;
import org.openmuc.jdlms.SecuritySuite.EncryptionMechanism;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.osgp.adapter.protocol.dlms.application.services.SecurityKeyService;
import org.osgp.adapter.protocol.dlms.application.threads.RecoverKeyProcessInitiator;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
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

public class Hls5Connector extends SecureDlmsConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hls5Connector.class);

    private static final int AES_GMC_128 = 128;

    private final RecoverKeyProcessInitiator recoverKeyProcessInitiator;

    @Autowired
    private SecurityKeyService securityKeyService;

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
            LOGGER.error("The IP address is not found: {}", device.getIpAddress(), e);
            // Unknown IP, unrecoverable.
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS,
                    "The IP address is not found: " + device.getIpAddress());
        } catch (final IOException e) {
            if (device.hasNewSecurityKey()) {
                // Queue key recovery process.
                this.recoverKeyProcessInitiator.initiate(device.getDeviceIdentification(), device.getIpAddress());
            }
            final String msg = String.format("Error creating connection for device %s with Ip address:%s Port:%d UseHdlc:%b UseSn:%b Message:%s",
                    device.getDeviceIdentification(),
                    device.getIpAddress(),
                    device.getPort(),
                    device.isUseHdlc(),
                    device.isUseSn(),
                    e.getMessage());
            LOGGER.error(msg);
            throw new ConnectionException(msg, e);
        } catch (final EncrypterException e) {
            LOGGER.error("decryption on security keys went wrong for device: {}", device.getDeviceIdentification(), e);
            throw new FunctionalException(FunctionalExceptionType.INVALID_DLMS_KEY_FORMAT, ComponentType.PROTOCOL_DLMS, e);
        }
    }

    @Override
    protected void setSecurity(final DlmsDevice device, final TcpConnectionBuilder tcpConnectionBuilder)
            throws TechnicalException, FunctionalException {

        final String deviceIdentification = device.getDeviceIdentification();
        final byte[] dlmsAuthenticationKey;
        final byte[] dlmsEncryptionKey;
        try {
            dlmsAuthenticationKey = this.securityKeyService.getDlmsAuthenticationKey(deviceIdentification);
            dlmsEncryptionKey = this.securityKeyService.getDlmsGlobalUnicastEncryptionKey(deviceIdentification);
        } catch (final EncrypterException e) {
            LOGGER.error("Error determining DLMS communication key setting up HLS5 connection", e);
            throw new FunctionalException(FunctionalExceptionType.INVALID_DLMS_KEY_ENCRYPTION,
                    ComponentType.PROTOCOL_DLMS);
        }

        // Validate keys before JDLMS does and throw a FunctionalException if
        // necessary
        this.validateKeys(dlmsAuthenticationKey, dlmsEncryptionKey);

        final SecuritySuite securitySuite = SecuritySuite.builder().setAuthenticationKey(dlmsAuthenticationKey)
                .setAuthenticationMechanism(AuthenticationMechanism.HLS5_GMAC)
                .setGlobalUnicastEncryptionKey(dlmsEncryptionKey)
                .setEncryptionMechanism(EncryptionMechanism.AES_GMC_128).build();

        tcpConnectionBuilder.setSecuritySuite(securitySuite).setClientId(this.clientAccessPoint);
    }

    private void validateKeys(final byte[] encryptionKey, final byte[] authenticationKey)
            throws FunctionalException {
        if (this.checkEmptyKey(encryptionKey)) {
            this.throwFunctionalException("The encryption key is empty", FunctionalExceptionType.INVALID_DLMS_KEY_ENCRYPTION);
        }

        if (this.checkEmptyKey(authenticationKey)) {
            this.throwFunctionalException("The authentication key is empty", FunctionalExceptionType.INVALID_DLMS_KEY_ENCRYPTION);
        }

        if (this.checkLenghtKey(encryptionKey)) {
            this.throwFunctionalException("The encryption key has an invalid length", FunctionalExceptionType.INVALID_DLMS_KEY_FORMAT);
        }

        if (this.checkLenghtKey(authenticationKey)) {
            this.throwFunctionalException("The authentication key has an invalid length", FunctionalExceptionType.INVALID_DLMS_KEY_FORMAT);
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

    private void throwFunctionalException(final String msg, final FunctionalExceptionType type) throws FunctionalException {
        LOGGER.error(msg);
        throw new FunctionalException(type, ComponentType.PROTOCOL_DLMS);
    }
}
