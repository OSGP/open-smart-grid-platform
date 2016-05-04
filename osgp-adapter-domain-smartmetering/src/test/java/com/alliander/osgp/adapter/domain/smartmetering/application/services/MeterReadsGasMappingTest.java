/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnitDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGasResponseDto;

public class MeterReadsGasMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();

    // Test the mapping of a complete MeterReadsGasDto object
    @Test
    public void testMeterReadsGasMappingTest() {
        // build test data
        final DlmsMeterValueDto consumption = new DlmsMeterValueDto(new BigDecimal(1.0), DlmsUnitDto.M3);
        final MeterReadsGasResponseDto meterReadsGasDto = new MeterReadsGasResponseDto(new Date(), consumption, new Date());
        // actual mapping
        final MeterReadsGas meterReadsGas = this.monitoringMapper.map(meterReadsGasDto, MeterReadsGas.class);
        // test mapping
        assertNotNull(meterReadsGas);
        assertEquals(meterReadsGasDto.getLogTime(), meterReadsGas.getLogTime());
        assertEquals(meterReadsGasDto.getCaptureTime(), meterReadsGas.getCaptureTime());

        final BigDecimal bigDecimal1 = consumption.getValue();
        final BigDecimal bigDecimal2 = meterReadsGas.getConsumption().getValue();
        assertTrue(bigDecimal1.compareTo(bigDecimal2) == 0);
        assertEquals(OsgpUnit.M3, meterReadsGas.getConsumption().getOsgpUnit());

    }

    // Test mapping when DlmsMeterValue is null;
    @Test
    public void testWithNullDlmsMeterValueDto() {
        // build test data
        final DlmsMeterValueDto consumption = null;
        final MeterReadsGasResponseDto meterReadsGasDto = new MeterReadsGasResponseDto(new Date(), consumption, new Date());
        // actual mapping
        final MeterReadsGas meterReadsGas = this.monitoringMapper.map(meterReadsGasDto, MeterReadsGas.class);
        // test mapping
        assertNotNull(meterReadsGas);
        assertEquals(meterReadsGasDto.getLogTime(), meterReadsGas.getLogTime());
        assertEquals(meterReadsGasDto.getCaptureTime(), meterReadsGas.getCaptureTime());
        assertNull(meterReadsGas.getConsumption());
    }

    // Dates can never be null, because of the way the constructor for a
    // MeterReadsGasDto is defined.
    @Test(expected = NullPointerException.class)
    public void testWithNullDates() {

        final DlmsMeterValueDto consumption = new DlmsMeterValueDto(new BigDecimal(1.0), DlmsUnitDto.M3);
        new MeterReadsGasResponseDto(null, consumption, null);

    }

}
