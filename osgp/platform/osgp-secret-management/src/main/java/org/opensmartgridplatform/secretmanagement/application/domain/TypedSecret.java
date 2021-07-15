/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.domain;

import java.util.Arrays;

import lombok.Getter;

/**
 * TypedSecret stores a secret (not necessarily an encrypted secret), along with it's type.
 */
@Getter
public class TypedSecret {
    private final SecretType secretType;
    private final byte[] secret;

    public TypedSecret(final byte[] secret, final SecretType secretType) {
        this.secret = secret == null ? null : Arrays.copyOf(secret, secret.length);
        if (secretType != null) {
            this.secretType = secretType;
        } else {
            throw new IllegalArgumentException("Secret type can not be NULL");
        }
    }

    public byte[] getSecret() {
        return this.secret == null ? null : Arrays.copyOf(this.secret, this.secret.length);
    }
}


