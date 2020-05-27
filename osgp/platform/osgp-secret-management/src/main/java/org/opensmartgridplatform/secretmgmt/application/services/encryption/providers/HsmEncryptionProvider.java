package org.opensmartgridplatform.secretmgmt.application.services.encryption.providers;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Logger;

public class HsmEncryptionProvider extends AbstractEncryptionProvider implements EncryptionProvider {

    private static final Logger LOGGER = Logger.getLogger(HsmEncryptionProvider.class.getName());

    public static final String ALGORITHM = "AES/CBC/NoPadding";
    public static final String PROVIDER = "nCipherKM";
    public static final String TYPE = "ncipher.sworld";
    public static final String KEYSTORENAME = "houston.keystore";

    private static final byte[] IV = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    protected int getIVLength() {
        return IV.length;
    }

    protected Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        return Cipher.getInstance(ALGORITHM, PROVIDER);
    }

    /**
     * This method reads the 'actual' encryption key (KEK) (from the database).
     * Normally this is the key start isValidFrom(now) and isValidUntil(now).
     *
     * @return the key that must be used for encryption/decryption
     * @throws Exception when keystore can not be accessed
     */
    protected Key getSecretEncryptionKey() throws Exception {

        KeyStore ks = KeyStore.getInstance(TYPE, PROVIDER);
        FileInputStream fIn = new FileInputStream(KEYSTORENAME);
        ks.load(fIn, null);
        Key key = ks.getKey(String.valueOf(1), null);
        fIn.close();

        return key;
    }

    protected AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return new IvParameterSpec(IV);
    }

    public EncryptionProviderType getType() {
        return EncryptionProviderType.HSM;
    }
}