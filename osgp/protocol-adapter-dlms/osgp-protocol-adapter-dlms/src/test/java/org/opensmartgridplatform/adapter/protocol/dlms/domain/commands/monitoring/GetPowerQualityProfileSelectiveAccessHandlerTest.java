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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.springframework.core.io.ClassPathResource;

@ExtendWith(MockitoExtension.class)
class GetPowerQualityProfileSelectiveAccessHandlerTest {

  @Mock private DlmsHelper dlmsHelper;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsDevice dlmsDevice;
  private ObjectConfigService objectConfigService;

  @BeforeEach
  public void setup() throws ProtocolAdapterException, IOException, ObjectConfigException {
    when(this.dlmsHelper.readLogicalName(any(DataObject.class), any(String.class)))
        .thenCallRealMethod();
    when(this.dlmsHelper.readObjectDefinition(any(DataObject.class), any(String.class)))
        .thenCallRealMethod();
    when(this.dlmsHelper.readLongNotNull(any(DataObject.class), any(String.class)))
        .thenCallRealMethod();
    when(this.dlmsHelper.readLong(any(DataObject.class), any(String.class))).thenCallRealMethod();
    when(this.dlmsHelper.convertDataObjectToDateTime(any(DataObject.class))).thenCallRealMethod();
    when(this.dlmsHelper.fromDateTimeValue(any())).thenCallRealMethod();
    when(this.dlmsHelper.getClockDefinition()).thenCallRealMethod();

    final List<DlmsProfile> dlmsProfileList = this.getDlmsProfileList();
    this.objectConfigService = new ObjectConfigService(dlmsProfileList);
  }

  @Test
  void testHandlePrivateProfileSelectiveAccess() throws ProtocolAdapterException {

    final GetPowerQualityProfileRequestDataDto requestDto =
        new GetPowerQualityProfileRequestDataDto(
            "PRIVATE",
            Date.from(Instant.now().minus(2, ChronoUnit.DAYS)),
            new Date(),
            new ArrayList<>());
    this.dlmsDevice = createDevice("SMR", "5.0");

    when(this.dlmsHelper.getAndCheck(
            any(DlmsConnectionManager.class),
            any(DlmsDevice.class),
            any(String.class),
            any(AttributeAddress.class)))
        .thenReturn(
            this.createPrivateCaptureObjects(),
            this.createProfileEntries(),
            this.createPrivateCaptureObjectsProfile2(),
            this.createProfileEntries());
    when(this.dlmsHelper.getScaledMeterValue(any(DataObject.class), any(DataObject.class), any())).thenReturn(mock(DlmsMeterValueDto.class));

    final GetPowerQualityProfileSelectiveAccessHandler handler =
        new GetPowerQualityProfileSelectiveAccessHandler(this.dlmsHelper, this.objectConfigService);

    final GetPowerQualityProfileResponseDto responseDto =
        handler.handle(this.conn, this.dlmsDevice, requestDto);

    assertThat(responseDto.getPowerQualityProfileResponseDatas()).hasSize(2);
    assertThat(responseDto.getPowerQualityProfileResponseDatas().get(0).getCaptureObjects())
        .hasSize(3);
    assertThat(responseDto.getPowerQualityProfileResponseDatas().get(0).getProfileEntries())
        .hasSize(4);

    for (final ProfileEntryDto profileEntryDto :
        responseDto.getPowerQualityProfileResponseDatas().get(0).getProfileEntries()) {
      assertThat(profileEntryDto.getProfileEntryValues()).hasSize(3);
    }
  }

  @Test
  void testHandlePublicProfileSelectiveAccess() throws ProtocolAdapterException {

    final GetPowerQualityProfileRequestDataDto requestDto =
        new GetPowerQualityProfileRequestDataDto(
            "PUBLIC",
            Date.from(Instant.now().minus(2, ChronoUnit.DAYS)),
            new Date(),
            new ArrayList<>());
    this.dlmsDevice = createDevice("SMR", "5.0");

    when(this.dlmsHelper.getAndCheck(
            any(DlmsConnectionManager.class),
            any(DlmsDevice.class),
            any(String.class),
            any(AttributeAddress.class)))
        .thenReturn(
            this.createPublicCaptureObjects(),
            this.createPublicProfileEntries(),
            this.createPublicCaptureObjectsProfile2(),
            this.createPublicProfileEntries());
    when(this.dlmsHelper.getScaledMeterValue(any(DataObject.class), any(DataObject.class), any())).thenReturn(mock(DlmsMeterValueDto.class));


    final GetPowerQualityProfileSelectiveAccessHandler handler =
        new GetPowerQualityProfileSelectiveAccessHandler(this.dlmsHelper, this.objectConfigService);

    final GetPowerQualityProfileResponseDto responseDto =
        handler.handle(this.conn, this.dlmsDevice, requestDto);

    assertThat(responseDto.getPowerQualityProfileResponseDatas()).hasSize(2);
    assertThat(responseDto.getPowerQualityProfileResponseDatas().get(0).getCaptureObjects())
        .hasSize(3);

    for (final ProfileEntryDto profileEntryDto :
        responseDto.getPowerQualityProfileResponseDatas().get(0).getProfileEntries()) {
      assertThat(profileEntryDto.getProfileEntryValues()).hasSize(3);
    }
  }

