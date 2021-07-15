/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

import java.util.Arrays;

import org.opensmartgridplatform.ws.schema.core.secret.management.SecretType;

public enum SecurityKeyType {
    /**
     * DLMS master key (Key Encryption Key)
     */
    E_METER_MASTER(SecretType.E_METER_MASTER_KEY),
    /**
     * DLMS authentication key
     */
    E_METER_AUTHENTICATION(SecretType.E_METER_AUTHENTICATION_KEY),
    /**
     * DLMS global unicast encryption key
     */
    E_METER_ENCRYPTION(SecretType.E_METER_ENCRYPTION_KEY_UNICAST),
    /**
     * M-Bus Default key
     */
    G_METER_MASTER(SecretType.G_METER_MASTER_KEY),
    /**
     * M-Bus User key
     */
    G_METER_ENCRYPTION(SecretType.G_METER_ENCRYPTION_KEY),
    /**
     * Password (e.g. used as DLMS Low Level Security secret)
     */
    PASSWORD(SecretType.PPP_PASSWORD);

    private final SecretType secretType;

    SecurityKeyType(final SecretType secretType) {
        this.secretType = secretType;
    }

    public SecretType toSecretType() {
        return this.secretType;
    }

    public static SecurityKeyType fromSecretType(final SecretType secretType) {
        return Arrays.stream(SecurityKeyType.values()).filter(skt -> skt.secretType.equals(secretType)).findFirst()
                     .orElseThrow(
                             () -> new IllegalArgumentException("Could not get value from secret type " + secretType));
    }
}
