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

/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
@ExtendWith(MockitoExtension.class)
public class GetPowerQualityProfileSelectiveAccessHandlerTest {

    @Mock
    private DlmsHelper dlmsHelper;

    @Mock
    private DlmsConnectionManager conn;

    @Mock
    private DlmsDevice dlmsDevice;

    @Test
    public void testHandlePrivateProfileSelectiveAccess() throws ProtocolAdapterException {

        // SETUP

        GetPowerQualityProfileRequestDataDto requestDto = new GetPowerQualityProfileRequestDataDto("PRIVATE",
                Date.from(Instant.now().minus(2, ChronoUnit.DAYS)), new Date(), new ArrayList<>());

        when(dlmsHelper.getAndCheck(any(DlmsConnectionManager.class), any(DlmsDevice.class), any(String.class),
                any(AttributeAddress.class)))
                .thenReturn(createCaptureObjects(), createProfileEntries(), createCaptureObjectsProfile2(),
                        createProfileEntries());

        when(dlmsHelper.readLogicalName(any(DataObject.class), any(String.class))).thenCallRealMethod();
        when(dlmsHelper.readObjectDefinition(any(DataObject.class), any(String.class))).thenCallRealMethod();
        when(dlmsHelper.readLongNotNull(any(DataObject.class), any(String.class))).thenCallRealMethod();
        when(dlmsHelper.readLong(any(DataObject.class), any(String.class))).thenCallRealMethod();
        when(dlmsHelper.convertDataObjectToDateTime(any(DataObject.class))).thenCallRealMethod();
        when(dlmsHelper.fromDateTimeValue(any())).thenCallRealMethod();
        when(dlmsHelper.getClockDefinition()).thenCallRealMethod();

        GetPowerQualityProfileSelectiveAccessHandler handler = new GetPowerQualityProfileSelectiveAccessHandler(
                dlmsHelper);

        // EXECUTE

        GetPowerQualityProfileResponseDto responseDto = handler.handle(conn, dlmsDevice, requestDto);

        // ASSERT

        assertThat(responseDto.getPowerQualityProfileResponseDatas().size()).isEqualTo(2);
        assertThat(responseDto.getPowerQualityProfileResponseDatas().get(0).getCaptureObjects().size()).isEqualTo(3);
        assertThat(responseDto.getPowerQualityProfileResponseDatas().get(0).getProfileEntries().size()).isEqualTo(4);

        for (ProfileEntryDto profileEntryDto : responseDto.getPowerQualityProfileResponseDatas().get(0)
                                                          .getProfileEntries()) {
            assertThat(profileEntryDto.getProfileEntryValues().size()).isEqualTo(3);
        }
    }

    private List<GetResult> createProfileEntries() {

        List<DataObject> structures = new ArrayList<>();

        DataObject structureData1 = DataObject.newStructureData(DataObject
                        .newOctetStringData(new byte[] { 7, (byte) 228, 3, 15, 7, 0, 0, 0, 0, (byte) 255, (byte) 196,
                                0 }),
                DataObject.newUInteger32Data(3), DataObject.newUInteger32Data(2));

        structures.add(structureData1);
        structures.add(DataObject.newStructureData(DataObject.newNullData(), DataObject.newUInteger32Data(3),
                DataObject.newUInteger32Data(2)));
        structures.add(DataObject.newStructureData(DataObject.newNullData(), DataObject.newUInteger32Data(3),
                DataObject.newUInteger32Data(2)));
        structures.add(DataObject.newStructureData(DataObject.newNullData(), DataObject.newUInteger32Data(3),
                DataObject.newUInteger32Data(2)));

        GetResult getResult = new GetResultImpl(DataObject.newArrayData(structures));

        return Collections.singletonList(getResult);
    }

    private List<GetResult> createCaptureObjects() {

        DataObject structureData1 = DataObject.newStructureData(DataObject.newUInteger32Data(8),
                DataObject.newOctetStringData(new byte[] { 0, 0, 1, 0, 0, (byte) 255 }), DataObject.newInteger32Data(2),
                DataObject.newUInteger32Data(0));
        DataObject structureData2 = DataObject.newStructureData(DataObject.newUInteger32Data(1),
                DataObject.newOctetStringData(new byte[] { 1, 0, 21, 4, 0, (byte) 255 }),
                DataObject.newInteger32Data(2), DataObject.newUInteger32Data(0));
        DataObject structureData3 = DataObject.newStructureData(DataObject.newUInteger32Data(1),
                DataObject.newOctetStringData(new byte[] { 1, 0, 23, 4, 0, (byte) 255 }),
                DataObject.newInteger32Data(2), DataObject.newUInteger32Data(0));

        GetResult getResult = new GetResultImpl(
                DataObject.newArrayData(Arrays.asList(structureData1, structureData2, structureData3)));

        return Collections.singletonList(getResult);
    }

    private List<GetResult> createCaptureObjectsProfile2() {

        DataObject structureData1 = DataObject.newStructureData(DataObject.newUInteger32Data(8),
                DataObject.newOctetStringData(new byte[] { 0, 0, 1, 0, 0, (byte) 255 }), DataObject.newInteger32Data(2),
                DataObject.newUInteger32Data(0));
        DataObject structureData2 = DataObject.newStructureData(DataObject.newUInteger32Data(1),
                DataObject.newOctetStringData(new byte[] { 1, 0, 31, 24, 0, (byte) 255 }),
                DataObject.newInteger32Data(2), DataObject.newUInteger32Data(0));
        DataObject structureData3 = DataObject.newStructureData(DataObject.newUInteger32Data(1),
                DataObject.newOctetStringData(new byte[] { 1, 0, 51, 24, 0, (byte) 255 }),
                DataObject.newInteger32Data(2), DataObject.newUInteger32Data(0));

        GetResult getResult = new GetResultImpl(
                DataObject.newArrayData(Arrays.asList(structureData1, structureData2, structureData3)));

        return Collections.singletonList(getResult);
    }

}