  private List<GetResult> createProfileEntries() {

    final List<DataObject> structures = new ArrayList<>();

    final DataObject structureData1 =
        DataObject.newStructureData(
            DataObject.newOctetStringData(
                new byte[] {7, (byte) 228, 3, 15, 7, 0, 0, 0, 0, (byte) 255, (byte) 196, 0}),
            DataObject.newUInteger32Data(3),
            DataObject.newUInteger32Data(2));

    structures.add(structureData1);
    structures.add(
        DataObject.newStructureData(
            DataObject.newNullData(),
            DataObject.newUInteger32Data(3),
            DataObject.newUInteger32Data(2)));
    structures.add(
        DataObject.newStructureData(
            DataObject.newNullData(),
            DataObject.newUInteger32Data(3),
            DataObject.newUInteger32Data(2)));
    structures.add(
        DataObject.newStructureData(
            DataObject.newNullData(),
            DataObject.newUInteger32Data(3),
            DataObject.newUInteger32Data(2)));

    final GetResult getResult = new GetResultImpl(DataObject.newArrayData(structures));

    return Collections.singletonList(getResult);
  }

  private List<GetResult> createPrivateCaptureObjects() {

    final DataObject clockDefinition = getClockDefinition();
    final DataObject structureData2 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 21, 4, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
    final DataObject structureData3 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 23, 4, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));

    final GetResult getResult =
        new GetResultImpl(
            DataObject.newArrayData(
                Arrays.asList(clockDefinition, structureData2, structureData3)));

    return Collections.singletonList(getResult);
  }

  private List<GetResult> createPrivateCaptureObjectsProfile2() {

    final DataObject clockDefinition = getClockDefinition();
    final DataObject structureData2 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 31, 24, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
    final DataObject structureData3 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 51, 24, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));

    final GetResult getResult =
        new GetResultImpl(
            DataObject.newArrayData(
                Arrays.asList(clockDefinition, structureData2, structureData3)));

    return Collections.singletonList(getResult);
  }

  private List<GetResult> createPublicCaptureObjects() {
    final DataObject clockDefinition = getClockDefinition();
    final DataObject structureData2 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 32, 32, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
    final DataObject structureData3 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 52, 32, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));

    final GetResult getResult =
        new GetResultImpl(
            DataObject.newArrayData(
                Arrays.asList(clockDefinition, structureData2, structureData3)));

    return Collections.singletonList(getResult);
  }

  private List<GetResult> createPublicCaptureObjectsProfile2() {

    final DataObject clockDefinition = getClockDefinition();
    final DataObject structureData2 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 32, 24, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
    final DataObject structureData3 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 52, 24, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));

    final GetResult getResult =
        new GetResultImpl(
            DataObject.newArrayData(
                Arrays.asList(clockDefinition, structureData2, structureData3)));

    return Collections.singletonList(getResult);
  }

  private List<GetResult> createPublicProfileEntries() {

    final List<DataObject> structures = new ArrayList<>();

    final DataObject structureData1 =
        DataObject.newStructureData(
            DataObject.newOctetStringData(
                new byte[] {7, (byte) 228, 3, 15, 7, 0, 0, 0, 0, (byte) 255, (byte) 196, 0}),
            DataObject.newUInteger32Data(3),
            DataObject.newUInteger32Data(2));

    structures.add(structureData1);

    final GetResult getResult = new GetResultImpl(DataObject.newArrayData(structures));

    return Collections.singletonList(getResult);
  }

  private static DataObject getClockDefinition() {
    return DataObject.newStructureData(
        DataObject.newUInteger32Data(8),
        DataObject.newOctetStringData(new byte[] {0, 0, 1, 0, 0, (byte) 255}),
        DataObject.newInteger32Data(2),
        DataObject.newUInteger32Data(0));
  }

  private List<DlmsProfile> getDlmsProfileList() throws IOException {
    final ObjectMapper objectMapper = new ObjectMapper();
    final List<DlmsProfile> DlmsProfileList = new ArrayList<>();
    final DlmsProfile dlmsProfile50 =
        objectMapper.readValue(
            new ClassPathResource("/dlmsprofile-smr50.json").getFile(), DlmsProfile.class);
    final DlmsProfile dlmsProfile51 =
        objectMapper.readValue(
            new ClassPathResource("/dlmsprofile-smr51.json").getFile(), DlmsProfile.class);
    final DlmsProfile dlmsProfile52 =
        objectMapper.readValue(
            new ClassPathResource("/dlmsprofile-smr52.json").getFile(), DlmsProfile.class);
    DlmsProfileList.add(dlmsProfile50);
    DlmsProfileList.add(dlmsProfile51);
    DlmsProfileList.add(dlmsProfile52);
    return DlmsProfileList;
  }

  private DlmsDevice createDevice(String protocol, String version) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol, version);

    return device;
  }
}
