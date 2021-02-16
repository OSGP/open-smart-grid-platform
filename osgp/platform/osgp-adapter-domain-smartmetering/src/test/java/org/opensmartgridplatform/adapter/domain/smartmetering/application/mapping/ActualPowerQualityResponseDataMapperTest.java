/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualValue;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDto;

public class ActualPowerQualityResponseDataMapperTest {

    private final MonitoringMapper mapper = new MonitoringMapper();

    private final static Class<?>[] EXPECTED_CLASS = new Class<?>[] { String.class, Date.class, BigDecimal.class,
            Long.class };

    @Test
    public void testConvertActualPowerQualityResponse() {
        final ActualPowerQualityDataDto responseDto = new ActualPowerQualityDataDto(new ArrayList<CaptureObjectDto>(),
                this.makeActualValueDtos());
        final ActualPowerQualityData response = this.mapper
                .map(responseDto, ActualPowerQualityData.class);
        assertThat(response).isNotNull();

        assertThat(response.getActualValues().size())
                .withFailMessage("response object should return same number of profilentries")
                .isEqualTo(EXPECTED_CLASS.length);
        int i = 0;
        for (final ActualValue actualValue : response.getActualValues()) {
            final Class<?> clazz = actualValue.getValue().getClass();
            assertThat(clazz).withFailMessage("the return class should be of the same type")
                             .isEqualTo(EXPECTED_CLASS[i++]);
        }
    }

    private List<ActualValueDto> makeActualValueDtos() {
        final List<ActualValueDto> result = new ArrayList<>();
        result.add(new ActualValueDto("Test"));
        result.add(new ActualValueDto(new Date()));
        result.add(new ActualValueDto(new BigDecimal(123.45d)));
        result.add(new ActualValueDto(12345L));
        return result;
    }
}
