/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.KeyGenerator;

import org.apache.commons.codec.binary.Hex;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.security.EncryptionService;

@Service(value = "dlmsKeyHelperService")
public class KeyHelperService {

    public static final int AES_GMC_128_KEY_SIZE = 128;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private EncryptionService encryptionService;

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
    public DlmsDevice storeNewKey(final DlmsDevice device, final byte[] key, final SecurityKeyType securityKeyType) {
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
     * @param securityKeyTypel
     *            Type of key
     * @return Saved device
     */
    public DlmsDevice storeNewKeyState(final DlmsDevice device, final SecurityKeyType securityKeyType) {
        final Date now = new Date();
        if (device.getValidSecurityKey(securityKeyType) != null) {
            device.getValidSecurityKey(securityKeyType).setValidTo(now);
        }

        device.getNewSecurityKey(securityKeyType).setValidFrom(now);
        return this.dlmsDeviceRepository.save(device);
    }

    public String getSecurityKey(final DlmsDevice dlmsDevice, final SecurityKeyType securityKeyType)
            throws FunctionalException {
        final byte[] generatedKey = this.generateKey();
        final byte[] encryptedKey = this.encryptionService.encrypt(generatedKey);

        // Add the new key and store in the repo
        DlmsDevice devicePostSave = this.storeNewKey(dlmsDevice, encryptedKey, securityKeyType);

        // Update key status
        devicePostSave = this.storeNewKeyState(devicePostSave, securityKeyType);

        return Hex.encodeHexString(encryptedKey);
    }

    public final byte[] generateKey() {
        try {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(AES_GMC_128_KEY_SIZE);
            return keyGenerator.generateKey().getEncoded();
        } catch (final NoSuchAlgorithmException e) {
            throw new AssertionError("Expected AES algorithm to be available for key generation.", e);
        }
    }

}
