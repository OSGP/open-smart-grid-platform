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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.DEFINABLE_LOAD_PROFILE;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.POWER_QUALITY_PROFILE_1;
import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.POWER_QUALITY_PROFILE_2;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.PowerQualityProfile;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;

@ExtendWith(MockitoExtension.class)
class GetPowerQualityProfileSelectiveAccessHandlerTest extends GetPowerQualityProfileTest {
  @Mock private DlmsHelper dlmsHelper;
  @Mock private DlmsConnectionManager conn;
  @Mock private DlmsDevice dlmsDevice;
  @Mock private ObjectConfigService objectConfigService;

  @ParameterizedTest
  @EnumSource(PowerQualityProfile.class)
  void testHandlePublicOrPrivateProfileWithSelectiveAccessWithPartialNonAllowedObjects(
      final PowerQualityProfile profile) throws ProtocolAdapterException, ObjectConfigException {

    final List<CosemObject> allPqObjectsForThisMeter = this.getObjects(true, profile.name());
    final GetPowerQualityProfileRequestDataDto requestDto =
        new GetPowerQualityProfileRequestDataDto(
            profile.name(),
            Date.from(Instant.now().minus(2, ChronoUnit.DAYS)),
            new Date(),
            new ArrayList<>());
    when(this.dlmsDevice.getProtocolName()).thenReturn(PROTOCOL_NAME);
    when(this.dlmsDevice.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
    when(this.dlmsHelper.readLogicalName(any(DataObject.class), any(String.class)))
        .thenCallRealMethod();
    when(this.dlmsHelper.readObjectDefinition(any(DataObject.class), any(String.class)))
        .thenCallRealMethod();
    when(this.dlmsHelper.readLongNotNull(any(DataObject.class), any(String.class)))
        .thenCallRealMethod();
    when(this.dlmsHelper.readLong(any(DataObject.class), any(String.class))).thenCallRealMethod();
    when(this.dlmsHelper.getScaledMeterValueWithScalerUnit(
            any(DataObject.class), any(String.class), any(String.class)))
        .thenCallRealMethod();
    when(this.dlmsHelper.convertDataObjectToDateTime(any(DataObject.class))).thenCallRealMethod();
    when(this.dlmsHelper.fromDateTimeValue(any())).thenCallRealMethod();
    when(this.objectConfigService.getCosemObject(
            PROTOCOL_NAME, PROTOCOL_VERSION, POWER_QUALITY_PROFILE_2))
        .thenReturn(
            this.createProfile(PQ_PROFILE_2, "POWER_QUALITY_PROFILE_2", PQ_PROFILE_2_INTERVAL));
    when(this.objectConfigService.getCosemObject(
            PROTOCOL_NAME, PROTOCOL_VERSION, POWER_QUALITY_PROFILE_1))
        .thenReturn(
            this.createProfile(PQ_PROFILE_1, "POWER_QUALITY_PROFILE_1", PQ_PROFILE_1_INTERVAL));
    when(this.objectConfigService.getCosemObject(
            PROTOCOL_NAME, PROTOCOL_VERSION, DEFINABLE_LOAD_PROFILE))
        .thenReturn(
            this.createProfile(PQ_DEFINABLE, "DEFINABLE_LOAD_PROFILE", PQ_DEFINABLE_INTERVAL));
    when(this.objectConfigService.getCosemObjects(
            PROTOCOL_NAME, PROTOCOL_VERSION, this.getPropertyObjects()))
        .thenReturn(allPqObjectsForThisMeter);

    when(this.dlmsHelper.getAndCheck(
            any(DlmsConnectionManager.class),
            any(DlmsDevice.class),
            eq("retrieve profile generic capture objects"),
            any(AttributeAddress.class)))
        .thenReturn(this.createPartialNotAllowedCaptureObjects());

    when(this.dlmsHelper.getAndCheck(
            any(DlmsConnectionManager.class),
            any(DlmsDevice.class),
            eq("retrieve profile generic buffer"),
            any(AttributeAddress.class)))
        .thenReturn(this.createProfileEntries());

    // EXECUTE
    final GetPowerQualityProfileSelectiveAccessHandler handler =
        new GetPowerQualityProfileSelectiveAccessHandler(this.dlmsHelper, this.objectConfigService);
    final GetPowerQualityProfileResponseDto responseDto =
        handler.handle(this.conn, this.dlmsDevice, requestDto);

    assertThat(responseDto.getPowerQualityProfileResponseDatas()).hasSize(3);

    this.verifyResponseData(responseDto, PQ_PROFILE_1, PQ_PROFILE_1_INTERVAL);
    this.verifyResponseData(responseDto, PQ_PROFILE_2, PQ_PROFILE_2_INTERVAL);
    this.verifyResponseData(responseDto, PQ_DEFINABLE, PQ_DEFINABLE_INTERVAL);
  }
}
