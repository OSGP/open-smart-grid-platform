package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.DataObjectToEventListConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FindEventsRequestDto;

/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class RetrieveEventsCommandExecutorTest {

    @Mock
    private DlmsHelper dlmsHelper;

    @Mock
    private DlmsDevice dlmsDevice;

    @Mock
    private DlmsConnectionManager conn;

    @Mock
    private DlmsMessageListener dlmsMessageListener;

    @Mock
    private DlmsConnection dlmsConnection;

    @Test
    public void testThis() throws ProtocolAdapterException, IOException {

        final DataObject fromDate = mock(DataObject.class);
        final DataObject toDate = mock(DataObject.class);
        final GetResult getResult = mock(GetResult.class);
        final DataObject resultData = mock(DataObject.class);
        final List<DataObject> resultList = new ArrayList<>();

        final FindEventsRequestDto findEventsRequestDto = new FindEventsRequestDto(
                EventLogCategoryDto.POWER_QUALITY_EVENT_LOG, DateTime.now().minusDays(70), DateTime.now());

        when(this.dlmsHelper.asDataObject(findEventsRequestDto.getFrom())).thenReturn(fromDate);
        when(this.dlmsHelper.asDataObject(findEventsRequestDto.getUntil())).thenReturn(toDate);
        when(this.dlmsHelper.convertDataObjectToDateTime(any(DataObject.class))).thenReturn(new CosemDateTimeDto());
        when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
        when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
        when(this.dlmsConnection.get(any(AttributeAddress.class))).thenReturn(getResult);
        when(getResult.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
        when(getResult.getResultData()).thenReturn(resultData);
        when(resultData.getValue()).thenReturn(this.generateDataObjects());

        final DataObjectToEventListConverter dataObjectToEventListConverter = new DataObjectToEventListConverter(
                this.dlmsHelper);

        final RetrieveEventsCommandExecutor executor = new RetrieveEventsCommandExecutor(this.dlmsHelper,
                dataObjectToEventListConverter);

        final List<EventDto> events = executor.execute(this.conn, this.dlmsDevice, findEventsRequestDto);

        for (final EventDto event : events) {
            System.out.println("Got event " + event.getEventCode());
        }

    }

    private List<DataObject> generateDataObjects() {

        final DataObject eventCode = DataObject.newInteger16Data((short) 86);
        final DataObject eventTime = DataObject.newDateTimeData(new CosemDateTime(2018, 12, 31, 23, 50, 0, 0));

        final DataObject struct = DataObject.newStructureData(eventTime, eventCode);

        return Arrays.asList(struct);
    }

}
