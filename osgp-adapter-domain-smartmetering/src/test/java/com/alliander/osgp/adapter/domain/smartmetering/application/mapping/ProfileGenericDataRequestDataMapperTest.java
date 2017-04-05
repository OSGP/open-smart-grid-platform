/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataRequestData;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataRequestDataDto;

public class ProfileGenericDataRequestDataMapperTest {

    private MonitoringMapper mapper = new MonitoringMapper();

    private static final ObisCodeValues OBIS_CODE_VALUES = new ObisCodeValues((byte) 1, (byte) 2, (byte) 3, (byte) 4,
            (byte) 5, (byte) 6);
    private static final DateTime BEGIN_DATE = new DateTime(2017, 1, 1, 0, 0, 0, DateTimeZone.UTC);
    private static final DateTime END_DATE = new DateTime(2017, 2, 1, 0, 0, 0, DateTimeZone.UTC);

    @Test
    public void shouldConvertValueObjectToDto() {
        final ProfileGenericDataRequestData source = new ProfileGenericDataRequestData(OBIS_CODE_VALUES,
                BEGIN_DATE.toDate(), END_DATE.toDate());
        final ProfileGenericDataRequestDataDto result = this.mapper.map(source, ProfileGenericDataRequestDataDto.class);

        assertEquals("mapped values should be identical", result.getBeginDate(), BEGIN_DATE.toDate());
        assertEquals("mapped values should be identical", result.getEndDate(), END_DATE.toDate());
        assertEquals("mapped values should be identical", result.getObisCode().getA(), OBIS_CODE_VALUES.getA());
        assertEquals("mapped values should be identical", result.getObisCode().getB(), OBIS_CODE_VALUES.getB());
        assertEquals("mapped values should be identical", result.getObisCode().getC(), OBIS_CODE_VALUES.getC());
        assertEquals("mapped values should be identical", result.getObisCode().getD(), OBIS_CODE_VALUES.getD());
        assertEquals("mapped values should be identical", result.getObisCode().getE(), OBIS_CODE_VALUES.getE());
        assertEquals("mapped values should be identical", result.getObisCode().getF(), OBIS_CODE_VALUES.getF());
    }

}
