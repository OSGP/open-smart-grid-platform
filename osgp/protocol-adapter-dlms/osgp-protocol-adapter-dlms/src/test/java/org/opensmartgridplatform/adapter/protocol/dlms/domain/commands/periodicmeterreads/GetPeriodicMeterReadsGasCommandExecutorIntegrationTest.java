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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AmrProfileStatusCodeHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.TestUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;

@RunWith(MockitoJUnitRunner.class)
public class GetPeriodicMeterReadsGasCommandExecutorIntegrationTest {

    private GetPeriodicMeterReadsGasCommandExecutor executor;

    private DlmsHelperService dlmsHelperService;
    private AmrProfileStatusCodeHelperService amrProfileStatusCodeHelperService;

    private DlmsConnectionManagerStub connectionManagerStub;
    private DlmsConnectionStub connectionStub;

    private final ObisCode OBIS_DAILY_DSMR4 = new ObisCode("1.0.99.2.0.255");
    private final ObisCode OBIS_INTERVAL_DSMR4 = new ObisCode("0.1.24.3.0.255");
    private final ObisCode OBIS_MONTHLY_DSMR4 = new ObisCode("0.0.98.1.0.255");

    private final ObisCode OBIS_DAILY_SMR5 = new ObisCode("0.1.24.3.1.255");
    private final ObisCode OBIS_INTERVAL_SMR5 = new ObisCode("0.1.24.3.0.255");
    private final ObisCode OBIS_MONTHLY_SMR5 = new ObisCode("0.1.24.3.2.255");

    private final ObisCode OBIS_CLOCK = new ObisCode("0.0.1.0.0.255");
    private final ObisCode OBIS_STATUS = new ObisCode("0.0.96.10.2.255");
    private final ObisCode OBIS_GAS_VALUE = new ObisCode("0.1.24.2.1.255");
    private final ObisCode OBIS_GAS_CAPTURE_TIME = new ObisCode("0.1.24.2.1.255");

    private final int CLASS_ID_CLOCK = 8;
    private final int CLASS_ID_DATA = 1;
    private final int CLASS_ID_EXTENDED_REGISTER = 4;
    private final int CLASS_ID_PROFILE = 7;

    private final byte ATTR_ID_VALUE = 2;
    private final byte ATTR_ID_BUFFER = 2;
    private final byte ATTR_ID_CAPTURE_TIME = 5;
    private final byte ATTR_ID_SCALER_UNIT = 3;

    private final DataObject CLOCK = DataObject.newStructureData(
            Arrays.asList(DataObject.newUInteger16Data(this.CLASS_ID_CLOCK),
                    DataObject.newOctetStringData(this.OBIS_CLOCK.bytes()),
                    DataObject.newInteger8Data(this.ATTR_ID_VALUE), DataObject.newUInteger16Data(0)));

    private final DataObject STATUS = DataObject.newStructureData(
            Arrays.asList(DataObject.newUInteger16Data(this.CLASS_ID_DATA),
                    DataObject.newOctetStringData(this.OBIS_STATUS.bytes()),
                    DataObject.newInteger8Data(this.ATTR_ID_VALUE), DataObject.newUInteger16Data(0)));

    private final DataObject GAS_VALUE = DataObject.newStructureData(
            Arrays.asList(DataObject.newUInteger16Data(this.CLASS_ID_EXTENDED_REGISTER),
                    DataObject.newOctetStringData(this.OBIS_GAS_VALUE.bytes()),
                    DataObject.newInteger8Data(this.ATTR_ID_VALUE), DataObject.newUInteger16Data(0)));

    private final DataObject GAS_CAPTURE_TIME = DataObject.newStructureData(
            Arrays.asList(DataObject.newUInteger16Data(this.CLASS_ID_EXTENDED_REGISTER),
                    DataObject.newOctetStringData(this.OBIS_GAS_CAPTURE_TIME.bytes()),
                    DataObject.newInteger8Data(this.ATTR_ID_CAPTURE_TIME), DataObject.newUInteger16Data(0)));

    @Before
    public void setUp() {
        this.dlmsHelperService = new DlmsHelperService();
        this.amrProfileStatusCodeHelperService = new AmrProfileStatusCodeHelperService();

        this.executor = new GetPeriodicMeterReadsGasCommandExecutor(this.dlmsHelperService,
                this.amrProfileStatusCodeHelperService);
        this.connectionStub = new DlmsConnectionStub();
        this.connectionManagerStub = new DlmsConnectionManagerStub(this.connectionStub);

        this.connectionStub.setDefaultReturnValue(DataObject.newArrayData(Collections.emptyList()));
    }

