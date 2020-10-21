/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.util.List;
import java.util.Map;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public interface SecurityKeyService {

    byte[] getKey(String deviceIdentification, SecurityKeyType type);
    Map<SecurityKeyType, byte[]> getKeys(String deviceIdentification, List<SecurityKeyType> keyTypes);

    byte[] getNewKey(String deviceIdentification, SecurityKeyType type);
    Map<SecurityKeyType, byte[]> getNewKeys(String deviceIdentification, List<SecurityKeyType> keyTypes);

    /**
     * Store new key
     * <p>
     * A new key is a security key with a device which status NEW.
     * This status is used when the new key is known, but not yet set on the device.
     * <p>
     * <strong>CAUTION:</strong> Only call this method when a successful
     * connection with the device has been set up (that is: a valid
     * communication key that works is known), and you are sure any existing new
     * key data that is not activated yet (for instance a new key stored earlier in an
     * attempt to replace the communication key that got aborted).<br>
     * <p>
     * The moment the new key is known to be transferred to the device, make
     * sure to activate it by calling
     * {@link #activateNewKey(String, SecurityKeyType)}.
     *
     * @param deviceIdentification
     *         DLMS device id
     * @param key
     *        key to store, unencrypted
     * @param keyType
     *         type of key
     *
     *
     * @see #activateNewKey(String, SecurityKeyType)
     */
    void storeNewKey(final String deviceIdentification, final SecurityKeyType keyType, final byte[] key);
    void storeNewKeys(final String deviceIdentification, final Map<SecurityKeyType, byte[]> keysByType);

    /**
     * @see #storeNewKey(String, SecurityKeyType, byte[])
     */
    //void aesDecryptAndStoreNewKey(final String deviceIdentification, final SecurityKeyType keyTypes,
    //        final byte[] aesEncryptedKeys) throws FunctionalException;

    boolean hasNewSecretOfType(String deviceIdentification, SecurityKeyType keyType);

    /**
     * Updates the state of a new key from 'new' to 'active'
     * <p>
     * This method should be called to activate a new key stored with
     * {@link #storeNewKeys(String, Map)} after it has
     * been confirmed to be set on the device.
     *
     * @param deviceIdentification
     *         DLMS device id
     * @param keyType
     *         type of key
     *
     * @throws ProtocolAdapterException
     *         if no new key is stored with the given device
     * @see #storeNewKeys(String, Map)
     */
    void activateNewKey(final String deviceIdentification, final SecurityKeyType keyType)
            throws ProtocolAdapterException;
    void activateNewKeys(final String deviceIdentification, final List<SecurityKeyType> keyTypes)
            throws ProtocolAdapterException;

    byte[] generate128BitsKeyAndStoreAsNewKey(final String deviceIdentification, final SecurityKeyType keyType);
    Map<SecurityKeyType, byte[]> generate128BitsKeysAndStoreAsNewKeys(final String deviceIdentification,
            final List<SecurityKeyType> keyTypes);

    //RSA decrypt key (from incoming requests) and encrypt with AES (for in memory storage)
    byte[] reEncryptKey(final byte[] externallyEncryptedKey) throws
            FunctionalException;

    //decrypt OSGP provided RSA key
    byte[] rsaDecrypt(final byte[] externallyEncryptedKey) throws FunctionalException;

    //AES decrypt (decrypt memory storage for actual use of key)
    byte[] aesDecryptKey(final byte[] encryptedKey) throws FunctionalException;

    //AES encrypt (encrypt for safe memory storage)
    byte[] aesEncryptKey(final byte[] plainKey) throws FunctionalException;

}
