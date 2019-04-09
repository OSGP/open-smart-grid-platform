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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.TestUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
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
        device.setSelectiveAccessSupported(true);

        // Create request object
        final Date timeFrom = new GregorianCalendar(2019, 1, 1).getTime();
        final Date timeTo = new GregorianCalendar(2019, 1, 5).getTime();
        final PeriodicMeterReadsRequestDto request = new PeriodicMeterReadsRequestDto(PeriodTypeDto.DAILY, timeFrom,
                timeTo, ChannelDto.fromNumber(1));

        // Execute request
        this.executor.execute(this.connectionManagerStub, device, request);

        // Get resulting requests from connection stub
        final List<AttributeAddress> requestedAttributeAddresses = this.connectionStub.getRequestedAttributeAddresses();

        // Check the results
        assertThat(requestedAttributeAddresses.size()).isEqualTo(2);

        // There should be 1 request to the buffer (id = 2) of a profile (class-id = 7)
        final SelectiveAccessDescription expectedSelectiveAccess = this
                .createSelectiveAccessDescription(timeFrom, timeTo);
        final AttributeAddress expectedAttributeAddressProfile = new AttributeAddress(7, new ObisCode("1.0.99.2.0.255"),
                2, expectedSelectiveAccess);

        final AttributeAddress attributeAddressProfile = requestedAttributeAddresses.stream()
                .filter(a -> a.getClassId() == 7).collect(Collectors.toList()).get(0);
        assertThat(attributeAddressProfile.getInstanceId().asDecimalString()).isEqualTo("1.0.99.2.0.255");
        assertThat(attributeAddressProfile.getId()).isEqualTo(2);
        TestUtil.assertAttributeAddressIs(attributeAddressProfile, expectedAttributeAddressProfile);

        // There should be 1 request to the scaler_unit (id = 3) of the meter value in the register (class-id = 3)
        final List<AttributeAddress> attributeAddressesScalerUnit = requestedAttributeAddresses.stream()
                .filter(a -> a.getClassId() == 4 && a.getId() == 3).collect(Collectors.toList());
        assertThat(attributeAddressesScalerUnit.size()).isEqualTo(1);
    }

    private SelectiveAccessDescription createSelectiveAccessDescription(final Date timeFrom, final Date timeTo) {
        final DataObject clock = DataObject
                .newStructureData(Arrays.asList(DataObject.newUInteger16Data(8), // Class-id Clock
                        DataObject.newOctetStringData((new ObisCode("0.0.1.0.0.255")).bytes()),  // Obis code clock
                        DataObject.newInteger8Data((byte) 2), // Attribute id
                        DataObject.newUInteger16Data(0)));

        final DataObject status = DataObject
                .newStructureData(Arrays.asList(DataObject.newUInteger16Data(1), // Class-id Data
                        DataObject.newOctetStringData((new ObisCode("0.0.96.10.2.255")).bytes()),  // Obis AMR status
                        DataObject.newInteger8Data((byte) 2), // Attribute id
                        DataObject.newUInteger16Data(0)));

        final DataObject gasValue = DataObject
                .newStructureData(Arrays.asList(DataObject.newUInteger16Data(4), // Class-id Extended register
                        DataObject.newOctetStringData((new ObisCode("0.1.24.2.1.255")).bytes()), // Obis Mbus value
                        DataObject.newInteger8Data((byte) 2), // Attribute id Value
                        DataObject.newUInteger16Data(0)));

        final DataObject gasCaptureTime = DataObject
                .newStructureData(Arrays.asList(DataObject.newUInteger16Data(4), // Class-id Extended register
                        DataObject.newOctetStringData((new ObisCode("0.1.24.2.1.255")).bytes()), // Obis Mbus value
                        DataObject.newInteger8Data((byte) 5), // Attribute id Capture time
                        DataObject.newUInteger16Data(0)));

        final DataObject from = DlmsHelper.asDataObject(new DateTime(timeFrom));
        final DataObject to = DlmsHelper.asDataObject(new DateTime(timeTo));

        final DataObject selectedValues = DataObject
                .newArrayData(Arrays.asList(clock, status, gasValue, gasCaptureTime));

        final DataObject expectedAccessParam = DataObject
                .newStructureData(Arrays.asList(clock, from, to, selectedValues));

        return new SelectiveAccessDescription(1, expectedAccessParam);
    }

}

