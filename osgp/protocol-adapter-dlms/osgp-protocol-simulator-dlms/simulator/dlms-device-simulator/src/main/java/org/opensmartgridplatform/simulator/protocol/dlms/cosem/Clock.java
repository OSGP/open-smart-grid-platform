// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemSnInterfaceObject;
import org.openmuc.jdlms.IllegalAttributeAccessException;
import org.openmuc.jdlms.datatypes.CosemDateFormat.Field;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.datetime.CompleteDateTimeAdjuster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CosemClass(id = 8)
public class Clock extends CosemSnInterfaceObject {

  private static final Logger LOGGER = LoggerFactory.getLogger(Clock.class);

  private static final byte[] LAST_SUNDAY_IN_MARCH_AT_0200 =
      new byte[] {
        (byte) 0xFF,
        (byte) 0xFF,
        0x03,
        (byte) 0xFE,
        0x07,
        0x02,
        0x00,
        0x00,
        0x00,
        (byte) 0xFF,
        (byte) 0xC4,
        (byte) 0x00
      };

  private static final byte[] LAST_SUNDAY_IN_OCTOBER_AT_0300 =
      new byte[] {
        (byte) 0xFF,
        (byte) 0xFF,
        0x0A,
        (byte) 0xFE,
        0x07,
        0x03,
        0x00,
        0x00,
        0x00,
        (byte) 0xFF,
        (byte) 0x88,
        (byte) 0x80
      };

  private static final int HUNDREDTHS_TO_NANO_CONVERSION = 10000000;
  public static final String LOGICAL_NAME_CLOCK = "0.0.1.0.0.255";

  /**
   * CosemAttribute annotation must be set on a FIELD. This field will not be returned due to the
   * existence of getTime() which returns a new instance.
   */
  @CosemAttribute(id = 2, type = Type.DATE_TIME, snOffset = 0x08)
  private final DataObject time = DataObject.newNullData();

  @CosemAttribute(id = 3, type = Type.LONG_INTEGER, snOffset = 0x10)
  public DataObject timeZone = DataObject.newInteger16Data((short) -60);

  @CosemAttribute(id = 5, type = Type.OCTET_STRING, snOffset = 0x20)
  public DataObject daylightSavingsBegin =
      DataObject.newOctetStringData(LAST_SUNDAY_IN_MARCH_AT_0200);

  @CosemAttribute(id = 6, type = Type.OCTET_STRING, snOffset = 0x28)
  public DataObject daylightSavingsEnd =
      DataObject.newOctetStringData(LAST_SUNDAY_IN_OCTOBER_AT_0300);

  @CosemAttribute(
      id = 7,
      type = Type.INTEGER,
      accessMode = AttributeAccessMode.READ_ONLY,
      snOffset = 0x30)
  public DataObject daylightSavingsDeviation = DataObject.newInteger8Data((byte) 60);

  /** Daylight savings feature enabled. */
  @CosemAttribute(id = 8, type = Type.BOOLEAN, snOffset = 0x38)
  public DataObject daylightSavingsEnabled = DataObject.newBoolData(true);

  /**
   * UTC date and time, used as internal time of the device without time zone and DST corrections
   * applied.
   */
  private LocalDateTime internalUtcDateTime;

  private Instant lastCalculation;

  /**
   * Constructor
   *
   * @param internalUtcDateTime UTC date and time, used as internal time of the device without time
   *     zone and DST corrections applied.
   */
  public Clock(final LocalDateTime internalUtcDateTime) {
    super(0x2BC0, LOGICAL_NAME_CLOCK);
    this.setInternalUtcDateTime(internalUtcDateTime);
  }

  public DataObject getTime() {
    this.advanceTime();
    return DataObject.newDateTimeData(this.fromLocalDateTime(this.internalUtcDateTime));
  }

  public void setTime(final DataObject time) throws IllegalAttributeAccessException {
    try {
      final CosemDateTime dateTime = CosemDateTime.decode(time.getValue());
      this.setInternalUtcDateTime(this.fromCosemDateTime(dateTime));
    } catch (final Exception e) {
      LOGGER.error("An error occured while setting the time attribute of CurrentClock.", e);
      throw new IllegalAttributeAccessException(AccessResultCode.OTHER_REASON);
    }
  }

