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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayloadZipper {
  private static final Logger LOGGER = LoggerFactory.getLogger(PayloadZipper.class);

  public static byte[] gzip(final byte[] payload) {
    try {
      final ByteArrayOutputStream os = new ByteArrayOutputStream();
      final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(os);
      gzipOutputStream.write(payload);
      gzipOutputStream.close();
      return os.toByteArray();
    } catch (final IOException e) {
      LOGGER.error("Error zipping message", e);
    }
    return new byte[0];
  }

  private PayloadZipper() {}
}
