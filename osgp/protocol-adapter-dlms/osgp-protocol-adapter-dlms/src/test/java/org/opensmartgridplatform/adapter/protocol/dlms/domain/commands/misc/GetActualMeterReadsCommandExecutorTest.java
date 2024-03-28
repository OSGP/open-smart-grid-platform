// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_EXPORT;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_2;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_IMPORT;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_2;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.CLOCK;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.objectconfig.AccessType;
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsDataType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.MeterType;
import org.opensmartgridplatform.dlms.objectconfig.ValueType;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.Register;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GetActualMeterReadsCommandExecutorTest {
  private static final int CLASS_ID_CLOCK = 8;
  private static final int ATTRIBUTE_ID_VALUE = 2;
  private static final int ATTRIBUTE_ID_SCALER_UNIT = 3;
  private static final String PROTOCOL_NAME = "SMR";
  private static final String PROTOCOL_VERSION = "5.0.0";
  private static final MessageMetadata MESSAGE_METADATA =
      MessageMetadata.newBuilder().withCorrelationUid("123456").build();
  private static final DateTime DATE_TIME = DateTime.parse("2018-12-31T23:00:00Z");
  private static final short SCALER = 0;
  private static final short UNIT = 30; // WH

  @Spy private DlmsHelper dlmsHelper;

  @Mock private ObjectConfigService objectConfigService;

  @Mock private DlmsDevice dlmsDevice;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Captor ArgumentCaptor<AttributeAddress> attributeAddressArgumentCaptor;

  @ParameterizedTest
  @CsvSource({"FIXED_IN_PROFILE", "DYNAMIC"})
  void testRetrieval(final ValueType valueType)
      throws ProtocolAdapterException, ObjectConfigException {
    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.dlmsDevice.getProtocolName()).thenReturn(PROTOCOL_NAME);
    when(this.dlmsDevice.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);

    final List<CosemObject> allObjects = this.createObjects(valueType);

    when(this.objectConfigService.getCosemObjects(
            PROTOCOL_NAME,
            PROTOCOL_VERSION,
            List.of(
                CLOCK,
                ACTIVE_ENERGY_IMPORT,
                ACTIVE_ENERGY_IMPORT_RATE_1,
                ACTIVE_ENERGY_IMPORT_RATE_2,
                ACTIVE_ENERGY_EXPORT,
                ACTIVE_ENERGY_EXPORT_RATE_1,
                ACTIVE_ENERGY_EXPORT_RATE_2)))
        .thenReturn(allObjects);

    final ActualMeterReadsQueryDto actualMeterReadsQueryDto = new ActualMeterReadsQueryDto();
    final List<AttributeAddress> expectedAttributeAddresses =
        this.getAttributeAddresses(allObjects);

    doReturn(this.generateMockedResult(allObjects, AccessResultCode.SUCCESS))
        .when(this.dlmsHelper)
        .getAndCheck(any(), any(), any(), any());

    final GetActualMeterReadsCommandExecutor executor =
        new GetActualMeterReadsCommandExecutor(this.objectConfigService, this.dlmsHelper);

    // EXECUTE
    final MeterReadsResponseDto responseDto =
        executor.execute(this.conn, this.dlmsDevice, actualMeterReadsQueryDto, MESSAGE_METADATA);

    // VERIFY
    verify(this.dlmsHelper, times(1))
        .getAndCheck(
            eq(this.conn),
            eq(this.dlmsDevice),
            eq("retrieve actual meter reads"),
            this.attributeAddressArgumentCaptor.capture());
    assertThat(this.attributeAddressArgumentCaptor.getAllValues())
        .usingRecursiveFieldByFieldElementComparator()
        .isEqualTo(expectedAttributeAddresses);
    assertThat(responseDto.getLogTime()).isEqualTo(this.DATE_TIME.toDate());
    this.assertValue(responseDto.getActiveEnergyImport(), 1);
    this.assertValue(responseDto.getActiveEnergyImportTariffOne(), 2);
    this.assertValue(responseDto.getActiveEnergyImportTariffTwo(), 3);
    this.assertValue(responseDto.getActiveEnergyExport(), 4);
    this.assertValue(responseDto.getActiveEnergyExportTariffOne(), 5);
    this.assertValue(responseDto.getActiveEnergyExportTariffTwo(), 6);
  }

  private List<CosemObject> createObjects(final ValueType valueType) {
    final CosemObject clockObject = this.createCosemObject(CLOCK.name(), 8, "0.0.1.0.0.255");

    final Register registerActiveEnergyImport =
        this.createRegister(ACTIVE_ENERGY_IMPORT, "3.0.0.0.0.0", "0, WH", valueType);
    final Register registerActiveEnergyImportRate1 =
        this.createRegister(ACTIVE_ENERGY_IMPORT_RATE_1, "3.0.0.0.0.1", "0, WH", valueType);
    final Register registerActiveEnergyImportRate2 =
        this.createRegister(ACTIVE_ENERGY_IMPORT_RATE_2, "3.0.0.0.0.2", "0, WH", valueType);
    final Register registerActiveEnergyExport =
        this.createRegister(ACTIVE_ENERGY_EXPORT, "4.0.0.0.0.0", "0, WH", valueType);
    final Register registerActiveEnergyExportRate1 =
        this.createRegister(ACTIVE_ENERGY_EXPORT_RATE_1, "4.0.0.0.0.1", "0, WH", valueType);
    final Register registerActiveEnergyExportRate2 =
        this.createRegister(ACTIVE_ENERGY_EXPORT_RATE_2, "4.0.0.0.0.2", "0, WH", valueType);

    return List.of(
        clockObject,
        registerActiveEnergyImport,
        registerActiveEnergyImportRate1,
        registerActiveEnergyImportRate2,
        registerActiveEnergyExport,
        registerActiveEnergyExportRate1,
        registerActiveEnergyExportRate2);
  }

  private List<Attribute> createScalerUnitAttributeList(
      final String value, final ValueType valueType) {
    return List.of(
        new Attribute(
            ATTRIBUTE_ID_SCALER_UNIT,
            "descr",
            null,
            DlmsDataType.DONT_CARE,
            valueType,
            value,
            null,
            AccessType.RW));
  }

  private List<MeterType> getMeterTypes(final MeterType meterType) {
    if (meterType.equals(MeterType.PP)) {
      return Collections.singletonList(MeterType.PP);
    } else {
      return Arrays.asList(MeterType.SP, MeterType.PP);
    }
  }

  private List<GetResult> generateMockedResult(
      final List<CosemObject> objects, final AccessResultCode resultCode) {
    final List<GetResult> results = new ArrayList<>();

    int idx = 1;
    for (final CosemObject object : objects) {
      if (object.getClassId() == CLASS_ID_CLOCK) {
        results.add(
            new GetResultImpl(
                DataObject.newDateTimeData(
                    new CosemDateTime(
                        this.DATE_TIME.getYear(),
                        this.DATE_TIME.getMonthOfYear(),
                        this.DATE_TIME.getDayOfMonth(),
                        this.DATE_TIME.getHourOfDay(),
                        this.DATE_TIME.getMinuteOfHour(),
                        this.DATE_TIME.getSecondOfMinute(),
                        0)),
                resultCode));
      } else {
        results.add(new GetResultImpl(DataObject.newInteger64Data(idx++), resultCode));
        if (((Register) object).needsScalerUnitFromMeter()) {
          final DataObject scalerUnit =
              DataObject.newStructureData(
                  Arrays.asList(
                      DataObject.newInteger16Data(SCALER), DataObject.newInteger16Data(UNIT)));
          results.add(new GetResultImpl(scalerUnit, resultCode));
        }
      }
    }
    return results;
  }

  private List<AttributeAddress> getAttributeAddresses(final List<CosemObject> objects) {
    final List<AttributeAddress> attributeAddresses = new ArrayList<>();
    for (final CosemObject cosemObject : objects) {
      attributeAddresses.add(
          new AttributeAddress(
              cosemObject.getClassId(), cosemObject.getObis(), ATTRIBUTE_ID_VALUE));
      if (cosemObject instanceof final Register register && register.needsScalerUnitFromMeter()) {
        attributeAddresses.add(
            new AttributeAddress(
                cosemObject.getClassId(), cosemObject.getObis(), ATTRIBUTE_ID_SCALER_UNIT));
      }
    }
    return attributeAddresses;
  }

  private CosemObject createCosemObject(final String tag, final int classId, final String obis) {
    return new CosemObject(
        tag, "descr", classId, 0, obis, "group", null, List.of(), Map.of(), List.of());
  }

  private Register createRegister(
      final DlmsObjectType tag,
      final String obis,
      final String scalerUnitValue,
      final ValueType valueType) {
    return new Register(
        tag.name(),
        "descr",
        InterfaceClass.REGISTER.id(),
        0,
        obis,
        "group",
        null,
        List.of(),
        null,
        scalerUnitValue != null
            ? this.createScalerUnitAttributeList(scalerUnitValue, valueType)
            : List.of());
  }

  private void assertValue(final DlmsMeterValueDto valueDto, final int expectedValue) {
    assertThat(valueDto.getValue()).isEqualByComparingTo(BigDecimal.valueOf(expectedValue));
    assertThat(valueDto.getDlmsUnit().getUnit()).isEqualTo("WH");
  }
}
