/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class EncryptionServiceTest {

  private static final String SRC_TEST_RESOURCES_SECRET = "src/test/resources/secret";
  private static final String TEST_CONTENT = "content to encrypt and decrypt";

  private static class TestableEncService extends EncryptionService {
    protected TestableEncService(final SecretKeySpec key) {
      super(key);
    }
  }

  private static final String PROVIDER = EncryptionService.PROVIDER;
  private static final String AES = EncryptionService.SECRET_KEY_SPEC;

  /** Secret key used to encrypt/decrypt other keys. */
  private static final String SRC_TEST_RESOURCES_KEYS_SECRET_AES =
      "src/test/resources/keys/secret.aes";

  /*
   * Authentication key in encrypted string format and decrypted binary
   * format.
   */
  private static final String AUTH_KEY_ENCRYPTED_STRING =
      "c19fe80a22a0f6c5cdaad0826c4d204f23694ded08d811b66e9b845d9f2157d2";
  private static final String SRC_TEST_RESOURCES_KEYS_AUTH_KEY_DECRYPTED =
      "src/test/resources/keys/authkeydecrypted";

  /*
   * Encryption key in encrypted string format and decrypted binary format.
   */
  private static final String ENC_KEY_ENCRYPTED_STRING =
      "867424ac75b6d53c89276d304608321f0a1f6e401f453f84adf3477c7ee1623c";
  private static final String SRC_TEST_RESOURCES_KEYS_ENC_KEY_DECRYPTED =
      "src/test/resources/keys/enckeydecrypted";

  /*
   * Master key in encrypted string format and decrypted binary format.
   */
  private static final String MASTER_KEY_ENCRYPTED_STRING =
      "55dc88791e6c8f6aff4c8be7714fb8d2ae3d02693ec474593acd3523ee032638";
  private static final String SRC_TEST_RESOURCES_KEYS_MASTER_KEY_DECRYPTED =
      "src/test/resources/keys/masterkeydecrypted";

  /**
   * Test decryption of master, encryption and authentication keys using 'secret.aes'.
   *
   * @throws FunctionalException
   */
  @Test
  public void testKeyDecryption()
      throws NoSuchAlgorithmException, IOException, NoSuchProviderException, DecoderException,
          FunctionalException {
    // Load the secret key.
    final SecretKeySpec secretKey = this.createSecretKeySpec(SRC_TEST_RESOURCES_KEYS_SECRET_AES);

    // Test the authentication key.
    this.decryptKeyTest(
        secretKey,
        AUTH_KEY_ENCRYPTED_STRING,
        SRC_TEST_RESOURCES_KEYS_AUTH_KEY_DECRYPTED,
        "Authentication Key decryption failed");

    // Test the encryption key.
    this.decryptKeyTest(
        secretKey,
        ENC_KEY_ENCRYPTED_STRING,
        SRC_TEST_RESOURCES_KEYS_ENC_KEY_DECRYPTED,
        "Encryption Key decryption failed");

    // Test the master key.
    this.decryptKeyTest(
        secretKey,
        MASTER_KEY_ENCRYPTED_STRING,
        SRC_TEST_RESOURCES_KEYS_MASTER_KEY_DECRYPTED,
        "Master Key decryption failed");
  }

  private void decryptKeyTest(
      final SecretKeySpec secretKey,
      final String encryptedKey,
      final String originalKeyFilePath,
      final String assertMsg)
      throws DecoderException, IOException, FunctionalException {
    // Try to decrypt the encrypted string key.
    final byte[] decryptedKeyBytes =
        new TestableEncService(secretKey).decrypt(Hex.decodeHex(encryptedKey.toCharArray()));

    // Load the original key and get the bytes.
    final SecretKeySpec originalKey = this.createSecretKeySpec(originalKeyFilePath);
    final byte[] originalKeyBytes = originalKey.getEncoded();

    // Check if the decrypted key matches the original key.
    assertThat(decryptedKeyBytes).withFailMessage(assertMsg).isEqualTo(originalKeyBytes);
  }

  @Test
  public void testEnDecrypt()
      throws NoSuchAlgorithmException, IOException, NoSuchProviderException, FunctionalException {
    final KeyGenerator keygen = KeyGenerator.getInstance(AES, PROVIDER);
    keygen.init(128);
    final SecretKey key = keygen.generateKey();
    final byte[] keyBytes = key.getEncoded();

    final SecretKeySpec secretKey = this.createSecretKeySpec(keyBytes);
    final EncryptionService encryptionService = new TestableEncService(secretKey);

    final byte[] encrypted = encryptionService.encrypt(TEST_CONTENT.getBytes());
    final byte[] decrypted = encryptionService.decrypt(encrypted);
    assertThat(new String(decrypted)).isEqualTo(TEST_CONTENT);
  }

  @Test
  public void testEnDecryptDifferentService()
      throws NoSuchAlgorithmException, IOException, NoSuchProviderException, FunctionalException {
    final KeyGenerator keygen = KeyGenerator.getInstance(AES, PROVIDER);
    keygen.init(128);
    final SecretKey key = keygen.generateKey();
    final byte[] keyBytes = key.getEncoded();

    final SecretKeySpec secretKey = this.createSecretKeySpec(keyBytes);
    final byte[] enc = new TestableEncService(secretKey).encrypt(TEST_CONTENT.getBytes());

    assertThat(new String(new TestableEncService(secretKey).decrypt(enc))).isEqualTo(TEST_CONTENT);
  }

  @Test
  public void testOpenSslSecret()
      throws NoSuchAlgorithmException, IOException, NoSuchProviderException, FunctionalException {
    final SecretKeySpec secretKey = this.createSecretKeySpec(new File(SRC_TEST_RESOURCES_SECRET));
    final byte[] enc = new TestableEncService(secretKey).encrypt(TEST_CONTENT.getBytes());

    assertThat(new String(new TestableEncService(secretKey).decrypt(enc))).isEqualTo(TEST_CONTENT);
  }

  @Test
  public void testOpenSslEncrypted()
      throws NoSuchAlgorithmException, IOException, NoSuchProviderException, FunctionalException {
    final byte[] encrypted = Files.readAllBytes(new File("src/test/resources/plain.enc").toPath());
    final SecretKeySpec secretKey =
        this.createSecretKeySpec(new File(SRC_TEST_RESOURCES_SECRET).getPath());
    final byte[] decrypted = new TestableEncService(secretKey).decrypt(encrypted);

    assertThat(new String(decrypted)).isEqualTo("hallo");
  }

  @Test
  public void testPrepended()
      throws NoSuchAlgorithmException, IOException, NoSuchProviderException, FunctionalException {
    final byte[] encrypted_prepended =
        Files.readAllBytes(new File("src/test/resources/prepended").toPath());

    final SecretKeySpec secretKey =
        this.createSecretKeySpec(new File(SRC_TEST_RESOURCES_SECRET).getPath());
    final byte[] decrypted_prepended =
        new TestableEncService(secretKey).decrypt(encrypted_prepended);

    // in this specific case the length of the decrypted bytes should become
    // 16, after 0 bytes are stripped of
    assertThat(decrypted_prepended.length).isEqualTo(16);
  }

  private SecretKeySpec createSecretKeySpec(final byte[] bytes) throws IOException {
    return new SecretKeySpec(bytes, EncryptionService.SECRET_KEY_SPEC);
  }

  private SecretKeySpec createSecretKeySpec(final String filePath) throws IOException {
    return new SecretKeySpec(
        Files.readAllBytes(new File(filePath).toPath()), EncryptionService.SECRET_KEY_SPEC);
  }

  private SecretKeySpec createSecretKeySpec(final File file) throws IOException {
    return new SecretKeySpec(Files.readAllBytes(file.toPath()), EncryptionService.SECRET_KEY_SPEC);
  }
}
