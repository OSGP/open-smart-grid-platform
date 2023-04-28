/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
    final byte[] wnm = new byte[header.length + apdu.length];
    System.arraycopy(header, 0, wnm, 0, header.length);
    System.arraycopy(apdu, 0, wnm, header.length, apdu.length);
    return wnm;
  }

  private static byte[] createMx382Apdu(final String equipmentIdentifier) {
    final ByteBuffer buf = ByteBuffer.allocate(64);
    buf.put((byte) 0xC2);
    buf.put((byte) 0x00);

    final byte[] classId = new byte[] {0x00, 0x01};
    buf.put(classId);
    final byte[] obiscode =
        new byte[] {(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x00};
    buf.put(obiscode);
    final byte attribute = 0x02;
    buf.put((byte) attribute);
    buf.put(equipmentIdentifier.getBytes());
    buf.flip();
    final byte[] bytes = new byte[buf.limit()];
    buf.get(bytes);
    return bytes;
  }

  private static byte[] createMx382WpduHeader(final int apduLength) {
    return new byte[] {0, 1, 0, 103, 0, 102, 0, (byte) apduLength};
  }
}
