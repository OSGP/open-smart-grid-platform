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

    private static final String KEY2 = "key";
    private static final String SECRET = "secret";
    private static final String SRC_TEST_RESOURCES_SECRET = "src/test/resources/secret";
    private static final String TESTJE = "testje";

    private static class TestableEncService extends EncryptionService {

        protected TestableEncService(final String keyPath) {
            super(keyPath);
        }

    }

    private static final String BC = "BC";
    private static final String AES = "AES";

    @Before
    public void init() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testEnDecrypt() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {

        final KeyGenerator keygen = KeyGenerator.getInstance(AES, BC);
        keygen.init(256);
        final SecretKey key = keygen.generateKey();

        final byte[] keyBytes = key.getEncoded();
        final File temp = File.createTempFile(SECRET, KEY2);
        Files.write(temp.toPath(), keyBytes, StandardOpenOption.APPEND);

        final EncryptionService encryptionService = new TestableEncService(temp.getPath());

        Assert.assertEquals(TESTJE, new String(encryptionService.decrypt(encryptionService.encrypt(TESTJE.getBytes()))));

    }

    @Test
    public void testEnDecryptDifferentService() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {

        final KeyGenerator keygen = KeyGenerator.getInstance(AES, BC);
        keygen.init(128);
        final SecretKey key = keygen.generateKey();

        final byte[] keyBytes = key.getEncoded();
        final File temp = File.createTempFile(SECRET, KEY2);
        Files.write(temp.toPath(), keyBytes, StandardOpenOption.APPEND);

        final byte[] enc = new TestableEncService(temp.getPath()).encrypt(TESTJE.getBytes());

        Assert.assertEquals(TESTJE, new String(new TestableEncService(temp.getPath()).decrypt(enc)));

    }

    @Test
    public void testOpenSslSecret() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {

        final byte[] enc = new TestableEncService(new File(SRC_TEST_RESOURCES_SECRET).getPath()).encrypt(TESTJE
                .getBytes());

        Assert.assertEquals(TESTJE,
                new String(new TestableEncService(new File(SRC_TEST_RESOURCES_SECRET).getPath()).decrypt(enc)));

    }

    @Test
    public void testOpenSslEncrypted() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        final byte[] encrypted = Files.readAllBytes(new File("src/test/resources/plain.enc").toPath());
        final byte[] decrypted = new EncryptionService(new File(SRC_TEST_RESOURCES_SECRET).getPath())
                .decrypt(encrypted);
        Assert.assertEquals("hallo", new String(decrypted));

    }

}
