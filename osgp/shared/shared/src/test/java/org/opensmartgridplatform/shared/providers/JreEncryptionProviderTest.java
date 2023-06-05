// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.providers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.opensmartgridplatform.shared.security.providers.JreEncryptionProvider;
import org.springframework.util.ResourceUtils;

class JreEncryptionProviderTest {

  @Test
  void testDecryptTwice() throws FileNotFoundException, DecoderException {
    final String encryptedKey =
        "bf1a7553451978483b6bcaf8d6d3135d6d7879ef8e353149aff4e791321df5b5f572c7bcc8937e02048a1f50483344ea";

    final File file = ResourceUtils.getFile("classpath:keys/secret.aes");
    final JreEncryptionProvider jreEncryptionProvider = new JreEncryptionProvider(file);

    final String decrypted1 = this.decrypt(jreEncryptionProvider, encryptedKey);
    final String decrypted2 = this.decrypt(jreEncryptionProvider, encryptedKey);
    assertThat(decrypted1).isEqualTo(decrypted2);
  }

  @Test
  void testEncryptDecrypt() throws FileNotFoundException, DecoderException {
    final String encryptedKey =
        "bf1a7553451978483b6bcaf8d6d3135d6d7879ef8e353149aff4e791321df5b5f572c7bcc8937e02048a1f50483344ea";

    final File file = ResourceUtils.getFile("classpath:keys/secret.aes");
    final JreEncryptionProvider jreEncryptionProvider = new JreEncryptionProvider(file);

    final String decrypted1 = this.decrypt(jreEncryptionProvider, encryptedKey);
    final String encryptKey2 = this.encrypt(jreEncryptionProvider, decrypted1);
    final String decrypted2 = this.decrypt(jreEncryptionProvider, encryptKey2);
    assertThat(decrypted1).isEqualTo(decrypted2);
  }

  private String decrypt(
      final JreEncryptionProvider jreEncryptionProvider, final String encryptedKey)
      throws DecoderException {
    final EncryptedSecret encryptedSecret =
        new EncryptedSecret(EncryptionProviderType.JRE, Hex.decodeHex(encryptedKey));
    final byte[] decryptSecret = jreEncryptionProvider.decrypt(encryptedSecret, "1");
    return Hex.encodeHexString(decryptSecret);
  }

  private String encrypt(
      final JreEncryptionProvider jreEncryptionProvider, final String decryptedKey)
      throws DecoderException {
    final EncryptedSecret encryptedSecret =
        jreEncryptionProvider.encrypt(Hex.decodeHex(decryptedKey), "1");
    return Hex.encodeHexString(encryptedSecret.getSecret());
  }
}
