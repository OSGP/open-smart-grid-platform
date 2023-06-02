//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;

// This class should be merged with RsaEncryptionService; it is almost the same except for different
// configuration
//  refactor it to 1 single configurable class with 2 instances with different configurations
public class RsaEncrypter {
  private static final int BLOCK_SIZE = 16;
  private static final String ALG = "RSA";
  private static final String ALGORITHM = "RSA/ECB/OAEPPadding";

  private Key publicKey;
  private Key privateKey;

  public void setPrivateKeyStore(final File privateKeyStoreFile) {
    try {
      final byte[] keyData = Files.readAllBytes(privateKeyStoreFile.toPath());
      final PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(keyData);
      this.privateKey = KeyFactory.getInstance(ALG).generatePrivate(privateKeySpec);
    } catch (final NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
      throw new EncrypterException("Could not get cipher", e);
    }
  }

  public void setPublicKeyStore(final File publicKeyStoreFile) {
    try {
      final byte[] keyData = Files.readAllBytes(publicKeyStoreFile.toPath());
      final X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyData);
      this.publicKey = KeyFactory.getInstance(ALG).generatePublic(publicKeySpec);
    } catch (final NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
      throw new EncrypterException("Could not set public keystore", e);
    }
  }

  // Suppress warning regarding algorithm security risks, because this particular algorithm is
  // required to succesfully communicate with other system components
  @SuppressWarnings("squid:S5542")
  protected Cipher getCipher() {
    try {
      return Cipher.getInstance(ALGORITHM);
    } catch (final NoSuchPaddingException | NoSuchAlgorithmException e) {
      throw new EncrypterException("Could not get cipher", e);
    }
  }

  protected Key getSecretEncryptionKey(final int cipherMode) {
    return cipherMode == Cipher.ENCRYPT_MODE ? this.publicKey : this.privateKey;
  }

  public byte[] encrypt(final byte[] secret) {
    if (secret == null) {
      throw new IllegalArgumentException("Can not encrypt NULL value");
    }
    try {
      final Cipher cipher = this.getCipher();
      cipher.init(Cipher.ENCRYPT_MODE, this.getSecretEncryptionKey(Cipher.ENCRYPT_MODE));
      return cipher.doFinal(secret);
    } catch (final Exception e) {
      throw new EncrypterException("Could not encrypt secret", e);
    }
  }

  public byte[] decrypt(final byte[] rsaEncrypted) {
    if (rsaEncrypted == null) {
      throw new IllegalArgumentException("Can not decrypt NULL value");
    }
    try {
      final Cipher cipher = this.getCipher();
      cipher.init(Cipher.DECRYPT_MODE, this.getSecretEncryptionKey(Cipher.DECRYPT_MODE));
      final byte[] decryptedData = cipher.doFinal(rsaEncrypted);

      if (this.checkNullBytesPrepended(decryptedData)) {
        return Arrays.copyOfRange(decryptedData, BLOCK_SIZE, decryptedData.length);
      } else {
        return decryptedData;
      }
    } catch (final Exception e) {
      throw new EncrypterException("Could not decrypt secret", e);
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
