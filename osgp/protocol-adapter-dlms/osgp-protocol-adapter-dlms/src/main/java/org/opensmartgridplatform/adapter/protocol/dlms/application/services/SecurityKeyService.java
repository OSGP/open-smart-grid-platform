/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public interface SecurityKeyService {

    //get decrypted keys
    byte[] getDlmsMasterKey(final String deviceIdentification);
    byte[] getDlmsAuthenticationKey(final String deviceIdentification);
    byte[] getDlmsGlobalUnicastEncryptionKey(final String deviceIdentification);
    byte[] getMbusDefaultKey(final String mbusDeviceIdentification); //G_MASTER key
    byte[] getDlmsPassword(final String deviceIdentification); //PPP_PASSWORD
    byte[] getMbusUserKey(final String mbusDeviceIdentification); //G_METER_ENCRYPTION_KEY, currently not used

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
     * @param device
     *         DLMS device
     * @param encryptedKey
     *         key encrypted with the symmetrical key internal to the DLMS
     *         protocol adapter.
     * @param keyType
     *         type of key
     *
     * @return saved device, with a new key of the given type
     *
     * @see #validateNewKey(DlmsDevice, SecurityKeyType)
     */
    DlmsDevice storeNewKey(final DlmsDevice device, final byte[] encryptedKey, final SecurityKeyType keyType);

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
     * @param device
     *         DLMS device
     * @param keyType
     *         type of key
     *
     * @return saved device, with a new security key that has become valid, and
     *         any previously valid security key marked as no longer valid
     *
     * @throws ProtocolAdapterException
     *         if no new key is stored with the given device
     * @see #storeNewKey(DlmsDevice, byte[], SecurityKeyType)
     */
    DlmsDevice validateNewKey(final DlmsDevice device, final SecurityKeyType keyType) throws ProtocolAdapterException;

    /**
     * Generates a new key that can be used as DLMS master key, authentication
     * key, global unicast encryption key, M-Bus Default key or M-Bus User key.
     * <p>
     * The master keys (DLMS master or M-Bus Default) cannot be changed on a
     * device, but can be generated for use in tests or with simulated devices.
     *
     * @return a new 16-byte AES key.
     */
    byte[] generateKey();

    /**
     * Convenience method to generate a new key that does not need to be used
     * immediately, and return it appropriately encrypted (AES) with the secret key
     * for the DLMS protocol adapter.
     *
     * @return a new encrypted key.
     *
     * @see #generateKey()
     */
    byte[] generateAndEncryptKey();

    //RSA decrypt key (from incoming requests) and encrypt with AES (for in memory storage)
    byte[] reEncryptKey(final byte[] externallyEncryptedKey, final SecurityKeyType keyType) throws FunctionalException;

    //AES decrypt (decrypt memory storage for actual use of key)
    byte[] decryptKey(final byte[] encryptedKey, final SecurityKeyType keyType) throws ProtocolAdapterException;

    //AES encrypt (encrypt for safe memory storage)
    byte[] encryptKey(final byte[] plainKey, final SecurityKeyType keyType) throws ProtocolAdapterException;

    //Note this method is moved to the appropriate command since it seems to be DSMR specific
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
     *
    public byte[] encryptMbusUserKey(final byte[] mbusDefaultKey, final byte[] mbusUserKey)
            throws ProtocolAdapterException {}
    */
}
