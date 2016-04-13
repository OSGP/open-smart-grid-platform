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

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.MeterValue;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpUnit;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainer;

public class PeriodicMeterReadsContainerMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // The registering below is needed because the same variables have different
    // names in these classes.
    @Before
    public void init() {
        this.mapperFactory.classMap(MeterValue.class, OsgpMeterValue.class).field("unit", "osgpUnit").byDefault()
        .register();
        this.mapperFactory
        .classMap(AmrProfileStatusCode.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusCode.class)
                .field("amrProfileStatusCodeFlags", "amrProfileStatusCodeFlag").byDefault().register();
    }

    // Test to check mapping when the List is empty.
    @Test
    public void testWithEmptyList() {

        // build test data
        final PeriodType periodType = PeriodType.DAILY;
        final List<PeriodicMeterReads> periodicMeterReadsList = new ArrayList<>();
        final PeriodicMeterReadsContainer periodicMeterReadsContainer = new PeriodicMeterReadsContainer(periodType,
                periodicMeterReadsList);

        // actual mapping
        final PeriodicMeterReadsResponse periodicMeterReadsResponse = this.mapperFactory.getMapperFacade().map(
                periodicMeterReadsContainer, PeriodicMeterReadsResponse.class);

        // check mapping
        assertNotNull(periodicMeterReadsResponse);
        assertTrue(periodicMeterReadsResponse.getPeriodicMeterReads().isEmpty());
        assertEquals(PeriodType.DAILY.name(), periodicMeterReadsResponse.getPeriodType().name());
    }

    // Test to check mapping when the List (and Set) are filled.
    @Test
    public void testMappingWithFilledListAndSet() {

        // build test data
        final Date date = new Date();
        final OsgpMeterValue osgpMeterValue = new OsgpMeterValue(new BigDecimal(1.0), OsgpUnit.KWH);
        final Set<AmrProfileStatusCodeFlag> flagSet = new TreeSet<>();
        flagSet.add(AmrProfileStatusCodeFlag.CLOCK_INVALID);
        final AmrProfileStatusCode amrProfileStatusCode = new AmrProfileStatusCode(flagSet);

        final PeriodType periodType = PeriodType.DAILY;
        final PeriodicMeterReads periodicMeterReads = new PeriodicMeterReads(date, osgpMeterValue, osgpMeterValue,
                amrProfileStatusCode);
        final List<PeriodicMeterReads> periodicMeterReadsList = new ArrayList<>();
        periodicMeterReadsList.add(periodicMeterReads);
        final PeriodicMeterReadsContainer periodicMeterReadsContainer = new PeriodicMeterReadsContainer(periodType,
                periodicMeterReadsList);

        // actual mapping
        final PeriodicMeterReadsResponse periodicMeterReadsResponse = this.mapperFactory.getMapperFacade().map(
                periodicMeterReadsContainer, PeriodicMeterReadsResponse.class);

        // check mapping
        assertNotNull(periodicMeterReadsResponse);
        assertEquals(periodType.name(), periodicMeterReadsResponse.getPeriodType().name());
        assertEquals(periodicMeterReadsList.size(), periodicMeterReadsResponse.getPeriodicMeterReads().size());
        assertEquals(periodicMeterReads.getActiveEnergyImport().getValue(), periodicMeterReadsResponse
                .getPeriodicMeterReads().get(0).getActiveEnergyImport().getValue());
        // NullPointerException?? value isn't mapped bydefault?
        assertEquals(periodicMeterReads.getActiveEnergyImport().getOsgpUnit().name(), periodicMeterReadsResponse
                .getPeriodicMeterReads().get(0).getActiveEnergyImport().getUnit().name());
        assertEquals(periodicMeterReads.getActiveEnergyExport().getValue(), periodicMeterReadsResponse
                .getPeriodicMeterReads().get(0).getActiveEnergyExport().getValue());
        assertEquals(periodicMeterReads.getActiveEnergyExport().getOsgpUnit().name(), periodicMeterReadsResponse
                .getPeriodicMeterReads().get(0).getActiveEnergyExport().getUnit().name());
        assertEquals(AmrProfileStatusCodeFlag.CLOCK_INVALID.name(), periodicMeterReadsResponse.getPeriodicMeterReads()
                .get(0).getAmrProfileStatusCode().getAmrProfileStatusCodeFlag().get(0).name());
    }
}
