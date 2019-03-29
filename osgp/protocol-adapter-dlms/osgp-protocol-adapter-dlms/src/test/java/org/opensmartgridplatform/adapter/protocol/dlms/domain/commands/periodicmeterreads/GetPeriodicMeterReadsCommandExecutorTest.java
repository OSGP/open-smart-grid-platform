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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AmrProfileStatusCodeHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.GetResultBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;

@RunWith(MockitoJUnitRunner.class)
public class GetPeriodicMeterReadsCommandExecutorTest {
    private static final AttributeAddress CLOCK = new AttributeAddress(8, new ObisCode(0, 0, 1, 0, 0, 255), 2);

    private GetPeriodicMeterReadsCommandExecutor executor;

    @Mock
    private DlmsMessageListener listener;

    @Mock
    private DlmsHelperService helperService;

    @Mock
    private AttributeAddressService attributeAddressService;

    @Mock
    private AmrProfileStatusCodeHelperService amrProfileStatusCodeHelperService;

    private DlmsConnectionManager connectionManager;

    @Captor ArgumentCaptor<Boolean> isSelectingValuesSupportedCaptor;

    @Before
    public void setUp() {
        this.executor = new GetPeriodicMeterReadsCommandExecutor(this.helperService,
                this.amrProfileStatusCodeHelperService, this.attributeAddressService);
        this.connectionManager = new DlmsConnectionManager(null, null, this.listener, null);
    }

    @Test
    public void testUsingIsSelectingValuesSupportedInExecute() throws Exception {

        for (final Protocol protocol : Protocol.values()) {
            this.assertIsSelectingValuesSupported(protocol.getName(), protocol.getVersion(), protocol.isSelectValuesInSelectiveAccessSupported() );
        }
    }

    private void assertIsSelectingValuesSupported(final String protocolName, final String protocolVersion,
            final boolean isSelectingValuesSupported) throws Exception {

        // Create device with request protocol version
        final DlmsDevice device = new DlmsDevice();
        device.setProtocol(protocolName, protocolVersion);

        // Setup mocks
        final GetResult getResult1 = new GetResultBuilder().build();
        final GetResult getResult2 = new GetResultBuilder().build();
        final DataObject resultData = DataObject.newArrayData(new ArrayList<>());
        final AttributeAddress[] attributeAddresses = new AttributeAddress[]{ CLOCK };

        when(this.helperService.getAndCheck(same(this.connectionManager), same(device), any(), any())).thenReturn(asList(getResult1, getResult2));
        when(this.helperService.readDataObject(any(), any())).thenReturn(resultData);
        when(this.attributeAddressService.getProfileBufferAndScalerUnitForPeriodicMeterReads(any(), any(),
                any(), anyBoolean())).thenReturn(attributeAddresses);

        // Execute request
        final Date timeFrom = new GregorianCalendar(2019, 1, 1).getTime();
        final Date timeTo = new GregorianCalendar(2019, 1, 5).getTime();
        final PeriodicMeterReadsRequestDto request = new PeriodicMeterReadsRequestDto(PeriodTypeDto.DAILY, timeFrom, timeTo);
        this.executor.execute(this.connectionManager, device, request);

        // Check if command executor uses right setting
        verify(this.attributeAddressService).getProfileBufferAndScalerUnitForPeriodicMeterReads(any(), any(),
                any(), this.isSelectingValuesSupportedCaptor.capture());
        assertThat(this.isSelectingValuesSupportedCaptor.getValue()).isEqualTo(isSelectingValuesSupported);

        // Reset mock
        reset(this.attributeAddressService);
    }
}

