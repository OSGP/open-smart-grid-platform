// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.hooks;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/** DLMS specific. */
public class SimulatePushedAlarmsHooks {

  /**
   * @throws UnknownHostException
   * @throws IOException
   */
  public static void simulateAlarm(
      final String deviceId, final byte[] alarmsToPush, final String host, final int port)
      throws UnknownHostException, IOException {

    try (final Socket socket = new Socket(host, port)) {
      final OutputStream outputStream = socket.getOutputStream();
      outputStream.write(deviceId.getBytes(StandardCharsets.US_ASCII));
      outputStream.write(alarmsToPush);
      socket.shutdownOutput();
      socket.shutdownInput();
    }
  }

  public static void simulateForwardedMx382Alarm(
      final String equipmentIdentifier, final String host, final int port) throws IOException {

    try (final Socket socket = new Socket(host, port)) {
      final OutputStream outputStream = socket.getOutputStream();
      outputStream.write(createMx382message(equipmentIdentifier));
      socket.shutdownOutput();
      socket.shutdownInput();
    }
  }

  private static byte[] createMx382message(final String equipmentIdentifier) {
    final byte[] apdu = createMx382Apdu(equipmentIdentifier);
    final byte[] header = createMx382WpduHeader(apdu.length);
    final byte[] completeMessage = new byte[header.length + apdu.length];
    System.arraycopy(header, 0, completeMessage, 0, header.length);
    System.arraycopy(apdu, 0, completeMessage, header.length, apdu.length);
    return completeMessage;
  }

  private static byte[] createMx382Apdu(final String equipmentIdentifier) {
    final ByteBuffer buf = ByteBuffer.allocate(64);
    final byte eventNotificationRequest = (byte) 0xC2;
    buf.put(eventNotificationRequest);
    final byte noDateTimePresent = (byte) 0x00;
    buf.put(noDateTimePresent);

    final byte[] classId = new byte[] {0x00, 0x01};
    buf.put(classId);
    final byte[] obiscode =
        new byte[] {(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x00};
    buf.put(obiscode);
    final byte attributeId = 0x02;
    buf.put((byte) attributeId);
    buf.put(equipmentIdentifier.getBytes());
    buf.flip();
    final byte[] bytes = new byte[buf.limit()];
    buf.get(bytes);
    return bytes;
  }

  private static byte[] createMx382WpduHeader(final int apduLength) {
    final byte version = 0x01;
    final byte sourceWPort = 0x67;
    final byte destinationWPort = 0x66; // pre-established client
    return new byte[] {
      0x00, version, 0x00, sourceWPort, 0x00, destinationWPort, 0x00, (byte) apduLength
    };
  }
}
