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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigConfiguration;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.AttributeAddressAssert;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.AmrProfileStatusCodeHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseItemDto;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GetPeriodicMeterReadsCommandExecutorIntegrationTest {

    private GetPeriodicMeterReadsCommandExecutor executor;

    private DlmsHelper dlmsHelper;
    private AmrProfileStatusCodeHelper amrProfileStatusCodeHelper;
    private DlmsObjectConfigService dlmsObjectConfigService;

    private DlmsConnectionManagerStub connectionManagerStub;
    private DlmsConnectionStub connectionStub;

    private final ObisCode OBIS_DAILY_DSMR4 = new ObisCode("1.0.99.2.0.255");
    private final ObisCode OBIS_INTERVAL_DSMR4 = new ObisCode("1.0.99.1.0.255");
    private final ObisCode OBIS_MONTHLY_DSMR4 = new ObisCode("0.0.98.1.0.255");

    private final ObisCode OBIS_DAILY_SMR5 = new ObisCode("1.0.99.2.0.255");
    private final ObisCode OBIS_INTERVAL_SMR5 = new ObisCode("1.0.99.1.0.255");
    private final ObisCode OBIS_MONTHLY_SMR5 = new ObisCode("1.0.98.1.0.255");

    private final ObisCode OBIS_CLOCK = new ObisCode("0.0.1.0.0.255");
    private final ObisCode OBIS_STATUS = new ObisCode("0.0.96.10.2.255");
    private final ObisCode OBIS_ACTIVE_ENERGY_IMPORT = new ObisCode("1.0.1.8.0.255");
    private final ObisCode OBIS_ACTIVE_ENERGY_EXPORT = new ObisCode("1.0.2.8.0.255");
    private final ObisCode OBIS_ACTIVE_ENERGY_IMPORT_RATE_1 = new ObisCode("1.0.1.8.1.255");
    private final ObisCode OBIS_ACTIVE_ENERGY_IMPORT_RATE_2 = new ObisCode("1.0.1.8.2.255");
    private final ObisCode OBIS_ACTIVE_ENERGY_EXPORT_RATE_1 = new ObisCode("1.0.2.8.1.255");
    private final ObisCode OBIS_ACTIVE_ENERGY_EXPORT_RATE_2 = new ObisCode("1.0.2.8.2.255");

    private final int CLASS_ID_CLOCK = 8;
    private final int CLASS_ID_DATA = 1;
    private final int CLASS_ID_REGISTER = 3;
    private final int CLASS_ID_PROFILE = 7;

    private final byte ATTR_ID_VALUE = 2;
    private final byte ATTR_ID_BUFFER = 2;
    private final byte ATTR_ID_SCALER_UNIT = 3;

    private final DataObject CLOCK = DataObject.newStructureData(
            Arrays.asList(DataObject.newUInteger16Data(this.CLASS_ID_CLOCK),
                    DataObject.newOctetStringData(this.OBIS_CLOCK.bytes()),
                    DataObject.newInteger8Data(this.ATTR_ID_VALUE), DataObject.newUInteger16Data(0)));

    private final DataObject STATUS = DataObject.newStructureData(
            Arrays.asList(DataObject.newUInteger16Data(this.CLASS_ID_DATA),
                    DataObject.newOctetStringData(this.OBIS_STATUS.bytes()),
                    DataObject.newInteger8Data(this.ATTR_ID_VALUE), DataObject.newUInteger16Data(0)));

    private final DataObject ACTIVE_ENERGY_IMPORT_RATE_1 = DataObject.newStructureData(
            Arrays.asList(DataObject.newUInteger16Data(this.CLASS_ID_REGISTER),
                    DataObject.newOctetStringData(this.OBIS_ACTIVE_ENERGY_IMPORT_RATE_1.bytes()),
                    DataObject.newInteger8Data(this.ATTR_ID_VALUE), DataObject.newUInteger16Data(0)));

    private final DataObject ACTIVE_ENERGY_IMPORT_RATE_2 = DataObject.newStructureData(
            Arrays.asList(DataObject.newUInteger16Data(this.CLASS_ID_REGISTER),
                    DataObject.newOctetStringData(this.OBIS_ACTIVE_ENERGY_IMPORT_RATE_2.bytes()),
                    DataObject.newInteger8Data(this.ATTR_ID_VALUE), DataObject.newUInteger16Data(0)));

    private final DataObject ACTIVE_ENERGY_EXPORT_RATE_1 = DataObject.newStructureData(
            Arrays.asList(DataObject.newUInteger16Data(this.CLASS_ID_REGISTER),
                    DataObject.newOctetStringData(this.OBIS_ACTIVE_ENERGY_EXPORT_RATE_1.bytes()),
                    DataObject.newInteger8Data(this.ATTR_ID_VALUE), DataObject.newUInteger16Data(0)));

    private final DataObject ACTIVE_ENERGY_EXPORT_RATE_2 = DataObject.newStructureData(
            Arrays.asList(DataObject.newUInteger16Data(this.CLASS_ID_REGISTER),
                    DataObject.newOctetStringData(this.OBIS_ACTIVE_ENERGY_EXPORT_RATE_2.bytes()),
                    DataObject.newInteger8Data(this.ATTR_ID_VALUE), DataObject.newUInteger16Data(0)));

    @Before
    public void setUp() {
        this.dlmsHelper = new DlmsHelper();
        this.amrProfileStatusCodeHelper = new AmrProfileStatusCodeHelper();
        final DlmsObjectConfigConfiguration dlmsObjectConfigConfiguration = new DlmsObjectConfigConfiguration();
        this.dlmsObjectConfigService = new DlmsObjectConfigService(this.dlmsHelper,
                dlmsObjectConfigConfiguration.getDlmsObjectConfigs());

        this.executor = new GetPeriodicMeterReadsCommandExecutor(this.dlmsHelper, this.amrProfileStatusCodeHelper,
                dlmsObjectConfigService);
        this.connectionStub = new DlmsConnectionStub();
        this.connectionManagerStub = new DlmsConnectionManagerStub(this.connectionStub);

        this.connectionStub.setDefaultReturnValue(DataObject.newArrayData(Collections.emptyList()));
    }

    @Test
    public void testExecuteDsmr4() throws Exception {
        for (final PeriodTypeDto type : PeriodTypeDto.values()) {
            this.testExecute(Protocol.DSMR_4_2_2, type, false);
        }
    }

    @Test
    public void testExecuteSmr5_0() throws Exception {
        for (final PeriodTypeDto type : PeriodTypeDto.values()) {
            this.testExecute(Protocol.SMR_5_0, type, false);
        }
    }

    @Test
    public void testExecuteSmr5_0_WithNullData() throws Exception {
        for (final PeriodTypeDto type : PeriodTypeDto.values()) {
            this.testExecute(Protocol.SMR_5_0, type, true);
        }
    }

    @Test
    public void testExecuteSmr5_1() throws Exception {
        for (final PeriodTypeDto type : PeriodTypeDto.values()) {
            this.testExecute(Protocol.SMR_5_1, type, false);
        }
    }

    @Test
    public void testExecuteSmr5_1_WithNullData() throws Exception {
        for (final PeriodTypeDto type : PeriodTypeDto.values()) {
            this.testExecute(Protocol.SMR_5_1, type, true);
        }
    }

    private void testExecute(final Protocol protocol, final PeriodTypeDto type, boolean useNullData) throws Exception {

        // Reset stub
        this.connectionStub.clearRequestedAttributeAddresses();

        // Create device with requested protocol version
        final DlmsDevice device = this.createDlmsDevice(protocol);

        // Create request object
        final Date timeFrom = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
        final Date timeTo = new GregorianCalendar(2019, Calendar.JANUARY, 5).getTime();
        final PeriodicMeterReadsRequestDto request = new PeriodicMeterReadsRequestDto(type, timeFrom, timeTo);

        // Get expected values
        final AttributeAddress expectedAttributeAddressProfile = this.createAttributeAddress(protocol, type, timeFrom,
                timeTo);
        List<AttributeAddress> expectedScalerUnitAttributeAddresses = this.getScalerUnitAttributeAddresses(type);
        int expectedTotalNumberOfAttributeAddresses = expectedScalerUnitAttributeAddresses.size() + 1;

        // Set response in stub
        setResponseForProfile(expectedAttributeAddressProfile, protocol, type, useNullData);
        setResponsesForScalerUnit(expectedScalerUnitAttributeAddresses);

        // Execute request
        PeriodicMeterReadsResponseDto response = this.executor.execute(this.connectionManagerStub, device, request);

        // Get resulting requests from connection stub
        final List<AttributeAddress> requestedAttributeAddresses = this.connectionStub.getRequestedAttributeAddresses();
        assertThat(requestedAttributeAddresses.size()).isEqualTo(expectedTotalNumberOfAttributeAddresses);

        // There should be 1 request to the buffer (id = 2) of a profile (class-id = 7)
        final AttributeAddress actualAttributeAddressProfile = requestedAttributeAddresses.stream()
                .filter(a -> a.getClassId() == this.CLASS_ID_PROFILE)
                .collect(Collectors.toList())
                .get(0);

        AttributeAddressAssert.is(actualAttributeAddressProfile, expectedAttributeAddressProfile);

        // There should be 4 requests to the scaler_units (id = 3) of the meter values in the registers (class-id = 3)
        final List<AttributeAddress> attributeAddressesScalerUnit = requestedAttributeAddresses.stream()
                .filter(a -> a.getClassId() == this.CLASS_ID_REGISTER && a.getId() == this.ATTR_ID_SCALER_UNIT)
                .collect(Collectors.toList());
        assertThat(attributeAddressesScalerUnit.size()).isEqualTo(expectedScalerUnitAttributeAddresses.size());

        // Check response
        assertThat(response.getPeriodType()).isEqualTo(type);
        List<PeriodicMeterReadsResponseItemDto> periodicMeterReads = response.getPeriodicMeterReads();
        assertThat(periodicMeterReads.size()).isEqualTo(2);
        PeriodicMeterReadsResponseItemDto periodicMeterRead1 = periodicMeterReads.get(0);
        PeriodicMeterReadsResponseItemDto periodicMeterRead2 = periodicMeterReads.get(1);

        if (!useNullData) { // The timestamps should be the same as the times set in the test
            assertThat(periodicMeterRead1.getLogTime()).isEqualTo(new GregorianCalendar(2019, Calendar.JANUARY, 1, 1, 0).getTime());
            assertThat(periodicMeterRead2.getLogTime()).isEqualTo(new GregorianCalendar(2019, Calendar.JANUARY, 2, 1, 0).getTime());
        } else { // The timestamps should be calculated using the periodType, starting from the time of period 1
            assertThat(periodicMeterRead1.getLogTime()).isEqualTo(new GregorianCalendar(2019, Calendar.JANUARY, 1, 1, 0).getTime());

            if (type == PeriodTypeDto.INTERVAL) {
                assertThat(periodicMeterRead2.getLogTime()).isEqualTo(new GregorianCalendar(2019, Calendar.JANUARY, 1
                        , 1, 15).getTime());
            } else if (type == PeriodTypeDto.DAILY) {
                assertThat(periodicMeterRead2.getLogTime()).isEqualTo(new GregorianCalendar(2019, Calendar.JANUARY, 2
                        , 1, 0).getTime());
            } else if (type == PeriodTypeDto.MONTHLY) {
                assertThat(periodicMeterRead2.getLogTime()).isEqualTo(new GregorianCalendar(2019, Calendar.FEBRUARY, 1
                        , 1, 0).getTime());
            }
        }

        if (type == PeriodTypeDto.MONTHLY || type == PeriodTypeDto.DAILY) {
            assertThat(periodicMeterRead1.getActiveEnergyImportTariffOne().getValue().longValue()).isEqualTo(1000L);
            assertThat(periodicMeterRead1.getActiveEnergyImportTariffTwo().getValue().longValue()).isEqualTo(2000L);
            assertThat(periodicMeterRead1.getActiveEnergyExportTariffOne().getValue().longValue()).isEqualTo(3000L);
            assertThat(periodicMeterRead1.getActiveEnergyExportTariffTwo().getValue().longValue()).isEqualTo(4000L);
            assertThat(periodicMeterRead2.getActiveEnergyImportTariffOne().getValue().longValue()).isEqualTo(1500L);
            assertThat(periodicMeterRead2.getActiveEnergyImportTariffTwo().getValue().longValue()).isEqualTo(2500L);
            assertThat(periodicMeterRead2.getActiveEnergyExportTariffOne().getValue().longValue()).isEqualTo(3500L);
            assertThat(periodicMeterRead2.getActiveEnergyExportTariffTwo().getValue().longValue()).isEqualTo(4500L);
        } else { // INTERVAL
            assertThat(periodicMeterRead1.getActiveEnergyImport().getValue().longValue()).isEqualTo(1000L);
            assertThat(periodicMeterRead1.getActiveEnergyExport().getValue().longValue()).isEqualTo(2000L);
            assertThat(periodicMeterRead2.getActiveEnergyImport().getValue().longValue()).isEqualTo(1500L);
            assertThat(periodicMeterRead2.getActiveEnergyExport().getValue().longValue()).isEqualTo(2500L);
        }
    }

    private DlmsDevice createDlmsDevice(final Protocol protocol) {
        final DlmsDevice device = new DlmsDevice();
        device.setProtocol(protocol.getName(), protocol.getVersion());
        device.setSelectiveAccessSupported(true);

        return device;
    }

    private AttributeAddress createAttributeAddress(final Protocol protocol, final PeriodTypeDto type,
                                                    final Date timeFrom, final Date timeTo) throws Exception {
        final DataObject from = this.dlmsHelper.asDataObject(new DateTime(timeFrom));
        final DataObject to = this.dlmsHelper.asDataObject(new DateTime(timeTo));

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

    private List<AttributeAddress> getScalerUnitAttributeAddresses(PeriodTypeDto type) throws Exception {
        List<AttributeAddress> attributeAddresses = new ArrayList<>();

        switch (type) {
            case MONTHLY:
            case DAILY:
                attributeAddresses.add(new AttributeAddress(this.CLASS_ID_REGISTER,
                        this.OBIS_ACTIVE_ENERGY_IMPORT_RATE_1, this.ATTR_ID_SCALER_UNIT, null));
                attributeAddresses.add(new AttributeAddress(this.CLASS_ID_REGISTER,
                        this.OBIS_ACTIVE_ENERGY_IMPORT_RATE_2, this.ATTR_ID_SCALER_UNIT, null));
                attributeAddresses.add(new AttributeAddress(this.CLASS_ID_REGISTER,
                        this.OBIS_ACTIVE_ENERGY_EXPORT_RATE_1, this.ATTR_ID_SCALER_UNIT, null));
                attributeAddresses.add(new AttributeAddress(this.CLASS_ID_REGISTER,
                        this.OBIS_ACTIVE_ENERGY_EXPORT_RATE_2, this.ATTR_ID_SCALER_UNIT, null));
                break;
            case INTERVAL:
                attributeAddresses.add(new AttributeAddress(this.CLASS_ID_REGISTER,
                        this.OBIS_ACTIVE_ENERGY_IMPORT, this.ATTR_ID_SCALER_UNIT, null));
                attributeAddresses.add(new AttributeAddress(this.CLASS_ID_REGISTER,
                        this.OBIS_ACTIVE_ENERGY_EXPORT, this.ATTR_ID_SCALER_UNIT, null));
                break;
            default:
                throw new Exception("Unexpected period type " + type);

        }
        return attributeAddresses;
    }

    private void setResponseForProfile(AttributeAddress attributeAddressForProfile, Protocol protocol,
                                       PeriodTypeDto type, boolean useNullData) {

        // PERIOD 1

        DataObject period1Clock = getDateAsOctetString(2019, 1, 1);
        DataObject period1Status = DataObject.newUInteger8Data((byte) 8);
        DataObject period1Value1 = DataObject.newUInteger32Data(1000L);
        DataObject period1Value2 = DataObject.newUInteger32Data(2000L);
        DataObject period1Value3 = DataObject.newUInteger32Data(3000L);
        DataObject period1Value4 = DataObject.newUInteger32Data(4000L);

        DataObject periodItem1;
        if (type == PeriodTypeDto.MONTHLY && protocol == Protocol.DSMR_4_2_2) {
            periodItem1 = DataObject.newStructureData(Arrays.asList(period1Clock, period1Value1, period1Value2,
                    period1Value3, period1Value4));
        } else {
            periodItem1 = DataObject.newStructureData(Arrays.asList(period1Clock, period1Status, period1Value1,
                    period1Value2, period1Value3, period1Value4));
        }

        // PERIOD 2

        DataObject period2Clock;
        if (useNullData) {
            period2Clock = DataObject.newNullData();
        } else {
            period2Clock = getDateAsOctetString(2019, 1, 2);
        }
        DataObject period2Status = DataObject.newUInteger8Data((byte) 8);
        DataObject period2Value1 = DataObject.newUInteger32Data(1500L);
        DataObject period2Value2 = DataObject.newUInteger32Data(2500L);
        DataObject period2Value3 = DataObject.newUInteger32Data(3500L);
        DataObject period2Value4 = DataObject.newUInteger32Data(4500L);

        DataObject periodItem2;
        if (type == PeriodTypeDto.MONTHLY && protocol == Protocol.DSMR_4_2_2) {
            periodItem2 = DataObject.newStructureData(Arrays.asList(period2Clock, period2Value1, period2Value2,
                    period2Value3, period2Value4));
        } else {
            periodItem2 = DataObject.newStructureData(Arrays.asList(period2Clock, period2Status, period2Value1,
                    period2Value2, period2Value3, period2Value4));
        }

        // Create returnvalue and set in stub
        DataObject responseDataObject = DataObject.newArrayData(Arrays.asList(periodItem1, periodItem2));
        connectionStub.addReturnValue(attributeAddressForProfile, responseDataObject);
    }

    private void setResponsesForScalerUnit(List<AttributeAddress> attributeAddressesForScalerUnit) {
        DataObject responseDataObject = DataObject.newStructureData(DataObject.newInteger8Data((byte) 0),
                DataObject.newEnumerateData(30));

        for (AttributeAddress attributeAddress : attributeAddressesForScalerUnit) {
            connectionStub.addReturnValue(attributeAddress, responseDataObject);
        }
    }

    private DataObject getDateAsOctetString(int year, int month, int day) {
        final CosemDateTime dateTime = new CosemDateTime(year, month, day, 0, 0, 0, 0);

        return DataObject.newOctetStringData(dateTime.encode());
    }

    // DSMR4

    private AttributeAddress createAttributeAddressDsmr4Daily(final DataObject from, final DataObject to) {
        final SelectiveAccessDescription expectedSelectiveAccess = this.createSelectiveAccessDescriptionDsmr4Daily(from,
                to);
        return new AttributeAddress(this.CLASS_ID_PROFILE, this.OBIS_DAILY_DSMR4, this.ATTR_ID_BUFFER,
                expectedSelectiveAccess);
    }

    private SelectiveAccessDescription createSelectiveAccessDescriptionDsmr4Daily(final DataObject from,
                                                                                  final DataObject to) {

        final DataObject selectedValues = DataObject.newArrayData(
                Arrays.asList(this.CLOCK, this.STATUS, this.ACTIVE_ENERGY_IMPORT_RATE_1,
                        this.ACTIVE_ENERGY_IMPORT_RATE_2, this.ACTIVE_ENERGY_EXPORT_RATE_1,
                        this.ACTIVE_ENERGY_EXPORT_RATE_2));

        final DataObject expectedAccessParam = DataObject.newStructureData(
                Arrays.asList(this.CLOCK, from, to, selectedValues));

        return new SelectiveAccessDescription(1, expectedAccessParam);
    }

    private AttributeAddress createAttributeAddressDsmr4Monthly(final DataObject from, final DataObject to) {
        final SelectiveAccessDescription expectedSelectiveAccess = this.createSelectiveAccessDescriptionDsmr4Monthly(
                from, to);
        return new AttributeAddress(this.CLASS_ID_PROFILE, this.OBIS_MONTHLY_DSMR4, this.ATTR_ID_BUFFER,
                expectedSelectiveAccess);
    }

    private SelectiveAccessDescription createSelectiveAccessDescriptionDsmr4Monthly(final DataObject from,
                                                                                    final DataObject to) {

        final DataObject selectedValues = DataObject.newArrayData(
                Arrays.asList(this.CLOCK, this.ACTIVE_ENERGY_IMPORT_RATE_1,
                        this.ACTIVE_ENERGY_IMPORT_RATE_2, this.ACTIVE_ENERGY_EXPORT_RATE_1,
                        this.ACTIVE_ENERGY_EXPORT_RATE_2));

        final DataObject expectedAccessParam = DataObject.newStructureData(
                Arrays.asList(this.CLOCK, from, to, selectedValues));

        return new SelectiveAccessDescription(1, expectedAccessParam);
    }

    private AttributeAddress createAttributeAddressDsmr4Interval(final DataObject from, final DataObject to) {
        final SelectiveAccessDescription expectedSelectiveAccess = this.createSelectiveAccessDescriptionDsmr4Interval(
                from, to);
        return new AttributeAddress(this.CLASS_ID_PROFILE, this.OBIS_INTERVAL_DSMR4, this.ATTR_ID_BUFFER,
                expectedSelectiveAccess);
    }

    private SelectiveAccessDescription createSelectiveAccessDescriptionDsmr4Interval(final DataObject from,
                                                                                     final DataObject to) {

        final DataObject selectedValues = DataObject.newArrayData(Collections.emptyList());

        final DataObject expectedAccessParam = DataObject.newStructureData(
                Arrays.asList(this.CLOCK, from, to, selectedValues));

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

        final DataObject expectedAccessParam = DataObject.newStructureData(
                Arrays.asList(this.CLOCK, from, to, selectedValues));

        return new SelectiveAccessDescription(1, expectedAccessParam);
    }
}

