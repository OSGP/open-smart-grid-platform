/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

import org.apache.commons.codec.binary.Hex;

public class KeySet implements Serializable {

    private static final long serialVersionUID = 1573954141584647005L;

    private byte[] authenticationKey;

    private byte[] encryptionKey;

    private byte[] masterKey;

    public KeySet(final byte[] authenticationKey, final byte[] encryptionKey, final byte[] masterKey) {
        this.authenticationKey = authenticationKey;
        this.encryptionKey = encryptionKey;
        this.masterKey = masterKey;
    }

    public byte[] getAuthenticationKey() {
        return this.authenticationKey;
    }

    public byte[] getEncryptionKey() {
        return this.encryptionKey;
    }

    public byte[] getMasterKey() {
        return this.masterKey;
    }

    @Override
    public String toString() {
        return "KeySet [authenticationKey=" + Hex.encodeHexString(this.authenticationKey) + ", encryptionKey="
                + Hex.encodeHexString(this.encryptionKey) + ", masterKey=" + Hex.encodeHexString(this.masterKey) + "]";
    }

}
