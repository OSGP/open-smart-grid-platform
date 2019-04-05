/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;

@RunWith(MockitoJUnitRunner.class)
public class GetPeriodicMeterReadsGasCommandExecutorTest {

    private GetPeriodicMeterReadsGasCommandExecutor executor;

    private DlmsConnectionManagerStub connectionManagerStub;
    private DlmsConnectionStub connectionStub;

    @Before
    public void setUp() {
        this.executor = new GetPeriodicMeterReadsGasCommandExecutor();
        this.connectionStub = new DlmsConnectionStub();
        this.connectionManagerStub = new DlmsConnectionManagerStub(this.connectionStub);

        this.connectionStub.setDefaultReturnValue(DataObject.newArrayData(Collections.emptyList()));
    }

    @Test
    public void testExecute() throws Exception {

        // Create device with requested protocol version
        final DlmsDevice device = new DlmsDevice();
        device.setProtocol("DSMR", "4.2.2");

        // Create request object
        final Date timeFrom = new GregorianCalendar(2019, 1, 1).getTime();
        final Date timeTo = new GregorianCalendar(2019, 1, 5).getTime();
        final PeriodicMeterReadsRequestDto request = new PeriodicMeterReadsRequestDto(PeriodTypeDto.DAILY, timeFrom,
                timeTo);

        // Execute request
        this.executor.execute(this.connectionManagerStub, device, request);

        // Get resulting requests from connection stub
        final List<AttributeAddress> requestedAttributeAddresses = this.connectionStub.getRequestedAttributeAddresses();

        // Check the results
        assertThat(requestedAttributeAddresses.size()).isEqualTo(5);

        // There should be 1 request to the buffer (id = 2) of a profile (class-id = 7)
        final AttributeAddress attributeAddressProfile = requestedAttributeAddresses.stream()
                .filter(a -> a.getClassId() == 7).collect(Collectors.toList()).get(0);
        assertThat(attributeAddressProfile.getInstanceId().asDecimalString()).isEqualTo("1.0.99.2.0.255");
        assertThat(attributeAddressProfile.getId()).isEqualTo(2);

        // There should be 4 requests to the scaler_unit (id = 3) of the meter values in registers (class-id = 3)
        final List<AttributeAddress> attributeAddressesScalerUnit = requestedAttributeAddresses.stream()
                .filter(a -> a.getClassId() == 3 && a.getId() == 3).collect(Collectors.toList());
        assertThat(attributeAddressesScalerUnit.size()).isEqualTo(4);
    }
}

