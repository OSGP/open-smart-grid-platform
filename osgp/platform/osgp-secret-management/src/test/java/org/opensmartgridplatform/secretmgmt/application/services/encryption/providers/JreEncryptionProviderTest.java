package org.opensmartgridplatform.secretmgmt.application.services.encryption.providers;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptedSecret;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class JreEncryptionProviderTest {

    private JreEncryptionProvider jreEncryptionProvider;

    @Test
    public void doTest() throws Exception {

        jreEncryptionProvider = new JreEncryptionProvider();

        String path = "src/test/resources/secret-mgmt-db.key";
        File keyFile = new File(path);
        jreEncryptionProvider.setKeyFile(keyFile);

        byte[] secret = HexUtils.fromHexString("00000000000000000000000000000000");

        EncryptedSecret encryptedSecret = new EncryptedSecret(jreEncryptionProvider.getType(), secret);

        assertThrows(
                IllegalStateException.class,
                () -> jreEncryptionProvider.decrypt(encryptedSecret, "1"),
                "Expected decrypt() to throw javax.crypto.BadPaddingException, but it didn't"
        );
    }
}
