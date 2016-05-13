package com.alliander.osgp.shared.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EncryptionServiceTest {

    @Before
    public void init() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testEnDecrypt() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {

        final KeyGenerator keygen = KeyGenerator.getInstance("AES", "BC");
        keygen.init(256);
        final SecretKey key = keygen.generateKey();

        final byte[] keyBytes = key.getEncoded();
        final File temp = File.createTempFile("secret", "key");
        Files.write(temp.toPath(), keyBytes, StandardOpenOption.APPEND);

        final EncryptionService encryptionService = new EncryptionService(temp.getPath());

        Assert.assertEquals("testje",
                new String(encryptionService.decrypt(encryptionService.encrypt("testje".getBytes()))));

    }

    @Test
    public void testEnDecryptDifferentService() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {

        final KeyGenerator keygen = KeyGenerator.getInstance("AES", "BC");
        keygen.init(128);
        final SecretKey key = keygen.generateKey();

        final byte[] keyBytes = key.getEncoded();
        final File temp = File.createTempFile("secret", "key");
        Files.write(temp.toPath(), keyBytes, StandardOpenOption.APPEND);

        final byte[] enc = new EncryptionService(temp.getPath()).encrypt("testje".getBytes());

        Assert.assertEquals("testje", new String(new EncryptionService(temp.getPath()).decrypt(enc)));

    }

    @Test
    public void testOpenSslSecret() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {

        // first external preparation....
        // DECRYPT EXISTING KEYS
        // - hex key from db - xxd -p -r <hex> <bin>
        // - openssl rsautl -decrypt -in <bin> -inkey devicekey_priv.der -out
        // <decrypted> -keyform DER -raw

        // GENERATE AES SECRET
        // - openssl enc -aes-128-cbc -k eduard -P -md sha1|grep key=|cut -d"="
        // -f2|xxd -p -r > <aes128cbckey>

        // ENCRYPT KEYS AGAIN
        // - openssl rsautl -decrypt -in <bin> -inkey devicekey_priv.der -out
        // <decrypted> -keyform DER -raw

        final byte[] enc = new EncryptionService(new File("src/test/resources/secret").getPath()).encrypt("testje"
                .getBytes());

        Assert.assertEquals("testje",
                new String(new EncryptionService(new File("src/test/resources/secret").getPath()).decrypt(enc)));

    }
}
