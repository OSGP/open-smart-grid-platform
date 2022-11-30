/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClockStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;

public class DlmsHelperTest {

  public static final DateTimeZone DATE_TIME_ZONE_AMSTERDAM =
      DateTimeZone.forID("Europe/Amsterdam");
  public static final DateTimeZone DATE_TIME_ZONE_NEW_YORK = DateTimeZone.forID("America/New_York");
  public static final DateTimeZone DATE_TIME_ZONE_UTC = DateTimeZone.UTC;
  public static final short YEAR = 2015;
  public static final byte MONTH_SUMMER_TIME = 7;
  public static final byte MONTH_WINTER_TIME = 2;
  public static final byte DAY = 21;
  public static final byte HOUR = 14;
  public static final byte MINUTE = 53;
  public static final byte SECOND = 7;
  public static final byte HUNDREDTHS = 23;

  public static final int NUM_BYTES_DATE_TIME = 12;
  public static final byte CLOCK_STATUS_DST = (byte) 0x80;
  public static final byte CLOCK_STATUS_NO_DST = 0;
  public static final byte DAY_OF_WEEK_UNDEFINED = (byte) 0xFF;
  public static final short DEVIATION_AMSTERDAM_SUMMER_TIME = -120;
  public static final short DEVIATION_AMSTERDAM_WINTER_TIME = -60;

  public static final int DLMS_UNIT_VAR = 29;
  public static final int DLMS_UNIT_WH = 30;

  private final DlmsHelper dlmsHelper = new DlmsHelper();

  @Test
  public void testGetWithListSupported() throws ProtocolAdapterException, IOException {
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);
    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final DlmsDevice dlmsDevice = mock(DlmsDevice.class);
    when(connectionManager.getConnection()).thenReturn(dlmsConnection);

    final AttributeAddress[] attrAddresses = new AttributeAddress[1];
    attrAddresses[0] = mock(AttributeAddress.class);

    when(dlmsDevice.isWithListSupported()).thenReturn(true);

