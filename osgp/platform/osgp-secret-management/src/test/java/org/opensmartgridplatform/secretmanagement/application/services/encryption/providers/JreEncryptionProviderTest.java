/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services.encryption.providers;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptedSecret;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class JreEncryptionProviderTest {

    private JreEncryptionProvider jreEncryptionProvider;

    @Test
    public void doTest() throws Exception {

        String path = "src/test/resources/secret-mgmt-db.key";
        File keyFile = new File(path);

        jreEncryptionProvider = new JreEncryptionProvider(keyFile);

        byte[] secret = HexUtils.fromHexString("00000000000000000000000000000000");

        EncryptedSecret encryptedSecret = new EncryptedSecret(jreEncryptionProvider.getType(), secret);

        assertThrows(
                IllegalStateException.class,
                () -> jreEncryptionProvider.decrypt(encryptedSecret, "1"),
                "Expected decrypt() to throw javax.crypto.BadPaddingException, but it didn't"
        );
    }
}
