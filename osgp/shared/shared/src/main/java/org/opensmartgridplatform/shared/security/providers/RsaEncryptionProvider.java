/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.security.providers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;

public class RsaEncryptionProvider extends AbstractEncryptionProvider implements EncryptionProvider {

    private static final String ALG = "RSA";
    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

    private Key publicKey;
    private Key privateKey;

    public void setPrivateKeyStore(File privateKeyStoreFile)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyData = Files.readAllBytes(privateKeyStoreFile.toPath());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(keyData);
        privateKey = KeyFactory.getInstance(ALG).generatePrivate(privateKeySpec);
        super.setKeyFile(privateKeyStoreFile);
    }

    public void setPublicKeyStore(File publicKeyStoreFile) throws EncrypterException {
        try {
            byte[] keyData = Files.readAllBytes(publicKeyStoreFile.toPath());
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyData);
            publicKey = KeyFactory.getInstance(ALG).generatePublic(publicKeySpec);
        }
        catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
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

    protected Key getSecretEncryptionKey(String key, int cipherMode) {
        return cipherMode == Cipher.ENCRYPT_MODE ? publicKey : privateKey;
    }

    protected AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return null;
    }

    public EncryptionProviderType getType() {
        return EncryptionProviderType.RSA;
    }
}

