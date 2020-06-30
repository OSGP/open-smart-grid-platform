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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.Secret;

@ExtendWith(MockitoExtension.class)
public class JreEncryptionProviderTest {

    private JreEncryptionProvider jreEncryptionProvider;

    @Test
    public void identityTest() {

        String path = "src/test/resources/secret-mgmt-db.key";
        File keyFile = new File(path);

        jreEncryptionProvider = new JreEncryptionProvider(keyFile);

        Secret secret = new Secret(HexUtils.fromHexString("5b3a65ba2a7d347f1eedf7fab25f2813"));

        EncryptedSecret encryptedSecret = jreEncryptionProvider.encrypt(secret, "1");

        String encryptedSecretAsString = HexUtils.toHexString(encryptedSecret.getSecret());

        assertEquals("f2edbdc2ad1dab1458f1b866c5a5e6a68873d5738b3742bf3fa5d673133313b6", encryptedSecretAsString);

        Secret decryptedSecret = jreEncryptionProvider.decrypt(encryptedSecret, "1");

        String decryptedSecretAsString = HexUtils.toHexString(decryptedSecret.getSecret());

        assertEquals("5b3a65ba2a7d347f1eedf7fab25f2813", decryptedSecretAsString);

    }

    @Test
    public void doErrorTest() {

        String path = "src/test/resources/secret-mgmt-db.key";
        File keyFile = new File(path);

        jreEncryptionProvider = new JreEncryptionProvider(keyFile);

        byte[] secret = HexUtils.fromHexString("00000000000000000000000000000000");

        EncryptedSecret encryptedSecret = new EncryptedSecret(jreEncryptionProvider.getType(), secret);

        assertThrows(IllegalStateException.class, () -> jreEncryptionProvider.decrypt(encryptedSecret, "1"),
                "Expected decrypt() to throw javax.crypto.BadPaddingException, but it didn't");
    }


}
