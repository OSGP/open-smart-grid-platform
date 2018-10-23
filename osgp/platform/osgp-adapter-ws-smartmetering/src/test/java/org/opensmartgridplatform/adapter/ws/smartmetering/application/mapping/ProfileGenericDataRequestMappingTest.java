/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.junit.Test;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataRequest;

public class ProfileGenericDataRequestMappingTest {

    private final MonitoringMapper mapper = new MonitoringMapper();

    private static final String DEVICE_NAME = "TEST10240000001";
    private static final Date DATE = new Date();

    @Test
    public void convertProfileGenericDataRequest() {
        final ProfileGenericDataRequest source = this.makeRequest();
        final Object result = this.mapper.map(source,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataRequest.class);
        assertNotNull("mapping ProfileGenericDataRequest should not return null", result);
        assertThat("mapping ProfileGenericDataRequest should return correct type", result,
                instanceOf(org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataRequest.class));
        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataRequest target = (org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataRequest) result;

        assertEquals(source.getDeviceIdentification(), target.getDeviceIdentification());
        assertEquals(source.getObisCode().getA(), target.getObisCode().getA());
        assertEquals(source.getObisCode().getF(), target.getObisCode().getF());
        final DateTime targetEndDate = new DateTime(target.getEndDate().getTime());
        assertEquals(source.getBeginDate().getYear(), targetEndDate.getYear());
    }

    private ProfileGenericDataRequest makeRequest() {
        final ProfileGenericDataRequest result = new ProfileGenericDataRequest();
        result.setObisCode(this.makeObisCodeValues());
        result.setDeviceIdentification(DEVICE_NAME);
        result.setBeginDate(this.makeGregorianCalendar());
        result.setEndDate(this.makeGregorianCalendar());
        return result;
    }

    private ObisCodeValues makeObisCodeValues() {
        final ObisCodeValues result = new ObisCodeValues();
        result.setA((short) 1);
        result.setB((short) 1);
        result.setC((short) 1);
        result.setD((short) 1);
        result.setE((short) 1);
        result.setF((short) 1);
        return result;
    }

    private XMLGregorianCalendar makeGregorianCalendar() {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(DATE);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("error creating XMLGregorianCalendar");
        }
    }
}
