/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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

//TODO merge with RsaEncryptionService; it is almost the same except for different configuration
//  refactor it to 1 single configurable class with 2 instances with different configurations
public class RsaEncrypter {
    private static final int BLOCK_SIZE = 16;
    private static final String ALG = "RSA";
    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

    //protected File keyFile;
    private Key publicKey;
    private Key privateKey;

    public void setPrivateKeyStore(File privateKeyStoreFile) throws EncrypterException {
        try {
            byte[] keyData = Files.readAllBytes(privateKeyStoreFile.toPath());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(keyData);
            this.privateKey = KeyFactory.getInstance(ALG).generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            throw new EncrypterException("Could not get cipher", e);
        }
        //this.setKeyFile(privateKeyStoreFile);
    }

    //protected void setKeyFile(final File keyFile) {
    //    this.keyFile = keyFile;
    //}

    public void setPublicKeyStore(File publicKeyStoreFile) throws EncrypterException {
        try {
            byte[] keyData = Files.readAllBytes(publicKeyStoreFile.toPath());
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyData);
            this.publicKey = KeyFactory.getInstance(ALG).generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            throw new EncrypterException("Could not set public keystore", e);
        }
    }

    protected Cipher getCipher() throws EncrypterException {
        try {
            return Cipher.getInstance(ALGORITHM);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new EncrypterException("Could not get cipher", e);
        }
    }

    protected Key getSecretEncryptionKey(int cipherMode) {
        return cipherMode == Cipher.ENCRYPT_MODE ? this.publicKey : this.privateKey;
    }

    public byte[] encrypt(final byte[] secret) throws EncrypterException {
        try {
            final Cipher cipher = this.getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, this.getSecretEncryptionKey(Cipher.ENCRYPT_MODE));
            byte[] rsa = cipher.doFinal(secret);
            return rsa;
        } catch (final Exception e) {
            throw new EncrypterException("Could not encrypt secret", e);
        }
    }

    public byte[] decrypt(final byte[] rsaEncrypted) throws EncrypterException {
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

