package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
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
public class FindEventsCommandExecutorTest {

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

    @Mock
    private DataObject resultData;

    @Mock
    private GetResult getResult;

    private FindEventsRequestDto findEventsRequestDto;
    private DataObjectToEventListConverter dataObjectToEventListConverter;

    @Before
    public void before() throws ProtocolAdapterException, IOException {

        final DataObject fromDate = mock(DataObject.class);
        final DataObject toDate = mock(DataObject.class);

        this.findEventsRequestDto = new FindEventsRequestDto(EventLogCategoryDto.POWER_QUALITY_EVENT_LOG,
                DateTime.now().minusDays(70), DateTime.now());

        this.dataObjectToEventListConverter = new DataObjectToEventListConverter(this.dlmsHelper);

        when(this.dlmsHelper.asDataObject(this.findEventsRequestDto.getFrom())).thenReturn(fromDate);
        when(this.dlmsHelper.asDataObject(this.findEventsRequestDto.getUntil())).thenReturn(toDate);
        when(this.dlmsHelper.convertDataObjectToDateTime(any(DataObject.class))).thenReturn(new CosemDateTimeDto());
        when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
        when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
        when(this.dlmsConnection.get(any(AttributeAddress.class))).thenReturn(this.getResult);
    }

    @After
    public void after() throws ProtocolAdapterException, IOException {
        verify(this.dlmsHelper).asDataObject(this.findEventsRequestDto.getFrom());
        verify(this.dlmsHelper).asDataObject(this.findEventsRequestDto.getUntil());
        verify(this.conn).getDlmsMessageListener();
        verify(this.conn).getConnection();
        verify(this.dlmsConnection).get(any(AttributeAddress.class));
    }

    @Test
    public void testRetrievalOfPowerQualityEvents() throws ProtocolAdapterException {

        when(this.getResult.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
        when(this.getResult.getResultData()).thenReturn(this.resultData);
        when(this.resultData.getValue()).thenReturn(this.generateDataObjects());

        final FindEventsCommandExecutor executor = new FindEventsCommandExecutor(this.dlmsHelper,
                this.dataObjectToEventListConverter);

        final List<EventDto> events = executor.execute(this.conn, this.dlmsDevice, this.findEventsRequestDto);

        assertThat(events.size()).isEqualTo(13);

        int firstEventCode = 77;
        for (final EventDto event : events) {
            assertThat(event.getEventCode()).isEqualTo(firstEventCode++);
        }

        verify(this.dlmsHelper, times(events.size())).convertDataObjectToDateTime(any(DataObject.class));
    }

    @Test(expected = ProtocolAdapterException.class)
    public void testOtherReasonResult() throws ProtocolAdapterException {

        when(this.getResult.getResultCode()).thenReturn(AccessResultCode.OTHER_REASON);

        new FindEventsCommandExecutor(this.dlmsHelper, this.dataObjectToEventListConverter).execute(this.conn,
                this.dlmsDevice, this.findEventsRequestDto);
    }

    @Test(expected = ProtocolAdapterException.class)
    public void testEmptyGetResult() throws ProtocolAdapterException, IOException {

        when(this.dlmsConnection.get(any(AttributeAddress.class))).thenReturn(null);

        new FindEventsCommandExecutor(this.dlmsHelper, this.dataObjectToEventListConverter).execute(this.conn,
                this.dlmsDevice, this.findEventsRequestDto);
    }

    private List<DataObject> generateDataObjects() {

        final List<DataObject> dataObjects = new ArrayList<>();

        IntStream.rangeClosed(77, 89).forEach(code -> {
            final DataObject eventCode = DataObject.newInteger16Data((short) code);
            final DataObject eventTime = DataObject.newDateTimeData(
                    new CosemDateTime(2018, 12, 31, 23, code - 60, 0, 0));

            final DataObject struct = DataObject.newStructureData(eventTime, eventCode);

            dataObjects.add(struct);

        });

        return dataObjects;
    }

}
