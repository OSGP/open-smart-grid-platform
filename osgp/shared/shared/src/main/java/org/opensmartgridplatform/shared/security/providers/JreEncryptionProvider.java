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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;

public class JreEncryptionProvider extends AbstractEncryptionProvider {

  private static final String DEFAULT_SINGLE_KEY_REFERENCE = "1";
  private static final String ALG = "AES";
  private static final String ALGORITHM = "AES/CBC/NoPADDING";
  private static final String PROVIDER = "SunJCE";
  private static final String FORMAT = "RAW";
  private static final byte[] IV = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
  private static final int KEY_LENGTH = 16;

  private final byte[] key;

  public JreEncryptionProvider(final File keyStoreFile) {
    try {
      super.setKeyFile(keyStoreFile);
      this.key = Files.readAllBytes(Paths.get(keyStoreFile.getAbsolutePath()));
    } catch (final IOException e) {
      throw new EncrypterException("Could not read keystore", e);
    }
  }

  @Override
  protected Cipher getCipher() {
    try {
      return Cipher.getInstance(ALGORITHM, PROVIDER);
    } catch (final NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException e) {
      throw new EncrypterException("Could not get cipher", e);
    }
  }

  @Override
  protected Key getSecretEncryptionKey(final String keyReference, final int cipherMode) {

    if (!keyReference.equals(DEFAULT_SINGLE_KEY_REFERENCE)) {
      throw new EncrypterException("Only keyReference '1' is valid in this implementation.");
    }

    return new SecretKey() {
      private static final long serialVersionUID = 4555243342661334965L;

      @Override
      public String getAlgorithm() {
        return ALG;
      }

      @Override
      public String getFormat() {
        return FORMAT;
      }

      @Override
      public byte[] getEncoded() {
        return JreEncryptionProvider.this.key;
      }
    };
  }

  @Override
  protected AlgorithmParameterSpec getAlgorithmParameterSpec() {
    return new IvParameterSpec(IV);
  }

  @Override
  public byte[] generateAes128BitsSecret(final String keyReference) {
    try {
      final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(KEY_LENGTH * 8);
      return this.encrypt(keyGenerator.generateKey().getEncoded(), keyReference).getSecret();
    } catch (final NoSuchAlgorithmException exc) {
      throw new EncrypterException("Could not generate secret", exc);
    }
  }

  @Override
  public EncryptionProviderType getType() {
    return EncryptionProviderType.JRE;
  }

  @Override
  public int getSecretByteLength() {
    return KEY_LENGTH;
  }
}
