/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;

public class MeterReadsGasMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();
    private static final Date DATE = new Date();
    private static final BigDecimal VALUE = new BigDecimal(1.0);
    private static final OsgpUnit OSGP_UNIT = OsgpUnit.M3;
    private static final OsgpUnitType OSGP_UNITTYPE = OsgpUnitType.M_3;

    /** Tests if a MeterReadsGas object can be mapped */
    @Test
    public void testMeterReadsGasMapping() {

        // build test data
        final OsgpMeterValue osgpMeterValue = new OsgpMeterValue(VALUE, OSGP_UNIT);
        final MeterReadsGas meterReadsGas = new MeterReadsGas(DATE, osgpMeterValue, DATE);

        // actual mapping
        final ActualMeterReadsGasResponse actualMeterReadsGasResponse = this.monitoringMapper.map(meterReadsGas,
                ActualMeterReadsGasResponse.class);

        // check mapping
        assertNotNull(actualMeterReadsGasResponse);
        assertNotNull(actualMeterReadsGasResponse.getConsumption());
        assertNotNull(actualMeterReadsGasResponse.getConsumption().getUnit());
        assertNotNull(actualMeterReadsGasResponse.getConsumption().getValue());

        this.checkDateMapping(actualMeterReadsGasResponse.getCaptureTime());
        this.checkDateMapping(actualMeterReadsGasResponse.getLogTime());
        assertEquals(OSGP_UNITTYPE, actualMeterReadsGasResponse.getConsumption().getUnit());
        assertEquals(VALUE, actualMeterReadsGasResponse.getConsumption().getValue());
    }

    /** Method to check the mapping of Date objects */
    private void checkDateMapping(final XMLGregorianCalendar calendar) {

        assertNotNull(calendar);

        // convert Date to a DateTime to enable comparison (Date has deprecated
        // method and test fails if these are used).
        final DateTime dateTime = new DateTime(DATE);

        assertEquals(dateTime.getYear(), calendar.getYear());
        assertEquals(dateTime.getMonthOfYear(), calendar.getMonth());
        assertEquals(dateTime.getDayOfMonth(), calendar.getDay());
        assertEquals(dateTime.getHourOfDay(), calendar.getHour());
        assertEquals(dateTime.getMinuteOfHour(), calendar.getMinute());
        assertEquals(dateTime.getSecondOfMinute(), calendar.getSecond());
        assertEquals(dateTime.getMillisOfSecond(), calendar.getMillisecond());
    }
}
