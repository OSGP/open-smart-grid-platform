/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.shared.security.EncryptionService;
import com.alliander.osgp.shared.security.RsaEncryptionService;

@Service(value = "dlmsReEncryptionService")
public class ReEncryptionService {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private RsaEncryptionService rsaEncryptionService;

    public byte[] reEncryptKey(final byte[] asymmetricEncryptedKey, final SecurityKeyType keyType)
            throws ProtocolAdapterException {

        if (asymmetricEncryptedKey == null) {
            return null;
        }

        try {
            /*
             * Replace the asymmetric encryption for which the public key is
             * shared with web service callers by a faster symmetric encryption
             * for use inside the protocol adapter only.
             */
            final byte[] decryptedKeyBytes = this.rsaEncryptionService.decrypt(asymmetricEncryptedKey);
            return this.encryptionService.encrypt(decryptedKeyBytes);
        } catch (final Exception e) {
            throw new ProtocolAdapterException("Error processing " + keyType + " key", e);
        }
    }
}
