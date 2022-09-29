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
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DataObjectDefinitions;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;

@ExtendWith(MockitoExtension.class)
public class GetPowerQualityProfileSelectiveAccessHandlerTest {

  @Mock private DlmsHelper dlmsHelper;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsDevice dlmsDevice;

  @BeforeEach
  public void setup() throws ProtocolAdapterException {
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
  }

  @Test
  public void testHandlePrivateProfileSelectiveAccess() throws ProtocolAdapterException {

    final GetPowerQualityProfileRequestDataDto requestDto =
        new GetPowerQualityProfileRequestDataDto(
            "PRIVATE",
            Date.from(Instant.now().minus(2, ChronoUnit.DAYS)),
            new Date(),
            new ArrayList<>());

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

    final GetPowerQualityProfileSelectiveAccessHandler handler =
        new GetPowerQualityProfileSelectiveAccessHandler(this.dlmsHelper);

    final GetPowerQualityProfileResponseDto responseDto =
        handler.handle(this.conn, this.dlmsDevice, requestDto);

    assertThat(responseDto.getPowerQualityProfileResponseDatas().size()).isEqualTo(2);
    assertThat(responseDto.getPowerQualityProfileResponseDatas().get(0).getCaptureObjects().size())
        .isEqualTo(3);
    assertThat(responseDto.getPowerQualityProfileResponseDatas().get(0).getProfileEntries().size())
        .isEqualTo(4);

    for (final ProfileEntryDto profileEntryDto :
        responseDto.getPowerQualityProfileResponseDatas().get(0).getProfileEntries()) {
      assertThat(profileEntryDto.getProfileEntryValues().size()).isEqualTo(3);
    }
  }

  @Test
  public void testHandlePublicProfileSelectiveAccess() throws ProtocolAdapterException {

    final GetPowerQualityProfileRequestDataDto requestDto =
        new GetPowerQualityProfileRequestDataDto(
            "PUBLIC",
            Date.from(Instant.now().minus(2, ChronoUnit.DAYS)),
            new Date(),
            new ArrayList<>());

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

    final GetPowerQualityProfileSelectiveAccessHandler handler =
        new GetPowerQualityProfileSelectiveAccessHandler(this.dlmsHelper);

    final GetPowerQualityProfileResponseDto responseDto =
        handler.handle(this.conn, this.dlmsDevice, requestDto);

    assertThat(responseDto.getPowerQualityProfileResponseDatas().size()).isEqualTo(2);
    assertThat(responseDto.getPowerQualityProfileResponseDatas().get(0).getCaptureObjects().size())
        .isEqualTo(3);

    for (final ProfileEntryDto profileEntryDto :
        responseDto.getPowerQualityProfileResponseDatas().get(0).getProfileEntries()) {
      assertThat(profileEntryDto.getProfileEntryValues().size()).isEqualTo(3);
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

    final DataObject structureData1 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(8),
            DataObject.newOctetStringData(new byte[] {0, 0, 1, 0, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
    final DataObject structureData2 = // AVERAGE_ACTIVE_POWER_IMPORT_L1("1.0.21.4.0.255")
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 21, 4, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
    final DataObject structureData3 = // AVERAGE_REACTIVE_POWER_IMPORT_L1("1.0.23.4.0.255"),
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 23, 4, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));

    final GetResult getResult =
        new GetResultImpl(
            DataObject.newArrayData(Arrays.asList(structureData1, structureData2, structureData3)));

    return Collections.singletonList(getResult);
  }

  private List<GetResult> createPrivateCaptureObjectsProfile2() {

    final DataObject structureData1 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(8),
            DataObject.newOctetStringData(new byte[] {0, 0, 1, 0, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
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
            DataObject.newArrayData(Arrays.asList(structureData1, structureData2, structureData3)));

    return Collections.singletonList(getResult);
  }

  private List<GetResult> createPublicCaptureObjects() {
    //    NR_VOLTAGE_SAGS_L1("1.0.32.32.0.255"),
    //        NR_VOLTAGE_SAGS_L2("1.0.52.32.0.255"),
    DataObjectDefinitions.getClockDefinition();
    final DataObject structureData1 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(8),
            DataObject.newOctetStringData(new byte[] {0, 0, 1, 0, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
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
            DataObject.newArrayData(Arrays.asList(structureData1, structureData2, structureData3)));

    return Collections.singletonList(getResult);
  }

  private List<GetResult> createPublicCaptureObjectsProfile2() {
    //    AVERAGE_VOLTAGE_L1("1.0.32.24.0.255"),
    //        AVERAGE_VOLTAGE_L2("1.0.52.24.0.255"),

    final DataObject structureData1 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(8),
            DataObject.newOctetStringData(new byte[] {0, 0, 1, 0, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
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
            DataObject.newArrayData(Arrays.asList(structureData1, structureData2, structureData3)));

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
    //    structures.add(
    //        DataObject.newStructureData(
    //            DataObject.newNullData(),
    //            DataObject.newUInteger32Data(3),
    //            DataObject.newUInteger32Data(2)));
    //    structures.add(
    //        DataObject.newStructureData(
    //            DataObject.newNullData(),
    //            DataObject.newUInteger32Data(3),
    //            DataObject.newUInteger32Data(2)));
    //    structures.add(
    //        DataObject.newStructureData(
    //            DataObject.newNullData(),
    //            DataObject.newUInteger32Data(3),
    //            DataObject.newUInteger32Data(2)));

    final GetResult getResult = new GetResultImpl(DataObject.newArrayData(structures));

    return Collections.singletonList(getResult);
  }
}
