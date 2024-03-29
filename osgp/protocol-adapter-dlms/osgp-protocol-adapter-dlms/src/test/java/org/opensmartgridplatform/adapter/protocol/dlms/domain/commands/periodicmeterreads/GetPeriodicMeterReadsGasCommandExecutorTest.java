// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.AmrProfileStatusCodeHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsDateTimeConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.valueobjects.CombinedDeviceModelCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.valueobjects.CombinedDeviceModelCode.CombinedDeviceModelCodeBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute;
import org.opensmartgridplatform.dlms.objectconfig.AccessType;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CaptureObject;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsDataType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.ValueType;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.ExtendedRegister;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.ProfileGeneric;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasResponseItemDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetPeriodicMeterReadsGasCommandExecutorTest {

  @InjectMocks private GetPeriodicMeterReadsGasCommandExecutor executor;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsHelper dlmsHelper;

  @Mock private ObjectConfigService objectConfigService;

  @Mock private DlmsConnectionManager connectionManager;

  @Mock private AmrProfileStatusCodeHelper amrProfileStatusCodeHelper;

  private final DlmsDevice device = this.createDevice(Protocol.DSMR_4_2_2);
  private final long from = 1111110L;
  private final long to = 2222222L;
  private final DateTime fromDateTime = new DateTime(this.from);
  private final DateTime toDateTime = new DateTime(this.to);
  private MessageMetadata messageMetadata;

  private static final int CLASS_ID_DATA = 1;
  private static final int CLASS_ID_EXTENDED_REGISTER = 4;
  private static final int CLASS_ID_PROFILE_GENERIC = 7;
  private static final int CLASS_ID_CLOCK = 8;

  private static final String SCALER_UNIT_DYNAMIC = "-3, M3";
  private static final String SCALER_UNIT_FIXED = "0, M3";

  @BeforeEach
  public void setUp() {

    final CombinedDeviceModelCode.CombinedDeviceModelCodeBuilder builder =
        new CombinedDeviceModelCodeBuilder();

    final CombinedDeviceModelCode combinedDeviceModelCode =
        builder
            .gatewayDeviceModelCode("GateWayDeviceModelCode")
            .channelBasedDeviceModelCode(1, "DeviceModelCh1")
            .channelBasedDeviceModelCode(2, "")
            .channelBasedDeviceModelCode(3, "")
            .channelBasedDeviceModelCode(4, "")
            .build();
    this.messageMetadata =
        MessageMetadata.newBuilder()
            .withCorrelationUid("123456")
            .withDeviceModelCode(combinedDeviceModelCode.toString())
            .build();
    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
  }

  @Test
  void testExecuteNullRequest() throws ProtocolAdapterException {
    try {
      this.executor.execute(this.connectionManager, this.device, null, this.messageMetadata);
      fail("Calling execute with null query should fail");
    } catch (final IllegalArgumentException e) {
      // expected exception
    }
  }

  @Test
  void testExecuteObjectNotFound() throws ProtocolAdapterException, ObjectConfigException {
    // SETUP
    final PeriodicMeterReadsRequestDto request =
        new PeriodicMeterReadsRequestDto(
            PeriodTypeDto.DAILY,
            this.fromDateTime.toDate(),
            this.toDateTime.toDate(),
            ChannelDto.ONE);
    when(this.objectConfigService.getOptionalCosemObject(any(), any(), any()))
        .thenReturn(Optional.empty());
    when(this.objectConfigService.getCosemObject(any(), any(), any()))
        .thenThrow(new IllegalArgumentException("Object not found"));

    // CALL
    try {
      this.executor.execute(this.connectionManager, this.device, request, this.messageMetadata);
      fail("When no matching profile object is found, then execute should fail");
    } catch (final IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("Object not found");
    }
  }

  @ParameterizedTest
  @CsvSource({"UTC,FIXED_IN_PROFILE", "Australia/Tasmania,DYNAMIC"})
  void testHappyWithDifferentTimeZones(final String timeZone, final ValueType valueType)
      throws Exception {

    // SETUP - request
    final PeriodTypeDto periodType = PeriodTypeDto.DAILY;
    final ChannelDto channel = ChannelDto.ONE;
    final PeriodicMeterReadsRequestDto request =
        new PeriodicMeterReadsRequestDto(
            periodType, this.fromDateTime.toDate(), this.toDateTime.toDate(), channel);

    this.device.setTimezone(timeZone);
    final DateTime convertedFromTime =
        DlmsDateTimeConverter.toDateTime(new Date(this.from), this.device.getTimezone());
    final DateTime convertedToTime =
        DlmsDateTimeConverter.toDateTime(new Date(this.to), this.device.getTimezone());

    // SETUP - dlms objects
    final ProfileGeneric profile = this.createProfile();
    final CosemObject clock = this.createClock();
    final CosemObject status = this.createStatus();
    final ExtendedRegister value_g = this.createMBusMasterValue(valueType);

    final CaptureObject captureObjectClock = new CaptureObject(clock, 2);
    final CaptureObject captureObjectStatus = new CaptureObject(status, 2);
    final CaptureObject captureObjectValue = new CaptureObject(value_g, 2);
    final CaptureObject captureObjectScalerUnit = new CaptureObject(value_g, 5);

    // SETUP - mock dlms object config to return attribute addresses
    when(this.objectConfigService.getOptionalCosemObject(
            "DSMR", "4.2.2", DlmsObjectType.DAILY_VALUES_G))
        .thenReturn(Optional.of(profile));
    when(this.objectConfigService.getCosemObject("DSMR", "4.2.2", DlmsObjectType.CLOCK))
        .thenReturn(clock);
    when(this.objectConfigService.getCosemObject(
            "DSMR", "4.2.2", DlmsObjectType.AMR_PROFILE_STATUS))
        .thenReturn(status);
    when(this.objectConfigService.getCosemObject("DSMR", "4.2.2", DlmsObjectType.MBUS_MASTER_VALUE))
        .thenReturn(value_g);
    when(this.objectConfigService.getCaptureObjects(profile, "DSMR", "4.2.2", "DeviceModelCh1"))
        .thenReturn(
            List.of(
                captureObjectClock,
                captureObjectStatus,
                captureObjectValue,
                captureObjectScalerUnit));

    when(this.dlmsHelper.getAccessSelectionTimeRangeParameter(
            eq(this.fromDateTime), eq(this.toDateTime), any()))
        .thenReturn(mock(DataObject.class));

    // SETUP - mock dlms helper to return data objects on request
    final DataObject data0 = mock(DataObject.class); // clock
    final DataObject data1 = mock(DataObject.class); // status
    final DataObject data2 = mock(DataObject.class); // value
    final DataObject data3 = mock(DataObject.class); // capture time
    final DataObject bufferedObject1 = mock(DataObject.class);
    when(bufferedObject1.getValue()).thenReturn(List.of(data0, data1, data2, data3));
    when(data1.isNumber()).thenReturn(true);
    when(data1.getValue()).thenReturn(0);

    final DataObject data4 = mock(DataObject.class); // clock
    final DataObject data5 = mock(DataObject.class); // status
    final DataObject data6 = mock(DataObject.class); // value
    final DataObject data7 = mock(DataObject.class); // capture time
    final DataObject bufferedObject2 = mock(DataObject.class);
    when(bufferedObject2.getValue()).thenReturn(List.of(data4, data5, data6, data7));
    when(data5.isNumber()).thenReturn(true);
    when(data5.getValue()).thenReturn(0);

    final DataObject data8 = mock(DataObject.class); // clock
    final DataObject data9 = mock(DataObject.class); // status
    final DataObject data10 = mock(DataObject.class); // value
    final DataObject data11 = mock(DataObject.class); // capture time
    final DataObject bufferedObject3 = mock(DataObject.class);
    when(bufferedObject3.getValue()).thenReturn(List.of(data8, data9, data10, data11));

    final DataObject resultData = mock(DataObject.class);
    when(resultData.getValue()).thenReturn(List.of(bufferedObject1, bufferedObject2));

    final String expectedDescription =
        "retrieve periodic meter reads for "
            + periodType
            + ", channel "
            + channel.getChannelNumber();
    final GetResult getResult = mock(GetResult.class);
    when(this.dlmsHelper.getAndCheck(
            eq(this.connectionManager), eq(this.device), eq(expectedDescription), any()))
        .thenReturn(List.of(getResult));

    when(this.dlmsHelper.readDataObject(eq(getResult), any(String.class))).thenReturn(resultData);

    // SETUP - mock dlms helper to return data objects for scaler units on request
    final String scalerUnit =
        valueType.equals(ValueType.DYNAMIC) ? SCALER_UNIT_DYNAMIC : SCALER_UNIT_FIXED;
    final GetResult getResultScalerUnits = mock(GetResult.class);
    final AttributeAddress expectedAttributeAddressScalerUnit =
        new AttributeAddress(
            CLASS_ID_EXTENDED_REGISTER,
            new ObisCode("0.1.24.1.0.255"),
            ExtendedRegisterAttribute.SCALER_UNIT.attributeId(),
            null);
    when(this.dlmsHelper.getWithList(
            eq(this.connectionManager), eq(this.device), refEq(expectedAttributeAddressScalerUnit)))
        .thenReturn(List.of(getResultScalerUnits));
    when(getResultScalerUnits.getResultCode()).thenReturn(AccessResultCode.SUCCESS);

    when(this.dlmsHelper.getScalerUnit(any(), any())).thenReturn(scalerUnit);

    // Make mocks return different times for each meterread. The last meterread has a time
    // outside the requested period, causing the meterread to be not included in the result.
    final CosemDateTimeDto timeMeterRead1 = new CosemDateTimeDto(this.fromDateTime);
    final CosemDateTimeDto timeMeterRead2 = new CosemDateTimeDto(this.fromDateTime.plusMinutes(1));
    final CosemDateTimeDto timeMeterRead3 = new CosemDateTimeDto(this.fromDateTime.plusYears(1));
    when(this.dlmsHelper.readDateTime(eq(data0), any())).thenReturn(timeMeterRead1);
    when(this.dlmsHelper.readDateTime(eq(data3), any())).thenReturn(timeMeterRead1);
    when(this.dlmsHelper.readDateTime(eq(data4), any())).thenReturn(timeMeterRead2);
    when(this.dlmsHelper.readDateTime(eq(data7), any())).thenReturn(timeMeterRead2);
    when(this.dlmsHelper.readDateTime(eq(data8), any())).thenReturn(timeMeterRead3);
    when(this.dlmsHelper.readDateTime(eq(data11), any())).thenReturn(timeMeterRead3);

    when(this.amrProfileStatusCodeHelper.toAmrProfileStatusCodeFlags(0)).thenReturn(Set.of());

    final DlmsMeterValueDto meterValue1 = mock(DlmsMeterValueDto.class);
    final DlmsMeterValueDto meterValue2 = mock(DlmsMeterValueDto.class);
    when(this.dlmsHelper.getScaledMeterValueWithScalerUnit(data2, scalerUnit, "gasValue"))
        .thenReturn(meterValue1);
    when(this.dlmsHelper.getScaledMeterValueWithScalerUnit(data6, scalerUnit, "gasValue"))
        .thenReturn(meterValue2);

    // CALL
    final PeriodicMeterReadGasResponseDto result =
        this.executor.execute(this.connectionManager, this.device, request, this.messageMetadata);

    // VERIFY - the right functions should be called
    verify(this.dlmsMessageListener)
        .setDescription(
            String.format(
                "GetPeriodicMeterReadsGas for channel ONE, DAILY from %s until %s, retrieve attribute: {%s,%s,%s}",
                convertedFromTime,
                convertedToTime,
                profile.getClassId(),
                profile.getObis().replace("x", String.valueOf(channel.getChannelNumber())),
                2));

    // Expect only one call to retrieve the values from the buffer
    verify(this.dlmsHelper, times(1))
        .getAndCheck(eq(this.connectionManager), eq(this.device), any(), any());

    // If a dynamic scalerUnit is used then expect 1 additional call
    verify(this.dlmsHelper, times(valueType == ValueType.DYNAMIC ? 1 : 0))
        .getWithList(eq(this.connectionManager), eq(this.device), any());

    // ASSERT - the result should contain 2 values
    final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads =
        result.getPeriodicMeterReadsGas();

    // Only 2 meterreads are expected. The 3rd meterread has a logtime outside the requested period.
    assertThat(periodicMeterReads).hasSize(2);

    assertThat(periodicMeterReads.stream().anyMatch(r -> r.getConsumption() == meterValue1))
        .isTrue();
    assertThat(periodicMeterReads.stream().anyMatch(r -> r.getConsumption() == meterValue2))
        .isTrue();
    assertThat(
            periodicMeterReads.stream()
                .filter(r -> r.getConsumption() == meterValue1)
                .allMatch(
                    r ->
                        this.areDatesEqual(r.getLogTime(), timeMeterRead1)
                            && this.areDatesEqual(r.getCaptureTime(), timeMeterRead1)))
        .isTrue();
    assertThat(
            periodicMeterReads.stream()
                .filter(r -> r.getConsumption() == meterValue2)
                .allMatch(
                    r ->
                        this.areDatesEqual(r.getLogTime(), timeMeterRead2)
                            && this.areDatesEqual(r.getCaptureTime(), timeMeterRead2)))
        .isTrue();
  }

  private CosemObject createCosemObject(
      final int classId,
      final String tag,
      final String obis,
      final String group,
      final List<Attribute> attributes) {
    return new CosemObject(
        tag, "descr", classId, 0, obis, group, null, List.of(), Map.of(), attributes);
  }

  private Attribute createAttribute(final int id, final String value) {
    return this.createAttribute(id, value, ValueType.FIXED_IN_PROFILE);
  }

  private Attribute createAttribute(final int id, final String value, final ValueType valueType) {
    return new Attribute(
        id, "descr", null, DlmsDataType.DONT_CARE, valueType, value, null, AccessType.RW);
  }

  private DlmsDevice createDevice(final Protocol protocol) {
    final DlmsDevice device = new DlmsDevice();
    device.setSelectiveAccessPeriodicMeterReadsSupported(true);
    device.setProtocol(protocol);
    return device;
  }

  private ProfileGeneric createProfile() {
    final Attribute attributeCaptureObjects =
        this.createAttribute(
            3, "CLOCK,2|AMR_PROFILE_STATUS,2|MBUS_MASTER_VALUE,2|MBUS_MASTER_VALUE,5");
    final Attribute attributeCapturePeriod = this.createAttribute(4, "86400");
    return new ProfileGeneric(
        "DAILY_VALUES_G",
        "descr",
        this.CLASS_ID_PROFILE_GENERIC,
        0,
        "1.x.3.4.5.6",
        "GAS",
        null,
        List.of(),
        Map.of(),
        List.of(attributeCaptureObjects, attributeCapturePeriod));
  }

  private CosemObject createClock() {
    return this.createCosemObject(
        this.CLASS_ID_CLOCK, "CLOCK", "0.0.1.0.0.255", "ABSTRACT", List.of());
  }

  private CosemObject createStatus() {
    return this.createCosemObject(
        this.CLASS_ID_DATA, "AMR_PROFILE_STATUS", "0.x.1.2.3.255", "GAS", List.of());
  }

  private ExtendedRegister createMBusMasterValue(final ValueType valueType) {
    final Attribute attributeScalerUnit = this.createAttribute(3, "0, M3", valueType);
    return new ExtendedRegister(
        "MBUS_MASTER_VALUE",
        "descr",
        this.CLASS_ID_EXTENDED_REGISTER,
        0,
        "0.x.24.1.0.255",
        "GAS",
        null,
        List.of(),
        Map.of(),
        List.of(attributeScalerUnit));
  }

  // Compares date with cosemDateTime. Note: cosemDateTime uses hundredths and
  // not milliseconds
  private boolean areDatesEqual(final Date date, final CosemDateTimeDto cosemDateTime) {
    final DateTime dateTime = new DateTime(date);
    final CosemDateDto cosemDate = cosemDateTime.getDate();
    final CosemTimeDto cosemTime = cosemDateTime.getTime();

    return (dateTime.getYear() == cosemDate.getYear()
        && dateTime.getMonthOfYear() == cosemDate.getMonth()
        && dateTime.getDayOfMonth() == cosemDate.getDayOfMonth()
        && dateTime.getHourOfDay() == cosemTime.getHour()
        && dateTime.getMinuteOfHour() == cosemTime.getMinute()
        && dateTime.getSecondOfMinute() == cosemTime.getSecond()
        && dateTime.getMillisOfSecond() == cosemTime.getHundredths() * 10);
  }
}
