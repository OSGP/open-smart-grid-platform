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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;

@ExtendWith(MockitoExtension.class)
public class GetPowerQualityProfileNoSelectiveAccessHandlerTest {

  @Mock private DlmsHelper dlmsHelper;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsDevice dlmsDevice;

  @Test
  public void testHandlePublicProfileWithoutSelectiveAccess() throws ProtocolAdapterException {

    // SETUP

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
            this.createPartialNotAllowedCaptureObjects(),
            this.createProfileEntries(),
            this.createPartialNotAllowedCaptureObjects(),
            this.createProfileEntries());

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

    final GetPowerQualityProfileNoSelectiveAccessHandler handler =
        new GetPowerQualityProfileNoSelectiveAccessHandler(this.dlmsHelper);

    // EXECUTE

    final GetPowerQualityProfileResponseDto responseDto =
        handler.handle(this.conn, this.dlmsDevice, requestDto);

    // ASSERT

    assertThat(responseDto.getPowerQualityProfileResponseDatas().size()).isEqualTo(2);
    assertThat(responseDto.getPowerQualityProfileResponseDatas().get(0).getCaptureObjects().size())
        .isEqualTo(2);
    assertThat(responseDto.getPowerQualityProfileResponseDatas().get(0).getProfileEntries().size())
        .isEqualTo(4);

    for (final ProfileEntryDto profileEntryDto :
        responseDto.getPowerQualityProfileResponseDatas().get(0).getProfileEntries()) {
      assertThat(profileEntryDto.getProfileEntryValues().size()).isEqualTo(2);
    }

    assertThat(
            responseDto
                .getPowerQualityProfileResponseDatas()
                .get(0)
                .getProfileEntries()
                .get(3)
                .getProfileEntryValues()
                .get(1)
                .getValue())
        .isNull();
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
            DataObject.newNullData(), // Null-data for time: calculate time based on previous time
            DataObject.newUInteger32Data(3),
            DataObject.newUInteger32Data(2)));
    structures.add(
        DataObject.newStructureData(
            DataObject.newNullData(), // Null-data for time: calculate time based on previous time
            DataObject.newUInteger32Data(3),
            DataObject.newUInteger32Data(2)));
    structures.add(
        DataObject.newStructureData(
            DataObject.newNullData(), // Null-data for time: calculate time based on previous time
            DataObject.newUInteger32Data(3),
            DataObject.newNullData())); // Null-data for value

    final GetResult getResult = new GetResultImpl(DataObject.newArrayData(structures));

    return Collections.singletonList(getResult);
  }

  private List<GetResult> createPartialNotAllowedCaptureObjects() {

    final DataObject allowedCaptureObject1 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(8),
            DataObject.newOctetStringData(new byte[] {0, 0, 1, 0, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
    final DataObject nonAllowedCaptureObject2 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {80, 0, 32, 32, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));
    final DataObject allowedCaptureObject3 =
        DataObject.newStructureData(
            DataObject.newUInteger32Data(1),
            DataObject.newOctetStringData(new byte[] {1, 0, 52, 32, 0, (byte) 255}),
            DataObject.newInteger32Data(2),
            DataObject.newUInteger32Data(0));

    final GetResult getResult =
        new GetResultImpl(
            DataObject.newArrayData(
                Arrays.asList(
                    allowedCaptureObject1, nonAllowedCaptureObject2, allowedCaptureObject3)));

    return Collections.singletonList(getResult);
  }
}
