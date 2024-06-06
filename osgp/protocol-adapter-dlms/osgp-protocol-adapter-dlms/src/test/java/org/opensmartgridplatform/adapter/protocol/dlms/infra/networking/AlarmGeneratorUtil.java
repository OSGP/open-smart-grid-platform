// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

class AlarmGeneratorUtil {

  private AlarmGeneratorUtil() {
    // Util class
  }

  public static byte[] dsmr4Alarm(final String identifier, final byte[] alarmsToPush)
      throws IOException {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    outputStream.write(identifier.getBytes(StandardCharsets.US_ASCII));
    outputStream.write(new byte[] {0x2C});
    outputStream.write(alarmsToPush);
    return outputStream.toByteArray();
  }

  public static byte[] smr5Alarm(
      final String deviceId, final int pushSetupBit, final List<byte[]> alarmsToPush)
      throws IOException {

    final byte pushSetupTypeByte = Byte.parseByte(Integer.toHexString(pushSetupBit));

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    outputStream.write(
        new byte[8]); // For SMR5 alarms, we get 8 additional addressing bytes in front of the alarm
    outputStream.write(new byte[] {0x0F}); // Data-notification
    outputStream.write(
        new byte[] {0x00, 0x00, 0x00, 0x01}); // Long-invoke-id-and-priority (can be ignored)
    outputStream.write(new byte[] {0x00}); // date-time (empty)
    outputStream.write(
        concatenate(
            new byte[] {0x02}, asByteArray(2 + alarmsToPush.size()))); // data-value – structure [3]
    outputStream.write(
        concatenate(
            new byte[] {0x09},
            asByteArray(deviceId.length()),
            deviceId.getBytes(StandardCharsets.US_ASCII))); // Equipment ID
    outputStream.write(
        concatenate(
            new byte[] {0x09},
            new byte[] {
              0x06, 0x00, pushSetupTypeByte, 0x19, 0x09, 0x00, (byte) 0xFF
            })); // Push Setup Alarms
    for (final byte[] alarmToPush : alarmsToPush) {
      outputStream.write(concatenate(new byte[] {0x06}, alarmToPush));
    }
    return outputStream.toByteArray();
  }

  private static byte[] asByteArray(final int i) {
    return new byte[] {Byte.parseByte(Integer.toHexString(i), 16)};
  }

  private static byte[] concatenate(final byte[]... byteArrayValues) throws IOException {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    for (final byte[] byteArray : byteArrayValues) {
      outputStream.write(byteArray);
    }
    return outputStream.toByteArray();
  }
}
