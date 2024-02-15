// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.objectconfig.AccessType;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsDataType;
import org.opensmartgridplatform.dlms.objectconfig.ValueType;
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
  public static final int DLMS_UNIT_UNDEFINED = 0;

  public static final int CLASS_ID = 3;
  public static final String OBIS = "1.0.32.7.0.255";
  private final int ATTRIBUTE_ID = 3;

  private final DlmsHelper dlmsHelper = new DlmsHelper();

  @Test
  void testGetWithList() throws ProtocolAdapterException, IOException {
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);
    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final DlmsDevice dlmsDevice = mock(DlmsDevice.class);
    final int withListMax = 5;
    when(dlmsDevice.getWithListMax()).thenReturn(withListMax);
    when(connectionManager.getConnection()).thenReturn(dlmsConnection);

    final AttributeAddress[] attrAddresses = new AttributeAddress[withListMax + 1];
    for (int i = 0; i <= withListMax; i++) {
      attrAddresses[i] = mock(AttributeAddress.class);
    }

    this.dlmsHelper.getWithList(connectionManager, dlmsDevice, attrAddresses);
    verify(dlmsConnection).get(Arrays.asList(attrAddresses).subList(0, withListMax));
    verify(dlmsConnection).get(Collections.singletonList(attrAddresses[withListMax]));
  }

  @ParameterizedTest
  @CsvSource({"0", "1"})
  void testNormalGetWithMultipleAddresses(final int getWithListMax)
      throws ProtocolAdapterException, IOException {
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);
    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final DlmsDevice dlmsDevice = mock(DlmsDevice.class);
    when(dlmsDevice.getWithListMax()).thenReturn(getWithListMax);
    when(connectionManager.getConnection()).thenReturn(dlmsConnection);

    final AttributeAddress[] attrAddresses = new AttributeAddress[2];
    attrAddresses[0] = mock(AttributeAddress.class);
    attrAddresses[1] = mock(AttributeAddress.class);

    this.dlmsHelper.getWithList(connectionManager, dlmsDevice, attrAddresses);

    verify(dlmsConnection).get(List.of(attrAddresses[0]));
    verify(dlmsConnection).get(List.of(attrAddresses[1]));
  }

  @ParameterizedTest
  @CsvSource({"0", "1", "2"})
  void testNormalGetWithSingleAddress(final int getWithListMax)
      throws ProtocolAdapterException, IOException {
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);
    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final DlmsDevice dlmsDevice = mock(DlmsDevice.class);
    when(dlmsDevice.getWithListMax()).thenReturn(getWithListMax);
    when(connectionManager.getConnection()).thenReturn(dlmsConnection);

    final AttributeAddress[] attrAddresses = new AttributeAddress[1];
    attrAddresses[0] = mock(AttributeAddress.class);

    this.dlmsHelper.getWithList(connectionManager, dlmsDevice, attrAddresses);

    verify(dlmsConnection).get(List.of(attrAddresses[0]));
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
  void testGetScaledMeterValueWithUndefinedUnit() {
    final GetResultImpl getResultValue = new GetResultImpl(DataObject.newUInteger16Data(21));
    final GetResultImpl getResultScalerUnit =
        new GetResultImpl(
            DataObject.newStructureData(
                DataObject.newInteger8Data((byte) -1),
                DataObject.newEnumerateData(DLMS_UNIT_UNDEFINED)));

    final Exception exception =
        assertThrows(
            ProtocolAdapterException.class,
            () -> {
              this.dlmsHelper.getScaledMeterValue(
                  getResultValue, getResultScalerUnit, "getScaledMeterValueTest");
            });
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

  @Test
  void testGetScalerUnit() {
    final DataObject wrongType = DataObject.newBoolData(false);
    final DataObject structureWithOnlyOneElement =
        DataObject.newStructureData(DataObject.newInteger8Data((byte) 2));
    final DataObject unitUndefined =
        DataObject.newStructureData(
            DataObject.newInteger8Data((byte) 2), DataObject.newEnumerateData(0));

    assertThrows(
        ProtocolAdapterException.class,
        () -> {
          this.dlmsHelper.getScalerUnit(wrongType, "getScalerUnitTest");
        });

    assertThrows(
        ProtocolAdapterException.class,
        () -> {
          this.dlmsHelper.getScalerUnit(structureWithOnlyOneElement, "getScalerUnitTest");
        });

    assertThrows(
        ProtocolAdapterException.class,
        () -> {
          this.dlmsHelper.getScalerUnit(unitUndefined, "getScalerUnitTest");
        });
  }

  void getScalerUnitValueFixedInProfile() throws ProtocolAdapterException {
    final int scaler = 0;
    final DlmsUnitTypeDto unit = DlmsUnitTypeDto.VOLT;

    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);

    final CosemObject cosemObject =
        this.newCosemObject(ValueType.FIXED_IN_PROFILE, scaler + ", " + unit.getUnit());

    final String result = this.dlmsHelper.getScalerUnitValue(connectionManager, cosemObject);
    assertThat(result).isEqualTo(scaler + ", " + unit.getUnit());
    verifyNoInteractions(connectionManager);
  }

  @Test
  void getScalerUnitValue() throws ProtocolAdapterException, IOException {
    final int scaler = -1;
    final DlmsUnitTypeDto unit = DlmsUnitTypeDto.VOLT;

    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final DataObject dataObject =
        DataObject.newArrayData(
            List.of(
                DataObject.newInteger32Data(scaler), DataObject.newInteger32Data(unit.getIndex())));
    this.mockGetAttribute(connectionManager, dataObject);

    final CosemObject cosemObject = this.newCosemObject(ValueType.DYNAMIC, "0, V");

    final String result = this.dlmsHelper.getScalerUnitValue(connectionManager, cosemObject);
    assertThat(result).isEqualTo(scaler + ", " + unit.getUnit());
  }

  @Test
  void getScalerUnitValueWrongDataType() throws ProtocolAdapterException, IOException {
    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final DataObject dataObject = DataObject.newInteger32Data(666);
    this.mockGetAttribute(connectionManager, dataObject);

    final CosemObject cosemObject = this.newCosemObject(ValueType.DYNAMIC, "0, V");

    final ProtocolAdapterException protocolAdapterException =
        assertThrows(
            ProtocolAdapterException.class,
            () -> {
              this.dlmsHelper.getScalerUnitValue(connectionManager, cosemObject);
            });
    assertThat(protocolAdapterException.getMessage())
        .contains("complex data (structure) expected while retrieving scaler and unit.");
  }

  @Test
  void getScalerUnitValueWrongSize() throws ProtocolAdapterException, IOException {
    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final DataObject dataObject =
        DataObject.newArrayData(List.of(DataObject.newInteger32Data(666)));
    this.mockGetAttribute(connectionManager, dataObject);

    final CosemObject cosemObject = this.newCosemObject(ValueType.DYNAMIC, "0, V");

    final ProtocolAdapterException protocolAdapterException =
        assertThrows(
            ProtocolAdapterException.class,
            () -> {
              this.dlmsHelper.getScalerUnitValue(connectionManager, cosemObject);
            });
    assertThat(protocolAdapterException.getMessage())
        .contains("expected 2 values while retrieving scaler and unit.");
  }

  @Test
  void getScalerUnitValueFunctionalException() throws ProtocolAdapterException, IOException {
    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);
    final GetResult getResult =
        new GetResultImpl(DataObject.newNullData(), AccessResultCode.OTHER_REASON);
    when(dlmsConnection.get(any(AttributeAddress.class))).thenReturn(getResult);
    when(connectionManager.getConnection()).thenReturn(dlmsConnection);

    final CosemObject cosemObject = this.newCosemObject(ValueType.DYNAMIC, "0, V");

    final ProtocolAdapterException protocolAdapterException =
        assertThrows(
            ProtocolAdapterException.class,
            () -> {
              this.dlmsHelper.getScalerUnitValue(connectionManager, cosemObject);
            });
    assertThat(protocolAdapterException.getMessage())
        .contains("FunctionalException occurred when reading dynamic scalar unit for object");
  }

  private void mockGetAttribute(
      final DlmsConnectionManager connectionManager, final DataObject dataObject)
      throws IOException {
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);
    final GetResult getResult = new GetResultImpl(dataObject, AccessResultCode.SUCCESS);
    when(dlmsConnection.get(any(AttributeAddress.class))).thenReturn(getResult);
    when(connectionManager.getConnection()).thenReturn(dlmsConnection);
  }

  private CosemObject newCosemObject(final ValueType valueType, final String value) {
    final Attribute attribute =
        new Attribute(
            this.ATTRIBUTE_ID,
            "descr",
            null,
            DlmsDataType.DONT_CARE,
            valueType,
            value,
            null,
            AccessType.RW);
    return new CosemObject(
        "TAG", "descr", CLASS_ID, 0, OBIS, "", null, List.of(), Map.of(), List.of(attribute));
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

    when(dlmsDevice.getWithListMax()).thenReturn(32);
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
