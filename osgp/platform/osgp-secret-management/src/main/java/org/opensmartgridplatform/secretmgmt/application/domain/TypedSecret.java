package org.opensmartgridplatform.secretmgmt.application.domain;

/**
 * TypedSecret stores HexEncoded secrets
 */
public class TypedSecret {
    public SecretType  secretType;
    public String      secret;

    public TypedSecret() {}

    public SecretType getSecretType() {
        return this.secretType;
    }

    public void setSecretType(SecretType secretType) {
        this.secretType = secretType;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}


