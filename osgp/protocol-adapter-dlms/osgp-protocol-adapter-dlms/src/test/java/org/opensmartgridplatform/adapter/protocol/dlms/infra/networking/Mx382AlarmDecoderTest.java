/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, (byte)Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

class Mx382AlarmDecoderTest {

  @Test
  void testDecode() throws UnrecognizedMessageDataException {
    final String equipmentIdentifier = "KA6P005039021110";
    final byte[] mx382message = new Mx382AlarmMessage(equipmentIdentifier).encode();
    final Mx382AlarmDecoder decoder = new Mx382AlarmDecoder();
    final InputStream is = new ByteArrayInputStream(mx382message);
    final DlmsPushNotification dlmsPushNotification = decoder.decodeMx382alarm(is);
    assertThat(dlmsPushNotification.getAlarms()).hasSize(1);
    assertThat(dlmsPushNotification.getAlarms().iterator().next())
        .isEqualTo(AlarmTypeDto.LAST_GASP);
    assertThat(dlmsPushNotification.getTriggerType()).isEqualTo(AlarmDecoder.PUSH_ALARM_TRIGGER);
    assertThat(dlmsPushNotification.getEquipmentIdentifier()).isEqualTo(equipmentIdentifier);
  }

  public static final class Mx382AlarmMessage {
    private final String equipmentIdentifier;
    private final byte[] obiscode =
        new byte[] {
          (byte) 0x128, (byte) 0x128, (byte) 0x128, (byte) 0x128, (byte) 0x128, (byte) 0x00
        };

    public Mx382AlarmMessage(final String equipmentIdentifier) {
      this.equipmentIdentifier = equipmentIdentifier;
    }

    public byte[] encode() {
      final byte[] apdu = this.createApdu();
      final byte[] header = this.createWpduHeader(apdu.length);
      final byte[] wnm = new byte[header.length + apdu.length];
      System.arraycopy(header, 0, wnm, 0, header.length);
      System.arraycopy(apdu, 0, wnm, header.length, apdu.length);
      return wnm;
    }

    private byte[] createApdu() {
      final ByteBuffer buf = ByteBuffer.allocate(64);
      buf.put((byte) -62);
      buf.put((byte) 0);

      final byte[] classId = new byte[] {0, 0x01};
      buf.put(classId);
      buf.put(this.obiscode);
      final byte attribute = 0x02;
      buf.put((byte) attribute);
      buf.put(this.equipmentIdentifier.getBytes());
      buf.flip();
      final byte[] bytes = new byte[buf.limit()];
      buf.get(bytes);
      return bytes;
    }

    private byte[] createWpduHeader(final int apduLength) {
      return new byte[] {0, 1, 0, 103, 0, 102, 0, (byte) apduLength};
    }
  }
}
