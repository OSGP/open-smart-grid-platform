/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import java.util.BitSet;
import org.bouncycastle.util.encoders.Hex;

public class AlarmGenerator {

  private static final int NUMBER_OF_BITS_IN_REGISTER = 32;

  public static void main(final String[] args) {

    System.out.println(AlarmGenerator.toHexString(new byte[] {0x10, 0x06}));

    //    final byte[] pushedAlarm = AlarmGenerator.createPushedAlarm(AlarmType.LAST_GASP);
    //    System.out.println(Arrays.toString(pushedAlarm));
  }

  public static String toHexString(final byte b) {
    return toHexString(new byte[] {b});
  }

  public static String toHexString(final byte[] ba) {
    final StringBuilder sb = new StringBuilder("[");
    boolean first = true;
    for (final byte b : ba) {
      if (!first) {
        sb.append(", ");
      } else {
        first = false;
      }
      sb.append("0x" + Hex.toHexString(new byte[] {b}));
    }
    sb.append("]");
    return sb.toString();
  }

  public static byte[] createPushedAlarm(final AlarmType alarmType) {
    final BitSet bitSet = new BitSet(NUMBER_OF_BITS_IN_REGISTER);
    if (alarmType != null) {
      bitSet.set(alarmType.getBit(), true);
    }
    final byte[] alarmByteArray = bitSet.toByteArray();
    // Reverse byte array LSB
    reverse(alarmByteArray);

    final byte[] pushedAlarm = new byte[4];
    System.arraycopy(
        alarmByteArray,
        0,
        pushedAlarm,
        pushedAlarm.length - alarmByteArray.length,
        alarmByteArray.length);

    return pushedAlarm;
  }

  public static void reverse(final byte[] array) {
    if (array == null) {
      return;
    }
    int i = 0;
    int j = array.length - 1;
    byte tmp;
    while (j > i) {
      tmp = array[j];
      array[j] = array[i];
      array[i] = tmp;
      j--;
      i++;
    }
  }
}