    @Test
    public void testExecuteDsmr4() throws Exception {
        for (final PeriodTypeDto type : PeriodTypeDto.values()) {
            this.testExecute(Protocol.DSMR_4_2_2, type);
        }
    }

    @Ignore // To be implemented
    @Test
    public void testExecuteSmr5_0() throws Exception {
        for (final PeriodTypeDto type : PeriodTypeDto.values()) {
            this.testExecute(Protocol.SMR_5_0, type);
        }
    }

    @Ignore // To be implemented
    @Test
    public void testExecuteSmr5_1() throws Exception {
        for (final PeriodTypeDto type : PeriodTypeDto.values()) {
            this.testExecute(Protocol.SMR_5_1, type);
        }
    }

    private void testExecute(final Protocol protocol, final PeriodTypeDto type) throws Exception {

        // Reset stub
        this.connectionStub.clearRequestedAttributeAddresses();

        // Create device with requested protocol version
        final DlmsDevice device = this.createDlmsDevice(protocol);

        // Create request object
        final Date timeFrom = new GregorianCalendar(2019, 1, 1).getTime();
        final Date timeTo = new GregorianCalendar(2019, 1, 5).getTime();
        final PeriodicMeterReadsRequestDto request = new PeriodicMeterReadsRequestDto(type, timeFrom, timeTo,
                ChannelDto.fromNumber(1));

        // Execute request
        this.executor.execute(this.connectionManagerStub, device, request);

        // Get resulting requests from connection stub
        final List<AttributeAddress> requestedAttributeAddresses = this.connectionStub.getRequestedAttributeAddresses();
        assertThat(requestedAttributeAddresses.size()).isEqualTo(2);

        // There should be 1 request to the buffer (id = 2) of a profile (class-id = 7)
        final AttributeAddress actualAttributeAddressProfile = requestedAttributeAddresses.stream()
                .filter(a -> a.getClassId() == this.CLASS_ID_PROFILE).collect(Collectors.toList()).get(0);

        final AttributeAddress expectedAttributeAddressProfile = this
                .createAttributeAddress(protocol, type, timeFrom, timeTo);
        TestUtil.assertAttributeAddressIs(actualAttributeAddressProfile, expectedAttributeAddressProfile);

        // There should be 1 request to the scaler_unit (id = 3) of the meter value in the register (class-id = 3)
        final List<AttributeAddress> attributeAddressesScalerUnit = requestedAttributeAddresses.stream()
                .filter(a -> a.getClassId() == this.CLASS_ID_EXTENDED_REGISTER && a.getId() == this.ATTR_ID_SCALER_UNIT)
                .collect(Collectors.toList());
        assertThat(attributeAddressesScalerUnit.size()).isEqualTo(1);
    }

    private DlmsDevice createDlmsDevice(final Protocol protocol) {
        final DlmsDevice device = new DlmsDevice();
        device.setProtocol(protocol.getName(), protocol.getVersion());
        device.setSelectiveAccessSupported(true);

        return device;
    }

    private AttributeAddress createAttributeAddress(final Protocol protocol, final PeriodTypeDto type,
            final Date timeFrom, final Date timeTo) throws Exception {
        final DataObject from = this.dlmsHelperService.asDataObject(new DateTime(timeFrom));
        final DataObject to = this.dlmsHelperService.asDataObject(new DateTime(timeTo));

        if (protocol == Protocol.DSMR_4_2_2) {
            if (type == PeriodTypeDto.DAILY) {
                return this.createAttributeAddressDsmr4Daily(from, to);
            } else if (type == PeriodTypeDto.MONTHLY) {
                return this.createAttributeAddressDsmr4Monthly(from, to);
            } else if (type == PeriodTypeDto.INTERVAL) {
                return this.createAttributeAddressDsmr4Interval(from, to);
            }
        } else if (protocol == Protocol.SMR_5_0 || protocol == Protocol.SMR_5_1) {
            if (type == PeriodTypeDto.DAILY) {
                return this.createAttributeAddressSmr5Daily(from, to);
            } else if (type == PeriodTypeDto.MONTHLY) {
                return this.createAttributeAddressSmr5Monthly(from, to);
            } else if (type == PeriodTypeDto.INTERVAL) {
                return this.createAttributeAddressSmr5Interval(from, to);
            }
        }

        throw new Exception(
                "Invalid combination of protocol " + protocol.getName() + " and version " + protocol.getVersion());
    }

    // DSMR4

