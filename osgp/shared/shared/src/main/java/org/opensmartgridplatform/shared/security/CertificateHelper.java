/*
 * Copyright 2015 Smart Society Services B.V.
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
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;

/** Helper routines for certificate operations. */
public final class CertificateHelper {

  /** private ctor */
  private CertificateHelper() {}

  /**
   * Create private key from private key file on disk
   *
   * @param keyPath path to key, encoded according to the PKCS #8 standard
   * @param keyType the name of the key algorithm
   * @param provider the name of the provider
   * @return instance of private key
   * @throws EncrypterException when creating the private key results in a NoSuchAlgorithmException,
   *     NoSuchProviderException or InvalidKeySpecException
   * @throws IOException thrown when IO difficulties occur
   */
  public static PrivateKey createPrivateKey(
      final String keyPath, final String keyType, final String provider) throws IOException {

    final byte[] key = readKeyFromDisk(keyPath);
    return createPrivateKey(key, keyType, provider);
  }

  /**
   * Create private key from Base64 text
   *
   * @param keyBase64 Base64 encoded key according to the PKCS #8 standard
   * @param keyType the name of the key algorithm
   * @param provider the name of the provider
   * @return instance of private key
   * @throws EncrypterException when creating the private key results in a NoSuchAlgorithmException,
   *     NoSuchProviderException or InvalidKeySpecException
   */
  public static PrivateKey createPrivateKeyFromBase64(
      final String keyBase64, final String keyType, final String provider) {

    final byte[] key = Base64.decodeBase64(keyBase64);
    return createPrivateKey(key, keyType, provider);
  }

  private static PrivateKey createPrivateKey(
      final byte[] key, final String algorithm, final String provider) {
    try {
      final PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(key);
      KeyFactory privateKeyFactory;
      privateKeyFactory = KeyFactory.getInstance(algorithm, provider);
      return privateKeyFactory.generatePrivate(privateKeySpec);
    } catch (final GeneralSecurityException e) {
      throw new EncrypterException(
          String.format(
              "Security exception creating private key for algorithm \"%s\" by provider \"%s\"",
              algorithm, provider),
          e);
    }
  }

  /**
   * Create public key from public key file on disk
   *
   * @param keyPath path to key, encoded according to the X.509 standard
   * @param keyType the name of the key algorithm
   * @param provider the name of the provider
   * @return instance of public key
   * @throws EncrypterException when creating the public key results in a NoSuchAlgorithmException,
   *     NoSuchProviderException or InvalidKeySpecException
   * @throws IOException thrown when IO difficulties occur
   */
  public static PublicKey createPublicKey(
      final String keyPath, final String keyType, final String provider) throws IOException {

    final byte[] key = readKeyFromDisk(keyPath);
    return createPublicKey(key, keyType, provider);
  }

  /**
   * Create public key from Base64 text
   *
   * @param keyBase64 Base64 encoded key according to the X.509 standard
   * @param keyType the name of the key algorithm
   * @param provider the name of the provider
   * @return instance of private key
   * @throws EncrypterException when creating the private key results in a NoSuchAlgorithmException,
   *     NoSuchProviderException or InvalidKeySpecException
   */
  public static PublicKey createPublicKeyFromBase64(
      final String keyBase64, final String keyType, final String provider) {

    final byte[] key = Base64.decodeBase64(keyBase64);
    return createPublicKey(key, keyType, provider);
  }

  private static PublicKey createPublicKey(
      final byte[] key, final String algorithm, final String provider) {
    try {
      final X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(key);
      final KeyFactory publicKeyFactory = KeyFactory.getInstance(algorithm, provider);
      return publicKeyFactory.generatePublic(publicKeySpec);
    } catch (final GeneralSecurityException e) {
      throw new EncrypterException(
          String.format(
              "Security exception creating public key for algorithm \"%s\" by provider \"%s\"",
              algorithm, provider),
          e);
    }
  }

  /**
   * Read certificate bytes from disk
   *
   * @return bytes of key
   * @throws IOException
   */
  private static byte[] readKeyFromDisk(final String keyPath) throws IOException {
    return Files.readAllBytes(new File(keyPath).toPath());
  }
}
