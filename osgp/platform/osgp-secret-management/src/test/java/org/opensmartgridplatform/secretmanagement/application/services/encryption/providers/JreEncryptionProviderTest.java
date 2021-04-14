/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services.encryption.providers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    String path = "src/test/resources/osgp-secret-management-db.key";
    File keyFile = new File(path);
    this.jreEncryptionProvider = new JreEncryptionProvider(keyFile);
  }

  @Test
  public void identityTest() throws EncrypterException {
    final byte[] secret = HexUtils.fromHexString(this.secretString);
    EncryptedSecret encryptedSecret = this.jreEncryptionProvider.encrypt(secret, JRE_KEY_REF);
    String encryptedSecretAsString = HexUtils.toHexString(encryptedSecret.getSecret());

    assertEquals(
        "f2edbdc2ad1dab1458f1b866c5a5e6a68873d5738b3742bf3fa5d673133313b6",
        encryptedSecretAsString);

    byte[] decryptedSecret = this.jreEncryptionProvider.decrypt(encryptedSecret, JRE_KEY_REF);
    String decryptedSecretAsString = HexUtils.toHexString(decryptedSecret);

    assertEquals(this.secretString, decryptedSecretAsString);
  }

  @Test
  public void doErrorTest() throws EncrypterException {
    byte[] secret = HexUtils.fromHexString("00000000000000000000000000000000");

    EncryptedSecret encryptedSecret =
        new EncryptedSecret(this.jreEncryptionProvider.getType(), secret);

    assertThrows(
        EncrypterException.class,
        () -> this.jreEncryptionProvider.decrypt(encryptedSecret, this.JRE_KEY_REF),
        "Expected decrypt() to throw javax.crypto.BadPaddingException, but it didn't");
  }

  @Test
  public void generateKeyAndCheckLengths() {
    byte[] encryptedSecretBytes = this.jreEncryptionProvider.generateAes128BitsSecret(JRE_KEY_REF);
    EncryptedSecret encryptedSecret =
        new EncryptedSecret(this.jreEncryptionProvider.getType(), encryptedSecretBytes);
    byte[] unencryptedSecretBytes =
        this.jreEncryptionProvider.decrypt(encryptedSecret, JRE_KEY_REF);
    String encryptedSecretAsString = HexUtils.toHexString(encryptedSecretBytes);
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
    byte[] secret = {0, 1, 2, 4, 8, 16, 32, 64, 127, 63, 31, 15, 7, 3, 0, 0};
    assertEquals(16, secret.length);
    // Create AES key
    KeyGenerator kgen = KeyGenerator.getInstance("AES");
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    sr.setSeed(System.currentTimeMillis());
    kgen.init(blockSize, sr);
    SecretKey key = kgen.generateKey();
    SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
    String[] algorithms = {
      "AES/CBC/NoPadding" /*HSM uses this*/, "AES/CBC/PKCS5PADDING" /*JRE uses this*/
    };
    int[][] runs = {{0, 0}, {1, 1}, {1, 0}, {0, 1}}; // Used as index of algorithms array
    int[] expectedErrorRun = runs[3];
    for (int[] run : runs) {
      String encAlg = algorithms[run[0]];
      String decAlg = algorithms[run[1]];
      // Encrypt secret
      Cipher cipher = Cipher.getInstance(encAlg);
      AlgorithmParameterSpec spec = new IvParameterSpec(new byte[blockByteSize]);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
      byte[] encrypted = cipher.doFinal(secret);
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
        boolean truncateNeeded = decrypted.length > secret.length;
        if (truncateNeeded) {
          decrypted = Arrays.copyOfRange(decrypted, 0, secret.length);
        }
        assertArrayEquals(secret, decrypted);
      } catch (BadPaddingException bpe) {
        assertArrayEquals(expectedErrorRun, run);
      }
    }
  }
}
