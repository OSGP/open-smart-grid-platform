package org.opensmartgridplatform.secretmgmt.application.services.encryption.providers;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;

public class JreEncryptionProvider extends AbstractEncryptionProvider implements EncryptionProvider {

    public static final String ALG = "AES";
    public static final String ALGORITHM = "AES/CBC/PKCS5PADDING";
    public static final String PROVIDER = "SunJCE";
    public static final String FORMAT = "RAW";
    private static final byte[] IV = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

    public JreEncryptionProvider() {
    }

    protected int getIVLength() {
        return IV.length;
    }

    protected Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        return Cipher.getInstance(ALGORITHM, PROVIDER);
    }

    protected Key getSecretEncryptionKey(String keyReference) throws Exception {

        if (!keyReference.equals("1")) {
            throw new IllegalStateException("Only keyReference '1' is valid in this implementation.");
        }

        //TODO add a cache (in abstract base?)
        byte[] key = Files.readAllBytes(Paths.get(keystore.getAbsolutePath()));

        return new SecretKey() {
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
                return key;
            }
        };
    }

    protected AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return new IvParameterSpec(IV);
    }

    public EncryptionProviderType getType() {
        return EncryptionProviderType.JRE;
    }
}
