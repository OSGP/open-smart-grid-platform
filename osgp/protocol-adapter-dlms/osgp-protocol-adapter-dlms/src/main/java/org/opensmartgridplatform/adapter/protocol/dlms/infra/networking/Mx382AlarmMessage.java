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

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.CosemDateTime;

public class Mx382AlarmMessage {

  Date timestamp;
  final ObisCode obiscode;
  final int attribute;
  final DataType dataValue;
  final String ipAddress;

  public static void main(final String[] args) {
    //
    final int signedByte = -62;
    final int unsignedByte =
        signedByte >= 0
            ? signedByte // you get this
            : 256 + signedByte; // or this
    System.out.println(unsignedByte);
    System.out.println(Arrays.toString(Mx382AlarmMessage.createApdu()));
  }

  public Mx382AlarmMessage(
      final Date timestamp,
      final ObisCode obiscode,
      final int attribute,
      final CosemDataType dataValue,
      final String ipAddress) {
    this.timestamp = timestamp;
    this.obiscode = obiscode;
    this.attribute = attribute;
    this.dataValue = dataValue;
    this.ipAddress = ipAddress;
  }

  public byte[] encode() throws DecoderException, IOException {
    final byte[] apdu = this.createApdu();
    final byte[] header = this.createWpduHeader(apdu.length);
    final byte[] wnm = new byte[header.length + apdu.length];
    System.arraycopy(header, 0, wnm, 0, header.length);
    System.arraycopy(apdu, 0, wnm, header.length, apdu.length);
    return wnm;
  }

  private byte[] createApdu() {
    final ByteBuffer buf = ByteBuffer.allocate(64);

    // event-notification-request
    buf.put((byte) -62);

    // time OPTIONAL
    if (this.timestamp == null) {
      buf.put((byte) 0);
    } else {
      buf.put((byte) 9);
      final CosemDateTime cdt = new CosemDateTime(this.timestamp);
      final int length = cdt.getBytes().length;
      buf.put((byte) length);
      buf.put(cdt.getBytes());
    }

    // cosem-attribute-descriptor //
    // class-id (Unsigned16)
    final byte[] classId = new byte[] {0, (byte) InterfaceClass.DATA.getValue()};
    buf.put(classId);

    // instance-id OCTET STRING (SIZE(6))
    buf.put(this.obiscode.getBytes());

    // attribute-id (Integer8)
    buf.put((byte) this.attribute);

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      this.dataValue.encode(baos);
    } catch (final IOException var5) {
      throw new IllegalStateException("Exception while encoding data value", var5);
    }

    buf.put(baos.toByteArray());
    buf.flip();
    final byte[] bytes = new byte[buf.limit()];
    buf.get(bytes);
    return bytes;
  }

  private byte[] createWpduHeader(final int apduLength) {
    final byte[] header = new byte[] {0, 1, 0, 103, 0, 102, 0, (byte) apduLength};
    return header;
  }
}