    private AttributeAddress createAttributeAddressDsmr4Daily(final DataObject from, final DataObject to) {
        final SelectiveAccessDescription expectedSelectiveAccess = this
                .createSelectiveAccessDescriptionDsmr4Daily(from, to);
        return new AttributeAddress(this.CLASS_ID_PROFILE, this.OBIS_DAILY_DSMR4, this.ATTR_ID_BUFFER,
                expectedSelectiveAccess);
    }

    private SelectiveAccessDescription createSelectiveAccessDescriptionDsmr4Daily(final DataObject from,
            final DataObject to) {

        final DataObject selectedValues = DataObject
                .newArrayData(Arrays.asList(this.CLOCK, this.STATUS, this.GAS_VALUE, this.GAS_CAPTURE_TIME));

        final DataObject expectedAccessParam = DataObject
                .newStructureData(Arrays.asList(this.CLOCK, from, to, selectedValues));

        return new SelectiveAccessDescription(1, expectedAccessParam);
    }

    private AttributeAddress createAttributeAddressDsmr4Monthly(final DataObject from, final DataObject to) {
        final SelectiveAccessDescription expectedSelectiveAccess = this
                .createSelectiveAccessDescriptionDsmr4Monthly(from, to);
        return new AttributeAddress(this.CLASS_ID_PROFILE, this.OBIS_MONTHLY_DSMR4, this.ATTR_ID_BUFFER,
                expectedSelectiveAccess);
    }

    private SelectiveAccessDescription createSelectiveAccessDescriptionDsmr4Monthly(final DataObject from,
            final DataObject to) {

        final DataObject selectedValues = DataObject
                .newArrayData(Arrays.asList(this.CLOCK, this.GAS_VALUE, this.GAS_CAPTURE_TIME));

        final DataObject expectedAccessParam = DataObject
                .newStructureData(Arrays.asList(this.CLOCK, from, to, selectedValues));

        return new SelectiveAccessDescription(1, expectedAccessParam);
    }

    private AttributeAddress createAttributeAddressDsmr4Interval(final DataObject from, final DataObject to) {
        final SelectiveAccessDescription expectedSelectiveAccess = this
                .createSelectiveAccessDescriptionDsmr4Interval(from, to);
        return new AttributeAddress(this.CLASS_ID_PROFILE, this.OBIS_INTERVAL_DSMR4, this.ATTR_ID_BUFFER,
                expectedSelectiveAccess);
    }

    private SelectiveAccessDescription createSelectiveAccessDescriptionDsmr4Interval(final DataObject from,
            final DataObject to) {

        final DataObject selectedValues = DataObject.newArrayData(Collections.emptyList());

        final DataObject expectedAccessParam = DataObject
                .newStructureData(Arrays.asList(this.CLOCK, from, to, selectedValues));

        return new SelectiveAccessDescription(1, expectedAccessParam);
    }

    // SMR5

    private AttributeAddress createAttributeAddressSmr5Daily(final DataObject from, final DataObject to) {
        final SelectiveAccessDescription expectedSelectiveAccess = this.createSelectiveAccessDescriptionSmr5(from, to);
        return new AttributeAddress(this.CLASS_ID_PROFILE, this.OBIS_DAILY_SMR5, this.ATTR_ID_BUFFER,
                expectedSelectiveAccess);
    }

    private AttributeAddress createAttributeAddressSmr5Monthly(final DataObject from, final DataObject to) {
        final SelectiveAccessDescription expectedSelectiveAccess = this.createSelectiveAccessDescriptionSmr5(from, to);
        return new AttributeAddress(this.CLASS_ID_PROFILE, this.OBIS_MONTHLY_SMR5, this.ATTR_ID_BUFFER,
                expectedSelectiveAccess);
    }

    private AttributeAddress createAttributeAddressSmr5Interval(final DataObject from, final DataObject to) {
        final SelectiveAccessDescription expectedSelectiveAccess = this.createSelectiveAccessDescriptionSmr5(from, to);
        return new AttributeAddress(this.CLASS_ID_PROFILE, this.OBIS_INTERVAL_SMR5, this.ATTR_ID_BUFFER,
                expectedSelectiveAccess);
    }

    private SelectiveAccessDescription createSelectiveAccessDescriptionSmr5(final DataObject from,
            final DataObject to) {

        final DataObject selectedValues = DataObject.newArrayData(Collections.emptyList());

        final DataObject expectedAccessParam = DataObject
                .newStructureData(Arrays.asList(this.CLOCK, from, to, selectedValues));

        return new SelectiveAccessDescription(1, expectedAccessParam);
    }
}

