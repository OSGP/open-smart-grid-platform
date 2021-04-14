/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.hooks;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
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
}
