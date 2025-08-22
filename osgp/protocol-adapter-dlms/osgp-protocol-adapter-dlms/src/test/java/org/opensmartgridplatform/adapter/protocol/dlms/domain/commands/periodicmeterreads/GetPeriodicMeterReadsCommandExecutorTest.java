// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.DateTimeHelper.areDatesEqual;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.ObjectConfigServiceHelper.createClock;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.ObjectConfigServiceHelper.createObject;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_2;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_2;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.DAILY_VALUES_COMBINED;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.DAILY_VALUES_E;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
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
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.dlms.objectconfig.AccessType;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CaptureObject;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsDataType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.ValueType;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.ProfileGeneric;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.Register;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseItemDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetPeriodicMeterReadsCommandExecutorTest {

  @InjectMocks private GetPeriodicMeterReadsCommandExecutor executor;

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
  private static final int CLASS_ID_REGISTER = 3;
  private static final int CLASS_ID_PROFILE_GENERIC = 7;

  private static final String SCALER_UNIT_DYNAMIC = "-2, WH";
  private static final String SCALER_UNIT_FIXED = "0, WH";

  @BeforeEach
  void setUp() {
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();
    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
  }

  @Test()
  void testExecuteNullRequest() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            this.executor.execute(this.connectionManager, this.device, null, this.messageMetadata));
  }

  @Test
  void testExecuteObjectNotFound() throws ObjectConfigException, ProtocolAdapterException {
    // SETUP
    final PeriodicMeterReadsRequestDto request =
        new PeriodicMeterReadsRequestDto(
            PeriodTypeDto.DAILY,
            this.fromDateTime.toDate(),
            this.toDateTime.toDate(),
            ChannelDto.ONE);
    when(this.objectConfigService.getOptionalCosemObject(
            this.device.getProtocolName(), this.device.getProtocolVersion(), DAILY_VALUES_E))
        .thenReturn(Optional.empty());
    when(this.objectConfigService.getCosemObject(
            this.device.getProtocolName(), this.device.getProtocolVersion(), DAILY_VALUES_COMBINED))
        .thenThrow(new IllegalArgumentException("Object not found"));

    // CALL
    try {
      this.executor.execute(this.connectionManager, this.device, request, this.messageMetadata);
      fail("When no matching object is found, then execute should fail");
    } catch (final IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("Object not found");
    }
  }

  @Test
  void testBundle() throws ProtocolAdapterException {
    final PeriodicMeterReadsRequestDataDto request =
        new PeriodicMeterReadsRequestDataDto(
            PeriodTypeDto.DAILY, new Date(this.from), new Date(this.to));

    final PeriodicMeterReadsRequestDto dto = this.executor.fromBundleRequestInput(request);

    assertThat(dto).isNotNull();
  }

  @ParameterizedTest
  @CsvSource({"UTC,FIXED_IN_PROFILE", "Australia/Tasmania,DYNAMIC"})
  void testHappyWithDifferentTimeZones(final String timeZone, final ValueType valueType)
      throws ObjectConfigException, ProtocolAdapterException {

    // SETUP - request
    final PeriodTypeDto periodType = PeriodTypeDto.DAILY;
    final PeriodicMeterReadsRequestDto request =
        new PeriodicMeterReadsRequestDto(periodType, new Date(this.from), new Date(this.to));

    this.device.setTimezone(timeZone);
    final DateTime convertedFromTime =
        DlmsDateTimeConverter.toDateTime(new Date(this.from), this.device.getTimezone());
    final DateTime convertedToTime =
        DlmsDateTimeConverter.toDateTime(new Date(this.to), this.device.getTimezone());

    // SETUP - dlms objects
    final ProfileGeneric profile = this.createProfile();
    final CosemObject clock = createClock();
    final CosemObject status = this.createStatus();
    final Register activeEnergyImportRate1 =
        this.createRegister(ACTIVE_ENERGY_IMPORT_RATE_1, "1.0.1.8.1.255", valueType);
    final Register activeEnergyImportRate2 =
        this.createRegister(ACTIVE_ENERGY_IMPORT_RATE_2, "1.0.1.8.2.255", valueType);
    final Register activeEnergyExportRate1 =
        this.createRegister(ACTIVE_ENERGY_EXPORT_RATE_1, "1.0.2.8.1.255", valueType);
    final Register activeEnergyExportRate2 =
        this.createRegister(ACTIVE_ENERGY_EXPORT_RATE_2, "1.0.2.8.2.255", valueType);

    final CaptureObject captureObjectClock = new CaptureObject(clock, 2);
    final CaptureObject captureObjectStatus = new CaptureObject(status, 2);
    final CaptureObject captureObjectImport1 = new CaptureObject(activeEnergyImportRate1, 2);
    final CaptureObject captureObjectImport2 = new CaptureObject(activeEnergyImportRate2, 2);
    final CaptureObject captureObjectExport1 = new CaptureObject(activeEnergyExportRate1, 2);
    final CaptureObject captureObjectExport2 = new CaptureObject(activeEnergyExportRate2, 2);

    // SETUP - mock dlms object config to return attribute addresses
    when(this.objectConfigService.getOptionalCosemObject("DSMR", "4.2.2", DAILY_VALUES_E))
        .thenReturn(Optional.of(profile));
    when(this.objectConfigService.getCosemObject("DSMR", "4.2.2", DlmsObjectType.CLOCK))
        .thenReturn(clock);
    when(this.objectConfigService.getCosemObject(
            "DSMR", "4.2.2", DlmsObjectType.AMR_PROFILE_STATUS))
        .thenReturn(status);
    when(this.objectConfigService.getCosemObject("DSMR", "4.2.2", ACTIVE_ENERGY_IMPORT_RATE_1))
        .thenReturn(activeEnergyImportRate1);
    when(this.objectConfigService.getCosemObject("DSMR", "4.2.2", ACTIVE_ENERGY_IMPORT_RATE_2))
        .thenReturn(activeEnergyImportRate2);
    when(this.objectConfigService.getCosemObject("DSMR", "4.2.2", ACTIVE_ENERGY_EXPORT_RATE_1))
        .thenReturn(activeEnergyExportRate1);
    when(this.objectConfigService.getCosemObject("DSMR", "4.2.2", ACTIVE_ENERGY_EXPORT_RATE_2))
        .thenReturn(activeEnergyExportRate2);
    when(this.objectConfigService.getCaptureObjects(profile, "DSMR", "4.2.2", null))
        .thenReturn(
            List.of(
                captureObjectClock,
                captureObjectStatus,
                captureObjectImport1,
                captureObjectImport2,
                captureObjectExport1,
                captureObjectExport2));

    when(this.dlmsHelper.getAccessSelectionTimeRangeParameter(
            eq(this.fromDateTime), eq(this.toDateTime), any()))
        .thenReturn(mock(DataObject.class));

    // SETUP - mock dlms helper to return data objects on request
    final DataObject clock1 = mock(DataObject.class);
    final DataObject status1 = mock(DataObject.class);
    final DataObject import11 = mock(DataObject.class);
    final DataObject import12 = mock(DataObject.class);
    final DataObject export11 = mock(DataObject.class);
    final DataObject export12 = mock(DataObject.class);
    final DataObject bufferedObject1 = mock(DataObject.class);
    when(bufferedObject1.getValue())
        .thenReturn(List.of(clock1, status1, import11, import12, export11, export12));
    when(status1.isNumber()).thenReturn(true);
    when(status1.getValue()).thenReturn(0);

    final DataObject clock2 = mock(DataObject.class);
    final DataObject status2 = mock(DataObject.class);
    final DataObject import21 = mock(DataObject.class);
    final DataObject import22 = mock(DataObject.class);
    final DataObject export21 = mock(DataObject.class);
    final DataObject export22 = mock(DataObject.class);
    final DataObject bufferedObject2 = mock(DataObject.class);
    when(bufferedObject2.getValue())
        .thenReturn(List.of(clock2, status2, import21, import22, export21, export22));
    when(status2.isNumber()).thenReturn(true);
    when(status2.getValue()).thenReturn(0);

    final DataObject clock3 = mock(DataObject.class);

    final DataObject resultData = mock(DataObject.class);
    when(resultData.getValue()).thenReturn(List.of(bufferedObject1, bufferedObject2));

    final String expectedDescription =
        "retrieve periodic meter reads for " + periodType + ", channel 0";
    final GetResult getResult = mock(GetResult.class);
    when(this.dlmsHelper.getAndCheck(
            eq(this.connectionManager), eq(this.device), eq(expectedDescription), any()))
        .thenReturn(List.of(getResult));

    when(this.dlmsHelper.readDataObject(eq(getResult), any(String.class))).thenReturn(resultData);

    // SETUP - mock dlms helper to return data objects for scaler units on request
    final String scalerUnit =
        valueType.equals(ValueType.DYNAMIC) ? SCALER_UNIT_DYNAMIC : SCALER_UNIT_FIXED;
    final GetResult getResultScalerUnit = mock(GetResult.class);
    final AttributeAddress expectedAddrScalerUnitImport1 =
        new AttributeAddress(
            CLASS_ID_REGISTER,
            new ObisCode("1.0.1.8.1.255"),
            RegisterAttribute.SCALER_UNIT.attributeId(),
            null);
    final AttributeAddress expectedAddrScalerUnitImport2 =
        new AttributeAddress(
            CLASS_ID_REGISTER,
            new ObisCode("1.0.1.8.2.255"),
            RegisterAttribute.SCALER_UNIT.attributeId(),
            null);
    final AttributeAddress expectedAddrScalerUnitExport1 =
        new AttributeAddress(
            CLASS_ID_REGISTER,
            new ObisCode("1.0.2.8.1.255"),
            RegisterAttribute.SCALER_UNIT.attributeId(),
            null);
    final AttributeAddress expectedAddrScalerUnitExport2 =
        new AttributeAddress(
            CLASS_ID_REGISTER,
            new ObisCode("1.0.2.8.2.255"),
            RegisterAttribute.SCALER_UNIT.attributeId(),
            null);
    when(this.dlmsHelper.getWithList(
            eq(this.connectionManager),
            eq(this.device),
            refEq(expectedAddrScalerUnitImport1),
            refEq(expectedAddrScalerUnitImport2),
            refEq(expectedAddrScalerUnitExport1),
            refEq(expectedAddrScalerUnitExport2)))
        .thenReturn(
            List.of(
                getResultScalerUnit,
                getResultScalerUnit,
                getResultScalerUnit,
                getResultScalerUnit));
    when(getResultScalerUnit.getResultCode()).thenReturn(AccessResultCode.SUCCESS);

    when(this.dlmsHelper.getScalerUnit(any(), any())).thenReturn(scalerUnit);

    // Make mocks return different times for each meterread. The last meterread has a time
    // outside the requested period, causing the meterread to be not included in the result.
    final CosemDateTimeDto timeMeterRead1 = new CosemDateTimeDto(this.fromDateTime);
    final CosemDateTimeDto timeMeterRead2 = new CosemDateTimeDto(this.fromDateTime.plusMinutes(1));
    final CosemDateTimeDto timeMeterRead3 = new CosemDateTimeDto(this.fromDateTime.plusYears(1));
    when(this.dlmsHelper.readDateTime(eq(clock1), any())).thenReturn(timeMeterRead1);
    when(this.dlmsHelper.readDateTime(eq(clock2), any())).thenReturn(timeMeterRead2);
    when(this.dlmsHelper.readDateTime(eq(clock3), any())).thenReturn(timeMeterRead3);

    when(this.amrProfileStatusCodeHelper.toAmrProfileStatusCodeFlags(0)).thenReturn(Set.of());

    final DlmsMeterValueDto meterValueImport11 = mock(DlmsMeterValueDto.class);
    final DlmsMeterValueDto meterValueImport12 = mock(DlmsMeterValueDto.class);
    final DlmsMeterValueDto meterValueExport11 = mock(DlmsMeterValueDto.class);
    final DlmsMeterValueDto meterValueExport12 = mock(DlmsMeterValueDto.class);
    final DlmsMeterValueDto meterValueImport21 = mock(DlmsMeterValueDto.class);
    final DlmsMeterValueDto meterValueImport22 = mock(DlmsMeterValueDto.class);
    final DlmsMeterValueDto meterValueExport21 = mock(DlmsMeterValueDto.class);
    final DlmsMeterValueDto meterValueExport22 = mock(DlmsMeterValueDto.class);
    when(this.dlmsHelper.getScaledMeterValueWithScalerUnit(
            eq(import11), any(), eq("electricityValue")))
        .thenReturn(meterValueImport11);
    when(this.dlmsHelper.getScaledMeterValueWithScalerUnit(
            import12, scalerUnit, "electricityValue"))
        .thenReturn(meterValueImport12);
    when(this.dlmsHelper.getScaledMeterValueWithScalerUnit(
            export11, scalerUnit, "electricityValue"))
        .thenReturn(meterValueExport11);
    when(this.dlmsHelper.getScaledMeterValueWithScalerUnit(
            export12, scalerUnit, "electricityValue"))
        .thenReturn(meterValueExport12);
    when(this.dlmsHelper.getScaledMeterValueWithScalerUnit(
            import21, scalerUnit, "electricityValue"))
        .thenReturn(meterValueImport21);
    when(this.dlmsHelper.getScaledMeterValueWithScalerUnit(
            import22, scalerUnit, "electricityValue"))
        .thenReturn(meterValueImport22);
    when(this.dlmsHelper.getScaledMeterValueWithScalerUnit(
            export21, scalerUnit, "electricityValue"))
        .thenReturn(meterValueExport21);
    when(this.dlmsHelper.getScaledMeterValueWithScalerUnit(
            export22, scalerUnit, "electricityValue"))
        .thenReturn(meterValueExport22);

    // CALL
    final PeriodicMeterReadsResponseDto result =
        this.executor.execute(this.connectionManager, this.device, request, this.messageMetadata);

    // VERIFY calls to mocks
    verify(this.dlmsMessageListener)
        .setDescription(
            String.format(
                "GetPeriodicMeterReads DAILY from %s until %s, retrieve attribute: {%s,%s,%s}",
                convertedFromTime,
                convertedToTime,
                profile.getClassId(),
                profile.getObis(),
                ProfileGenericAttribute.BUFFER.attributeId()));

    // Expect only one call to retrieve the values from the buffer
    verify(this.dlmsHelper, times(1))
        .getAndCheck(eq(this.connectionManager), eq(this.device), any(), any());

    // If a dynamic scalerUnit is used then expect 1 additional call
    verify(this.dlmsHelper, times(valueType == ValueType.DYNAMIC ? 1 : 0))
        .getWithList(eq(this.connectionManager), eq(this.device), any(AttributeAddress[].class));

    // ASSERT - the result should contain 2 values
    final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads =
        result.getPeriodicMeterReads();

    // Only 2 meterreads are expected. The 3rd meterread has a logtime outside the requested period.
    Assertions.assertThat(periodicMeterReads).hasSize(2);

    if (areDatesEqual(periodicMeterReads.get(0).getLogTime(), timeMeterRead1)) {
      this.checkValues(
          periodicMeterReads.get(0),
          timeMeterRead1,
          meterValueImport11,
          meterValueImport12,
          meterValueExport11,
          meterValueExport12);
      this.checkValues(
          periodicMeterReads.get(1),
          timeMeterRead2,
          meterValueImport21,
          meterValueImport22,
          meterValueExport21,
          meterValueExport22);
    } else {
      {
        this.checkValues(
            periodicMeterReads.get(1),
            timeMeterRead1,
            meterValueImport11,
            meterValueImport12,
            meterValueExport11,
            meterValueExport12);
        this.checkValues(
            periodicMeterReads.get(0),
            timeMeterRead2,
            meterValueImport21,
            meterValueImport22,
            meterValueExport21,
            meterValueExport22);
      }
    }
  }

  private void checkValues(
      final PeriodicMeterReadsResponseItemDto response,
      final CosemDateTimeDto time,
      final DlmsMeterValueDto import1,
      final DlmsMeterValueDto import2,
      final DlmsMeterValueDto export1,
      final DlmsMeterValueDto export2) {
    assertThat(areDatesEqual(response.getLogTime(), time)).isTrue();
    assertThat(response.getActiveEnergyImportTariffOne()).isEqualTo(import1);
    assertThat(response.getActiveEnergyImportTariffTwo()).isEqualTo(import2);
    assertThat(response.getActiveEnergyExportTariffOne()).isEqualTo(export1);
    assertThat(response.getActiveEnergyExportTariffTwo()).isEqualTo(export2);
  }

  private DlmsDevice createDevice(final Protocol protocol) {
    final DlmsDevice dlmsDevice = new DlmsDevice();
    dlmsDevice.setProtocol(protocol);
    dlmsDevice.setSelectiveAccessPeriodicMeterReadsSupported(true);
    return dlmsDevice;
  }

  private ProfileGeneric createProfile() {
    final Attribute attributeCaptureObjects =
        this.createAttribute(
            3,
            "CLOCK,2|AMR_PROFILE_STATUS,2|ACTIVE_ENERGY_IMPORT_RATE_1,2|ACTIVE_ENERGY_IMPORT_RATE_2,2|ACTIVE_ENERGY_EXPORT_RATE_1,2|ACTIVE_ENERGY_EXPORT_RATE_2,2|MBUS_MASTER_VALUE,2|MBUS_MASTER_VALUE,5");
    final Attribute attributeCapturePeriod = this.createAttribute(4, "86400");
    return new ProfileGeneric(
        "DAILY_VALUES_E",
        "descr",
        this.CLASS_ID_PROFILE_GENERIC,
        0,
        "1.2.3.4.5.6",
        "ELECTRICITY",
        null,
        List.of(),
        Map.of(),
        List.of(attributeCaptureObjects, attributeCapturePeriod));
  }

  private CosemObject createStatus() {
    return createObject(
        this.CLASS_ID_DATA, "AMR_PROFILE_STATUS", "0.0.96.10.2.255", "ABSTRACT", List.of());
  }

  private Register createRegister(
      final DlmsObjectType type, final String obis, final ValueType valueType) {
    final Attribute attributeScalerUnit = this.createAttribute(3, "0, WH", valueType);
    return new Register(
        type.name(),
        "descr",
        this.CLASS_ID_REGISTER,
        0,
        obis,
        "ELECTRICITY",
        null,
        List.of(),
        Map.of(),
        List.of(attributeScalerUnit));
  }

  private Attribute createAttribute(final int id, final String value) {
    return this.createAttribute(id, value, ValueType.FIXED_IN_PROFILE);
  }

  private Attribute createAttribute(final int id, final String value, final ValueType valueType) {
    return new Attribute(
        id, "descr", null, DlmsDataType.DONT_CARE, valueType, value, null, AccessType.RW);
  }
}
