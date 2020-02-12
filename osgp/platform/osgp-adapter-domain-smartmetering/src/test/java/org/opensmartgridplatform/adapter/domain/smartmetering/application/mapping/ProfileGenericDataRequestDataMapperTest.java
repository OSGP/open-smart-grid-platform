/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ProfileGenericDataRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileGenericDataRequestDataDto;

public class ProfileGenericDataRequestDataMapperTest {

    private static final String MAPPED_FIELD_VALUE_MESSAGE = "Mapped field should have the same value.";

    private static final DateTime BEGIN_DATE = new DateTime(2017, 1, 1, 0, 0, 0, DateTimeZone.UTC);
    private static final DateTime END_DATE = new DateTime(2017, 2, 1, 0, 0, 0, DateTimeZone.UTC);
    private static final ObisCodeValues OBIS_CODE_VALUES = new ObisCodeValues((byte) 1, (byte) 2, (byte) 3, (byte) 4,
            (byte) 5, (byte) 6);

    private final MonitoringMapper mapper = new MonitoringMapper();

    @Test
    public void shouldConvertValueObjectToDto() {
        final ProfileGenericDataRequestData source = new ProfileGenericDataRequestData(OBIS_CODE_VALUES,
                BEGIN_DATE.toDate(), END_DATE.toDate());
        final ProfileGenericDataRequestDataDto result = this.mapper.map(source, ProfileGenericDataRequestDataDto.class);

        assertThat(result.getBeginDate()).withFailMessage(MAPPED_FIELD_VALUE_MESSAGE).isEqualTo(BEGIN_DATE.toDate());
        assertThat(result.getEndDate()).withFailMessage(MAPPED_FIELD_VALUE_MESSAGE).isEqualTo(END_DATE.toDate());
        assertThat(result.getObisCode().getA()).withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo(OBIS_CODE_VALUES.getA());
        assertThat(result.getObisCode().getB()).withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo(OBIS_CODE_VALUES.getB());
        assertThat(result.getObisCode().getC()).withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo(OBIS_CODE_VALUES.getC());
        assertThat(result.getObisCode().getD()).withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo(OBIS_CODE_VALUES.getD());
        assertThat(result.getObisCode().getE()).withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo(OBIS_CODE_VALUES.getE());
        assertThat(result.getObisCode().getF()).withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
                .isEqualTo(OBIS_CODE_VALUES.getF());
    }

}
