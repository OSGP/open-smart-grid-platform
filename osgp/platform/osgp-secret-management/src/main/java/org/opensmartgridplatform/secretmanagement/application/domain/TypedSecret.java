/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.domain;

/**
 * TypedSecret stores a secret (not necessarily an encrypted secret), along with it's type.
 */
public class TypedSecret {
    private SecretType secretType;
    private String secret;

    public String getSecret() {
        return secret;
    }

    public SecretType getSecretType() {
        return secretType;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setSecretType(SecretType secretType) {
        this.secretType = secretType;
    }

}


