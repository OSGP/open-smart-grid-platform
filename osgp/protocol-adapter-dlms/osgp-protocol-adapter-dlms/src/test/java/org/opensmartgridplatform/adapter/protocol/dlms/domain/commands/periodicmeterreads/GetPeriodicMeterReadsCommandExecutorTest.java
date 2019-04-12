/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.GetPeriodicMeterReadsCommandExecutor.PERIODIC_E_METER_READS;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AmrProfileStatusCodeHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.ProtocolFactory;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseItemDto;

@RunWith(MockitoJUnitRunner.class)
public class GetPeriodicMeterReadsCommandExecutorTest {

    @InjectMocks
    private GetPeriodicMeterReadsCommandExecutor executor;

    @Mock
    private DlmsMessageListener dlmsMessageListener;
    @Mock
    private DlmsHelperService dlmsHelperService;
    @Mock
    private AttributeAddressService attributeAddressService;
    @Mock
    private AmrProfileStatusCodeHelperService amrProfileStatusCodeHelperService;
    @Mock
    private ProtocolFactory protocolFactory;
    @Mock
    private DlmsConnectionManager connectionManager;

    @Before
    public void setUp() {
        when(connectionManager.getDlmsMessageListener()).thenReturn(dlmsMessageListener);
    }

    @Test
    public void testHappy() throws Exception {

        // SETUP
        String protocolName = "test-protocol-name";
        String protocolVersion = "test-protocol-version";
        Protocol procotol = Protocol.DSMR_4_2_2;
        when(protocolFactory.getInstance(protocolName, protocolVersion)).thenReturn(procotol);

        DlmsDevice device = new DlmsDevice();
        device.setProtocol(protocolName, protocolVersion);

        PeriodTypeDto periodType = PeriodTypeDto.DAILY;
        long from = 1111111L;
        long to = 2222222L;
        int classId = 8;
        String address = "0.0.1.0.0.255";
        int id = 2;
        AttributeAddress attributeAddress = createAttributeAddress(classId, address, id);
        when(this.attributeAddressService.getProfileBufferAndScalerUnitForPeriodicMeterReads(eq(periodType),
                argThat(new DateTimeMatcher(from)), argThat(new DateTimeMatcher(to)),
                eq(procotol.isSelectValuesInSelectiveAccessSupported()))).thenReturn(
                new AttributeAddress[] { attributeAddress });

        DataObject data0 = mock(DataObject.class);
        DataObject data1 = mock(DataObject.class);
        when(data1.isNumber()).thenReturn(true);
        DataObject data2 = mock(DataObject.class);
        DataObject data3 = mock(DataObject.class);
        DataObject data4 = mock(DataObject.class);
        DataObject data5 = mock(DataObject.class);
        DataObject bufferedObject = mock(DataObject.class);
        when(bufferedObject.getValue()).thenReturn(asList(data0, data1, data2, data3, data4, data5));
        DataObject resultData = mock(DataObject.class);
        when(resultData.getValue()).thenReturn(Collections.singletonList(bufferedObject));

        String expectedDescription = "retrieve periodic meter reads for " + periodType;
        GetResult result0 = mock(GetResult.class);
        GetResult result1 = mock(GetResult.class);
        GetResult result2 = mock(GetResult.class);
        GetResult result3 = mock(GetResult.class);
        GetResult result4 = mock(GetResult.class);
        GetResult result5 = mock(GetResult.class);
        when(this.dlmsHelperService.getAndCheck(this.connectionManager, device, expectedDescription,
                attributeAddress)).thenReturn(asList(result0, result1, result2, result3, result4, result5));

        when(this.dlmsHelperService.readDataObject(result0, PERIODIC_E_METER_READS)).thenReturn(resultData);

        CosemDateTimeDto cosemDateTime = mock(CosemDateTimeDto.class);
        String expectedDateTimeDescription = String.format("Clock from %s buffer", periodType);
        when(this.dlmsHelperService.readDateTime(data0, expectedDateTimeDescription)).thenReturn(cosemDateTime);
        DateTime bufferedDateTime = DateTime.now();
        when(cosemDateTime.asDateTime()).thenReturn(bufferedDateTime);

        // TODO: mock other calls

        PeriodicMeterReadsRequestDto request = new PeriodicMeterReadsRequestDto(periodType, new Date(from),
                new Date(to));

        // CALL
        PeriodicMeterReadsResponseDto result = this.executor.execute(this.connectionManager, device, request);

        // VERIFY
        verify(dlmsMessageListener).setDescription(
                String.format("GetPeriodicMeterReads DAILY from %s until %s, retrieve attribute: {%s,%s,%s}",
                        new DateTime(from), new DateTime(to), classId, address, id));

        verify(this.dlmsHelperService).validateBufferedDateTime(same(bufferedDateTime), same(cosemDateTime),
                argThat(new DateTimeMatcher(from)), argThat(new DateTimeMatcher(to)));

        List<PeriodicMeterReadsResponseItemDto> periodicMeterReads = result.getPeriodicMeterReads();
        // TODO: assert contents of result
    }

    private AttributeAddress createAttributeAddress(int classId, String address, int id) {
        AttributeAddress attributeAddress = mock(AttributeAddress.class);
        when(attributeAddress.getClassId()).thenReturn(classId);
        when(attributeAddress.getInstanceId()).thenReturn(new ObisCode(address));
        when(attributeAddress.getId()).thenReturn(id);
        return attributeAddress;
    }
}

