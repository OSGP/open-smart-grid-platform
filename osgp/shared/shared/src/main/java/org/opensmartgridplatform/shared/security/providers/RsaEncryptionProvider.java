package org.opensmartgridplatform.shared.security.providers;

import java.io.File;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;

public class RsaEncryptionProvider extends AbstractEncryptionProvider implements EncryptionProvider {

    public static final String ALG = "RSA";
    public static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

    private Key publicKey;
    private Key privateKey;

    public RsaEncryptionProvider(File privateKeyStoreFile, File publicKeyStoreFile) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALG);

            if (privateKeyStoreFile != null) {
                byte[] keyData = Files.readAllBytes(privateKeyStoreFile.toPath());
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(keyData);
                privateKey = keyFactory.generatePrivate(privateKeySpec);
                super.setKeyFile(privateKeyStoreFile);
            }
            if (publicKeyStoreFile != null) {
                byte[] keyData = Files.readAllBytes(publicKeyStoreFile.toPath());
                X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyData);
                publicKey = keyFactory.generatePublic(publicKeySpec);
            }
        } catch (Exception e) {
            throw new EncrypterException("Something went wrong during construction of "
                    + "RsaEncryptionProvider", e);
        }
    }

    protected Cipher getCipher() throws javax.crypto.NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(ALGORITHM);
    }

    protected Key getSecretEncryptionKey(String key, int cipherMode) {
        if (cipherMode == Cipher.ENCRYPT_MODE) {
            if (publicKey == null) {
                throw new EncrypterException("Cannot RSA encrypt because no public key is defined.");
            }
            return publicKey;
        } else if (cipherMode == Cipher.DECRYPT_MODE) {
            if (privateKey == null) {
                throw new EncrypterException("Cannot RSA encrypt because no private key is defined.");
            }
            return privateKey;
        }
        throw new EncrypterException("Invalid cipher mode specified.");
    }

    protected AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return null;
    }

    public EncryptionProviderType getType() {
        return EncryptionProviderType.RSA;
    }
}

