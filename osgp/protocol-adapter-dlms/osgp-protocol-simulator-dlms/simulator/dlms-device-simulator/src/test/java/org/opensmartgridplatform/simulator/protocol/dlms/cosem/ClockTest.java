//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.IllegalAttributeAccessException;
import org.openmuc.jdlms.datatypes.CosemDateFormat.Field;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;

public class ClockTest {

  private static final long CLOCK_TRANSITION_TIME = 2000L;

  private static final byte[] DST_BEGIN_AUSTRALIA_SYDNEY =
      new byte[] {
        (byte) 0xFF,
        (byte) 0xFF,
        0x0A,
        0x01,
        0x07,
        0x02,
        0x00,
        0x00,
        0x00,
        (byte) 0xFD,
        (byte) 0xA8,
        0x00
      };

  private static final byte[] DST_END_AUSTRALIA_SYDNEY =
      new byte[] {
        (byte) 0xFF,
        (byte) 0xFF,
        0x04,
        0x01,
        0x07,
        0x03,
        0x00,
        0x00,
        0x00,
        (byte) 0xFD,
        0x6C,
        (byte) 0x80
      };

  private static final short TIME_ZONE_OFFSET_AUSTRALIA_SYDNEY = -600;

  @Test
  public void transitionToDstNorthernHemisphereTest()
      throws IllegalAttributeAccessException, InterruptedException {
    // One second before DST
    final String hexDateTime = "07E1031A07013B3B00FFC400";
    final Clock clock = this.getDutchClock(hexDateTime);
    this.transistionToDst(clock, hexDateTime);
  }

  @Test
  public void transitionToDstSouthernHemisphereTest()
      throws IllegalAttributeAccessException, InterruptedException {
    // One second before DST
    final String hexDateTime = "07E10A0107013B3B00FDA800";
    final Clock clock = this.getAustralianClock(hexDateTime);
    this.transistionToDst(clock, hexDateTime);
  }

  @Disabled
  @Test
  public void transitionFromDstNorthernHemisphereTest()
      throws IllegalAttributeAccessException, InterruptedException {
    // One second before end of DST
    final String hexDateTime = "07E10A1D07023B3B00FF8880";
    final Clock clock = this.getDutchClock(hexDateTime);
    this.transistionFromDst(clock, hexDateTime);
  }

  @Disabled
  @Test
  public void transitionFromDstSouthernHemisphereTest()
      throws IllegalAttributeAccessException, InterruptedException {
    // One second before end of DST
    final String hexDateTime = "07E1040207023B3B00FD6C80";
    final Clock clock = this.getAustralianClock(hexDateTime);
    this.transistionFromDst(clock, hexDateTime);
  }

  @Test
  public void setDateTimeWithIncorrectTimezone() {
    Assertions.assertThrows(
        IllegalAttributeAccessException.class,
        () -> {
          final Clock clock = new Clock(LocalDateTime.now());
          clock.timeZone = DataObject.newInteger16Data((short) -480);

          final byte[] hexDateTime = Hex.decode("07E10A1D07023B3B00FF8880");
          clock.setTime(DataObject.newOctetStringData(hexDateTime));
        });
  }

  @Test
  public void setDateTimeWithIncorrectDstFlag() {
    Assertions.assertThrows(
        IllegalAttributeAccessException.class,
        () -> {
          final Clock clock = new Clock(LocalDateTime.now());

          final byte[] hexDateTime = Hex.decode("07E1011D07023B3B00FF8880");
          clock.setTime(DataObject.newOctetStringData(hexDateTime));
        });
  }

  private void transistionToDst(final Clock clock, final String hexDateTime)
      throws InterruptedException {
    Thread.sleep(CLOCK_TRANSITION_TIME);

    final CosemDateTime dateTime = this.getCosemDateTime(hexDateTime);
    final CosemDateTime returned = clock.getTime().getValue();
    /*
     * Transition from 01:59:59 to 2 o'clock is a transition to DST, so then
     * the clock is forwarded an hour. The hour therefore differences 2 from
     * the given hour.
     */
    this.doDstTansitionAssertions(dateTime, returned, 2, true);
  }

  private void transistionFromDst(final Clock clock, final String hexDateTime)
      throws InterruptedException {
    Thread.sleep(CLOCK_TRANSITION_TIME);

    final CosemDateTime dateTime = this.getCosemDateTime(hexDateTime);
    final CosemDateTime returned = clock.getTime().getValue();
    /*
     * Transition from 02:59:59 to 3 o'clock is a transition from DST, so
     * the clock is set back to 2 o'clock. The hour therefore stays the
     * same.
     */
    this.doDstTansitionAssertions(dateTime, returned, 0, false);
  }

  private void doDstTansitionAssertions(
      final CosemDateTime pre,
      final CosemDateTime post,
      final int hourDifference,
      final boolean isDst) {
    assertEquals(pre.get(Field.HOUR) + hourDifference, post.get(Field.HOUR));
    assertEquals(0, post.get(Field.MINUTE));
    assertEquals(pre.get(Field.DAY_OF_MONTH), post.get(Field.DAY_OF_MONTH));
    assertEquals(pre.get(Field.MONTH), post.get(Field.MONTH));
    assertEquals(pre.get(Field.YEAR), post.get(Field.YEAR));

    if (isDst) {
      assertEquals((byte) 0x80, post.get(Field.CLOCK_STATUS));
    } else {
      assertEquals(0x00, post.get(Field.CLOCK_STATUS));
    }
  }

  private CosemDateTime getCosemDateTime(final String hexDateTime) {
    return CosemDateTime.decode(Hex.decode(hexDateTime));
  }

  private Clock getDutchClock(final String hexDateTime) throws IllegalAttributeAccessException {
    /*
     * Clock default settings are from Dutch Smart Meter Requirements
     * specification.
     */
    final Clock clock = new Clock(LocalDateTime.now());
    final byte[] input = Hex.decode(hexDateTime);
    clock.setTime(DataObject.newOctetStringData(input));

    return clock;
  }

  private Clock getAustralianClock(final String hexDateTime)
      throws IllegalAttributeAccessException {
    final Clock clock = new Clock(LocalDateTime.now());
    clock.daylightSavingsBegin = DataObject.newOctetStringData(DST_BEGIN_AUSTRALIA_SYDNEY);
    clock.daylightSavingsEnd = DataObject.newOctetStringData(DST_END_AUSTRALIA_SYDNEY);
    clock.timeZone = DataObject.newInteger16Data(TIME_ZONE_OFFSET_AUSTRALIA_SYDNEY);

    final byte[] input = Hex.decode(hexDateTime);
    clock.setTime(DataObject.newOctetStringData(input));

    return clock;
  }
}
