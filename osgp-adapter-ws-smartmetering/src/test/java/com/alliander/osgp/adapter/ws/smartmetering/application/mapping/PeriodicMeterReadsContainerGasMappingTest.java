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
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas;

public class PeriodicMeterReadsContainerGasMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // classmap is needed because of different variable names
    @Before
    public void init() {
        this.mapperFactory.classMap(PeriodicMeterReadsContainerGas.class, PeriodicMeterReadsGasResponse.class)
                .field("meterReadsGas", "periodicMeterReadsGas").byDefault().register();
        this.mapperFactory
                .classMap(com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusCode.class,
                        AmrProfileStatusCode.class).field("amrProfileStatusCodeFlag", "amrProfileStatusCodeFlags")
                .byDefault().register();
        // Converter is needed because of difference in names for values in
        // enums OsgpUnitType and OsgpUnit
        this.mapperFactory.getConverterFactory().registerConverter(new MeterValueConverter());
    }

    // Test to see if mapping succeeds with an empty List.
    @Test
    public void testWithEmptyList() {
        // build test data
        final PeriodType periodType = PeriodType.DAILY;
        final List<PeriodicMeterReadsGas> periodicMeterReadsGasList = new ArrayList<>();
        final PeriodicMeterReadsContainerGas periodicMeterReadsContainerGas = new PeriodicMeterReadsContainerGas(
                periodType, periodicMeterReadsGasList);

        // actual mapping
        final PeriodicMeterReadsGasResponse periodicMeterReadsGasResponse = this.mapperFactory.getMapperFacade().map(
                periodicMeterReadsContainerGas, PeriodicMeterReadsGasResponse.class);

        // check mapping
        assertNotNull(periodicMeterReadsGasResponse);
        assertTrue(periodicMeterReadsGasResponse.getPeriodicMeterReadsGas().isEmpty());
        assertEquals(periodType.name(), periodicMeterReadsGasResponse.getPeriodType().name());
    }

    // Test to see if mapping succeeds with a filled List and Set.
    @Test
    public void testWithFilledList() {

        // build test data
        final Date date = new Date();
        final OsgpMeterValue osgpMeterValue = new OsgpMeterValue(new BigDecimal(1.0), OsgpUnit.M3);
        final Set<AmrProfileStatusCodeFlag> flagSet = new TreeSet<>();
        flagSet.add(AmrProfileStatusCodeFlag.CLOCK_INVALID);
        final AmrProfileStatusCode amrProfileStatusCode = new AmrProfileStatusCode(flagSet);

        final PeriodType periodType = PeriodType.DAILY;
        final PeriodicMeterReadsGas periodicMeterReadsGas = new PeriodicMeterReadsGas(date, osgpMeterValue, date,
                amrProfileStatusCode);
        final List<PeriodicMeterReadsGas> periodicMeterReadsList = new ArrayList<>();
        periodicMeterReadsList.add(periodicMeterReadsGas);
        final PeriodicMeterReadsContainerGas periodicMeterReadsContainer = new PeriodicMeterReadsContainerGas(
                periodType, periodicMeterReadsList);

        // actual mapping
        final PeriodicMeterReadsGasResponse periodicMeterReadsResponseGas = this.mapperFactory.getMapperFacade().map(
                periodicMeterReadsContainer, PeriodicMeterReadsGasResponse.class);

        // check mapping
        assertNotNull(periodicMeterReadsResponseGas);
        assertEquals(periodType.name(), periodicMeterReadsResponseGas.getPeriodType().name());
        assertEquals(periodicMeterReadsList.size(), periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().size());
        assertEquals(periodicMeterReadsGas.getConsumption().getValue(), periodicMeterReadsResponseGas
                .getPeriodicMeterReadsGas().get(0).getConsumption().getValue());
        assertEquals(OsgpUnitType.M_3, periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0).getConsumption()
                .getUnit());

        this.checkDateMapping(date, periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0).getCaptureTime());
        this.checkDateMapping(date, periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0).getLogTime());

        assertEquals(AmrProfileStatusCodeFlag.CLOCK_INVALID.name(), periodicMeterReadsResponseGas
                .getPeriodicMeterReadsGas().get(0).getAmrProfileStatusCode().getAmrProfileStatusCodeFlag().get(0)
                .name());
    }

    // method to check the mapping of Date objects
    private void checkDateMapping(final Date date, final XMLGregorianCalendar calendar) {

        // convert Date to a DateTime to enable comparison (Date has deprecated
        // method and test fails if these are used).
        final DateTime dateTime = new DateTime(date);

        assertEquals(dateTime.getYear(), calendar.getYear());
        assertEquals(dateTime.getMonthOfYear(), calendar.getMonth());
        assertEquals(dateTime.getDayOfMonth(), calendar.getDay());
        assertEquals(dateTime.getHourOfDay(), calendar.getHour());
        assertEquals(dateTime.getMinuteOfHour(), calendar.getMinute());
        assertEquals(dateTime.getSecondOfMinute(), calendar.getSecond());
        assertEquals(dateTime.getMillisOfSecond(), calendar.getMillisecond());
    }

}
