// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.iec61850;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class RegisterDeviceRequestTest {

  private static final String TESTED_SERIAL_NUMBER = "1234567890";
  private static final String TESTED_IP_ADDRESS = "127.0.0.1";
  private static final byte[] TESTED_BYTES =
      (TESTED_SERIAL_NUMBER + "," + TESTED_IP_ADDRESS).getBytes(StandardCharsets.US_ASCII);

  @Test
  public void testRegisterDeviceRequestFromBytes() throws Exception {

    final RegisterDeviceRequest request = new RegisterDeviceRequest(TESTED_BYTES);

    assertThat(request.getSerialNumber()).isEqualTo(TESTED_SERIAL_NUMBER);
    assertThat(request.getIpAddress()).isEqualTo(TESTED_IP_ADDRESS);

    assertThat(request.getDeviceIdentification())
        .withFailMessage("Device identification should be serial number prefixed with 'KAI-'")
        .isEqualTo("KAI-" + TESTED_SERIAL_NUMBER);
    assertThat(request.toByteArray())
        .withFailMessage(
            "Byte array should match the byte[] used to construct the "
                + RegisterDeviceRequest.class.getSimpleName())
        .isEqualTo(TESTED_BYTES);
    assertThat(request.getSize())
        .withFailMessage("Size should be the length of the byte array representation")
        .isEqualTo(TESTED_BYTES.length);
  }

  @Test
  public void testRegisterDeviceRequestFromFieldValues() throws Exception {

    final RegisterDeviceRequest request =
        new RegisterDeviceRequest(TESTED_SERIAL_NUMBER, TESTED_IP_ADDRESS);

    assertThat(request.getSerialNumber()).isEqualTo(TESTED_SERIAL_NUMBER);
    assertThat(request.getIpAddress()).isEqualTo(TESTED_IP_ADDRESS);

    assertThat(request.getDeviceIdentification())
        .withFailMessage("Device identification should be serial number prefixed with 'KAI-'")
        .isEqualTo("KAI-" + TESTED_SERIAL_NUMBER);

    assertThat(request.toByteArray())
        .withFailMessage(
            "Byte array should match the ASCII bytes for the concatenation of serial number, separator '/' and the IP address"
                + RegisterDeviceRequest.class.getSimpleName())
        .isEqualTo(TESTED_BYTES);
    assertThat(request.getSize())
        .withFailMessage("Size should be the length of the byte array representation")
        .isEqualTo(TESTED_BYTES.length);
  }
}
