/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PrivateKey;

import javax.crypto.Cipher;

import org.apache.commons.codec.DecoderException;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.osgp.adapter.protocol.dlms.application.threads.RecoverKeyProcessInitiator;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

public class Hls5Connector {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hls5Connector.class);

    private final int responseTimeout;

    private final int logicalDeviceAddress;

    private final int clientAccessPoint;

    private final RecoverKeyProcessInitiator recoverKeyProcessInitiator;

    private final DlmsDeviceRepository dlmsDeviceRepository;

    private DlmsDevice device;

    private String devicePrivateKeyPath;
    private static final String ALGORITHM = "RSA";

    public Hls5Connector(final RecoverKeyProcessInitiator recoverKeyProcessInitiator,
            final DlmsDeviceRepository dlmsDeviceRepository, final int responseTimeout, final int logicalDeviceAddress,
            final int clientAccessPoint, final String devicePrivateKeyPath) {
        this.recoverKeyProcessInitiator = recoverKeyProcessInitiator;
        this.dlmsDeviceRepository = dlmsDeviceRepository;
        this.responseTimeout = responseTimeout;
        this.logicalDeviceAddress = logicalDeviceAddress;
        this.clientAccessPoint = clientAccessPoint;
        this.devicePrivateKeyPath = devicePrivateKeyPath;
    }

    public void setDevice(final DlmsDevice device) {
        this.device = device;
    }

    public ClientConnection connect() throws TechnicalException {
        if (this.device == null) {
            throw new IllegalStateException("Can not connect because no device is set.");
        }

        this.checkIpAddress();

        try {
            final ClientConnection connection = this.createConnection();
            this.discardInvalidKeys();
            return connection;
        } catch (final UnknownHostException e) {
            LOGGER.warn("The IP address is not found: {}", this.device.getIpAddress(), e);
            // Unknown IP, unrecoverable.
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS, "The IP address is not found: "
                    + this.device.getIpAddress());
        } catch (final IOException e) {
            if (this.device.hasNewSecurityKey()) {
                // Queue key recovery process.
                this.recoverKeyProcessInitiator.initiate(this.device.getDeviceIdentification(),
                        this.device.getIpAddress());
            }
            throw new ConnectionException(e);
        }
    }

    private void checkIpAddress() throws TechnicalException {
        if (this.device.getIpAddress() == null) {
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS, "Unable to get HLS5 connection for device "
                    + this.device.getDeviceIdentification() + ", because the IP address is not set.");
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
     *             When there are problems reading or decrypting the encrypted
     *             security and authorisation keys.
     */
    private ClientConnection createConnection() throws IOException, TechnicalException {
        final SecurityKey validAuthenticationKey = this.getSecurityKey(SecurityKeyType.E_METER_AUTHENTICATION);
        final SecurityKey validEncryptionKey = this.getSecurityKey(SecurityKeyType.E_METER_ENCRYPTION);

        try {
            final String authenticationKeyValue = validAuthenticationKey.getKey();
            final String encryptionKeyValue = validEncryptionKey.getKey();

            // Decode the key from Hexstring to bytes
            final byte[] authenticationKey = org.apache.commons.codec.binary.Hex.decodeHex(authenticationKeyValue
                    .toCharArray());
            final byte[] encryptionKey = org.apache.commons.codec.binary.Hex
                    .decodeHex(encryptionKeyValue.toCharArray());

            // Decrypt the key
            final byte[] decryptedAuthentication = this.decrypt(authenticationKey);
            final byte[] decryptedEncryption = this.decrypt(encryptionKey);

            // Setup connection to device
            final TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(
                    InetAddress.getByName(this.device.getIpAddress()))
            .useGmacAuthentication(decryptedAuthentication, decryptedEncryption)
            .enableEncryption(decryptedEncryption).responseTimeout(this.responseTimeout)
            .logicalDeviceAddress(this.logicalDeviceAddress).clientAccessPoint(this.clientAccessPoint);

            final Integer challengeLength = this.device.getChallengeLength();
            if (challengeLength != null) {
                tcpConnectionBuilder.challengeLength(challengeLength);
            }

            return tcpConnectionBuilder.buildLnConnection();
        } catch (final DecoderException e) {
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS, "Error while reading RSA key! ");
        }
    }

    private byte[] decrypt(final byte[] inputData) throws TechnicalException {
        byte[] decryptedData = null;
        ObjectInputStream inputStream = null;
        PrivateKey privateKey;
        try {
            // Read the private key from the file.
            inputStream = new ObjectInputStream(new FileInputStream(this.devicePrivateKeyPath));
            privateKey = (PrivateKey) inputStream.readObject();

            // Get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            // Decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedData = cipher.doFinal(inputData);
        } catch (final Exception ex) {
            LOGGER.error("Unexpected exception during decryption", ex);
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS, "Error while decrypting RSA key!");
        } finally {
            try {
                inputStream.close();
            } catch (final IOException e) {
                LOGGER.error("Unexpected exception during closing of inputstream", e);
                throw new TechnicalException(ComponentType.PROTOCOL_DLMS, "Error while closing inputstream!");
            }
        }
        return decryptedData;
    }

    private void discardInvalidKeys() {
        this.device.discardInvalidKeys();
        this.device = this.dlmsDeviceRepository.save(this.device);
    }

    /**
     * Get the valid securityKey of a given type for the device.
     *
     * @param securityKeyType
     * @return SecurityKey
     * @throws TechnicalException
     *             when there is no valid key of the given type.
     */
    private SecurityKey getSecurityKey(final SecurityKeyType securityKeyType) throws TechnicalException {
        final SecurityKey securityKey = this.device.getValidSecurityKey(securityKeyType);
        if (securityKey == null) {
            throw new TechnicalException(ComponentType.PROTOCOL_DLMS, String.format(
                    "There is no valid key for device '%s' of type '%s'.", this.device.getDeviceIdentification(),
                    securityKeyType.name()));
        }

        return securityKey;
    }
}
