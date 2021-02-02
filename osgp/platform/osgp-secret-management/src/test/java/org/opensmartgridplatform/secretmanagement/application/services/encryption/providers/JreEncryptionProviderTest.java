/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services.encryption.providers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.providers.JreEncryptionProvider;

@ExtendWith(MockitoExtension.class)
public class JreEncryptionProviderTest {
    private static final String JRE_KEY_REF = "1";
    private final String secretString = "5b3a65ba2a7d347f1eedf7fab25f2813";

    private JreEncryptionProvider jreEncryptionProvider;

    @BeforeEach
    public void setUp() {
        String path = "src/test/resources/osgp-secret-management-db.key";
        File keyFile = new File(path);
        this.jreEncryptionProvider = new JreEncryptionProvider(keyFile);
    }
    @Test
    public void identityTest() throws EncrypterException {
        final byte[] secret = HexUtils.fromHexString(this.secretString);
        EncryptedSecret encryptedSecret = this.jreEncryptionProvider.encrypt(secret, JRE_KEY_REF);
        String encryptedSecretAsString = HexUtils.toHexString(encryptedSecret.getSecret());

        assertEquals("f2edbdc2ad1dab1458f1b866c5a5e6a68873d5738b3742bf3fa5d673133313b6", encryptedSecretAsString);

        byte[] decryptedSecret = this.jreEncryptionProvider.decrypt(encryptedSecret, JRE_KEY_REF);
        String decryptedSecretAsString = HexUtils.toHexString(decryptedSecret);

        assertEquals(this.secretString, decryptedSecretAsString);

    }

    @Test
    public void doErrorTest() throws EncrypterException {
        byte[] secret = HexUtils.fromHexString("00000000000000000000000000000000");

        EncryptedSecret encryptedSecret = new EncryptedSecret(this.jreEncryptionProvider.getType(), secret);

        assertThrows(EncrypterException.class, () -> this.jreEncryptionProvider.decrypt(encryptedSecret,
                this.JRE_KEY_REF),
                "Expected decrypt() to throw javax.crypto.BadPaddingException, but it didn't");
    }

    @Test
    public void generateKeyAndCheckLengths() {
        byte[] encryptedSecretBytes = this.jreEncryptionProvider.generateAes128BitsSecret(JRE_KEY_REF);
        EncryptedSecret encryptedSecret = new EncryptedSecret(this.jreEncryptionProvider.getType(),
                encryptedSecretBytes);
        byte[] unencryptedSecretBytes = this.jreEncryptionProvider.decrypt(encryptedSecret, JRE_KEY_REF);
        String encryptedSecretAsString = HexUtils.toHexString(encryptedSecretBytes);
        assertEquals(16, unencryptedSecretBytes.length);
        assertEquals(32, encryptedSecretBytes.length);
        assertEquals(64, encryptedSecretAsString.length());
    }
}
