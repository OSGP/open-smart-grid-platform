// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.security;

public interface EncryptionDelegate {
  EncryptedSecret encrypt(
      EncryptionProviderType encryptionProviderType, byte[] secret, String keyReference);

  byte[] decrypt(EncryptedSecret secret, String keyReference);

  byte[] generateAes128BitsSecret(
      EncryptionProviderType encryptionProviderType, String keyReference);

  int getSecretByteLength(EncryptionProviderType encryptionProviderType);
}
