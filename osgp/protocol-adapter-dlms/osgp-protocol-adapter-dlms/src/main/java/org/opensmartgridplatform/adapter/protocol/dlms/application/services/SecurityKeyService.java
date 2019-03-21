/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.security.EncryptionService;
import org.opensmartgridplatform.shared.security.RsaEncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SecurityKeyService offers a single point of entry for all code that needs to
 * deal with any of the DLMS security keys.
 * <p>
 * All code using or updating DLMS security keys for devices should use this
 * service to delegate all key handling to.
 */
@Service(value = "dlmsSecurityKeyService")
@Transactional(value = "transactionManager")
public class SecurityKeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityKeyService.class);

    public static final int AES_GMC_128_KEY_SIZE = 128;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private RsaEncryptionService rsaEncryptionService;

    /**
     * Re-encrypts the given key with a secret known only inside this protocol
     * adapter.
     * <p>
     * New keys can be provided to OSGP from outside in a form encrypted with
     * the public key from an asymmetrical key pair for the platform, which is
     * available to external organizations.<br>
     * Inside the DLMS protocol adapter keys are encrypted with a faster
     * symmetrical encryption using a secret key that is not supposed to be
     * known outside this protocol adapter.
     *
     * @param externallyEncryptedKey
     *            key encrypted with the externally known public key for OSGP
     * @param keyType
     *            type of the key, for logging purposes
     * @return the key encrypted with the symmetrical secret key used only
     *         inside the DLMS protocol adapter, or an empty byte array if
     *         {@code externallyEncryptedKey == null}
     * @throws FunctionalException
     *             in case of a encryption/decryption errors while handling the
     *             key
     */
    public byte[] reEncryptKey(final byte[] externallyEncryptedKey, final SecurityKeyType keyType)
            throws FunctionalException {

        if (externallyEncryptedKey == null) {
            return new byte[0];
        }

        final byte[] key = this.rsaDecrypt(externallyEncryptedKey, keyType);
        return this.aesEncrypt(key, keyType);
    }

    private byte[] rsaDecrypt(final byte[] externallyEncryptedKey, final SecurityKeyType keyType)
            throws FunctionalException {
        try {
            return this.rsaEncryptionService.decrypt(externallyEncryptedKey);
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during decryption", e);

            throw new FunctionalException(FunctionalExceptionType.DECRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS,
                    new EncrypterException(
                            String.format("Unexpected exception during decryption of %s key.", keyType)));
        }
    }

    private byte[] aesEncrypt(final byte[] key, final SecurityKeyType keyType) throws FunctionalException {
        try {
            return this.encryptionService.encrypt(key);
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during encryption", e);

            throw new FunctionalException(FunctionalExceptionType.ENCRYPTION_EXCEPTION, ComponentType.PROTOCOL_DLMS,
                    new EncrypterException(
                            String.format("Unexpected exception during encryption of %s key.", keyType)));
        }
    }

    /**
     * Decrypts the given symmetrically encrypted key.
     * <p>
     * <strong>NB:</strong> Only decrypt keys like this at the moment they are
     * required as part of the communication with a device.
     *
     * @param encryptedKey
     *            key encrypted with the symmetrical key internal to the DLMS
     *            protocol adapter.
     * @param keyType
     *            type of the key, for logging purposes
     * @return the plain key, or an empty byte array if
     *         {@code encryptedKey == null}
     */
    public byte[] decryptKey(final byte[] encryptedKey, final SecurityKeyType keyType) throws ProtocolAdapterException {
        if (encryptedKey == null) {
            return new byte[0];
        }
        try {
            return this.encryptionService.decrypt(encryptedKey);
        } catch (final Exception e) {
            throw new ProtocolAdapterException("Error decrypting " + keyType + " key", e);
        }
    }

    /**
     * Encrypts the given {@code plainKey} with the symmetrical secret key that
     * is internal to the DLMS protocol adapter.
     *
     * @param plainKey
     *            plain key without encryption
     * @param keyType
     *            type of the key, for logging purposes
     * @return the given key encrypted with the symmetrical key internal to the
     *         DLMS protocol adapter.
     */
    public byte[] encryptKey(final byte[] plainKey, final SecurityKeyType keyType) throws ProtocolAdapterException {
        if (plainKey == null) {
            return new byte[0];
        }
        try {
            return this.encryptionService.encrypt(plainKey);
        } catch (final Exception e) {
            throw new ProtocolAdapterException("Error encrypting " + keyType + " key", e);
        }
    }

    /**
     * Retrieves the DLMS master key (KEK) for the device with the given
     * {@code deviceIdentification}.
     * <p>
     * <strong>NB:</strong> Only retrieve keys like this at the moment they are
     * required as part of the communication with a device.
     *
     * @param deviceIdentification
     *            the identification of a DLMS device.
     * @return the key, possibly {@code null} if either the device is not found
     *         or it does not have a valid master key.
     * @throws EncrypterException
     *             if there is an error decoding the key.
     */
    public byte[] getDlmsMasterKey(final String deviceIdentification) {
        LOGGER.info("Retrieving DLMS master key for device {}", deviceIdentification);
        return this.getKey(deviceIdentification, SecurityKeyType.E_METER_MASTER);
    }

    /**
     * Retrieves the DLMS authentication key for the device with the given
     * {@code deviceIdentification}.
     * <p>
     * <strong>NB:</strong> Only retrieve keys like this at the moment they are
     * required as part of the communication with a device.
     *
     * @param deviceIdentification
     *            the identification of a DLMS device.
     * @return the key, possibly {@code null} if either the device is not found
     *         or it does not have a valid authentication key.
     * @throws EncrypterException
     *             if there is an error decoding the key.
     */
    public byte[] getDlmsAuthenticationKey(final String deviceIdentification) {
        LOGGER.info("Retrieving DLMS authentication key for device {}", deviceIdentification);
        return this.getKey(deviceIdentification, SecurityKeyType.E_METER_AUTHENTICATION);
    }

    /**
     * Retrieves the DLMS global unicast encryption key for the device with the
     * given {@code deviceIdentification}.
     * <p>
     * <strong>NB:</strong> Only retrieve keys like this at the moment they are
     * required as part of the communication with a device.
     *
     * @param deviceIdentification
     *            the identification of a DLMS device.
     * @return the key, possibly an empty byte array if either the device is not
     *         found or it does not have a valid global unicast encryption key.
     * @throws EncrypterException
     *             if there is an error decoding the key.
     */
    public byte[] getDlmsGlobalUnicastEncryptionKey(final String deviceIdentification) {
        LOGGER.info("Retrieving DLMS global unicast encryption key for device {}", deviceIdentification);
        return this.getKey(deviceIdentification, SecurityKeyType.E_METER_ENCRYPTION);
    }

    /**
     * Retrieves the M-Bus Default key for the M-Bus device with the given
     * {@code mbusDeviceIdentification}.
     * <p>
     * <strong>NB:</strong> Only retrieve keys like this at the moment they are
     * required as part of the communication with a DLMS gateway device.
     *
     * @param mbusDeviceIdentification
     *            the identification of an M-Bus device.
     * @return the key, possibly an empty byte array if either the device is not
     *         found or it does not have a valid M-Bus Default key.
     * @throws EncrypterException
     *             if there is an error decoding the key.
     */
    public byte[] getMbusDefaultKey(final String mbusDeviceIdentification) {
        LOGGER.info("Retrieving M-Bus Default key for device {}", mbusDeviceIdentification);
        return this.getKey(mbusDeviceIdentification, SecurityKeyType.G_METER_MASTER);
    }

    /**
     * Retrieves the M-Bus User key for the M-Bus device with the given
     * {@code mbusDeviceIdentification}.
     * <p>
     * <strong>NB:</strong> Only retrieve keys like this at the moment they are
     * required as part of the communication with a DLMS gateway device.
     *
     * @param mbusDeviceIdentification
     *            the identification of an M-Bus device.
     * @return the key, possibly an empty byte array if either the device is not
     *         found or it does not have a valid M-Bus User key.
     * @throws EncrypterException
     *             if the key is found, but there is an error decoding or
     *             decrypting the key.
     */
    public byte[] getMbusUserKey(final String mbusDeviceIdentification) {
        LOGGER.info("Retrieving M-Bus User key for device {}", mbusDeviceIdentification);
        return this.getKey(mbusDeviceIdentification, SecurityKeyType.G_METER_ENCRYPTION);
    }

    /**
     * Retrieves the DLMS low level security password for the device with the
     * given {@code deviceIdentification}.
     * <p>
     * <strong>NB:</strong> Only retrieve keys like this at the moment they are
     * required as part of the communication with a device.
     *
     * @param deviceIdentification
     *            the identification of a DLMS device.
     * @return the key, possibly an empty byte array if either the device is not
     *         found or it does not have a valid password.
     * @throws EncrypterException
     *             if there is an error decoding the key.
     */
    public byte[] getDlmsPassword(final String deviceIdentification) {
        LOGGER.info("Retrieving DLMS LLS Password for device {}", deviceIdentification);
        return this.getKey(deviceIdentification, SecurityKeyType.PASSWORD);
    }

    private byte[] getKey(final String deviceIdentification, final SecurityKeyType securityKeyType) {

        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
        if (dlmsDevice == null) {
            LOGGER.warn("No DlmsDevice found for identification {} - returning null as {} key.", deviceIdentification,
                    securityKeyType);
            return new byte[0];
        }

        final SecurityKey securityKey = dlmsDevice.getValidSecurityKey(securityKeyType);
        if (securityKey == null) {
            LOGGER.warn("No valid {} key found with device {} - returning null.", securityKeyType,
                    deviceIdentification);
            return new byte[0];
        }

        try {
            final byte[] encryptedKey = Hex.decodeHex(securityKey.getKey().toCharArray());
            return this.encryptionService.decrypt(encryptedKey);
        } catch (final DecoderException | FunctionalException e) {
            throw new EncrypterException("Error decoding " + securityKey + " for device " + deviceIdentification, e);
        }
    }

    /**
     * Store new key
     * <p>
     * A new key is a security key with a device that does not have a valid from
     * date. This situation occurs in the process of updating a key, when the
     * new key is known, but not yet set on the device.
     * <p>
     * <strong>CAUTION:</strong> Only call this method when a successful
     * connection with the device has been set up (that is: a valid
     * communication key that works is known), and you are sure any existing new
     * key data is NOT VALID (for instance a new key stored earlier in an
     * attempt to replace the communication key that got aborted).<br>
     * <strong>This method will throw away any earlier stored new key and
     * replace it.</strong>
     * <p>
     * The moment the new key is known to be transferred to the device, make
     * sure to update its status from a new key to a valid key (and invalidating
     * any previous key) by calling
     * {@link #validateNewKey(DlmsDevice, SecurityKeyType)}.
     *
     * @see #validateNewKey(DlmsDevice, SecurityKeyType)
     * @param device
     *            DLMS device
     * @param encryptedKey
     *            key encrypted with the symmetrical key internal to the DLMS
     *            protocol adapter.
     * @param keyType
     *            type of key
     * @return saved device, with a new key of the given type
     */
    public DlmsDevice storeNewKey(final DlmsDevice device, final byte[] encryptedKey, final SecurityKeyType keyType) {
        this.removeEarlierStoredNewKeyIfFound(device, keyType);
        this.addNewKeyToDevice(device, encryptedKey, keyType);
        return this.dlmsDeviceRepository.save(device);
    }

    private void removeEarlierStoredNewKeyIfFound(final DlmsDevice device, final SecurityKeyType keyType) {
        final SecurityKey existingKey = device.getNewSecurityKey(keyType);
        if (existingKey != null) {
            LOGGER.warn("Removing earlier stored key in the NEW state: {}", existingKey);
            device.getSecurityKeys().remove(existingKey);
        }
    }

    private void addNewKeyToDevice(final DlmsDevice device, final byte[] encryptedKey, final SecurityKeyType keyType) {
        final SecurityKey newKey = new SecurityKey(device, keyType, Hex.encodeHexString(encryptedKey), null, null);
        device.addSecurityKey(newKey);
    }

    /**
     * Updates the state of a new key (having valid from date {@code null}) to
     * be considered valid (setting valid from to now).<br>
     * This invalidates any previous valid key (setting valid of the previous
     * key to now).
     * <p>
     * This method should be called to validate a new key stored with
     * {@link #storeNewKey(DlmsDevice, byte[], SecurityKeyType)} after it has
     * been confirmed to be set on the device.
     *
     * @see #storeNewKey(DlmsDevice, byte[], SecurityKeyType)
     * @param device
     *            DLMS device
     * @param keyType
     *            type of key
     * @return saved device, with a new security key that has become valid, and
     *         any previously valid security key marked as no longer valid
     * @throws ProtocolAdapterException
     *             if no new key is stored with the given device
     */
    public DlmsDevice validateNewKey(final DlmsDevice device, final SecurityKeyType keyType)
            throws ProtocolAdapterException {

        final SecurityKey newKey = this.findNewKey(device, keyType);
        final SecurityKey previousValidKey = device.getValidSecurityKey(keyType);
        return this.updateDeviceWithNewValidKey(device, previousValidKey, newKey);
    }

    private SecurityKey findNewKey(final DlmsDevice device, final SecurityKeyType keyType)
            throws ProtocolAdapterException {
        final SecurityKey newKey = device.getNewSecurityKey(keyType);
        if (newKey == null) {
            throw new ProtocolAdapterException(
                    "No new " + keyType + " key found with device " + device.getDeviceIdentification());
        }
        return newKey;
    }

    private DlmsDevice updateDeviceWithNewValidKey(final DlmsDevice device, final SecurityKey previousValidKey,
            final SecurityKey newKey) {

        final Date now = new Date();
        if (previousValidKey != null) {
            previousValidKey.setValidTo(now);
        }
        newKey.setValidFrom(now);
        return this.dlmsDeviceRepository.save(device);
    }

    /**
     * Generates a new key that can be used as DLMS master key, authentication
     * key, global unicast encryption key, M-Bus Default key or M-Bus User key.
     * <p>
     * The master keys (DLMS master or M-Bus Default) cannot be changed on a
     * device, but can be generated for use in tests or with simulated devices.
     *
     * @return a new 16-byte AES key.
     */
    public byte[] generateKey() {
        try {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(AES_GMC_128_KEY_SIZE);
            return keyGenerator.generateKey().getEncoded();
        } catch (final NoSuchAlgorithmException e) {
            throw new AssertionError("Expected AES algorithm to be available for key generation.", e);
        }
    }

    /**
     * Convenience method to generate a new key that does not need to be used
     * immediately, and return it appropriately encrypted with the secret key
     * for the DLMS protocol adapter.
     *
     * @see #generateKey()
     * @return a new encrypted key.
     */
    public byte[] generateAndEncryptKey() {
        try {
            return this.encryptionService.encrypt(this.generateKey());
        } catch (final FunctionalException e) {
            throw new EncrypterException("Error encrypting freshly generated key", e);
        }
    }

    /**
     * Encrypts a new M-Bus User key with the M-Bus Default key for use as M-Bus
     * Client Setup transfer_key parameter.
     * <p>
     * Note that the specifics of the encryption of the M-Bus User key depend on
     * the M-Bus version the devices support. This method should be appropriate
     * for use with DSMR 4 M-Bus devices.
     * <p>
     * The encryption is performed by applying an AES/CBC/NoPadding cipher
     * initialized for encryption with the given mbusDefaultKey and an
     * initialization vector of 16 zero-bytes to the given mbusUserKey.
     *
     * @return the properly wrapped User key for a DSMR 4 M-Bus User key change.
     */
    public byte[] encryptMbusUserKey(final byte[] mbusDefaultKey, final byte[] mbusUserKey)
            throws ProtocolAdapterException {

        final Key secretkeySpec = new SecretKeySpec(mbusDefaultKey, "AES");

        try {

            final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

            final IvParameterSpec params = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.ENCRYPT_MODE, secretkeySpec, params);

            return cipher.doFinal(mbusUserKey);

        } catch (final NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            final String message = "Error encrypting M-Bus User key with M-Bus Default key for transfer.";
            LOGGER.error(message, e);
            throw new ProtocolAdapterException(message);
        }
    }

}
