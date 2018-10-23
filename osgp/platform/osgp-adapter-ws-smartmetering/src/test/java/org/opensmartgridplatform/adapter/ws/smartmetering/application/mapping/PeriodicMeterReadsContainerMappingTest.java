/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReads;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainer;

public class PeriodicMeterReadsContainerMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();
    private static final PeriodType PERIODTYPE = PeriodType.DAILY;
    private static final Date DATE = new Date();
    private static final BigDecimal VALUE = new BigDecimal(1.0);
    private static final OsgpUnit OSGP_UNIT = OsgpUnit.M3;
    private static final OsgpUnitType OSGP_UNITTYPE = OsgpUnitType.M_3;
    private static final AmrProfileStatusCodeFlag AMRCODEFLAG = AmrProfileStatusCodeFlag.CLOCK_INVALID;

    /**
     * Tests the mapping of a PeriodMeterReadsContainer object with an empty
     * List.
     */
    @Test
    public void testWithEmptyList() {

        // build test data
        final List<PeriodicMeterReads> periodicMeterReadsList = new ArrayList<>();
        final PeriodicMeterReadsContainer periodicMeterReadsContainer = new PeriodicMeterReadsContainer(PERIODTYPE,
                periodicMeterReadsList);

        // actual mapping
        final PeriodicMeterReadsResponse periodicMeterReadsResponse = this.monitoringMapper.map(
                periodicMeterReadsContainer, PeriodicMeterReadsResponse.class);

        // check mapping
        assertNotNull(periodicMeterReadsResponse);
        assertTrue(periodicMeterReadsResponse.getPeriodicMeterReads().isEmpty());
        assertEquals(PERIODTYPE.name(), periodicMeterReadsResponse.getPeriodType().name());
    }

    /**
     * Tests the mapping of a PeriodicMeterReadsContainer object with a filled
     * List and Set.
     */
    @Test
    public void testMappingWithFilledListAndSet() {

        // build test data
        final OsgpMeterValue osgpMeterValue = new OsgpMeterValue(VALUE, OSGP_UNIT);
        final Set<AmrProfileStatusCodeFlag> flagSet = new TreeSet<>();
        flagSet.add(AMRCODEFLAG);
        final AmrProfileStatusCode amrProfileStatusCode = new AmrProfileStatusCode(flagSet);

        final PeriodicMeterReads periodicMeterReads = new PeriodicMeterReads(DATE, osgpMeterValue, osgpMeterValue,
                amrProfileStatusCode);
        final List<PeriodicMeterReads> periodicMeterReadsList = new ArrayList<>();
        periodicMeterReadsList.add(periodicMeterReads);
        final PeriodicMeterReadsContainer periodicMeterReadsContainer = new PeriodicMeterReadsContainer(PERIODTYPE,
                periodicMeterReadsList);

        // actual mapping
        final PeriodicMeterReadsResponse periodicMeterReadsResponse = this.monitoringMapper.map(
                periodicMeterReadsContainer, PeriodicMeterReadsResponse.class);

        // check mapping
        assertNotNull(periodicMeterReadsResponse);
        assertNotNull(periodicMeterReadsResponse.getPeriodicMeterReads());
        assertNotNull(periodicMeterReadsResponse.getPeriodType());
        assertNotNull(periodicMeterReadsResponse.getPeriodicMeterReads().get(0));
        assertNotNull(periodicMeterReadsResponse.getPeriodicMeterReads().get(0).getActiveEnergyExport());
        assertNotNull(periodicMeterReadsResponse.getPeriodicMeterReads().get(0).getActiveEnergyImport());
        assertNotNull(periodicMeterReadsResponse.getPeriodicMeterReads().get(0).getAmrProfileStatusCode());

        assertEquals(PERIODTYPE.name(), periodicMeterReadsResponse.getPeriodType().name());
        assertEquals(periodicMeterReadsList.size(), periodicMeterReadsResponse.getPeriodicMeterReads().size());
        assertEquals(VALUE, periodicMeterReadsResponse.getPeriodicMeterReads().get(0).getActiveEnergyImport()
                .getValue());
        assertEquals(OSGP_UNITTYPE.name(), periodicMeterReadsResponse.getPeriodicMeterReads().get(0)
                .getActiveEnergyImport().getUnit().name());
        assertEquals(VALUE, periodicMeterReadsResponse.getPeriodicMeterReads().get(0).getActiveEnergyExport()
                .getValue());
        assertEquals(OSGP_UNITTYPE.name(), periodicMeterReadsResponse.getPeriodicMeterReads().get(0)
                .getActiveEnergyExport().getUnit().name());
        assertEquals(AMRCODEFLAG.name(), periodicMeterReadsResponse.getPeriodicMeterReads().get(0)
                .getAmrProfileStatusCode().getAmrProfileStatusCodeFlag().get(0).name());
        // For more information on the mapping of Date to XmlGregorianCalendar
        // objects, refer to the DateMappingTest
    }
}
