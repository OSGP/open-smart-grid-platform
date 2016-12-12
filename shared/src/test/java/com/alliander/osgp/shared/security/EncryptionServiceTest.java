package com.alliander.osgp.shared.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Assert;
import org.junit.Test;

public class EncryptionServiceTest {

    private static final String SRC_TEST_RESOURCES_SECRET = "src/test/resources/secret";
    private static final String TEST_CONTENT = "content to encrypt and decrypt";

    private static class TestableEncService extends EncryptionService {
        protected TestableEncService(final SecretKeySpec key) {
            super(key);
        }
    }

    private static final String PROVIDER = EncryptionService.PROVIDER;
    private static final String AES = EncryptionService.SECRET_KEY_SPEC;

    @Test
    public void testStringKeys() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        final String key1 = "bc082efed278e1bbebddc0431877d4fae80fa4e72925b6ad0bc67c84b8721598eda8458bcc1b2827fe6e5e7918ce22fd";
        this.testStringKey(key1);

        final String key2 = "bc082efed278e1bbebddc0431877d4fa2df7728229f3e03c57b2549142b40d047b35011dbf9f77ad91db5fe6f19a7b9c";
        this.testStringKey(key2);

        final String key3 = "bc082efed278e1bbebddc0431877d4fa16374b00e96dd102beab666dcb72efbd1f0b868412497f6d3d0c62caa4700585";
        this.testStringKey(key3);
    }

    private void testStringKey(final String input) throws NoSuchAlgorithmException, IOException,
            NoSuchProviderException {
        final KeyGenerator keygen = KeyGenerator.getInstance(AES, PROVIDER);
        keygen.init(128);
        final SecretKey key = keygen.generateKey();
        final byte[] keyBytes = key.getEncoded();

        final SecretKeySpec secretKey = this.createSecretKeySpec(keyBytes);
        final EncryptionService encryptionService = new TestableEncService(secretKey);
        final byte[] encrypted = encryptionService.encrypt(input.getBytes());
        final byte[] decrypted = encryptionService.decrypt(encrypted);
        Assert.assertEquals(input, new String(decrypted));
    }

    @Test
    public void testEnDecrypt() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        final KeyGenerator keygen = KeyGenerator.getInstance(AES, PROVIDER);
        keygen.init(128);
        final SecretKey key = keygen.generateKey();
        final byte[] keyBytes = key.getEncoded();

        final SecretKeySpec secretKey = this.createSecretKeySpec(keyBytes);
        final EncryptionService encryptionService = new TestableEncService(secretKey);

        final byte[] encrypted = encryptionService.encrypt(TEST_CONTENT.getBytes());
        final byte[] decrypted = encryptionService.decrypt(encrypted);
        Assert.assertEquals(TEST_CONTENT, new String(decrypted));
    }

    @Test
    public void testEnDecryptDifferentService() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        final KeyGenerator keygen = KeyGenerator.getInstance(AES, PROVIDER);
        keygen.init(128);
        final SecretKey key = keygen.generateKey();
        final byte[] keyBytes = key.getEncoded();

        final SecretKeySpec secretKey = this.createSecretKeySpec(keyBytes);
        final byte[] enc = new TestableEncService(secretKey).encrypt(TEST_CONTENT.getBytes());

        Assert.assertEquals(TEST_CONTENT, new String(new TestableEncService(secretKey).decrypt(enc)));
    }

    @Test
    public void testOpenSslSecret() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        final SecretKeySpec secretKey = this.createSecretKeySpec(new File(SRC_TEST_RESOURCES_SECRET));
        final byte[] enc = new TestableEncService(secretKey).encrypt(TEST_CONTENT.getBytes());

        Assert.assertEquals(TEST_CONTENT, new String(new TestableEncService(secretKey).decrypt(enc)));
    }

    @Test
    public void testOpenSslEncrypted() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        final byte[] encrypted = Files.readAllBytes(new File("src/test/resources/plain.enc").toPath());
        final SecretKeySpec secretKey = this.createSecretKeySpec(new File(SRC_TEST_RESOURCES_SECRET).getPath());
        final byte[] decrypted = new TestableEncService(secretKey).decrypt(encrypted);

        Assert.assertEquals("hallo", new String(decrypted));
    }

    @Test
    public void testPrepended() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        final byte[] encrypted_prepended = Files.readAllBytes(new File("src/test/resources/prepended").toPath());

        final SecretKeySpec secretKey = this.createSecretKeySpec(new File(SRC_TEST_RESOURCES_SECRET).getPath());
        final byte[] decrypted_prepended = new TestableEncService(secretKey).decrypt(encrypted_prepended);

        // in this specific case the length of the decrypted bytes should become
        // 16, after 0 bytes are stripped of
        Assert.assertEquals(16, decrypted_prepended.length);

    }

    private SecretKeySpec createSecretKeySpec(final byte[] bytes) throws IOException {
        return new SecretKeySpec(bytes, EncryptionService.SECRET_KEY_SPEC);
    }

    private SecretKeySpec createSecretKeySpec(final String filePath) throws IOException {
        return new SecretKeySpec(Files.readAllBytes(new File(filePath).toPath()), EncryptionService.SECRET_KEY_SPEC);
    }

    private SecretKeySpec createSecretKeySpec(final File file) throws IOException {
        return new SecretKeySpec(Files.readAllBytes(file.toPath()), EncryptionService.SECRET_KEY_SPEC);
    }
}
