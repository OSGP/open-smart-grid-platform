/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

public class RsaEncryptionServiceTest {

    private static final int KEYSIZE = 2048;
    /*
     * RSA encryption with the padding used can only encrypt to eleven bytes
     * less than the keysize in bytes.
     */
    private static final int MAX_INPUT_BYTES = KEYSIZE / 8 - 11;

    private static final String PRIVATE_KEY_PATH = "src/test/resources/keys/dlms_device_keys_private";
    private static final String PUBLIC_KEY_PATH = "src/test/resources/keys/dlms_device_keys_public";

    /*
     * RSA encrypted authentication key in hex string format and decrypted
     * binary format.
     */
    private static final String AUTHENTICATION_KEY_ENCRYPTED_HEX_STRING =
            "9eab9df8169a9c22d694067435b584d573b1a57d62d491b58fd9058e994861666831fb9f5ddbf5aba9ef"
                    +
                    "169256cffc8e540c34b3f92246d062889eca13639fe317e92beec86b48b14d5ef4b74682497eed7d8ea3ae6ea3dfa1877045653cb989146f826b2d97a3294a2aa22f804b1f389d06"
                    +
                    "84482dde33e6cdfc51700156e3be94fc8d5b3a1302b3f3992564982e7cd7885c26fa96eeb7cab5a13d6d7fd341f665d61581dd71f652dc278823216ab75b5a430edc826021c4a2dc"
                    +
                    "9de95fbdfb0e79421e2662743650690bc6b69b0b91035e96cb6396626aa1c252cddf87046dc53b9da0c8d74b517c2845b2e8eaaf72e97d41df1c4ce232e7bb082c82154e9ae5";
    private static final String AUTHENTICATION_KEY_PATH = "src/test/resources/keys/authkeydecrypted";

    /*
     * RSA encrypted encryption key in hex string format and decrypted binary
     * format.
     */
    private static final String ENCRYPTION_KEY_ENCRYPTED_HEX_STRING =
            "4e6fb5bd62d7a21f87438c04f518939cce7cfe8259ff40d9e3ff4a3a8c3befdad191eb066c8332d6d3066a2e"
                    +
                    "d866774616c2b893da4543998eb57fcf35323cd2b41960e857c1a99f5cb59405081712ab23da97353014f500046756eab2620d13a269b83cbefbdfb5e275862b34dd407fd745a1bc"
                    +
                    "a18f1b66cb114641212579c6da03e86be2973f8dd6988b15bb6e9ef0f5637827829fc2241891c050a95ef5fc787f740a40aa2d528c69f99c76ad380bba3725929fcbe11ab72cf61e"
                    +
                    "342ab95fc3b883372c110830f28144894aa2919a590822b1e594b807e86f49093982b871c658db0b6c08a90bae55c731efb3d40f245d8c0ad1478b55fa68cced3c1386a7";
    private static final String ENCRYPTION_KEY_PATH = "src/test/resources/keys/enckeydecrypted";

    /*
     * RSA encrypted master key in hex string format and decrypted binary
     * format.
     */
    private static final String MASTER_KEY_ENCRYPTED_HEX_STRING =
            "6fa7f5f19812391b2803a142f17c67aa0e3fc23b537ae6f9cd34a850d4fd5f4d60a3b2bdd6f8cb356e00e6c4e104"
                    +
                    "fb5ea521eeabd8cb69d8f7a5cbe2b20e010c089ee346aaa13c9abdc5e0c9ba0fcafff53d2dcd3c1b7a8ee3c3f76e0d00fcd043940586f055c5e19a0fa7eeff6a7894e128029eaf11"
                    +
                    "c1734565f3f5b614bfab9ea5ce24bf34d2e59878dc2401bd175333315ce197d4243dced9c4e28a23bc91dca432985debe81cf5912df7e99b28f596f335e80678d7b5d1edc93be8bf"
                    +
                    "22d77b2e172ccd7c6907454a983999840bf540343d281e8f9871386f005fe40065fcbe218bdc605be4e759cb1b8d5760eab7b8ceb95cfae2224c15045834962f9b6b";
    private static final String MASTER_KEY_PATH = "src/test/resources/keys/masterkeydecrypted";

    @Test
    public void testDecryptionOfKeys() throws Exception {
        final RsaEncryptionService rsaEncryptionService = this.createRsaEncryptionServiceFromStoredKeys();

        this.assertDecryptionOfKey(rsaEncryptionService, AUTHENTICATION_KEY_ENCRYPTED_HEX_STRING,
                AUTHENTICATION_KEY_PATH, "authentication key");

        this.assertDecryptionOfKey(rsaEncryptionService, ENCRYPTION_KEY_ENCRYPTED_HEX_STRING, ENCRYPTION_KEY_PATH,
                "encryption key");

        this.assertDecryptionOfKey(rsaEncryptionService, MASTER_KEY_ENCRYPTED_HEX_STRING, MASTER_KEY_PATH,
                "master key");
    }

    private void assertDecryptionOfKey(final RsaEncryptionService rsaEncryptionService, final String encryptedHexString,
            final String keyPath, final String keyType) throws Exception {

        final byte[] expected = Files.readAllBytes(Paths.get(keyPath));
        final byte[] actual = rsaEncryptionService.decrypt(Hex.decodeHex(encryptedHexString.toCharArray()));
        assertThat(Hex.encodeHexString(actual)).withFailMessage("decrypted " + keyType)
                                               .isEqualTo(Hex.encodeHexString(expected));
    }

    @Test
    public void testEncryptDecryptReturnsInput() {
        final KeyPair freshKeyPair = RsaEncryptionService.createKeyPair(KEYSIZE);
        final RsaEncryptionService rsaEncryptionService = new RsaEncryptionService(freshKeyPair);
        final byte[] input = this.createRandomInput();
        final byte[] encrypted = rsaEncryptionService.encrypt(input);
        final byte[] decrypted = rsaEncryptionService.decrypt(encrypted);
        assertThat(decrypted).withFailMessage("decrypted bytes after encryption").isEqualTo(input);

    }

    private RsaEncryptionService createRsaEncryptionServiceFromStoredKeys() {
        final KeyPair keyPair = new KeyPair(RsaEncryptionService.readPublicKeyFromFile(PUBLIC_KEY_PATH),
                RsaEncryptionService.readPrivateKeyFromFile(PRIVATE_KEY_PATH));
        return new RsaEncryptionService(keyPair);
    }

    private byte[] createRandomInput() {
        final Random random = new Random();
        final int length = random.nextInt(MAX_INPUT_BYTES);
        final byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return randomBytes;
    }

    public static void createNewKeysForTests() throws IOException {
        final KeyPair keyPair = RsaEncryptionService.createKeyPair(KEYSIZE);
        RsaEncryptionService.saveKeyToFile(keyPair.getPrivate(), PRIVATE_KEY_PATH);
        RsaEncryptionService.saveKeyToFile(keyPair.getPublic(), PUBLIC_KEY_PATH);
    }
}
