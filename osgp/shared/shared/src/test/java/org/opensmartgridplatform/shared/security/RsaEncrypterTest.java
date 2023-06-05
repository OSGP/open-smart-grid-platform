// Copyright 2021 Alliander N.V.
// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

public class RsaEncrypterTest {

  private static final String SECRET = "this is my secret";

  private static final String ENCRYPTED_SECRET_HEX_STRING =
      "59946b3af1c7e3654d4938711a85632929dcf435334d39f4d8302f8ecda5fe56b20b52fc2df4b64451b59103d3d8ecb8d074bb7d26c5fd2484432b5c1476bd9f4e7e0e56c0aa42ba546d65a020f95752480939d51638bdf91525f3395dca76ff3f8a14939c3b004d68f9410d584bd2869d9eb2c4845246d7e9ffd15a575786cbba7fddb4c12dcb0a2d5527ce7938a6be30768aa3b0e78a701eda375a5dc9ba49571fd8d79b0855ce61b643f34b5241faedc746f4854b0a0007033a6c38e67d40eae3cb2212f1b59a76e2f5780f9f0e7fe02a42b6ff0f7da87553e8ebd3b83e599accb4a2cacf1984f221fc3c2371f663b9a8eee26dd5d7f9e5d3419bc7301570";

  private RsaEncrypter rsaEncrypter;

  @BeforeEach
  public void setup() {
    this.rsaEncrypter = new RsaEncrypter();
  }

  @Test
  public void shouldDecrypt() throws IOException, DecoderException {
    this.rsaEncrypter.setPrivateKeyStore(this.loadPrivateKeyFile());

    final byte[] decrypted =
        this.rsaEncrypter.decrypt(Hex.decodeHex(ENCRYPTED_SECRET_HEX_STRING.toCharArray()));

    assertThat(new String(decrypted)).isEqualTo(SECRET);
  }

  @Test
  public void shouldDecryptAfterEncrypt() throws IOException {
    this.rsaEncrypter.setPublicKeyStore(this.loadPublicKeyFile());
    this.rsaEncrypter.setPrivateKeyStore(this.loadPrivateKeyFile());

    final byte[] encryptedSecret = this.rsaEncrypter.encrypt(SECRET.getBytes());
    final byte[] decryptedSecret = this.rsaEncrypter.decrypt(encryptedSecret);

    assertThat(new String(decryptedSecret)).isEqualTo(SECRET);
  }

  private File loadPublicKeyFile() throws FileNotFoundException {
    return ResourceUtils.getFile("classpath:keys/dlms_device_keys_public");
  }

  private File loadPrivateKeyFile() throws FileNotFoundException {
    return ResourceUtils.getFile("classpath:keys/dlms_device_keys_private");
  }
}
