// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.secretmanagement.application.services.encryption.providers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.opensmartgridplatform.shared.security.providers.JreEncryptionProvider;

@ExtendWith(MockitoExtension.class)
public class JreEncryptionProviderTest {
  private static final String JRE_KEY_REF = "1";
  private final String secretString = "5b3a65ba2a7d347f1eedf7fab25f2813";

  private JreEncryptionProvider jreEncryptionProvider;

  @BeforeEach
  public void setUp() {
    final String path = "src/test/resources/osgp-secret-management-db.key";
    final File keyFile = new File(path);
    this.jreEncryptionProvider = new JreEncryptionProvider(keyFile);
  }

  @Test
  public void shouldDecryptEncryptedSecret() throws EncrypterException {
    final byte[] secret = HexUtils.fromHexString(this.secretString);
    final EncryptedSecret encryptedSecret = this.jreEncryptionProvider.encrypt(secret, JRE_KEY_REF);

    final byte[] decrypted =
        this.jreEncryptionProvider.decrypt(
            new EncryptedSecret(EncryptionProviderType.JRE, encryptedSecret.getSecret()),
            JRE_KEY_REF);

    final String decryptedAsString = HexUtils.toHexString(decrypted);

    assertThat(decryptedAsString).isEqualTo(this.secretString);
  }
}
