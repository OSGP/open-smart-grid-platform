// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.security.providers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;

@Slf4j
public class HsmEncryptionProvider extends AbstractEncryptionProvider {

  private static final int KEY_LENGTH = 16;
  private static final String ALGORITHM = "AES/CBC/NoPadding";
  private static final String PROVIDER = "nCipherKM";
  private static final String TYPE = "ncipher.sworld";
  private static final byte[] IV = new byte[KEY_LENGTH];

  private final KeyStore keyStore;

  public HsmEncryptionProvider(final File keyStoreFile) {
    try {
      super.setKeyFile(keyStoreFile);
      this.keyStore = KeyStore.getInstance(TYPE, PROVIDER);
      try (final FileInputStream fIn = new FileInputStream(keyStoreFile)) {
        this.keyStore.load(fIn, null);
      }
    } catch (final CertificateException
        | NoSuchAlgorithmException
        | NoSuchProviderException
        | IOException
        | KeyStoreException e) {
      throw new EncrypterException("Could not read keystore", e);
    }
  }

  @Override
  public byte[] decrypt(final EncryptedSecret secret, final String keyReference) {
    log.info("DECRYPTING WITH HSM");
    final byte[] decryptedSecret = super.decrypt(secret, keyReference);
    if (decryptedSecret.length > KEY_LENGTH) {
      // This provider uses NoPadding, but since decrypted byte size is bigger than key byte size,
      // the secrets were apparently encrypted using padding of some kind; truncate the padded
      // bytes.
      final byte[] truncatedDecryptedSecretBytes =
          Arrays.copyOfRange(decryptedSecret, 0, KEY_LENGTH);
      log.trace(
          "Truncating decrypted key from {} to {}",
          Hex.encodeHexString(decryptedSecret),
          Hex.encodeHexString(truncatedDecryptedSecretBytes));
      return truncatedDecryptedSecretBytes;
    }
    return decryptedSecret;
  }

  @Override
  public byte[] generateAes128BitsSecret(final String keyReference) {
    try {
      return this.encrypt(KeyGenerator.getInstance("AES").generateKey().getEncoded(), keyReference)
          .getSecret();
    } catch (final NoSuchAlgorithmException exc) {
      throw new EncrypterException("Could not generate secret", exc);
    }
  }

  @Override
  public int getSecretByteLength() {
    return KEY_LENGTH;
  }

  @Override
  protected Cipher getCipher() {
    try {
      return Cipher.getInstance(ALGORITHM, PROVIDER);
    } catch (final NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException e) {
      throw new EncrypterException("Could not get cipher", e);
    }
  }

  /**
   * This method reads the encryption key specified by keyReference from the Hsm.
   *
   * @return the key that must be used for encryption/decryption
   */
  @Override
  protected Key getSecretEncryptionKey(final String keyReference, final int cipherMode) {
    try {
      return this.keyStore.getKey(keyReference, null);
    } catch (final UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
      throw new EncrypterException("Could not get keystore from key", e);
    }
  }

  @Override
  protected AlgorithmParameterSpec getAlgorithmParameterSpec() {
    return new IvParameterSpec(IV);
  }

  @Override
  public EncryptionProviderType getType() {
    return EncryptionProviderType.HSM;
  }
}
