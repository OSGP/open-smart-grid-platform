/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.monitoring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.PowerQualityProfile;
import org.opensmartgridplatform.dlms.objectconfig.PowerQualityRequest;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.*;

@ExtendWith(MockitoExtension.class)
class GetPowerQualityProfileNoSelectiveAccessHandlerTest extends ObjectConfigServiceHelper {

  private static final String PROTOCOL_NAME = "SMR";
  private static final String PROTOCOL_VERSION = "5.0.0";
  private static final int CLASS_ID_DATA = 1;
  private static final int CLASS_ID_REGISTER = 3;
  private static final String OBIS_PRIVATE = "0.1.99.1.1.255";
  private static final String OBIS_PUBLIC = "0.1.94.31.6.255";
  private static final String OBIS_CLOCK = "0.0.1.0.0.255";
  private static final String OBIS_INSTANTANEOUS_VOLTAGE_L1 = "1.0.32.7.0.255";
  private static final String UNIT_UNDEFINED = "UNDEFINED";
  private static final String UNIT_VOLT = "V";

  @Mock private DlmsHelper dlmsHelper;
  @Mock private DlmsConnectionManager conn;
  @Mock private DlmsDevice dlmsDevice;
  @Mock private ObjectConfigService objectConfigService;

  @ParameterizedTest
  @EnumSource(PowerQualityProfile.class)
  void testHandlePublicOrPrivateProfileWithoutSelectiveAccessWithPartialNonAllowedObjects(
      final PowerQualityProfile profile) throws ProtocolAdapterException, ObjectConfigException {

    final boolean polyPhase = true;
    //   final List<CosemObject> allPqObjectsForThisMeter = this.getObjects(polyPhase);
    final GetPowerQualityProfileRequestDataDto requestDto =
        new GetPowerQualityProfileRequestDataDto(
            profile.name(),
            Date.from(Instant.now().minus(2, ChronoUnit.DAYS)),
            new Date(),
            new ArrayList<>());
    when(this.objectConfigService.getCosemObject(any(), any(), eq(DlmsObjectType.CLOCK)))
        .thenReturn(this.getClockObject());
    when(this.dlmsDevice.getProtocolName()).thenReturn(PROTOCOL_NAME);
    when(this.dlmsDevice.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
    when(this.dlmsHelper.readLogicalName(any(DataObject.class), any(String.class)))
        .thenCallRealMethod();
    when(this.dlmsHelper.readObjectDefinition(any(DataObject.class), any(String.class)))
        .thenCallRealMethod();
    when(this.dlmsHelper.readLongNotNull(any(DataObject.class), any(String.class)))
        .thenCallRealMethod();
    when(this.dlmsHelper.readLong(any(DataObject.class), any(String.class))).thenCallRealMethod();
    when(this.dlmsHelper.convertDataObjectToDateTime(any(DataObject.class))).thenCallRealMethod();
    when(this.dlmsHelper.fromDateTimeValue(any())).thenCallRealMethod();
    when(this.dlmsHelper.getScaledMeterValueWithScalerUnit(any(DataObject.class), any(), any()))
        .thenReturn(new DlmsMeterValueDto(BigDecimal.TEN, DlmsUnitTypeDto.VOLT));
    //    when(this.objectConfigService.getCosemObjectsWithProperties(
    //            PROTOCOL_NAME,
    //            PROTOCOL_VERSION,
    //            this.getObjectProperties(profile.name(), PowerQualityRequest.PERIODIC)))
    //        .thenReturn(allPqObjectsForThisMeter);
    when(this.objectConfigService.getCosemObject(
            PROTOCOL_NAME, PROTOCOL_VERSION, POWER_QUALITY_PROFILE_2))
        .thenReturn(
            this.createObject(7, "0.1.99.1.2.255", "POWER_QUALITY_PROFILE_2", null, polyPhase));
    if (profile.name().equals("PRIVATE")) {
      when(this.objectConfigService.getCosemObject(
              PROTOCOL_NAME, PROTOCOL_VERSION, POWER_QUALITY_PROFILE_1))
          .thenReturn(
              this.createObject(7, OBIS_PRIVATE, "POWER_QUALITY_PROFILE_1", null, polyPhase));

    } else {
      when(this.objectConfigService.getCosemObject(
              PROTOCOL_NAME, PROTOCOL_VERSION, DEFINABLE_LOAD_PROFILE))
          .thenReturn(this.createObject(7, OBIS_PUBLIC, "DEFINABLE_LOAD_PROFILE", null, polyPhase));
    }

    when(this.dlmsHelper.getAndCheck(
            any(DlmsConnectionManager.class),
            any(DlmsDevice.class),
            any(String.class),
            any(AttributeAddress.class)))
        .thenReturn(
            this.createPartialNotAllowedCaptureObjects(),
            this.createProfileEntries(),
            this.createPartialNotAllowedCaptureObjects(),
            this.createProfileEntries());

    // EXECUTE
    final GetPowerQualityProfileNoSelectiveAccessHandler handler =
        new GetPowerQualityProfileNoSelectiveAccessHandler(
            this.dlmsHelper, this.objectConfigService);
    final GetPowerQualityProfileResponseDto responseDto =
        handler.handle(this.conn, this.dlmsDevice, requestDto);

    assertThat(responseDto.getPowerQualityProfileResponseDatas()).hasSize(2);

    PowerQualityProfileDataDto powerQualityProfile1 =
        responseDto.getPowerQualityProfileResponseDatas().get(0);
    assertThat(powerQualityProfile1.getLogicalName().toString())
        .hasToString(profile.name().equals("PRIVATE") ? OBIS_PRIVATE : OBIS_PUBLIC);
    assertThat(powerQualityProfile1.getCaptureObjects()).hasSize(2);

    CaptureObjectDto captureObjectClock = powerQualityProfile1.getCaptureObjects().get(0);
    assertThat(captureObjectClock.getLogicalName()).isEqualTo(OBIS_CLOCK);
    assertThat(captureObjectClock.getUnit()).isEqualTo(UNIT_UNDEFINED);

    CaptureObjectDto captureObject = powerQualityProfile1.getCaptureObjects().get(1);
    assertThat(captureObject.getLogicalName()).isEqualTo(OBIS_INSTANTANEOUS_VOLTAGE_L1);
    assertThat(captureObject.getUnit()).isEqualTo(UNIT_VOLT);
  }

  @ParameterizedTest
  @EnumSource(PowerQualityProfile.class)
  void testHandlePublicOrPrivateProfileWithInvalidConfigThrowsException(
      final PowerQualityProfile profile) throws ObjectConfigException {

    final boolean polyPhase = true;
    final GetPowerQualityProfileRequestDataDto requestDto =
        new GetPowerQualityProfileRequestDataDto(
            profile.name(),
            Date.from(Instant.now().minus(2, ChronoUnit.DAYS)),
            new Date(),
            new ArrayList<>());
    when(this.objectConfigService.getCosemObject(any(), any(), eq(DlmsObjectType.CLOCK)))
        .thenReturn(this.getClockObject());
    when(this.dlmsDevice.getProtocolName()).thenReturn(PROTOCOL_NAME);
    when(this.dlmsDevice.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
    when(this.objectConfigService.getCosemObjectsWithProperties(
            PROTOCOL_NAME,
            PROTOCOL_VERSION,
            this.getObjectProperties(profile.name(), PowerQualityRequest.PERIODIC)))
        .thenThrow(ObjectConfigException.class);
    when(this.objectConfigService.getCosemObject(
            PROTOCOL_NAME, PROTOCOL_VERSION, POWER_QUALITY_PROFILE_2))
        .thenReturn(
            this.createObject(7, "0.1.99.1.2.255", "POWER_QUALITY_PROFILE_2", null, polyPhase));
    if (profile.name().equals("PRIVATE")) {
      when(this.objectConfigService.getCosemObject(
              PROTOCOL_NAME, PROTOCOL_VERSION, POWER_QUALITY_PROFILE_1))
          .thenReturn(
              this.createObject(7, OBIS_PRIVATE, "POWER_QUALITY_PROFILE_1", null, polyPhase));

    } else {
      when(this.objectConfigService.getCosemObject(
              PROTOCOL_NAME, PROTOCOL_VERSION, DEFINABLE_LOAD_PROFILE))
          .thenReturn(this.createObject(7, OBIS_PUBLIC, "DEFINABLE_LOAD_PROFILE", null, polyPhase));
    }

    final GetPowerQualityProfileNoSelectiveAccessHandler handler =
        new GetPowerQualityProfileNoSelectiveAccessHandler(
            this.dlmsHelper, this.objectConfigService);

    assertThrows(
        ProtocolAdapterException.class,
        () -> handler.handle(this.conn, this.dlmsDevice, requestDto));
  }

  @Test
  void testInvalidProfileThrowsException() {
    final String INVALID_PROFILE_NAME = "INVALID";
    final GetPowerQualityProfileRequestDataDto requestDto =
        new GetPowerQualityProfileRequestDataDto(
            INVALID_PROFILE_NAME,
            Date.from(Instant.now().minus(2, ChronoUnit.DAYS)),
            new Date(),
            new ArrayList<>());
    when(this.dlmsDevice.getProtocolName()).thenReturn(PROTOCOL_NAME);
    when(this.dlmsDevice.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);

    final GetPowerQualityProfileNoSelectiveAccessHandler handler =
        new GetPowerQualityProfileNoSelectiveAccessHandler(
            this.dlmsHelper, this.objectConfigService);

    assertThrows(
        IllegalArgumentException.class,
        () -> handler.handle(this.conn, this.dlmsDevice, requestDto));
  }

  private List<CosemObject> getObjects(final boolean polyphase) {
    final CosemObject dataObject =
        this.createObject(
            CLASS_ID_DATA, "1.0.0.0.0.1", NUMBER_OF_VOLTAGE_SAGS_FOR_L2.name(), null, polyphase);

    final CosemObject registerVoltObject =
        this.createObject(
            CLASS_ID_REGISTER,
            OBIS_INSTANTANEOUS_VOLTAGE_L1,
            INSTANTANEOUS_VOLTAGE_L1.name(),
            "0, " + UNIT_VOLT,
            polyphase);

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

  private List<CosemObject> getInvalidObjects(final boolean polyphase) {
    final CosemObject dataObject = this.createObject(CLASS_ID_DATA, null, null, null, polyphase);
    return new ArrayList<>(Arrays.asList(dataObject));
  }

  private List<GetResult> createProfileEntries() {

    final List<DataObject> structures = new ArrayList<>();

    structures.add(
        DataObject.newStructureData(
            DataObject.newOctetStringData(
                new byte[] {7, (byte) 228, 3, 15, 7, 0, 0, 0, 0, (byte) 255, (byte) 196, 0}),
            DataObject.newUInteger32Data(3),
            DataObject.newUInteger32Data(2)));
    structures.add(
        DataObject.newStructureData(
            DataObject.newNullData(), // Null-data for time: calculate time based on  previous time
            DataObject.newUInteger32Data(3),
            DataObject.newUInteger32Data(2)));
    structures.add(
        DataObject.newStructureData(
            DataObject.newNullData(), // Null-data for time: calculate time based on previous time
            DataObject.newUInteger32Data(3),
            DataObject.newUInteger32Data(2)));
    structures.add(
        DataObject.newStructureData(
            DataObject.newNullData(), // Null-data for time: calculate time based on  previous time
            DataObject.newUInteger32Data(3),
            DataObject.newNullData())); // Null-data for value

    final GetResult getResult = new GetResultImpl(DataObject.newArrayData(structures));

    return Collections.singletonList(getResult);
  }

  private List<GetResult> createPartialNotAllowedCaptureObjects() {
    final DataObject allowedCaptureObjectClock =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(8),
            DataObject.newOctetStringData(new byte[] {0, 0, 1, 0, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
    final DataObject allowedCaptureObjectINSTANTANEOUS_VOLTAGE_L1 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 32, 7, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
    final DataObject nonAllowedCaptureObject1 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {80, 0, 32, 32, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
    final DataObject nonAllowedCaptureObject2 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 52, 32, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));

    final GetResult getResult =
        new GetResultImpl(
            DataObject.newArrayData(
                Arrays.asList(
                    allowedCaptureObjectClock,
                    allowedCaptureObjectINSTANTANEOUS_VOLTAGE_L1,
                    nonAllowedCaptureObject1,
                    nonAllowedCaptureObject2)));

    return Collections.singletonList(getResult);
  }
}
