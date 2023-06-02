//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.security;

/**
 * Secret class to store any binary encrypted secret. The secret can be anything. A secret contains
 * the type of encryption provider that was used to encrypt it. There is no encoding/decoding.
 */
public class EncryptedSecret {
  private final byte[] secret;
  private final EncryptionProviderType type;

  public EncryptedSecret(final EncryptionProviderType type, final byte[] secretBytes) {
    this.type = type;
    this.secret = secretBytes;
  }

  public EncryptionProviderType getType() {
    return this.type;
  }

  public byte[] getSecret() {
    return this.secret;
  }
}
