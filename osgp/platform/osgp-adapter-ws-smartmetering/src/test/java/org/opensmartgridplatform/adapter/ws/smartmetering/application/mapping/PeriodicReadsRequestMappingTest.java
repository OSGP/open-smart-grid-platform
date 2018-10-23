/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;

public class PeriodicReadsRequestMappingTest {

    private XMLGregorianCalendar xmlCalendar;
    private MonitoringMapper monitoringMapper = new MonitoringMapper();
    private static final PeriodType PERIODTYPE = PeriodType.DAILY;

    /**
     * Needed to initialize a XMLGregorianCalendar object.
     */
    @Before
    public void init() {
        try {
            this.xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
        } catch (final DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests if a NullPointerException is thrown when a PeriodicReadsRequest -
     * with a PeriodicReadsRequestData that is null - is mapped.
     */
    @Test(expected = NullPointerException.class)
    public void testWithNullPeriodicReadsRequestData() {

        // build test data
        final PeriodicReadsRequest periodicReadsRequest = new PeriodicReadsRequest();
        periodicReadsRequest.setPeriodicReadsRequestData(null);

        // actual mapping
        this.monitoringMapper.map(periodicReadsRequest, PeriodicMeterReadsQuery.class);

    }

    /**
     * Tests if a PeriodicReadsRequest object is mapped successfully when it is
     * completely initialized.
     */
    @Test
    public void testCompletePeriodicReadsRequestMapping() {

        // build test data
        final PeriodicReadsRequestData periodicReadsRequestData = new PeriodicReadsRequestData();
        periodicReadsRequestData.setBeginDate(this.xmlCalendar);
        periodicReadsRequestData.setEndDate(this.xmlCalendar);
        periodicReadsRequestData.setPeriodType(PERIODTYPE);
        final PeriodicReadsRequest periodicReadsRequest = new PeriodicReadsRequest();
        periodicReadsRequest.setPeriodicReadsRequestData(periodicReadsRequestData);

        // actual mapping
        final PeriodicMeterReadsQuery periodicMeterReadsQuery = this.monitoringMapper.map(periodicReadsRequest,
                PeriodicMeterReadsQuery.class);

        // check mapping
        assertNotNull(periodicMeterReadsQuery);
        assertNotNull(periodicMeterReadsQuery.getDeviceIdentification());
        assertNotNull(periodicMeterReadsQuery.getPeriodType());
        assertNotNull(periodicMeterReadsQuery.getBeginDate());
        assertNotNull(periodicMeterReadsQuery.getEndDate());

        assertEquals(PERIODTYPE.name(), periodicMeterReadsQuery.getPeriodType().name());
        assertFalse(periodicMeterReadsQuery.isMbusDevice());
        assertTrue(periodicMeterReadsQuery.getDeviceIdentification().isEmpty());
        // For more information on the mapping of Date to XmlGregorianCalendar
        // objects, refer to the DateMappingTest

    }
}
