/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.security.providers;

import java.io.File;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;

public abstract class AbstractEncryptionProvider implements EncryptionProvider {

  private static final int BLOCK_SIZE = 16;

  protected File keyFile;

  @Override
  public abstract EncryptionProviderType getType();

  protected abstract Cipher getCipher();

  protected abstract AlgorithmParameterSpec getAlgorithmParameterSpec();

  protected abstract Key getSecretEncryptionKey(String keyReference, int cipherMode);

  protected void setKeyFile(final File keyFile) {
    this.keyFile = keyFile;
  }

  @Override
  public EncryptedSecret encrypt(final byte[] secret, final String keyReference) {
    try {
      final Cipher cipher = this.getCipher();
      cipher.init(
          Cipher.ENCRYPT_MODE,
          this.getSecretEncryptionKey(keyReference, Cipher.ENCRYPT_MODE),
          this.getAlgorithmParameterSpec());
      return new EncryptedSecret(this.getType(), cipher.doFinal(secret));
    } catch (final Exception e) {
      throw new EncrypterException("Could not encrypt secret with keyReference " + keyReference, e);
    }
  }

  @Override
  public byte[] decrypt(final EncryptedSecret secret, final String keyReference) {

    if (secret.getType() != this.getType()) {
      throw new EncrypterException(
          "EncryptionProvider for type "
              + this.getType().name()
              + " cannot decrypt secrets of type "
              + secret.getType().name());
    }

    try {
      final Cipher cipher = this.getCipher();
      cipher.init(
          Cipher.DECRYPT_MODE,
          this.getSecretEncryptionKey(keyReference, Cipher.DECRYPT_MODE),
          this.getAlgorithmParameterSpec());
      final byte[] decryptedData = cipher.doFinal(secret.getSecret());
      if (this.checkNullBytesPrepended(decryptedData)) {
        return Arrays.copyOfRange(decryptedData, BLOCK_SIZE, decryptedData.length);
      } else {
        return decryptedData;
      }
    } catch (final Exception e) {
      throw new EncrypterException("Could not decrypt secret with keyReference " + keyReference, e);
    }
  }

  private boolean checkNullBytesPrepended(final byte[] bytes) {
    if (bytes.length > BLOCK_SIZE) {
      for (short s = 0; s < BLOCK_SIZE; s++) {
        if (bytes[s] != 0) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
