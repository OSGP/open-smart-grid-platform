/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileGenericDataRequestDataDto;

public class ProfileGenericDataRequestDataMapperTest {

    private static final String MAPPED_FIELD_VALUE_MESSAGE = "Mapped field should have the same value.";

    private static final DateTime BEGIN_DATE = new DateTime(2017, 1, 1, 0, 0, 0, DateTimeZone.UTC);
    private static final DateTime END_DATE = new DateTime(2017, 2, 1, 0, 0, 0, DateTimeZone.UTC);
    private static final ObisCodeValues OBIS_CODE_VALUES = new ObisCodeValues((byte) 1, (byte) 2, (byte) 3, (byte) 4,
            (byte) 5, (byte) 6);

    private MonitoringMapper mapper = new MonitoringMapper();

    @Test
    public void shouldConvertValueObjectToDto() {
        final ProfileGenericDataRequestData source = new ProfileGenericDataRequestData(OBIS_CODE_VALUES,
                BEGIN_DATE.toDate(), END_DATE.toDate());
        final ProfileGenericDataRequestDataDto result = this.mapper.map(source, ProfileGenericDataRequestDataDto.class);

        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, result.getBeginDate(), BEGIN_DATE.toDate());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, result.getEndDate(), END_DATE.toDate());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, result.getObisCode().getA(), OBIS_CODE_VALUES.getA());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, result.getObisCode().getB(), OBIS_CODE_VALUES.getB());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, result.getObisCode().getC(), OBIS_CODE_VALUES.getC());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, result.getObisCode().getD(), OBIS_CODE_VALUES.getD());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, result.getObisCode().getE(), OBIS_CODE_VALUES.getE());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, result.getObisCode().getF(), OBIS_CODE_VALUES.getF());
    }

}
