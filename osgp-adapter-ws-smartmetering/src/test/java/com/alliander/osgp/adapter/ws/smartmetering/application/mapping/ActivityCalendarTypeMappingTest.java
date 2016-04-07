/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.DayProfileActionType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.DayProfileActionsType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.DayType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SeasonType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SeasonsType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.WeekType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ClockStatus;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDateTime;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DayProfile;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WeekProfile;

public class ActivityCalendarTypeMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // This mapping needs the following converters.
    @Before
    public void init() {
        this.mapperFactory.getConverterFactory().registerConverter(new ActivityCalendarConverter());
        this.mapperFactory.getConverterFactory().registerConverter(new CosemDateTimeConverter());
        this.mapperFactory.getConverterFactory().registerConverter(new CosemTimeConverter());
    }

    // Method to test mapping from ActivityCalendarType to ActivityCalendar.
    @Test
    public void testActivityCalendarTypeMapping() {
        // build test data
        final DayProfileActionsType dayProfileActionsType = new DayProfileActionsType();
        final DayProfileActionType dayProfileActionType = new DayProfileActionType();
        dayProfileActionType.setScriptSelector(BigInteger.ZERO);
        dayProfileActionType.setStartTime(new byte[] { 10, 35, 2, 0 });
        dayProfileActionsType.getDayProfileAction().add(dayProfileActionType);
        final DayType dayType = new DayType();
        dayType.setDayId(BigInteger.TEN);
        dayType.setDaySchedule(dayProfileActionsType);

        final WeekType weekType = new WeekType();
        final String weekProfileName = "weekProfile1";
        weekType.setWeekProfileName(weekProfileName);
        weekType.setSunday(dayType);
        weekType.setMonday(dayType);
        weekType.setTuesday(dayType);
        weekType.setWednesday(dayType);
        weekType.setThursday(dayType);
        weekType.setFriday(dayType);
        weekType.setSaturday(dayType);

        final SeasonType seasonType = new SeasonType();
        final String seasonProfileName = "seasonProfile1";
        seasonType.setSeasonProfileName(seasonProfileName);
        seasonType.setSeasonStart(new byte[] { (byte) 0x07, (byte) 0xE0, 4, 7, (byte) 0xFF, 10, 34, 35, 10, 0, 0,
                (byte) 0xFF });
        seasonType.setWeekProfile(weekType);

        final ActivityCalendarType activityCalendarType = new ActivityCalendarType();
        final byte[] activePassiveCalendarTime = { (byte) 0x07, (byte) 0xE0, 4, 7, (byte) 0xFF, 10, 34, 35, 10, 0, 0, 0 };
        final String calendarName = "calendar1";
        final SeasonsType seasonsType = new SeasonsType();
        seasonsType.getSeason().add(seasonType);
        activityCalendarType.setActivatePassiveCalendarTime(activePassiveCalendarTime);
        activityCalendarType.setCalendarName(calendarName);
        activityCalendarType.setSeasonProfile(seasonsType);

        // actual mapping
        final ActivityCalendar activityCalendar = this.mapperFactory.getMapperFacade().map(activityCalendarType,
                ActivityCalendar.class);

        // check mapping
        assertNotNull(activityCalendar);
        assertEquals(calendarName, activityCalendar.getCalendarName());
        this.checkByteArrayToCosemDateTimeMapping(activityCalendar.getActivatePassiveCalendarTime());
        assertEquals(seasonProfileName, activityCalendar.getSeasonProfileList().get(0).getSeasonProfileName());
        this.checkByteArrayToCosemDateTimeMapping(activityCalendar.getSeasonProfileList().get(0).getSeasonStart());
        this.checkWeekProfileMapping(activityCalendar.getSeasonProfileList().get(0).getWeekProfile());
    }

    // Method to check WeekProfile mapping
    private void checkWeekProfileMapping(final WeekProfile weekProfile) {
        assertEquals("weekProfile1", weekProfile.getWeekProfileName());
        this.checkDayTypeMapping(weekProfile.getSunday());
        this.checkDayTypeMapping(weekProfile.getMonday());
        this.checkDayTypeMapping(weekProfile.getTuesday());
        this.checkDayTypeMapping(weekProfile.getWednesday());
        this.checkDayTypeMapping(weekProfile.getThursday());
        this.checkDayTypeMapping(weekProfile.getFriday());
        this.checkDayTypeMapping(weekProfile.getSaturday());

    }

    // Method to check DayType mapping
    private void checkDayTypeMapping(final DayProfile dayProfile) {
        assertEquals(new Integer(BigInteger.TEN.intValue()), dayProfile.getDayId());
        assertEquals(new Integer(BigInteger.ZERO.intValue()), dayProfile.getDayProfileActionList().get(0)
                .getScriptSelector());
        assertEquals(10, dayProfile.getDayProfileActionList().get(0).getStartTime().getHour());
        assertEquals(35, dayProfile.getDayProfileActionList().get(0).getStartTime().getMinute());
        assertEquals(2, dayProfile.getDayProfileActionList().get(0).getStartTime().getSecond());
        assertEquals(0, dayProfile.getDayProfileActionList().get(0).getStartTime().getHundredths());

    }

    // Method to check byte[] to CosemDateTime mapping
    private void checkByteArrayToCosemDateTimeMapping(final CosemDateTime cosemDateTime) {

        assertEquals(2016, cosemDateTime.getDate().getYear());
        assertEquals(4, cosemDateTime.getDate().getMonth());
        assertEquals(7, cosemDateTime.getDate().getDayOfMonth());
        assertEquals(0xFF, cosemDateTime.getDate().getDayOfWeek());

        assertEquals(10, cosemDateTime.getTime().getHour());
        assertEquals(34, cosemDateTime.getTime().getMinute());
        assertEquals(35, cosemDateTime.getTime().getSecond());
        assertEquals(10, cosemDateTime.getTime().getHundredths());
        assertEquals(0, cosemDateTime.getDeviation());

        final int clockStatus = 0xFF;
        assertEquals(clockStatus, ClockStatus.STATUS_NOT_SPECIFIED);

    }

}
