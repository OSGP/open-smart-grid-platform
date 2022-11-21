/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.AVERAGE_CURRENT_L1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.AVERAGE_REACTIVE_POWER_IMPORT_L1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.AVERAGE_REACTIVE_POWER_IMPORT_L2;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.INSTANTANEOUS_VOLTAGE_L1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.NUMBER_OF_POWER_FAILURES;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
import org.opensmartgridplatform.dlms.objectconfig.Attribute;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.MeterType;
import org.opensmartgridplatform.dlms.objectconfig.ObjectProperty;
import org.opensmartgridplatform.dlms.objectconfig.PowerQualityProfile;
import org.opensmartgridplatform.dlms.objectconfig.PowerQualityRequest;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityValueDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GetActualPowerQualityCommandExecutorTest {
  private static final int CLASS_ID_DATA = 1;
  private static final int CLASS_ID_REGISTER = 3;
  private static final int CLASS_ID_CLOCK = 8;
  private static final int ATTRIBUTE_ID_VALUE = 2;
  private static final int ATTRIBUTE_ID_SCALER_UNIT = 3;
  private static final String PROTOCOL_NAME = "SMR";
  private static final String PROTOCOL_VERSION = "5.0.0";
  private static final MessageMetadata MESSAGE_METADATA =
      MessageMetadata.newBuilder().withCorrelationUid("123456").build();

  @Spy private DlmsHelper dlmsHelper;

  @Mock private ObjectConfigService objectConfigService;

  @Mock private DlmsDevice dlmsDevice;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Captor ArgumentCaptor<AttributeAddress> attributeAddressArgumentCaptor;

  private ActualPowerQualityRequestDto actualPowerQualityRequestDto;

  @ParameterizedTest
  @EnumSource(PowerQualityProfile.class)
  void testRetrievalPolyPhase(final PowerQualityProfile profile)
      throws ProtocolAdapterException, ObjectConfigException {
    this.executeAndAssert(profile.name(), true);
  }

  @ParameterizedTest
  @EnumSource(PowerQualityProfile.class)
  void testRetrievalSinglePhase(final PowerQualityProfile profile)
      throws ProtocolAdapterException, ObjectConfigException {
    this.executeAndAssert(profile.name(), false);
  }

  @Test
  void testOtherReasonResult() throws ProtocolAdapterException, ObjectConfigException {
    this.actualPowerQualityRequestDto = new ActualPowerQualityRequestDto("PRIVATE");

    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.objectConfigService.getCosemObject(any(), any(), eq(DlmsObjectType.CLOCK)))
        .thenReturn(this.getClockObject());
    doReturn(this.generateMockedResult(this.getObjects(false), AccessResultCode.OTHER_REASON))
        .when(this.dlmsHelper)
        .getAndCheck(
            eq(this.conn),
            eq(this.dlmsDevice),
            eq("retrieve actual power quality"),
            any(AttributeAddress.class));

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              new GetActualPowerQualityCommandExecutor(this.dlmsHelper, this.objectConfigService)
                  .execute(
                      this.conn,
                      this.dlmsDevice,
                      this.actualPowerQualityRequestDto,
                      MESSAGE_METADATA);
            });
  }

  @Test
  void testUnknownProfile() {
    this.actualPowerQualityRequestDto = new ActualPowerQualityRequestDto("UNKNOWN_PROFILE");

    final GetActualPowerQualityCommandExecutor executor =
        new GetActualPowerQualityCommandExecutor(this.dlmsHelper, this.objectConfigService);

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              executor.execute(
                  this.conn, this.dlmsDevice, this.actualPowerQualityRequestDto, MESSAGE_METADATA);
            });
  }

  void executeAndAssert(final String profileType, final boolean polyPhase)
      throws ProtocolAdapterException, ObjectConfigException {
    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.objectConfigService.getCosemObject(any(), any(), eq(DlmsObjectType.CLOCK)))
        .thenReturn(this.getClockObject());
    when(this.dlmsDevice.isPolyphase()).thenReturn(polyPhase);
    when(this.dlmsDevice.getProtocolName()).thenReturn(PROTOCOL_NAME);
    when(this.dlmsDevice.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);

    final List<CosemObject> allPqObjectsForThisMeter = this.getObjects(polyPhase);
    final List<CosemObject> allObjectsThatShouldBeRequested = new ArrayList<>();
    allObjectsThatShouldBeRequested.add(this.getClockObject());
    allObjectsThatShouldBeRequested.addAll(allPqObjectsForThisMeter);
    final List<CosemObject> allPqObjects = new ArrayList<>(allPqObjectsForThisMeter);
    allPqObjects.add(this.getObjectWithWrongMeterType(polyPhase));

    when(this.objectConfigService.getCosemObjectsWithProperties(
            PROTOCOL_NAME, PROTOCOL_VERSION, this.getObjectProperties(profileType)))
        .thenReturn(allPqObjects);

    this.actualPowerQualityRequestDto = new ActualPowerQualityRequestDto(profileType);
    final List<AttributeAddress> expectedAttributeAddresses =
        this.getAttributeAddresses(allObjectsThatShouldBeRequested);

    doReturn(this.generateMockedResult(allObjectsThatShouldBeRequested, AccessResultCode.SUCCESS))
        .when(this.dlmsHelper)
        .getAndCheck(any(), any(), any(), any());

    final GetActualPowerQualityCommandExecutor executor =
        new GetActualPowerQualityCommandExecutor(this.dlmsHelper, this.objectConfigService);

    // EXECUTE
    final ActualPowerQualityResponseDto responseDto =
        executor.execute(
            this.conn, this.dlmsDevice, this.actualPowerQualityRequestDto, MESSAGE_METADATA);

    // VERIFY
    verify(this.dlmsHelper, times(1))
        .getAndCheck(
            eq(this.conn),
            eq(this.dlmsDevice),
            eq("retrieve actual power quality"),
            this.attributeAddressArgumentCaptor.capture());
    assertThat(this.attributeAddressArgumentCaptor.getAllValues())
        .usingRecursiveFieldByFieldElementComparator()
        .isEqualTo(expectedAttributeAddresses);

    assertThat(responseDto.getActualPowerQualityData().getPowerQualityValues())
        .hasSize(allObjectsThatShouldBeRequested.size());
    assertThat(responseDto.getActualPowerQualityData().getPowerQualityObjects())
        .hasSize(allObjectsThatShouldBeRequested.size());

    for (int i = 0; i < allObjectsThatShouldBeRequested.size(); i++) {
      final CosemObject object = allObjectsThatShouldBeRequested.get(i);

      final String expectedUnit = this.getExpectedUnit(object);

      final PowerQualityObjectDto powerQualityObjectDto =
          responseDto.getActualPowerQualityData().getPowerQualityObjects().get(i);
      assertThat(powerQualityObjectDto.getName()).isEqualTo(object.getTag());
      assertThat(powerQualityObjectDto.getUnit()).isEqualTo(expectedUnit);

      final PowerQualityValueDto powerQualityValue =
          responseDto.getActualPowerQualityData().getPowerQualityValues().get(i);
      this.assertValue(powerQualityValue.getValue(), i, object);
    }
  }

  private void assertValue(final Serializable value, final int i, final CosemObject object) {
    switch (object.getClassId()) {
      case CLASS_ID_CLOCK:
        assertThat(value).isEqualTo(DateTime.parse("2018-12-31T23:00:00Z").toDate());
        break;
      case CLASS_ID_DATA:
        assertThat(value).isEqualTo(BigDecimal.valueOf(i));
        break;
      case CLASS_ID_REGISTER:
        final int scaler = this.getScaler(object);
        final BigDecimal expectedValue = BigDecimal.valueOf(i, -scaler);
        assertThat((BigDecimal) value).isEqualByComparingTo(expectedValue);
        break;
      default:
        fail("Unexpected class id: " + object.getClassId());
    }
  }

  private byte getScaler(final CosemObject object) {
    final String scalerUnit = object.getAttribute(ATTRIBUTE_ID_SCALER_UNIT).getValue();
    return Byte.parseByte(scalerUnit.split(",")[0]);
  }

  private String getExpectedUnit(final CosemObject object) {
    if (object.getClassId() == CLASS_ID_REGISTER) {
      final String scalerUnit = object.getAttribute(ATTRIBUTE_ID_SCALER_UNIT).getValue();
      final String unit = scalerUnit.split(",")[1].trim();

      switch (unit) {
        case "V":
          return "V";
        case "VAR":
          return "VAR";
        case "A":
          return "AMP";
        default:
          fail("Unexpected unit: " + unit);
      }
    }

    return null;
  }

  private CosemObject getClockObject() {

    final CosemObject clock = new CosemObject();
    clock.setClassId(8);
    clock.setObis("0.0.1.0.0.255");
    clock.setTag(DlmsObjectType.CLOCK.name());

    return clock;
  }

  private List<CosemObject> getObjects(final boolean polyphase) {
    final CosemObject dataObject =
        this.createObject(
            CLASS_ID_DATA, "1.0.0.0.0.1", NUMBER_OF_POWER_FAILURES.name(), null, polyphase);

    final CosemObject registerVoltObject =
        this.createObject(
            CLASS_ID_REGISTER, "3.0.0.0.0.1", INSTANTANEOUS_VOLTAGE_L1.name(), "0, V", polyphase);

    final CosemObject registerAmpereObject =
        this.createObject(
            CLASS_ID_REGISTER, "3.0.0.0.0.2", AVERAGE_CURRENT_L1.name(), "-1, A", polyphase);

    final CosemObject registerVarObject =
        this.createObject(
            CLASS_ID_REGISTER,
            "3.0.0.0.0.3",
            AVERAGE_REACTIVE_POWER_IMPORT_L1.name(),
            "2, VAR",
            polyphase);

    return new ArrayList<>(
        Arrays.asList(dataObject, registerVoltObject, registerAmpereObject, registerVarObject));
  }

  private CosemObject createObject(
      final int classId,
      final String obis,
      final String tag,
      final String scalerUnitValue,
      final boolean polyphase) {
    final CosemObject object = new CosemObject();
    object.setClassId(classId);
    object.setObis(obis);
    object.setTag(tag);
    if (scalerUnitValue != null) {
      object.setAttributes(this.createScalerUnitAttributeList(scalerUnitValue));
    }
    object.setMeterTypes(this.getMeterTypes(polyphase));

    return object;
  }

  private List<Attribute> createScalerUnitAttributeList(final String value) {
    final Attribute scalerUnitAttribute = new Attribute();
    scalerUnitAttribute.setId(ATTRIBUTE_ID_SCALER_UNIT);
    scalerUnitAttribute.setValue(value);
    return Collections.singletonList(scalerUnitAttribute);
  }

  private CosemObject getObjectWithWrongMeterType(final boolean polyphase) {

    // This object has the wrong meter type. The value shouldn't be requested by the commandexecutor
    final CosemObject objectWithWrongMeterType = new CosemObject();
    objectWithWrongMeterType.setClassId(CLASS_ID_DATA);
    objectWithWrongMeterType.setObis("1.0.0.0.0.2");
    objectWithWrongMeterType.setTag(AVERAGE_REACTIVE_POWER_IMPORT_L2.name());
    if (polyphase) {
      objectWithWrongMeterType.setMeterTypes(Collections.singletonList(MeterType.SP));
    } else {
      objectWithWrongMeterType.setMeterTypes(Collections.singletonList(MeterType.PP));
    }

    return objectWithWrongMeterType;
  }

  private List<MeterType> getMeterTypes(final boolean polyphase) {
    if (polyphase) {
      return Collections.singletonList(MeterType.PP);
    } else {
      return Arrays.asList(MeterType.SP, MeterType.SP);
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
                DataObject.newDateTimeData(new CosemDateTime(2018, 12, 31, 23, 0, 0, 0)),
                resultCode));
      } else {
        results.add(new GetResultImpl(DataObject.newInteger64Data(idx++), resultCode));
      }
    }
    return results;
  }

  private EnumMap<ObjectProperty, List<Object>> getObjectProperties(final String profile) {
    // Create map with the required properties and values for the power quality objects
    final EnumMap<ObjectProperty, List<Object>> pqProperties = new EnumMap<>(ObjectProperty.class);
    pqProperties.put(ObjectProperty.PQ_PROFILE, Collections.singletonList(profile));
    pqProperties.put(
        ObjectProperty.PQ_REQUEST,
        Arrays.asList(PowerQualityRequest.ONDEMAND.name(), PowerQualityRequest.BOTH.name()));

    return pqProperties;
  }

  private List<AttributeAddress> getAttributeAddresses(final List<CosemObject> objects) {
    return objects.stream()
        .map(
            object ->
                new AttributeAddress(object.getClassId(), object.getObis(), ATTRIBUTE_ID_VALUE))
        .collect(Collectors.toList());
  }
}
