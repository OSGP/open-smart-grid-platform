/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;

@RunWith(MockitoJUnitRunner.class)
public class AttributeAddressHelperServiceTest {
    private static final AttributeAddress CLOCK = new AttributeAddress(8, new ObisCode(0, 0, 1, 0, 0, 255), 2);
    private static final AttributeAddress AMR_PROFILE_STATUS = new AttributeAddress(1, new ObisCode(0, 0, 96, 10, 2, 255), 2);
    private static final AttributeAddress ACTIVE_ENERGY_IMPORT_RATE_1 = new AttributeAddress(3, new ObisCode(1, 0, 1, 8, 1, 255), 2);
    private static final AttributeAddress ACTIVE_ENERGY_IMPORT_RATE_2 = new AttributeAddress(3, new ObisCode(1, 0, 1, 8, 2, 255), 2);
    private static final AttributeAddress ACTIVE_ENERGY_EXPORT_RATE_1 = new AttributeAddress(3, new ObisCode(1, 0, 2, 8, 1, 255), 2);
    private static final AttributeAddress ACTIVE_ENERGY_EXPORT_RATE_2 = new AttributeAddress(3, new ObisCode(1, 0, 2, 8, 2, 255), 2);

    private GetPeriodicMeterReadsCommandExecutor executor;

    @Mock
    private DlmsMessageListener listener;

    @Mock
    private DlmsHelperService helperService;

    @Mock
    private AttributeAddressHelperService service;

    @Before
    public void setUp() {
        this.service = new AttributeAddressHelperService(this.helperService);
    }

    @Test
    public void testSelectedValuesForPeriodicMeterReads() throws Exception {
        final List<AttributeAddress> expectedAttributeAddressesDaily = Arrays.asList(
                CLOCK, AMR_PROFILE_STATUS,
                ACTIVE_ENERGY_IMPORT_RATE_1, ACTIVE_ENERGY_IMPORT_RATE_2,
                ACTIVE_ENERGY_EXPORT_RATE_1, ACTIVE_ENERGY_EXPORT_RATE_2);

        final List<AttributeAddress> expectedAttributeAddressesMonthly = Arrays.asList(
                CLOCK,
                ACTIVE_ENERGY_IMPORT_RATE_1, ACTIVE_ENERGY_IMPORT_RATE_2,
                ACTIVE_ENERGY_EXPORT_RATE_1, ACTIVE_ENERGY_EXPORT_RATE_2);

        List<DataObject> selectedValues;

        selectedValues = this.getAttributeAddresses(PeriodTypeDto.INTERVAL, true);
        Assertions.assertThat(selectedValues.size()).isEqualTo(0);

        selectedValues = this.getAttributeAddresses(PeriodTypeDto.DAILY, true);
        this.assertListContainsObisCodes(selectedValues, expectedAttributeAddressesDaily);

        selectedValues = this.getAttributeAddresses(PeriodTypeDto.MONTHLY, true);
        this.assertListContainsObisCodes(selectedValues, expectedAttributeAddressesMonthly);

        selectedValues = this.getAttributeAddresses(PeriodTypeDto.INTERVAL, false);
        Assertions.assertThat(selectedValues.size()).isEqualTo(0);

        selectedValues = this.getAttributeAddresses(PeriodTypeDto.DAILY, false);
        Assertions.assertThat(selectedValues.size()).isEqualTo(0);

        selectedValues = this.getAttributeAddresses(PeriodTypeDto.MONTHLY, false);
        Assertions.assertThat(selectedValues.size()).isEqualTo(0);
    }

    private List<DataObject> getAttributeAddresses(final PeriodTypeDto periodType,
            final boolean isSelectingValuesSupported) throws Exception {

        // Setup mocks
        when(this.helperService.getAMRProfileDefinition()).thenReturn(DataObjectDefinitions.getAMRProfileDefinition());
        when(this.helperService.getClockDefinition()).thenReturn(DataObjectDefinitions.getClockDefinition());

        // Setup captor to capture the selected values
        final ArgumentCaptor<DataObject> dataObjectSelectedValuesCaptor = ArgumentCaptor.forClass(DataObject.class);
        when(this.helperService.getAccessSelectionTimeRangeParameter(any(), any(), dataObjectSelectedValuesCaptor.capture())).thenReturn(DataObject.newNullData());

        final AttributeAddress[] attributeAddresses =
                this.service.getProfileBufferAndScalerUnitForPeriodicMeterReads(periodType, DateTime.now(), DateTime.now(),
                isSelectingValuesSupported);

        // Return the list with selected values
        return dataObjectSelectedValuesCaptor.getValue().getValue();
    }

    private void assertListContainsObisCodes(final List<DataObject> dataObjects, final List<AttributeAddress> attributeAddresses) {
        Assertions.assertThat(dataObjects.size()).isEqualTo(attributeAddresses.size());

        for (final AttributeAddress attributeAddress : attributeAddresses) {
            this.assertListContainsObisCode(dataObjects, attributeAddress.getClassId(), attributeAddress.getInstanceId(),
                    attributeAddress.getId());
        }
    }

    private void assertListContainsObisCode(final List<DataObject> dataObjects, final int classId, final ObisCode obisCode,
            final int id) {
        boolean found = false;

        for (final DataObject dataObject : dataObjects) {
            final List<DataObject> dataObjectValues = dataObject.getValue();
            final int dataObjectClassId = dataObjectValues.get(0).getValue();
            final byte[] dataObjectObisCode = dataObjectValues.get(1).getValue();
            final int dataObjectId = ((Byte)dataObjectValues.get(2).getValue()).intValue();

            if (classId == dataObjectClassId && Arrays.equals(obisCode.bytes(), dataObjectObisCode) && id == dataObjectId) {
                found = true;
            }
        }

        Assertions.assertThat(found).isEqualTo(true);
    }
}

