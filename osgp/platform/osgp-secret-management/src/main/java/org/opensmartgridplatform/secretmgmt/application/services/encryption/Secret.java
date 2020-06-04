package org.opensmartgridplatform.secretmgmt.application.services.encryption;

import lombok.Getter;

/**
 * Secret class to stores any readable secret as binary data. This can be a password or a key or anything else.
 * There is no encoding/decoding.
 */
@Getter
public class Secret {

    private final byte[] secret;

    public Secret(final byte[] secret) {
        this.secret = secret;
    }
}