  private void advanceTime() {
    final Duration duration = Duration.between(this.lastCalculation, Instant.now());
    this.setInternalUtcDateTime(this.internalUtcDateTime.plus(duration));
  }

  private void setInternalUtcDateTime(final LocalDateTime internalUtcDateTime) {
    this.internalUtcDateTime = internalUtcDateTime;
    this.lastCalculation = Instant.now();
  }

  private short getTimeZoneValue() {
    return this.timeZone.getValue();
  }

  private byte getDaylightSavingsDeviationValue() {
    return this.daylightSavingsDeviation.getValue();
  }

  private boolean getDaylightSavingsEnabledValue() {
    return (boolean) this.daylightSavingsEnabled.getValue();
  }

  private boolean inDaylightSavingsTime(final LocalDateTime dateTime) {
    if (!this.getDaylightSavingsEnabledValue()) {
      return false;
    }

    LocalDateTime dstBegin = this.fromDateTimeBytes(this.daylightSavingsBegin.getValue());
    LocalDateTime dstEnd = this.fromDateTimeBytes(this.daylightSavingsEnd.getValue());

    /*
     * Correct year for the southern hemisphere, where the DST end is before
     * the DST begin in the same year.
     */
    if (dstEnd.isBefore(dstBegin)) {
      if (dateTime.isAfter(dstBegin)) {
        dstEnd = dstEnd.plusYears(1L);
      } else {
        dstBegin = dstBegin.minusYears(1L);
      }
    }

    return dateTime.isAfter(dstBegin) && dateTime.isBefore(dstEnd);
  }

  private CosemDateTime fromLocalDateTime(final LocalDateTime dateTime) {
    int deviation = this.getTimeZoneValue();

    final List<CosemDateTime.ClockStatus> clockStatus = new ArrayList<>();
    if (this.inDaylightSavingsTime(dateTime)) {
      clockStatus.add(CosemDateTime.ClockStatus.DAYLIGHT_SAVING_ACTIVE);
      deviation -= this.getDaylightSavingsDeviationValue();
    }

    final LocalDateTime calculatedDateTime = dateTime.minusMinutes(deviation);

    return new CosemDateTime(
        calculatedDateTime.getYear(),
        calculatedDateTime.getMonthValue(),
        calculatedDateTime.getDayOfMonth(),
        calculatedDateTime.getDayOfWeek().getValue(),
        calculatedDateTime.getHour(),
        calculatedDateTime.getMinute(),
        calculatedDateTime.getSecond(),
        calculatedDateTime.getNano() / HUNDREDTHS_TO_NANO_CONVERSION,
        deviation,
        clockStatus.toArray(new CosemDateTime.ClockStatus[0]));
  }

  private LocalDateTime fromCosemDateTime(final CosemDateTime dateTime) {
    LocalDateTime result =
        LocalDateTime.of(
            dateTime.get(Field.YEAR),
            dateTime.get(Field.MONTH),
            dateTime.get(Field.DAY_OF_MONTH),
            dateTime.get(Field.HOUR),
            dateTime.get(Field.MINUTE),
            dateTime.get(Field.SECOND),
            dateTime.get(Field.HUNDREDTHS) * HUNDREDTHS_TO_NANO_CONVERSION);

    int deviation = this.getTimeZoneValue();
    if (this.isDaylightSavingStatusSet(dateTime)) {
      if (!this.inDaylightSavingsTime(this.fromDateTimeBytes(dateTime.encode()))) {
        throw new IllegalArgumentException(
            "CosemDateTime with DST flag set must be inside DST period.");
      }

      deviation -= this.getDaylightSavingsDeviationValue();
    }
    result = result.plusMinutes(deviation);

    /*
     * Check if total deviation of given time matches the local clock
     * settings.
     */
    final int receivedDeviation = dateTime.get(Field.DEVIATION);
    if (deviation != receivedDeviation) {
      throw new IllegalArgumentException(
          "Timezone and DST deviations do not match with device configuration.");
    }

    return result;
  }

  private LocalDateTime fromDateTimeBytes(final byte[] dateTime) {
    return this.internalUtcDateTime.with(new CompleteDateTimeAdjuster(dateTime));
  }

  private boolean isDaylightSavingStatusSet(final CosemDateTime dateTime) {
    return (dateTime.get(Field.CLOCK_STATUS) & 0x80) == 0x80;
  }
}
