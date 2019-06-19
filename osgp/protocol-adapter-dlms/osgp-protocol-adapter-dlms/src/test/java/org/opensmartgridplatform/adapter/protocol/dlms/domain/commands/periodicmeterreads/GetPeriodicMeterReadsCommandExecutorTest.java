/**
 * Copyright 2019 Smart Society Services B.V.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.AmrProfileStatusCodeHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.GetPeriodicMeterReadsCommandExecutor.PERIODIC_E_METER_READS;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class GetPeriodicMeterReadsCommandExecutorTest {

    @InjectMocks
    private GetPeriodicMeterReadsCommandExecutor executor;

    @Mock
    private DlmsMessageListener dlmsMessageListener;

    @Mock
    private DlmsHelper dlmsHelper;

//    @Mock
//    private AttributeAddressService attributeAddressService;

    @Mock
    private AmrProfileStatusCodeHelper amrProfileStatusCodeHelper;

    @Mock
    private DlmsConnectionManager connectionManager;

    @Before
    public void setUp() {
        when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    }

    @Test
    public void testHappy() throws Exception {

        // SETUP
        final Protocol procotol = Protocol.DSMR_4_2_2;

        final DlmsDevice device = new DlmsDevice();
        device.setProtocol(procotol.getName(), procotol.getVersion());

        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;
        final long from = 1111111L;
        final long to = 2222222L;
        final int classId = 8;
        final String address = "0.0.1.0.0.255";
        final int id = 2;
        final AttributeAddress attributeAddress = this.createAttributeAddress(classId, address, id);
//        when(this.attributeAddressService
//                .getProfileBufferAndScalerUnitForPeriodicMeterReads(eq(periodType), argThat(new DateTimeMatcher(from)),
//                        argThat(new DateTimeMatcher(to)), eq(procotol.isSelectValuesInSelectiveAccessSupported())))
//                .thenReturn(new AttributeAddress[] { attributeAddress });

        final DataObject data0 = mock(DataObject.class);
        final DataObject data1 = mock(DataObject.class);
        when(data1.isNumber()).thenReturn(true);
        final DataObject data2 = mock(DataObject.class);
        final DataObject data3 = mock(DataObject.class);
        final DataObject data4 = mock(DataObject.class);
        final DataObject data5 = mock(DataObject.class);
        final DataObject bufferedObject = mock(DataObject.class);
        when(bufferedObject.getValue()).thenReturn(asList(data0, data1, data2, data3, data4, data5));
        final DataObject resultData = mock(DataObject.class);
        when(resultData.getValue()).thenReturn(Collections.singletonList(bufferedObject));

        final String expectedDescription = "retrieve periodic meter reads for " + periodType;
        final GetResult result0 = mock(GetResult.class);
        final GetResult result1 = mock(GetResult.class);
        final GetResult result2 = mock(GetResult.class);
        final GetResult result3 = mock(GetResult.class);
        final GetResult result4 = mock(GetResult.class);
        final GetResult result5 = mock(GetResult.class);
        when(this.dlmsHelper.getAndCheck(this.connectionManager, device, expectedDescription, attributeAddress))
                .thenReturn(asList(result0, result1, result2, result3, result4, result5));

        when(this.dlmsHelper.readDataObject(result0, PERIODIC_E_METER_READS)).thenReturn(resultData);

        final CosemDateTimeDto cosemDateTime = mock(CosemDateTimeDto.class);
        final String expectedDateTimeDescription = String.format("Clock from %s buffer", periodType);
        when(this.dlmsHelper.readDateTime(data0, expectedDateTimeDescription)).thenReturn(cosemDateTime);
        final DateTime bufferedDateTime = DateTime.now();
        when(cosemDateTime.asDateTime()).thenReturn(bufferedDateTime);

        // TODO: mock other calls

        final PeriodicMeterReadsRequestDto request = new PeriodicMeterReadsRequestDto(periodType, new Date(from),
                new Date(to));

        // CALL
        final PeriodicMeterReadsResponseDto result = this.executor.execute(this.connectionManager, device, request);

        // VERIFY
        verify(this.dlmsMessageListener).setDescription(
                String.format("GetPeriodicMeterReads DAILY from %s until %s, retrieve attribute: {%s,%s,%s}",
                        new DateTime(from), new DateTime(to), classId, address, id));

        verify(this.dlmsHelper).validateBufferedDateTime(same(bufferedDateTime), same(cosemDateTime),
                argThat(new DateTimeMatcher(from)), argThat(new DateTimeMatcher(to)));

        final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads = result.getPeriodicMeterReads();
        // TODO: assert contents of result
    }

    private AttributeAddress createAttributeAddress(final int classId, final String address, final int id) {
        final AttributeAddress attributeAddress = mock(AttributeAddress.class);
        when(attributeAddress.getClassId()).thenReturn(classId);
        when(attributeAddress.getInstanceId()).thenReturn(new ObisCode(address));
        when(attributeAddress.getId()).thenReturn(id);
        return attributeAddress;
    }
}

