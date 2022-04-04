/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services.encryption.providers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.providers.JreEncryptionProvider;

@ExtendWith(MockitoExtension.class)
public class JreEncryptionProviderTest {
  private static final String JRE_KEY_REF = "1";
  private final String secretString = "5b3a65ba2a7d347f1eedf7fab25f2813";

  private JreEncryptionProvider jreEncryptionProvider;

  @BeforeEach
  public void setUp() {
    final String path = "src/test/resources/osgp-secret-management-db.key";
    final File keyFile = new File(path);
    this.jreEncryptionProvider = new JreEncryptionProvider(keyFile);
  }

  @Test
  public void identityTest() throws EncrypterException {
    final byte[] secret = HexUtils.fromHexString(this.secretString);
    final EncryptedSecret encryptedSecret = this.jreEncryptionProvider.encrypt(secret, JRE_KEY_REF);
    final String encryptedSecretAsString = HexUtils.toHexString(encryptedSecret.getSecret());

    assertEquals(
        "f7bd9d697a0daa8cdfc3ae50dec3caede18e017aa2b4944efc89da23d2aece18",
        encryptedSecretAsString);

    final byte[] decryptedSecret = this.jreEncryptionProvider.decrypt(encryptedSecret, JRE_KEY_REF);
    final String decryptedSecretAsString = HexUtils.toHexString(decryptedSecret);

    assertEquals(this.secretString, decryptedSecretAsString);
  }

  @Test
  public void generateKeyAndCheckLengths() {
    final byte[] encryptedSecretBytes =
        this.jreEncryptionProvider.generateAes128BitsSecret(JRE_KEY_REF);
    final EncryptedSecret encryptedSecret =
        new EncryptedSecret(this.jreEncryptionProvider.getType(), encryptedSecretBytes);
    final byte[] unencryptedSecretBytes =
        this.jreEncryptionProvider.decrypt(encryptedSecret, JRE_KEY_REF);
    final String encryptedSecretAsString = HexUtils.toHexString(encryptedSecretBytes);
    assertEquals(16, unencryptedSecretBytes.length);
    assertEquals(32, encryptedSecretBytes.length);
    assertEquals(64, encryptedSecretAsString.length());
  }

  /**
   * This test is created more for documentation than for testing purposes. It does not even test
   * JreEncryptionProvider, but serves as an explanation for how different combinations of
   * encryption paddings works in practice. Bottom-line is that NoPadding AES encryption leads to
   * secret-size (16 byte) encrypted secrets and PKCS5Padding AES encryption leads to secret-size +
   * 1 block (32 byte) encrypted secrets. Decrypting a PKCS5Padded secret using NoPadding results in
   * a 32-byte decrypted secret, which can be truncated to obtain the original secret. Conversely,
   * decrypting a NoPadding secret using PKCS5Padding results in a BadPaddingException.
   *
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws InvalidAlgorithmParameterException
   */
  @Test
  public void testPaddingAndKeySizes()
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
          BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
    final int blockSize = 128;
    final int blockByteSize = 128 / 8;
    // Create secret
    final byte[] secret = {0, 1, 2, 4, 8, 16, 32, 64, 127, 63, 31, 15, 7, 3, 0, 0};
    assertEquals(16, secret.length);
    // Create AES key
    final KeyGenerator kgen = KeyGenerator.getInstance("AES");
    final SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    sr.setSeed(System.currentTimeMillis());
    kgen.init(blockSize, sr);
    final SecretKey key = kgen.generateKey();
    final SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
    final String[] algorithms = {
      "AES/CBC/NoPadding" /*HSM uses this*/, "AES/CBC/PKCS5PADDING" /*JRE uses this*/
    };
    final int[][] runs = {{0, 0}, {1, 1}, {1, 0}, {0, 1}}; // Used as index of algorithms array
    final int[] expectedErrorRun = runs[3];
    for (final int[] run : runs) {
      final String encAlg = algorithms[run[0]];
      final String decAlg = algorithms[run[1]];
      // Encrypt secret
      Cipher cipher = Cipher.getInstance(encAlg);
      final AlgorithmParameterSpec spec = new IvParameterSpec(new byte[blockByteSize]);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
      final byte[] encrypted = cipher.doFinal(secret);
      assertEquals(
          encAlg.equals(algorithms[0]) ? secret.length : secret.length + blockByteSize,
          encrypted.length);
      // Decrypt secret
      cipher = Cipher.getInstance(decAlg);
      cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
      try {
        byte[] decrypted = cipher.doFinal(encrypted);
        assertFalse(Arrays.equals(run, expectedErrorRun));
        assertEquals(
            encAlg.equals(decAlg) ? secret.length : secret.length + blockByteSize,
            decrypted.length);
        final boolean truncateNeeded = decrypted.length > secret.length;
        if (truncateNeeded) {
          decrypted = Arrays.copyOfRange(decrypted, 0, secret.length);
        }
        assertArrayEquals(secret, decrypted);
      } catch (final BadPaddingException bpe) {
        assertArrayEquals(expectedErrorRun, run);
      }
    }
  }
}
