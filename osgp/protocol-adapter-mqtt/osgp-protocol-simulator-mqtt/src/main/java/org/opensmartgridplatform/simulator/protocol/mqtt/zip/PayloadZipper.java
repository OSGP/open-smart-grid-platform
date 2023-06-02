//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0

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
