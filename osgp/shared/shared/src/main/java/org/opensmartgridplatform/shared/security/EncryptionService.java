/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Encryption service class that offers encrypt and decrypt methods to encrypt or decrypt data. You
 * can use this service as a bean, for example by including its package in component scan. This
 * service uses a property encryption.key.path that should point to a File containing the secret
 * key. When encrypting apart from this service always use {@link #getIvbytes()}.
 */
@Component
public class EncryptionService {
  /** the algorithm used to load the secret key */
  public static final String SECRET_KEY_SPEC = "AES";

  private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionService.class);
  /** the algorithm used for reading the secret key */
  public static final String ALGORITHM = "AES/CBC/PKCS5PADDING";
  /** the id of the provider used */
  public static final String PROVIDER = "SunJCE";

  @Value("${encryption.key.path:#{null}}}")
  private String keyPath;

  private SecretKey key;

  private static final byte[] IVBYTES = new byte[16];

  /**
   * for testability
   *
   * @param key A SecretKeySpec instance
   */
  protected EncryptionService(final SecretKeySpec key) {
    this.key = key;
  }

  public EncryptionService() {
    // Default constructor.
  }

  /*
   * initialization of the IVBYTES used for encryption and decryption clients
   * (encryptors) have to use these ivBytes when encrypting, for example:
   * openssl enc -e -aes-128-cbc ..... -iv 000102030405060708090a0b0c0d0e0f
   */
  static {
    for (short s = 0; s < IVBYTES.length; s++) {
      IVBYTES[s] = (byte) s;
    }
  }

  @PostConstruct
  private void initEncryption() throws FunctionalException {
    if (this.key == null) {
      LOGGER.info("This EncryptionService not initialized since secret is not specified.");
      return;
    }

    try {
      this.key =
          new SecretKeySpec(Files.readAllBytes(new File(this.keyPath).toPath()), SECRET_KEY_SPEC);
    } catch (final IOException e) {
      final String errorMessage =
          String.format("Unexpected exception when reading keys. Key Path: %s", this.keyPath);
      LOGGER.error(errorMessage);

      throw new FunctionalException(
          FunctionalExceptionType.READING_KEY_EXCEPTION, ComponentType.SHARED, e);
    }
  }

  /* package private */
  EncryptionService withSecretKeyAt(final String keyPath) throws FunctionalException {
    this.keyPath = keyPath;
    this.initEncryption();
    return this;
  }

  /**
   * Decrypts the data using the key, Strips off iv bytes when they are there (first 16 0 bytes).
   *
   * @param inputData the data to be decrypted
   * @return the decrypted version of the inputData
   * @throws FunctionalException in case of a NoSuchAlgorithmException, NoSuchPaddingException,
   *     InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
   *     NoSuchProviderException or InvalidAlgorithmParameterException
   */
  public byte[] decrypt(final byte[] inputData) throws FunctionalException {

    try {
      final Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
      cipher.init(Cipher.DECRYPT_MODE, this.key, new IvParameterSpec(IVBYTES));
      final byte[] decryptedData = cipher.doFinal(inputData);
      if (this.checkNullBytesPrepended(decryptedData)) {
        return Arrays.copyOfRange(decryptedData, IVBYTES.length, decryptedData.length);
      } else {
        return decryptedData;
      }
    } catch (final NoSuchAlgorithmException
        | NoSuchPaddingException
        | InvalidKeyException
        | IllegalBlockSizeException
        | BadPaddingException
        | NoSuchProviderException
        | InvalidAlgorithmParameterException e) {
      LOGGER.error("Unexpected exception during decryption", e);

      throw new FunctionalException(
          FunctionalExceptionType.DECRYPTION_EXCEPTION, ComponentType.SHARED, e);
    }
  }

  /**
   *
   *
   * <pre>
   *         - When aes decrypting data (both Java / bouncy castle and openssl) sometimes 16 0 bytes are prepended.
   *         - Possibly this has to do with padding during encryption
   *         - openssl as well as Java / bouncy castle don't prefix iv bytes when aes encrypting data (seen in junit test and commandline)
   *         - makeSimulatorKey.sh (device simulator) assumes decrypted data are prepended with 0 bytes, at present this is correct
   * </pre>
   *
   * @param bytes the array to check
   * @return true if the array is prepended with 0 bytes, false otherwise
   */
  private boolean checkNullBytesPrepended(final byte[] bytes) {
    if (bytes.length > IVBYTES.length) {
      boolean nullBytesPrepended = false;
      for (short s = 0; s < IVBYTES.length; s++) {
        if (bytes[s] == 0) {
          nullBytesPrepended = true;
        } else {
          return false;
        }
      }
      return nullBytesPrepended;
    } else {
      return false;
    }
  }

  /**
   * Encrypts the data using the key
   *
   * @param inputData the data to be encrypted
   * @return the encrypted version of the inputData
   * @throws FunctionalException in case of a NoSuchAlgorithmException, NoSuchPaddingException,
   *     InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
   *     NoSuchProviderException or InvalidAlgorithmParameterException
   */
  public byte[] encrypt(final byte[] inputData) throws FunctionalException {
    try {
      final Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
      cipher.init(Cipher.ENCRYPT_MODE, this.key, new IvParameterSpec(IVBYTES));
      return cipher.doFinal(inputData);
    } catch (final NoSuchAlgorithmException
        | NoSuchPaddingException
        | InvalidKeyException
        | IllegalBlockSizeException
        | BadPaddingException
        | NoSuchProviderException
        | InvalidAlgorithmParameterException e) {
      LOGGER.error("Unexpected exception during encryption", e);

      throw new FunctionalException(
          FunctionalExceptionType.ENCRYPTION_EXCEPTION, ComponentType.SHARED, e);
    }
  }

  public static byte[] getIvbytes() {
    return Arrays.copyOf(IVBYTES, IVBYTES.length);
  }
}
