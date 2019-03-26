/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
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
    private AttributeAddressHelperService attributeAddressHelperService;

    @Mock
    private AmrProfileStatusCodeHelperService amrProfileStatusCodeHelperService;

    private DlmsConnectionHolder connectionHolder;

    @Before
    public void setUp() {
        this.executor = new GetPeriodicMeterReadsCommandExecutor(this.helperService,
                this.amrProfileStatusCodeHelperService, this.attributeAddressHelperService);
        this.connectionHolder = new DlmsConnectionHolder(null, null, this.listener, null);
    }

    @Test
    public void testUsingIsSelectingValuesSupportedInExecute() throws Exception {

        // DSMR 4.2.2 supports selecting values in a buffer.
        this.assertIsSelectingValuesSupported("DSMR", "4.2.2", true);

        // SMR 5.0 doesn't support selecting values in a buffer.
        this.assertIsSelectingValuesSupported("SMR", "5.0", false);

        // SMR 5.1 doesn't support selecting values in a buffer.
        this.assertIsSelectingValuesSupported("SMR", "5.1", false);

        // For other protocols it is assumed that they support selecting values in a buffer.
        this.assertIsSelectingValuesSupported("other", "5.0", true);
    }

    private void assertIsSelectingValuesSupported(final String protocolName, final String protocolVersion,
            final Boolean isSelectingValuesSupported) throws Exception {

        // Create device with request protocol version
        final DlmsDevice device = new DlmsDevice();
        device.setProtocol(protocolName, protocolVersion);

        // Setup mock
        final GetResult getResult1 = new GetResultBuilder().build();
        final GetResult getResult2 = new GetResultBuilder().build();
        final DataObject resultData = DataObject.newArrayData(new ArrayList<>());

        when(this.helperService.getAndCheck(same(this.connectionHolder), same(device), any(), any())).thenReturn(asList(getResult1, getResult2));
        when(this.helperService.readDataObject(any(), any())).thenReturn(resultData);

        // Setup captor to capture the selected values
        final AttributeAddress[] attributeAddresses = new AttributeAddress[]{ CLOCK };
        final ArgumentCaptor<Boolean> isSelectingValuesSupportedCaptor = ArgumentCaptor.forClass(Boolean.class);
        when(this.attributeAddressHelperService.getProfileBufferAndScalerUnitForPeriodicMeterReads(any(), any(),
                any(), isSelectingValuesSupportedCaptor.capture())).thenReturn(attributeAddresses);

        // Execute request
        final Date timeFrom = new GregorianCalendar(2019, 1, 1).getTime();
        final Date timeTo = new GregorianCalendar(2019, 1, 5).getTime();
        final PeriodicMeterReadsRequestDto request = new PeriodicMeterReadsRequestDto(PeriodTypeDto.DAILY, timeFrom, timeTo);
        this.executor.execute(this.connectionHolder, device, request);

        // Check if command executor uses right setting
        Assertions.assertThat(isSelectingValuesSupportedCaptor.getValue()).isEqualTo(isSelectingValuesSupported);
    }
}