    this.dlmsHelper.getWithList(connectionManager, dlmsDevice, attrAddresses);
    verify(dlmsConnection).get(Arrays.asList(attrAddresses));
  }

  @Test
  public void testGetWithListWorkaround() throws ProtocolAdapterException, IOException {
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);
    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final DlmsDevice dlmsDevice = mock(DlmsDevice.class);
    when(connectionManager.getConnection()).thenReturn(dlmsConnection);

    final AttributeAddress[] attrAddresses = new AttributeAddress[1];
    attrAddresses[0] = mock(AttributeAddress.class);

    when(dlmsDevice.isWithListSupported()).thenReturn(false);

    this.dlmsHelper.getWithList(connectionManager, dlmsDevice, attrAddresses);
    verify(dlmsConnection).get(attrAddresses[0]);
  }

  /*
   * this test is here because the jDMLS code throws a NullPointerException
   * instead of a ResponseTimeoutException (specific type of IOException via
   * NonFatalJDlmsException and JDlmsException).
   */
  @Test
  public void testGetWithListException() throws IOException {
    this.assertGetWithListException(IOException.class, ConnectionException.class);
    this.assertGetWithListException(NullPointerException.class, ConnectionException.class);
    this.assertGetWithListException(RuntimeException.class, ProtocolAdapterException.class);
  }

  @Test
  public void testDateTimeSummerTime() {
    final DataObject dateInSummerTimeDataObject =
        this.dlmsHelper.asDataObject(this.dateTimeSummerTime());

    assertThat(dateInSummerTimeDataObject.isCosemDateFormat()).isTrue();
    assertThat(dateInSummerTimeDataObject.getValue() instanceof CosemDateTime).isTrue();

    final CosemDateTime cosemDateTime = dateInSummerTimeDataObject.getValue();

    assertThat(cosemDateTime.encode()).isEqualTo(this.byteArraySummerTime());
  }

  @Test
  public void testDateTimeSummerTimeWithZonedTime() {
    final DataObject dateInSummerTimeDataObject =
        this.dlmsHelper.asDataObject(this.convertToZonedDateTime(this.dateTimeSummerTime()));

    assertThat(dateInSummerTimeDataObject.isCosemDateFormat()).isTrue();
    assertThat(dateInSummerTimeDataObject.getValue() instanceof CosemDateTime).isTrue();

    final CosemDateTime cosemDateTime = dateInSummerTimeDataObject.getValue();

    assertThat(cosemDateTime.encode()).isEqualTo(this.byteArraySummerTime());
  }

  @Test
  public void testDateTimeWinterTime() {
    final DataObject dateInWinterTimeDataObject =
        this.dlmsHelper.asDataObject(this.dateTimeWinterTime());

    assertThat(dateInWinterTimeDataObject.isCosemDateFormat()).isTrue();
    assertThat(dateInWinterTimeDataObject.getValue() instanceof CosemDateTime).isTrue();

    final CosemDateTime cosemDateTime = dateInWinterTimeDataObject.getValue();

    assertThat(cosemDateTime.encode()).isEqualTo(this.byteArrayWinterTime());
  }

  @Test
  public void testDateTimeWinterTimeWithZonedTime() {
    final DateTime dateTime = this.dateTimeWinterTime();
    final ZonedDateTime zonedDateTime = this.convertToZonedDateTime(dateTime);

    final DataObject dateInWinterTimeDataObject = this.dlmsHelper.asDataObject(zonedDateTime);

    assertThat(dateInWinterTimeDataObject.isCosemDateFormat()).isTrue();
    assertThat(dateInWinterTimeDataObject.getValue() instanceof CosemDateTime).isTrue();

    final CosemDateTime cosemDateTime = dateInWinterTimeDataObject.getValue();

    assertThat(cosemDateTime.encode()).isEqualTo(this.byteArrayWinterTime());
  }

  @Test
  public void testFromByteArraySummerTime() {
    final CosemDateTimeDto cosemDateTime =
        this.dlmsHelper.fromDateTimeValue(this.byteArraySummerTime());

    assertThat(cosemDateTime.isDateTimeSpecified()).isTrue();

    final DateTime dateInSummerTime = cosemDateTime.asDateTime();

    assertThat(ISODateTimeFormat.dateTime().print(dateInSummerTime))
        .isEqualTo("2015-07-21T14:53:07.230+02:00");
  }

  @Test
  public void testFromByteArrayWinterTime() {
    final CosemDateTimeDto cosemDateTime =
        this.dlmsHelper.fromDateTimeValue(this.byteArrayWinterTime());

    assertThat(cosemDateTime.isDateTimeSpecified()).isTrue();

    final DateTime dateInWinterTime = cosemDateTime.asDateTime();

    assertThat(ISODateTimeFormat.dateTime().print(dateInWinterTime))
        .isEqualTo("2015-02-21T14:53:07.230+01:00");
  }

  @Test
  public void testFromByteArrayUnspecifiedTime() {
    final CosemDateTimeDto cosemDateTime =
        this.dlmsHelper.fromDateTimeValue(this.byteArrayUnspecifiedTime());

    assertThat(cosemDateTime.isDateTimeSpecified()).isFalse();
    assertThat(cosemDateTime.isLocalDateSpecified()).isFalse();
    assertThat(cosemDateTime.isLocalDateTimeSpecified()).isFalse();
    assertThat(cosemDateTime.isLocalTimeSpecified()).isFalse();
    assertThat(cosemDateTime.isDeviationSpecified()).isFalse();

    assertThat(cosemDateTime.asDateTime()).isNull();
  }

  @Test
  public void testCorrectLogMessageForBitStringObject() {
    final String expected = "number of bytes=2, value=37440, bits=10010010 01000000 ";
    final String logMessage = this.dlmsHelper.getDebugInfoBitStringBytes(new byte[] {-110, 64});

    assertThat(logMessage).isEqualTo(expected);
  }

  @Test
  public void testByteArrayToHexString() throws ProtocolAdapterException {
    final byte[] bytes = new byte[] {25, 24, 7, 118};
    final DataObject dataObject = DataObject.newOctetStringData(bytes);
    final String hexString =
        this.dlmsHelper.readHexString(dataObject, "reading a Hexadecimal String");

    assertThat(hexString).isEqualTo("19180776");
  }

  @Test
  void testGetScaledMeterValue() throws ProtocolAdapterException {
    final GetResultImpl getResultValue = new GetResultImpl(DataObject.newUInteger16Data(21));
    final GetResultImpl getResultScalerUnit =
        new GetResultImpl(
            DataObject.newStructureData(
                DataObject.newInteger8Data((byte) -1), DataObject.newEnumerateData(DLMS_UNIT_WH)));

    final DlmsMeterValueDto meterValueDto =
        this.dlmsHelper.getScaledMeterValue(
            getResultValue, getResultScalerUnit, "getScaledMeterValueTest");

    assertThat(meterValueDto.getValue()).isEqualTo(BigDecimal.valueOf(2.1));
    assertThat(meterValueDto.getDlmsUnit()).isEqualTo(DlmsUnitTypeDto.KWH);
  }

  @Test
  void testGetScaledMeterValueWithSpecifiedScalerAndUnit() throws ProtocolAdapterException {
    final GetResultImpl getResultValue = new GetResultImpl(DataObject.newUInteger16Data(5));

    final DlmsMeterValueDto meterValueDto =
        this.dlmsHelper.getScaledMeterValueWithScalerUnit(
            getResultValue, "0, V", "getScaledMeterValueTest with specified scaler and unit");

    assertThat(meterValueDto.getValue()).isEqualTo(BigDecimal.valueOf(5));
    assertThat(meterValueDto.getDlmsUnit()).isEqualTo(DlmsUnitTypeDto.VOLT);
  }

  @Test
  void testGetScaledMeterValueWithDataObject() throws ProtocolAdapterException {
    final DataObject value = DataObject.newUInteger16Data(10);
    final DataObject scalerUnit =
        DataObject.newStructureData(
            DataObject.newInteger8Data((byte) 2), DataObject.newEnumerateData(DLMS_UNIT_VAR));

    final DlmsMeterValueDto meterValueDto =
        this.dlmsHelper.getScaledMeterValue(
            value, scalerUnit, "getScaledMeterValueTest with DataObject");

    assertThat(meterValueDto.getValue()).isEqualTo(BigDecimal.valueOf(1000.0));
    assertThat(meterValueDto.getDlmsUnit()).isEqualTo(DlmsUnitTypeDto.VAR);
  }

  private void assertGetWithListException(
      final Class<? extends Exception> jdlmsExceptionClazz,
      final Class<? extends Exception> exceptionClazz)
      throws IOException {
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);
    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final DlmsDevice dlmsDevice = mock(DlmsDevice.class);
    when(dlmsDevice.getDeviceIdentification()).thenReturn("666");
    when(connectionManager.getConnection()).thenReturn(dlmsConnection);

    final AttributeAddress[] attrAddresses = new AttributeAddress[1];
    attrAddresses[0] = mock(AttributeAddress.class);

    when(dlmsDevice.isWithListSupported()).thenReturn(true);
    when(dlmsConnection.get(Arrays.asList(attrAddresses))).thenThrow(jdlmsExceptionClazz);

    final Exception exception =
        assertThrows(
            exceptionClazz,
            () -> {
              this.dlmsHelper.getWithList(connectionManager, dlmsDevice, attrAddresses);
            });
    assertThat(exception.getMessage()).contains(dlmsDevice.getDeviceIdentification());
  }

  private DateTime dateTimeSummerTime() {
    return new DateTime(
        YEAR,
        MONTH_SUMMER_TIME,
        DAY,
        HOUR,
        MINUTE,
        SECOND,
        HUNDREDTHS * 10,
        DATE_TIME_ZONE_AMSTERDAM);
  }

  private DateTime dateTimeSummerTimeUtc() {
    /*
     * Original time in Europe/Amsterdam is in UTC+2 for the summer time, so
     * subtract 2 from the hour for UTC time at the same instant.
     */
    return new DateTime(
        YEAR,
        MONTH_SUMMER_TIME,
        DAY,
        HOUR - 2,
        MINUTE,
        SECOND,
        HUNDREDTHS * 10,
        DATE_TIME_ZONE_UTC);
  }

  private DateTime dateTimeWinterTime() {
    return new DateTime(
        YEAR,
        MONTH_WINTER_TIME,
        DAY,
        HOUR,
        MINUTE,
        SECOND,
        HUNDREDTHS * 10,
        DATE_TIME_ZONE_AMSTERDAM);
  }

  private DateTime dateTimeWinterTimeNewYork() {
    /*
     * New York - for the winter date time - is in UTC-5, original time in
     * Europe/Amsterdam is in UTC+1 then, so subtract 6 from the hour to get
     * New York time for the same instant.
     */
    return new DateTime(
        YEAR,
        MONTH_WINTER_TIME,
        DAY,
        HOUR - 6,
        MINUTE,
        SECOND,
        HUNDREDTHS * 10,
        DATE_TIME_ZONE_NEW_YORK);
  }

  private byte[] byteArraySummerTime() {

    final ByteBuffer bb = ByteBuffer.allocate(NUM_BYTES_DATE_TIME);
    bb.putShort(YEAR);
    bb.put(MONTH_SUMMER_TIME);
    bb.put(DAY);
    bb.put(DAY_OF_WEEK_UNDEFINED);
    bb.put(HOUR);
    bb.put(MINUTE);
    bb.put(SECOND);
    bb.put(HUNDREDTHS);
    bb.putShort(DEVIATION_AMSTERDAM_SUMMER_TIME);
    bb.put(CLOCK_STATUS_DST);

    return bb.array();
  }

  private byte[] byteArrayWinterTime() {

    final ByteBuffer bb = ByteBuffer.allocate(NUM_BYTES_DATE_TIME);
    bb.putShort(YEAR);
    bb.put(MONTH_WINTER_TIME);
    bb.put(DAY);
    bb.put(DAY_OF_WEEK_UNDEFINED);
    bb.put(HOUR);
    bb.put(MINUTE);
    bb.put(SECOND);
    bb.put(HUNDREDTHS);
    bb.putShort(DEVIATION_AMSTERDAM_WINTER_TIME);
    bb.put(CLOCK_STATUS_NO_DST);

    return bb.array();
  }

  private byte[] byteArrayUnspecifiedTime() {

    final ByteBuffer bb = ByteBuffer.allocate(NUM_BYTES_DATE_TIME);
    bb.putShort((short) CosemDateDto.YEAR_NOT_SPECIFIED);
    bb.put((byte) CosemDateDto.MONTH_NOT_SPECIFIED);
    bb.put((byte) CosemDateDto.DAY_OF_MONTH_NOT_SPECIFIED);
    bb.put((byte) CosemDateDto.DAY_OF_WEEK_NOT_SPECIFIED);
    bb.put((byte) CosemTimeDto.HOUR_NOT_SPECIFIED);
    bb.put((byte) CosemTimeDto.MINUTE_NOT_SPECIFIED);
    bb.put((byte) CosemTimeDto.SECOND_NOT_SPECIFIED);
    bb.put((byte) CosemTimeDto.HUNDREDTHS_NOT_SPECIFIED);
    bb.putShort((short) CosemDateTimeDto.DEVIATION_NOT_SPECIFIED);
    bb.put((byte) ClockStatusDto.STATUS_NOT_SPECIFIED);

    return bb.array();
  }

  private ZonedDateTime convertToZonedDateTime(final DateTime dateTime) {
    final Instant instant = Instant.ofEpochMilli(dateTime.getMillis());
    final ZoneId zoneId = ZoneId.of(dateTime.getZone().getID(), ZoneId.SHORT_IDS);
    return ZonedDateTime.ofInstant(instant, zoneId);
  }
}
