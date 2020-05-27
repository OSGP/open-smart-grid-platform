package org.opensmartgridplatform.secretmgmt.application.services.encryption.providers;

import org.apache.tomcat.util.buf.HexUtils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;

public class JreEncryptionProvider extends AbstractEncryptionProvider implements EncryptionProvider {

    private static final String SECRET_ENCRYPTION_KEY = "1cb340f6edab9d9b3f2912877c9ed161"; //TODO read from file secres.aes as specified by encryption.key.path
    public static final String ALG = "AES";
    public static final String ALGORITHM = "AES/CBC/PKCS5PADDING";
    public static final String PROVIDER = "SunJCE";
    public static final String FORMAT = "RAW";
    private static final byte[] IV = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

    protected int getIVLength() {
        return IV.length;
    }

    protected Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        return Cipher.getInstance(ALGORITHM, PROVIDER);
    }

    protected Key getSecretEncryptionKey() {
        //TODO read this key from a specified resource ${encryption.key.path}" which currently refers to 'secret.aes' containing '1cb340f6edab9d9b3f2912877c9ed161'
        byte[] secretEncryptionKey = HexUtils.fromHexString(SECRET_ENCRYPTION_KEY);

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
                return secretEncryptionKey;
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
