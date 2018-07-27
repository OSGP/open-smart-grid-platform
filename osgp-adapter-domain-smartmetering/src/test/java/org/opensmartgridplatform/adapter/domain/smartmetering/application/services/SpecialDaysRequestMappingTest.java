/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDate;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDay;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDayDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDaysRequestDto;

// Tests the mapping of SpecialDaysRequest objects in ConfigurationService.
public class SpecialDaysRequestMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();

    // To check mapping of SpecialDaysRequest, when its SpecialDaysRequestData
    // is null
    @Test
    public void testSpecialDaysRequestMappingNull() {
        final String deviceIdentification = "nr1";
        final SpecialDaysRequestData specialDaysRequestData = null;
        final SpecialDaysRequest specialDaysRequestValueObject = 
                new SpecialDaysRequest(deviceIdentification,
                specialDaysRequestData);

        final SpecialDaysRequestDto specialDaysRequestDto = this.configurationMapper.map(specialDaysRequestValueObject,
                SpecialDaysRequestDto.class);

        assertNotNull(specialDaysRequestDto);
        assertEquals(deviceIdentification, specialDaysRequestDto.getDeviceIdentification());
        assertNull(specialDaysRequestDto.getSpecialDaysRequestData());

    }

    // To check mapping of SpecialDaysRequest, when its SpecialDaysRequestData
    // has an empty List.
    @Test
    public void testSpecialDaysRequestMappingEmptyList() {
        final String deviceIdentification = "nr1";
        final SpecialDaysRequestData specialDaysRequestData = new SpecialDaysRequestDataBuilder().build();
        final SpecialDaysRequest specialDaysRequestValueObject = 
                new SpecialDaysRequest(deviceIdentification,
                specialDaysRequestData);

        final SpecialDaysRequestDto specialDaysRequestDto = this.configurationMapper.map(specialDaysRequestValueObject,
                SpecialDaysRequestDto.class);

        assertEquals(deviceIdentification, specialDaysRequestDto.getDeviceIdentification());
        assertNotNull(specialDaysRequestDto.getSpecialDaysRequestData());
    }

    // To check mapping of SpecialDaysRequest, when its SpecialDaysRequestData
    // has a filled List (1 value).
    @Test
    public void testSpecialDaysRequestMappingNonEmptyList() {
        final String deviceIdentification = "nr1";
        final int year = 2016;
        final int month = 3;
        final int dayOfMonth = 11;
        final int dayId = 1;
        final SpecialDay specialDay = new SpecialDay(new CosemDate(year, month, dayOfMonth), dayId);
        final SpecialDaysRequestData specialDaysRequestData = new SpecialDaysRequestDataBuilder().addSpecialDay(
                specialDay).build();
        final SpecialDaysRequest specialDaysRequestValueObject = 
                new SpecialDaysRequest(deviceIdentification,
                specialDaysRequestData);

        final SpecialDaysRequestDto specialDaysRequestDto = this.configurationMapper.map(specialDaysRequestValueObject,
                SpecialDaysRequestDto.class);
        assertEquals(deviceIdentification, specialDaysRequestDto.getDeviceIdentification());

        final SpecialDaysRequestDataDto requestDataDto = specialDaysRequestDto.getSpecialDaysRequestData();
        assertNotNull(requestDataDto);
        assertNotNull(requestDataDto.getSpecialDays());
        assertEquals(dayId, requestDataDto.getSpecialDays().size());

        final SpecialDayDto specialDayDto = requestDataDto.getSpecialDays().get(0);
        assertEquals(dayId, specialDayDto.getDayId());

        final CosemDateDto specialDayDateDto = specialDayDto.getSpecialDayDate();
        assertEquals(year, specialDayDateDto.getYear());
        assertEquals(month, specialDayDateDto.getMonth());
        assertEquals(dayOfMonth, specialDayDateDto.getDayOfMonth());
    }

    // To check mapping of SpecialDaysRequest, when its SpecialDaysRequestData
    // has a filled List (1 value), where CosemDate is not specified.
    @Test
    public void testSpecialDaysRequestMappingNonEmptyListNoCosemDate() {
        final String deviceIdentification = "nr1";
        final int dayId = 1;
        final SpecialDay specialDay = new SpecialDay(new CosemDate(), dayId);
        final SpecialDaysRequestData specialDaysRequestData = new SpecialDaysRequestDataBuilder().addSpecialDay(
                specialDay).build();
        final SpecialDaysRequest specialDaysRequestValueObject = 
                new SpecialDaysRequest(deviceIdentification,
                specialDaysRequestData);

        final SpecialDaysRequestDto specialDaysRequestDto = this.configurationMapper.map(specialDaysRequestValueObject,
                SpecialDaysRequestDto.class);
        assertEquals(deviceIdentification, specialDaysRequestDto.getDeviceIdentification());

        final SpecialDaysRequestDataDto requestDataDto = specialDaysRequestDto.getSpecialDaysRequestData();
        assertNotNull(requestDataDto);
        assertNotNull(requestDataDto.getSpecialDays());
        assertEquals(dayId, requestDataDto.getSpecialDays().size());

        final SpecialDayDto specialDayDto = requestDataDto.getSpecialDays().get(0);
        assertEquals(dayId, specialDayDto.getDayId());

        final CosemDate specialDayDate = specialDay.getSpecialDayDate();
        final CosemDateDto specialDayDateDto = specialDayDto.getSpecialDayDate();
        assertEquals(specialDayDate.getYear(), specialDayDateDto.getYear());
        assertEquals(specialDayDate.getMonth(), specialDayDateDto.getMonth());
        assertEquals(specialDayDate.getDayOfMonth(), specialDayDateDto.getDayOfMonth());
    }

}
