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
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_MASTER_VALUE;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.MBUS_MASTER_VALUE_5MIN;
import static org.opensmartgridplatform.dlms.objectconfig.ValueType.DYNAMIC;
import static org.opensmartgridplatform.dlms.objectconfig.ValueType.FIXED_IN_PROFILE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.joda.time.DateTime;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import org.opensmartgridplatform.dlms.objectconfig.DlmsDataType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.ValueType;
import org.opensmartgridplatform.dlms.objectconfig.dlmsclasses.ExtendedRegister;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsGasResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GetActualMeterReadsGasCommandExecutorTest {
  private static final int VALUE = 1001;
  private static final int ATTRIBUTE_ID_VALUE = 2;
  private static final int ATTRIBUTE_ID_SCALER_UNIT = 3;
  private static final int ATTRIBUTE_ID_TIME = 5;
  private static final String PROTOCOL_NAME = "SMR";
  private static final String PROTOCOL_VERSION = "5.0.0";
  private static final String DEVICE_MODEL_CODE = "G4";
  private static final MessageMetadata MESSAGE_METADATA =
      MessageMetadata.newBuilder()
          .withCorrelationUid("123456")
          .withDeviceModelCode(",G4,G4,G4,G4")
          .build();
  private static final DateTime DATE_TIME = DateTime.parse("2018-12-31T23:00:00Z");
  private static final short SCALER = 0;
  private static final short UNIT = 13; // M3
  private static final List<Integer> ALL_CHANNELS = List.of(1, 2, 3, 4);

  @Spy private DlmsHelper dlmsHelper;

  @Mock private ObjectConfigService objectConfigService;

  @Mock private DlmsDevice dlmsDevice;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Captor ArgumentCaptor<AttributeAddress> attributeAddressArgumentCaptor;

  private static Stream<Arguments> generateCombinations() {
    final List<Arguments> arguments = new ArrayList<>();

    final List<ValueType> valueTypes = List.of(FIXED_IN_PROFILE, DYNAMIC);

    for (final ValueType valueType : valueTypes) {
      for (final int channel : ALL_CHANNELS) {
        arguments.add(Arguments.of(valueType, channel, true));
        arguments.add(Arguments.of(valueType, channel, false));
      }
    }

    return arguments.stream();
  }

  @ParameterizedTest
  @MethodSource("generateCombinations")
  void testRetrieval(
      final ValueType valueType, final int channel, final boolean mbusMasterValue5minPresent)
      throws ProtocolAdapterException, ObjectConfigException {
    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.dlmsDevice.getProtocolName()).thenReturn(PROTOCOL_NAME);
    when(this.dlmsDevice.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);

    final ExtendedRegister mbusValueObject = this.createObject(valueType, false);
    final ExtendedRegister mbusValueObject5min = this.createObject(valueType, true);

    if (mbusMasterValue5minPresent) {
      when(this.objectConfigService.getOptionalCosemObject(
              PROTOCOL_NAME, PROTOCOL_VERSION, MBUS_MASTER_VALUE_5MIN, DEVICE_MODEL_CODE))
          .thenReturn(Optional.of(mbusValueObject5min));
    } else {
      when(this.objectConfigService.getCosemObject(
              PROTOCOL_NAME, PROTOCOL_VERSION, MBUS_MASTER_VALUE, DEVICE_MODEL_CODE))
          .thenReturn(mbusValueObject);
      when(this.objectConfigService.getOptionalCosemObject(
              PROTOCOL_NAME, PROTOCOL_VERSION, MBUS_MASTER_VALUE_5MIN, DEVICE_MODEL_CODE))
          .thenReturn(Optional.empty());
    }

    final ActualMeterReadsQueryDto actualMeterReadsQueryDto =
        new ActualMeterReadsQueryDto(ChannelDto.fromNumber(channel));
    final List<AttributeAddress> expectedAttributeAddresses =
        this.getAttributeAddresses(
            mbusValueObject, mbusValueObject5min, channel, mbusMasterValue5minPresent);

    doReturn(this.generateMockedResult(mbusValueObject, AccessResultCode.SUCCESS))
        .when(this.dlmsHelper)
        .getAndCheck(any(), any(), any(), any());

    final GetActualMeterReadsGasCommandExecutor executor =
        new GetActualMeterReadsGasCommandExecutor(this.objectConfigService, this.dlmsHelper);

    // EXECUTE
    final MeterReadsGasResponseDto responseDto =
        executor.execute(this.conn, this.dlmsDevice, actualMeterReadsQueryDto, MESSAGE_METADATA);

    // VERIFY
    verify(this.dlmsHelper, times(1))
        .getAndCheck(
            eq(this.conn),
            eq(this.dlmsDevice),
            eq("retrieve actual meter reads for mbus " + ChannelDto.fromNumber(channel)),
            this.attributeAddressArgumentCaptor.capture());
    assertThat(this.attributeAddressArgumentCaptor.getAllValues())
        .usingRecursiveFieldByFieldElementComparator()
        .isEqualTo(expectedAttributeAddresses);

    assertThat(responseDto.getCaptureTime()).isEqualTo(this.DATE_TIME.toDate());
    assertThat(responseDto.getConsumption().getValue())
        .isEqualByComparingTo(BigDecimal.valueOf(VALUE));
    assertThat(responseDto.getConsumption().getDlmsUnit()).isEqualTo(DlmsUnitTypeDto.M3);
  }

  private ExtendedRegister createObject(final ValueType valueType, final boolean masterValue5min) {
    if (masterValue5min) {
      return this.createExtendedRegister(MBUS_MASTER_VALUE_5MIN, "1.x.1.0.0.0", "0, M3", valueType);
    } else {
      return this.createExtendedRegister(MBUS_MASTER_VALUE, "1.x.0.0.0.0", "0, M3", valueType);
    }
  }

  private List<GetResult> generateMockedResult(
      final ExtendedRegister object, final AccessResultCode resultCode) {
    final List<GetResult> results = new ArrayList<>();

    results.add(new GetResultImpl(DataObject.newInteger64Data(VALUE), resultCode));
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

    if (object.needsScalerUnitFromMeter()) {
      final DataObject scalerUnit =
          DataObject.newStructureData(
              Arrays.asList(
                  DataObject.newInteger16Data(SCALER), DataObject.newInteger16Data(UNIT)));
      results.add(new GetResultImpl(scalerUnit, resultCode));
    }

    return results;
  }

  private List<AttributeAddress> getAttributeAddresses(
      final ExtendedRegister cosemObject,
      final ExtendedRegister cosemObject5min,
      final int channel,
      final boolean masterValue5minPresent) {
    final List<AttributeAddress> attributeAddresses = new ArrayList<>();

    final int classId = cosemObject.getClassId();
    final String obis;
    if (masterValue5minPresent) {
      obis = cosemObject5min.getObis().replace("x", String.valueOf(channel));
    } else {
      obis = cosemObject.getObis().replace("x", String.valueOf(channel));
    }

    attributeAddresses.add(new AttributeAddress(classId, obis, ATTRIBUTE_ID_VALUE));
    attributeAddresses.add(new AttributeAddress(classId, obis, ATTRIBUTE_ID_TIME));
    if (cosemObject.needsScalerUnitFromMeter()) {
      attributeAddresses.add(new AttributeAddress(classId, obis, ATTRIBUTE_ID_SCALER_UNIT));
    }

    return attributeAddresses;
  }

  private ExtendedRegister createExtendedRegister(
      final DlmsObjectType tag,
      final String obis,
      final String scalerUnitValue,
      final ValueType valueType) {
    return new ExtendedRegister(
        tag.name(),
        "descr",
        InterfaceClass.EXTENDED_REGISTER.id(),
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
}
