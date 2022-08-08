/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.simulator.protocol.mqtt.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class PayloadZipper {

  public static byte[] gzip(final byte[] payload) {
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    try (final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(os)) {
      gzipOutputStream.write(payload);
    } catch (final IOException e) {
      throw new RuntimeException("Error zipping payload", e);
    }
    return os.toByteArray();
  }

  private PayloadZipper() {}
}
