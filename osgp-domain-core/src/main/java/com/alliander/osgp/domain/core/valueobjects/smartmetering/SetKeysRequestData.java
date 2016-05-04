/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public class SetKeysRequestData implements Serializable, ActionRequest {

    private static final long serialVersionUID = 1573954141584647005L;

    private byte[] authenticationKey;

    private byte[] encryptionKey;

    public SetKeysRequestData(final byte[] authenticationKey, final byte[] encryptionKey) {
        this.authenticationKey = authenticationKey;
        this.encryptionKey = encryptionKey;
    }

    public byte[] getAuthenticationKey() {
        return this.authenticationKey;
    }

    public byte[] getEncryptionKey() {
        return this.encryptionKey;
    }

    @Override
    public void validate() throws FunctionalException {
        // No validation needed
    }
}
