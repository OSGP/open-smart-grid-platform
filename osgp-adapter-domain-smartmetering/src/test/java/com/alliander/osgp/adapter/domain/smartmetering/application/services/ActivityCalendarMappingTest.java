/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ClockStatus;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDate;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDateTime;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemTime;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DayProfile;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DayProfileAction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SeasonProfile;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WeekProfile;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendarDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ClockStatusDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTimeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemTimeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DayProfileActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DayProfileDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfileDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.WeekProfileDto;

// Test mapping of ActivityCalendar objects
public class ActivityCalendarMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();
    private CosemDateTime cosemDateTime;

    @Before
    public void init() {
        this.cosemDateTime = new CosemDateTime(new CosemDate(2016, 3, 16), new CosemTime(11, 45, 33), 1,
                new ClockStatus(ClockStatus.STATUS_NOT_SPECIFIED));
    }

    // Neither the CosemDateTime or List<SeasonProfile> of a ActivityCalendar
    // may ever be null. Tests to make sure a NullPointerException is thrown
    // when they are.
    @Test(expected = NullPointerException.class)
    public void testNullCosemDateTime() {
        final String calendarName = "calendar";
        final CosemDateTime activePassiveCalendarTime = null;
        final List<SeasonProfile> seasonProfileList = new ArrayList<>();

        new ActivityCalendar(calendarName, activePassiveCalendarTime, seasonProfileList);
    }

    @Test(expected = NullPointerException.class)
    public void testNullList() {
        final String calendarName = "calendar";
        final CosemDateTime activePassiveCalendarTime = new CosemDateTime();
        final List<SeasonProfile> seasonProfileList = null;

        new ActivityCalendar(calendarName, activePassiveCalendarTime, seasonProfileList);
    }

    // Tests mapping when List is empty
    @Test
    public void testWithEmptyList() {

        // build test data
        final ActivityCalendar activityCalendar = new ActivityCalendarBuilder().withCosemDateTime(this.cosemDateTime)
                .build();

        // actual mapping
        final ActivityCalendarDto activityCalendarDto = this.configurationMapper.map(activityCalendar,
                ActivityCalendarDto.class);

        // check if mapping succeeded
        assertNotNull(activityCalendar);
        assertNotNull(activityCalendarDto);
        assertNotNull(activityCalendarDto.getActivatePassiveCalendarTime());
        assertNotNull(activityCalendarDto.getSeasonProfileList());

        assertEquals(activityCalendar.getCalendarName(), activityCalendarDto.getCalendarName());
        this.testEmptyListMapping(activityCalendar.getSeasonProfileList(), activityCalendarDto.getSeasonProfileList());
        this.testCosemDateTimeMapping(activityCalendar.getActivatePassiveCalendarTime(),
                activityCalendarDto.getActivatePassiveCalendarTime());

    }

    // Test mapping with a CosemDateTime object AND an empty list
    @Test
    public void testWithCosemDateTime() {
        // build test data
        final ActivityCalendar activityCalendar = new ActivityCalendarBuilder().withCosemDateTime(this.cosemDateTime)
                .build();

        // actual mapping
        final ActivityCalendarDto activityCalendarDto = this.configurationMapper.map(activityCalendar,
                ActivityCalendarDto.class);

        // check if mapping succeeded
        assertNotNull(activityCalendar);
        assertNotNull(activityCalendarDto);
        assertNotNull(activityCalendarDto.getActivatePassiveCalendarTime());
        assertNotNull(activityCalendarDto.getSeasonProfileList());

        assertEquals(activityCalendar.getCalendarName(), activityCalendarDto.getCalendarName());
        this.testEmptyListMapping(activityCalendar.getSeasonProfileList(), activityCalendarDto.getSeasonProfileList());
        this.testCosemDateTimeMapping(activityCalendar.getActivatePassiveCalendarTime(),
                activityCalendarDto.getActivatePassiveCalendarTime());

    }

    // Test the mapping of a complete ActivityCalendar object
    @Test
    public void testCompleteMapping() {
        // build test data
        final ActivityCalendar activityCalendar = new ActivityCalendarBuilder().withCosemDateTime(this.cosemDateTime)
                .withFilledList().build();

        // actual mapping
        final ActivityCalendarDto activityCalendarDto = this.configurationMapper.map(activityCalendar,
                ActivityCalendarDto.class);

        // check if mapping succeeded
        assertNotNull(activityCalendar);
        assertNotNull(activityCalendarDto);
        assertNotNull(activityCalendarDto.getActivatePassiveCalendarTime());
        assertNotNull(activityCalendarDto.getSeasonProfileList());

        assertEquals(activityCalendar.getCalendarName(), activityCalendarDto.getCalendarName());
        this.testListMapping(activityCalendar.getSeasonProfileList(), activityCalendarDto.getSeasonProfileList());
        this.testCosemDateTimeMapping(activityCalendar.getActivatePassiveCalendarTime(),
                activityCalendarDto.getActivatePassiveCalendarTime());
    }

    // method to test mapping of Filled Lists
    private void testListMapping(final List<SeasonProfile> seasonProfileList,
            final List<SeasonProfileDto> seasonProfileDtoList) {

        assertNotNull(seasonProfileList);
        assertNotNull(seasonProfileDtoList);
        assertEquals(seasonProfileList.size(), seasonProfileDtoList.size());

        assertFalse(seasonProfileList.isEmpty());
        assertFalse(seasonProfileDtoList.isEmpty());
        assertEquals(seasonProfileList.isEmpty(), seasonProfileDtoList.isEmpty());

        final SeasonProfile seasonProfile = seasonProfileList.get(0);
        final SeasonProfileDto seasonProfileDto = seasonProfileDtoList.get(0);
        assertEquals(seasonProfile.getSeasonProfileName(), seasonProfileDto.getSeasonProfileName());
        this.testCosemDateTimeMapping(seasonProfile.getSeasonStart(), seasonProfileDto.getSeasonStart());

        final WeekProfile weekProfile = seasonProfile.getWeekProfile();
        final WeekProfileDto weekProfileDto = seasonProfileDto.getWeekProfile();
        assertEquals(weekProfile.getWeekProfileName(), weekProfileDto.getWeekProfileName());

        final DayProfile dayProfile = weekProfile.getMonday();
        final DayProfileDto dayProfileDto = weekProfileDto.getMonday();
        assertEquals(dayProfile.getDayId(), dayProfileDto.getDayId());
        assertEquals(dayProfile.getDayProfileActionList().size(), dayProfile.getDayProfileActionList().size());

        final DayProfileAction dayProfileAction = dayProfile.getDayProfileActionList().get(0);
        final DayProfileActionDto dayProfileActionDto = dayProfileDto.getDayProfileActionList().get(0);
        assertEquals(dayProfileAction.getScriptSelector(), dayProfileActionDto.getScriptSelector());

        final CosemTime cosemTime = dayProfileAction.getStartTime();
        final CosemTimeDto cosemTimeDto = dayProfileActionDto.getStartTime();
        assertEquals(cosemTime.getHour(), cosemTimeDto.getHour());
        assertEquals(cosemTime.getMinute(), cosemTimeDto.getMinute());
        assertEquals(cosemTime.getSecond(), cosemTimeDto.getSecond());
        assertEquals(cosemTime.getHundredths(), cosemTimeDto.getHundredths());

    }

    // method to test mapping of Empty lists
    private void testEmptyListMapping(final List<SeasonProfile> seasonProfileList,
            final List<SeasonProfileDto> seasonProfileDtoList) {

        assertNotNull(seasonProfileList);
        assertNotNull(seasonProfileDtoList);
        assertEquals(seasonProfileList.size(), seasonProfileDtoList.size());
        assertTrue(seasonProfileList.isEmpty());
        assertTrue(seasonProfileDtoList.isEmpty());
        assertEquals(seasonProfileList.isEmpty(), seasonProfileDtoList.isEmpty());

    }

    // method to test mapping of CosemDateTime objects
    private void testCosemDateTimeMapping(final CosemDateTime cosemDateTime, final CosemDateTimeDto cosemDateTimeDto) {

        // make sure neither is null
        assertNotNull(cosemDateTime);
        assertNotNull(cosemDateTimeDto);

        // check variables
        assertEquals(cosemDateTime.getDeviation(), cosemDateTimeDto.getDeviation());

        final ClockStatus clockStatus = cosemDateTime.getClockStatus();
        final ClockStatusDto clockStatusDto = cosemDateTimeDto.getClockStatus();
        assertEquals(clockStatus.getStatus(), clockStatusDto.getStatus());
        assertEquals(clockStatus.isSpecified(), clockStatusDto.isSpecified());

        final CosemDate cosemDate = cosemDateTime.getDate();
        final CosemDateDto cosemDateDto = cosemDateTimeDto.getDate();
        assertEquals(cosemDate.getYear(), cosemDateDto.getYear());
        assertEquals(cosemDate.getMonth(), cosemDateDto.getMonth());
        assertEquals(cosemDate.getDayOfMonth(), cosemDateDto.getDayOfMonth());
        assertEquals(cosemDate.getDayOfWeek(), cosemDateDto.getDayOfWeek());

        final CosemTime cosemTime = cosemDateTime.getTime();
        final CosemTimeDto cosemTimeDto = cosemDateTimeDto.getTime();
        assertEquals(cosemTime.getHour(), cosemTimeDto.getHour());
        assertEquals(cosemTime.getMinute(), cosemTimeDto.getMinute());
        assertEquals(cosemTime.getSecond(), cosemTimeDto.getSecond());
        assertEquals(cosemTime.getHundredths(), cosemTimeDto.getHundredths());
    }

}
