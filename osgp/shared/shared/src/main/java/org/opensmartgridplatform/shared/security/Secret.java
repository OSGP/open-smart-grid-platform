/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.security;

/**
 * Secret class to stores any readable secret as binary data. This can be a password or a key or anything else.
 * There is no encoding/decoding.
 */
public class Secret {

    private final byte[] secretBytes;

    public Secret(final byte[] secretBytes) {
        this.secretBytes = secretBytes;
    }

    public byte[] getSecret() {
        return secretBytes;
    }
}
