/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.SecurityUtils;
import org.openmuc.jdlms.SecurityUtils.KeyId;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.exceptionhandling.EncrypterException;
import com.alliander.osgp.shared.security.EncryptionService;

/**
 * This is the command executor that corresponds with UpdateFirmwareRequest.
 * Some code may look odd, specifically in the execute() method. The reason is
 * that the device may (sometimes) return NOT_OK after a replacekeys request but
 * was in fact successful! Actually the situation is that (sometimes) the device
 * returns NOT_OK but does replace the keys. So the key that was sent to the
 * device that received the status NOT_OK should be saved, so in case the
 * supposedly valid key (the key that was on the device before replace keys was
 * executed) does not work anymore the new (but supposedly NOT_OK) key can be
 * tried. This key is recognized because both: valid_to=null and valid_from=null
 * ! If that key works we know the device gave the wrong response and this key
 * should be made valid. See also DlmsDevice: discardInvalidKeys,
 * promoteInvalidKeys, het/hasNewSecurityKey.
 *
 */
@Component
public class ReplaceKeyCommandExecutor implements CommandExecutor<ReplaceKeyCommandExecutor.KeyWrapper, DlmsDevice> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplaceKeyCommandExecutor.class);

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    static class KeyWrapper {
        private final byte[] bytes;
        private final KeyId keyId;
        private final SecurityKeyType securityKeyType;

        public KeyWrapper(final byte[] bytes, final KeyId keyId, final SecurityKeyType securityKeyType) {
            this.bytes = bytes;
            this.keyId = keyId;
            this.securityKeyType = securityKeyType;
        }

        public byte[] getBytes() {
            return this.bytes;
        }

        public KeyId getKeyId() {
            return this.keyId;
        }

        public SecurityKeyType getSecurityKeyType() {
            return this.securityKeyType;
        }
    }

    public static KeyWrapper wrap(final byte[] bytes, final KeyId keyId, final SecurityKeyType securityKeyType) {
        return new KeyWrapper(bytes, keyId, securityKeyType);
    }

    @Override
    public DlmsDevice execute(final DlmsConnection conn, final DlmsDevice device,
            final ReplaceKeyCommandExecutor.KeyWrapper keyWrapper) throws ProtocolAdapterException {

        // Add the new key and store in the repo
        DlmsDevice devicePostSave = this.storeNewKey(device, keyWrapper.getBytes(), keyWrapper.getSecurityKeyType());

        // Send the key to the device.
        this.sendToDevice(conn, devicePostSave, keyWrapper);

        // Update key status
        devicePostSave = this.storeNewKeyState(devicePostSave, keyWrapper.getSecurityKeyType());

        return devicePostSave;
    }

    /**
     * Send the key to the device.
     *
     * @param conn
     *            jDLMS connection.
     * @param device
     *            Device instance
     * @param keyWrapper
     *            Key data
     * @throws IOException
     * @throws ProtocolAdapterException
     */
    private void sendToDevice(final DlmsConnection conn, final DlmsDevice device,
            final ReplaceKeyCommandExecutor.KeyWrapper keyWrapper) throws ProtocolAdapterException {
        try {
            // Decrypt the cipher text using the private key.
            byte[] decryptedKey = this.encryptionService.decrypt(keyWrapper.getBytes());
            decryptedKey = Arrays.copyOfRange(decryptedKey, 16, decryptedKey.length);

            byte[] decryptedMasterKey = this.encryptionService.decrypt(this.getMasterKey(device));
            decryptedMasterKey = Arrays.copyOfRange(decryptedMasterKey, 16, decryptedMasterKey.length);

            final MethodParameter methodParameterAuth = SecurityUtils.globalKeyTransfer(decryptedMasterKey,
                    decryptedKey, keyWrapper.getKeyId());
            final MethodResultCode methodResultCode = conn.action(methodParameterAuth).getResultCode();

            if (!MethodResultCode.SUCCESS.equals(methodResultCode)) {
                throw new ProtocolAdapterException("AccessResultCode for replace keys was not SUCCESS: "
                        + methodResultCode);
            }

            // Update key of current connection
            if (keyWrapper.securityKeyType == SecurityKeyType.E_METER_AUTHENTICATION) {
                conn.changeClientGlobalAuthenticationKey(decryptedKey);
            } else if (keyWrapper.securityKeyType == SecurityKeyType.E_METER_ENCRYPTION) {
                conn.changeClientGlobalEncryptionKey(decryptedKey);
            }
        } catch (final IOException e) {
            throw new ConnectionException(e);
        } catch (final EncrypterException e) {
            LOGGER.error("Unexpected exception during decryption of security keys", e);
            throw new ProtocolAdapterException("Unexpected exception during decryption of security keys, reason = "
                    + e.getMessage());
        }
    }

    /**
     * Get the valid master key from the device.
     *
     * @param device
     *            Device instance
     * @return The valid master key.
     * @throws ProtocolAdapterException
     *             when master key can not be decoded to a valid hex value.
     */
    private byte[] getMasterKey(final DlmsDevice device) throws ProtocolAdapterException {
        try {
            final SecurityKey masterKey = device.getValidSecurityKey(SecurityKeyType.E_METER_MASTER);
            return Hex.decodeHex(masterKey.getKey().toCharArray());
        } catch (final DecoderException e) {
            throw new ProtocolAdapterException("Error while decoding key hex string.", e);
        }
    }

    /**
     * Store new key
     *
     * CAUTION: only call when a successful connection with the device has been
     * made, and you are sure any existing new key data is NOT VALID.
     *
     * @param device
     *            Device
     * @param key
     *            Key data
     * @param securityKeyType
     *            Type of key
     * @return Saved device
     */
    private DlmsDevice storeNewKey(final DlmsDevice device, final byte[] key, final SecurityKeyType securityKeyType) {
        // If a new key exists, delete this key.
        final SecurityKey existingKey = device.getNewSecurityKey(securityKeyType);
        if (existingKey != null) {
            device.getSecurityKeys().remove(existingKey);
        }

        device.addSecurityKey(new SecurityKey(device, securityKeyType, Hex.encodeHexString(key), null, null));
        return this.dlmsDeviceRepository.save(device);
    }

    /**
     * Store new key state
     *
     * @param device
     *            Device
     * @param securityKeyType
     *            Type of key
     * @return Saved device
     */
    private DlmsDevice storeNewKeyState(final DlmsDevice device, final SecurityKeyType securityKeyType) {
        final Date now = new Date();
        device.getValidSecurityKey(securityKeyType).setValidTo(now);
        device.getNewSecurityKey(securityKeyType).setValidFrom(now);
        return this.dlmsDeviceRepository.save(device);
    }
}
