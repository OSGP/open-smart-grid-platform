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

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.DayProfileActionType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.DayProfileActionsType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.DayType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SeasonType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SeasonsType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.WeekType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DayProfile;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WeekProfile;

public class ActivityCalendarTypeMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();
    private static final String CALENDARNAME = "calendar1";
    private static final String SEASONPROFILENAME = "seasonProfile1";
    private static final String WEEKPROFILENAME = "weekProfile1";
    private static final byte[] COSEMDATETIME_BYTE_ARRAY = { (byte) 0x07, (byte) 0xE0, 4, 7, (byte) 0xFF, 10, 34, 35,
            10, -1, -120, (byte) 0xFF };
    private static final byte[] COSEMTIME_BYTE_ARRAY = { 10, 34, 35, 10 };

    /**
     * Method to test mapping from ActivityCalendarType to ActivityCalendar.
     */
    @Test
    public void testActivityCalendarTypeMapping() {

        // build test data
        final ActivityCalendarType activityCalendarType = this.buildActivityCalendarTypeObject();

        // actual mapping
        final ActivityCalendar activityCalendar = this.configurationMapper.map(activityCalendarType,
                ActivityCalendar.class);

        // check mapping
        assertNotNull(activityCalendar);
        assertNotNull(activityCalendar.getSeasonProfileList());
        assertNotNull(activityCalendar.getSeasonProfileList().get(0));

        // For more info on byte[] to CosemDateTime object mapping, refer to the
        // CosemDateTimeConverterTest.
        assertNotNull(activityCalendar.getActivatePassiveCalendarTime());
        assertNotNull(activityCalendar.getSeasonProfileList().get(0).getSeasonStart());

        assertEquals(CALENDARNAME, activityCalendar.getCalendarName());
        assertEquals(SEASONPROFILENAME, activityCalendar.getSeasonProfileList().get(0).getSeasonProfileName());

        this.checkWeekProfileMapping(activityCalendar.getSeasonProfileList().get(0).getWeekProfile());
    }

    /**
     * Method to build an ActivityCalendarType object
     */
    private ActivityCalendarType buildActivityCalendarTypeObject() {

        // Build a DayType
        final DayProfileActionsType dayProfileActionsType = new DayProfileActionsType();
        final DayProfileActionType dayProfileActionType = new DayProfileActionType();
        dayProfileActionType.setScriptSelector(BigInteger.ZERO);
        dayProfileActionType.setStartTime(COSEMTIME_BYTE_ARRAY);
        dayProfileActionsType.getDayProfileAction().add(dayProfileActionType);
        final DayType dayType = new DayType();
        dayType.setDayId(BigInteger.TEN);
        dayType.setDaySchedule(dayProfileActionsType);

        // Build a WeekType
        final WeekType weekType = new WeekType();
        weekType.setWeekProfileName(WEEKPROFILENAME);
        weekType.setSunday(dayType);
        weekType.setMonday(dayType);
        weekType.setTuesday(dayType);
        weekType.setWednesday(dayType);
        weekType.setThursday(dayType);
        weekType.setFriday(dayType);
        weekType.setSaturday(dayType);

        // Build a SeasonType
        final SeasonType seasonType = new SeasonType();
        seasonType.setSeasonProfileName(SEASONPROFILENAME);
        seasonType.setSeasonStart(COSEMDATETIME_BYTE_ARRAY);
        seasonType.setWeekProfile(weekType);

        // Build an ActivityCalendarType.
        final ActivityCalendarType activityCalendarType = new ActivityCalendarType();
        final SeasonsType seasonsType = new SeasonsType();
        seasonsType.getSeason().add(seasonType);
        activityCalendarType.setActivatePassiveCalendarTime(COSEMDATETIME_BYTE_ARRAY);
        activityCalendarType.setCalendarName(CALENDARNAME);
        activityCalendarType.setSeasonProfile(seasonsType);

        return activityCalendarType;
    }

    /**
     * Method to check WeekProfile mapping
     */
    private void checkWeekProfileMapping(final WeekProfile weekProfile) {
        assertNotNull(weekProfile);
        assertEquals(WEEKPROFILENAME, weekProfile.getWeekProfileName());
        this.checkDayTypeMapping(weekProfile.getSunday());
        this.checkDayTypeMapping(weekProfile.getMonday());
        this.checkDayTypeMapping(weekProfile.getTuesday());
        this.checkDayTypeMapping(weekProfile.getWednesday());
        this.checkDayTypeMapping(weekProfile.getThursday());
        this.checkDayTypeMapping(weekProfile.getFriday());
        this.checkDayTypeMapping(weekProfile.getSaturday());

    }

    /**
     * Method to check DayType mapping
     */
    private void checkDayTypeMapping(final DayProfile dayProfile) {

        assertNotNull(dayProfile);
        assertNotNull(dayProfile.getDayId());
        assertNotNull(dayProfile.getDayProfileActionList());
        assertNotNull(dayProfile.getDayProfileActionList().get(0));

        assertEquals(new Integer(BigInteger.TEN.intValue()), dayProfile.getDayId());
        assertEquals(new Integer(BigInteger.ZERO.intValue()), dayProfile.getDayProfileActionList().get(0)
                .getScriptSelector());

        // For more info on byte[] to CosemTime object mapping, refer to the
        // CosemTimeConverterTest.
        assertNotNull(dayProfile.getDayProfileActionList().get(0).getStartTime());
    }

}
