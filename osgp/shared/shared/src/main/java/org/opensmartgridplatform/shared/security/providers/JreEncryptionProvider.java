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
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;

public class JreEncryptionProvider implements EncryptionProvider {

  private static final String DEFAULT_SINGLE_KEY_REFERENCE = "1";
  private static final String ALG = "AES";
  private static final String ALGORITHM = "AES/GCM/NoPadding";
  private static final String PROVIDER = "SunJCE";
  private static final String FORMAT = "RAW";
  private static final int KEY_LENGTH = 16;
  private static final int GCM_IV_LENGTH = 16;

  private final byte[] key;

  private final SecureRandom secureRandom = new SecureRandom();

  public JreEncryptionProvider(final File keyStoreFile) {
    try {
      this.key = Files.readAllBytes(Paths.get(keyStoreFile.getAbsolutePath()));
    } catch (final IOException e) {
      throw new EncrypterException("Could not read keystore", e);
    }
  }

  public JreEncryptionProvider(final byte[] keyStore) {
    this.key = keyStore;
  }

  private Cipher getCipher() {
    try {
      return Cipher.getInstance(ALGORITHM, PROVIDER);
    } catch (final NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException e) {
      throw new EncrypterException("Could not get cipher", e);
    }
  }

  private Key getSecretEncryptionKey(final String keyReference) {

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
  public byte[] generateAes128BitsSecret(final String keyReference) {
    try {
      final KeyGenerator keyGenerator = KeyGenerator.getInstance(ALG);
      keyGenerator.init(KEY_LENGTH * 8);
      return this.encrypt(keyGenerator.generateKey().getEncoded(), keyReference).getSecret();
    } catch (final NoSuchAlgorithmException e) {
      throw new EncrypterException("Could not generate secret", e);
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

  @Override
  public EncryptedSecret encrypt(final byte[] secret, final String keyReference) {
    try {
      final Cipher cipher = this.getCipher();

      final byte[] iv = new byte[GCM_IV_LENGTH]; // NEVER REUSE THIS IV WITH SAME KEY
      this.secureRandom.nextBytes(iv);

      final GCMParameterSpec parameterSpec = new GCMParameterSpec(KEY_LENGTH * 8, iv);

      cipher.init(Cipher.ENCRYPT_MODE, this.getSecretEncryptionKey(keyReference), parameterSpec);

      final byte[] bytes = cipher.doFinal(secret);
      final ByteBuffer byteBuffer = ByteBuffer.allocate(GCM_IV_LENGTH + bytes.length);
      byteBuffer.put(iv);
      byteBuffer.put(bytes);

      return new EncryptedSecret(this.getType(), byteBuffer.array());

    } catch (final Exception e) {
      throw new EncrypterException("Could not encrypt secret with keyReference " + keyReference, e);
    }
  }

  @Override
  public byte[] decrypt(final EncryptedSecret secret, final String keyReference) {

    if (secret.getType() != this.getType()) {
      throw new EncrypterException(
          String.format(
              "EncryptionProvider for type %s cannot decrypt secrets of type %s",
              this.getType().name(), secret.getType().name()));
    }

    try {
      final Cipher cipher = this.getCipher();
      final AlgorithmParameterSpec gcmIv =
          new GCMParameterSpec(KEY_LENGTH * 8, secret.getSecret(), 0, GCM_IV_LENGTH);
      cipher.init(Cipher.DECRYPT_MODE, this.getSecretEncryptionKey(keyReference), gcmIv);

      return cipher.doFinal(
          secret.getSecret(), GCM_IV_LENGTH, secret.getSecret().length - GCM_IV_LENGTH);

    } catch (final Exception e) {
      throw new EncrypterException("Could not decrypt secret with keyReference " + keyReference, e);
    }
  }
}
