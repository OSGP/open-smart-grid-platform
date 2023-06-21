// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QualityConverter {

  private static final Logger LOGGER = LoggerFactory.getLogger(QualityConverter.class);

  private QualityConverter() {
    // Hide public constructor for class with only static methods
  }

  public static short toShort(final byte[] value) {

    final byte[] ba = reverseAllBitsAndBytes(value);
    final ByteBuffer bb = ByteBuffer.wrap(ba);
    final short s = bb.getShort();

    LOGGER.debug(
        "Converting byte array [{}], reversed [{}] into short [{}]",
        toString(value),
        toString(ba),
        s);
    return s;
  }

  public static byte[] fromShort(final short value) {
    final byte[] b = ByteBuffer.allocate(2).putShort(value).array();
    LOGGER.debug("Converting short [{}] into byte array [{}]", value, toString(b));
    return b;
  }

  private static byte reverseBitsInByte(final byte val) {
    byte tmp = val;
    byte result = 0;

    int counter = 8;
    while (counter-- > 0) {
      result <<= 1;
      result |= (byte) (tmp & 1);
      tmp = (byte) (tmp >> 1);
    }

    return result;
  }

  private static byte[] reverseBytesInByteArray(final byte[] val) {

    final byte[] result = new byte[val.length];
    final int maxIndex = val.length - 1;
    for (int i = 0; i < val.length; i++) {
      result[i] = val[maxIndex - i];
    }
    return result;
  }

  private static byte[] reverseAllBitsAndBytes(final byte[] val) {

    final byte[] result = reverseBytesInByteArray(val);

    for (int i = 0; i < result.length; i++) {
      result[i] = reverseBitsInByte(result[i]);
    }

    return result;
  }

  private static String toString(final byte[] value) {
    String string = "";
    for (final byte b : value) {
      string += String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }
    return string;
  }
}
