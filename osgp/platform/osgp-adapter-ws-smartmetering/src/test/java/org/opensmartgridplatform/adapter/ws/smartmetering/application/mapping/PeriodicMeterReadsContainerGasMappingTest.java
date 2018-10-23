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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas;

public class PeriodicMeterReadsContainerGasMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();
    private static final PeriodType PERIODTYPE = PeriodType.DAILY;
    private static final Date DATE = new Date();
    private static final BigDecimal VALUE = new BigDecimal(1.0);
    private static final OsgpUnit OSGP_UNIT = OsgpUnit.M3;
    private static final OsgpUnitType OSGP_UNITTYPE = OsgpUnitType.M_3;
    private static final AmrProfileStatusCodeFlag AMRCODEFLAG = AmrProfileStatusCodeFlag.CLOCK_INVALID;

    /**
     * Tests if mapping a PeriodicMeterReadsContainerGas object with an empty
     * list succeeds.
     */
    @Test
    public void testWithEmptyList() {

        // build test data
        final List<PeriodicMeterReadsGas> periodicMeterReadsGasList = new ArrayList<>();
        final PeriodicMeterReadsContainerGas periodicMeterReadsContainerGas = new PeriodicMeterReadsContainerGas(
                PERIODTYPE, periodicMeterReadsGasList);

        // actual mapping
        final PeriodicMeterReadsGasResponse periodicMeterReadsGasResponse = this.monitoringMapper.map(
                periodicMeterReadsContainerGas, PeriodicMeterReadsGasResponse.class);

        // check mapping
        assertNotNull(periodicMeterReadsGasResponse);
        assertNotNull(periodicMeterReadsGasResponse.getPeriodicMeterReadsGas());
        assertNotNull(periodicMeterReadsGasResponse.getPeriodType());

        assertTrue(periodicMeterReadsGasResponse.getPeriodicMeterReadsGas().isEmpty());
        assertEquals(PERIODTYPE.name(), periodicMeterReadsGasResponse.getPeriodType().name());
    }

    /**
     * Tests if mapping a PeriodicMeterReadsContainerGas object with a filled
     * List and Set succeeds.
     */
    @Test
    public void testWithFilledList() {

        // build test data
        final OsgpMeterValue osgpMeterValue = new OsgpMeterValue(VALUE, OSGP_UNIT);
        final Set<AmrProfileStatusCodeFlag> flagSet = new TreeSet<>();
        flagSet.add(AMRCODEFLAG);
        final AmrProfileStatusCode amrProfileStatusCode = new AmrProfileStatusCode(flagSet);

        final PeriodicMeterReadsGas periodicMeterReadsGas = new PeriodicMeterReadsGas(DATE, osgpMeterValue, DATE,
                amrProfileStatusCode);
        final List<PeriodicMeterReadsGas> periodicMeterReadsList = new ArrayList<>();
        periodicMeterReadsList.add(periodicMeterReadsGas);
        final PeriodicMeterReadsContainerGas periodicMeterReadsContainer = new PeriodicMeterReadsContainerGas(
                PERIODTYPE, periodicMeterReadsList);

        // actual mapping
        final PeriodicMeterReadsGasResponse periodicMeterReadsResponseGas = this.monitoringMapper.map(
                periodicMeterReadsContainer, PeriodicMeterReadsGasResponse.class);

        // check mapping

        assertNotNull(periodicMeterReadsResponseGas);
        assertNotNull(periodicMeterReadsResponseGas.getPeriodicMeterReadsGas());
        assertNotNull(periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0));
        assertNotNull(periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0).getAmrProfileStatusCode());
        assertNotNull(periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0).getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlag());
        assertNotNull(periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0).getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlag().get(0));
        assertNotNull(periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0).getConsumption());
        assertNotNull(periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0).getConsumption().getUnit());
        assertNotNull(periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0).getConsumption().getValue());

        assertEquals(PERIODTYPE.name(), periodicMeterReadsResponseGas.getPeriodType().name());
        assertEquals(periodicMeterReadsList.size(), periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().size());
        assertEquals(VALUE, periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0).getConsumption().getValue());
        assertEquals(OSGP_UNITTYPE, periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0).getConsumption()
                .getUnit());
        assertEquals(AMRCODEFLAG.name(), periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0)
                .getAmrProfileStatusCode().getAmrProfileStatusCodeFlag().get(0).name());
        // For more information on the mapping of Date to XmlGregorianCalendar
        // objects, refer to the DateMappingTest
    }
}
