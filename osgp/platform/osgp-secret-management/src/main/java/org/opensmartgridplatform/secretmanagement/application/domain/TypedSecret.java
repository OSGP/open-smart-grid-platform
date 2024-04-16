// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.secretmanagement.application.domain;

import java.util.Arrays;
import lombok.Getter;

/** TypedSecret stores a secret (not necessarily an encrypted secret), along with it's type. */
@Getter
public class TypedSecret {
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
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

  public String getSecretAsHexString() {
    final byte[] bytes = this.getSecret();
    final char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      final int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }
}
