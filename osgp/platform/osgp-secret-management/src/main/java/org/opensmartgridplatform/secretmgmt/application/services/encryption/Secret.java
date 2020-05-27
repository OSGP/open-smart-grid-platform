package org.opensmartgridplatform.secretmgmt.application.services.encryption;

/**
 * Secret class to stores any readable secret as binary data. This can be a password or a key or anything else.
 * There is no encoding/decoding.
 */
public class Secret {

    private byte[] secret;

    public Secret(byte[] secret) {
        this.secret = secret;
    }
    public byte[] getSecret() {
        return secret;
    }

}
