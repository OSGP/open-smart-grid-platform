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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ClockStatus;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDateTime;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DayProfile;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WeekProfile;

public class ActivityCalendarTypeMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();
    private static final String CALENDARNAME = "calendar1";
    private static final String SEASONPROFILENAME = "seasonProfile1";
    private static final String WEEKPROFILENAME = "weekProfile1";
    private static final byte FIRST_BYTE_FOR_YEAR = (byte) 0x07;
    private static final byte SECOND_BYTE_FOR_YEAR = (byte) 0xE0;
    private static final byte BYTE_FOR_MONTH = 4;
    private static final byte BYTE_FOR_DAY_OF_MONTH = 7;
    private static final byte BYTE_FOR_DAY_OF_WEEK = (byte) 0xFF;
    private static final byte BYTE_FOR_HOUR_OF_DAY = 10;
    private static final byte BYTE_FOR_MINUTE_OF_HOUR = 34;
    private static final byte BYTE_FOR_SECOND_OF_MINUTE = 35;
    private static final byte BYTE_FOR_HUNDREDS_0F_SECONDS = 10;
    private static final byte FIRST_BYTE_FOR_DEVIATION = -1;
    private static final byte SECOND_BYTE_FOR_DEVIATION = -120;
    private static final byte BYTE_FOR_CLOCKSTATUS = (byte) 0xFF;
    private static final byte[] COSEMDATETIME_BYTE_ARRAY = { FIRST_BYTE_FOR_YEAR, SECOND_BYTE_FOR_YEAR, BYTE_FOR_MONTH,
        BYTE_FOR_DAY_OF_MONTH, BYTE_FOR_DAY_OF_WEEK, BYTE_FOR_HOUR_OF_DAY, BYTE_FOR_MINUTE_OF_HOUR,
            BYTE_FOR_SECOND_OF_MINUTE, BYTE_FOR_HUNDREDS_0F_SECONDS, FIRST_BYTE_FOR_DEVIATION,
            SECOND_BYTE_FOR_DEVIATION, BYTE_FOR_CLOCKSTATUS };
    private static final byte[] COSEMTIME_BYTE_ARRAY = { BYTE_FOR_HOUR_OF_DAY, BYTE_FOR_MINUTE_OF_HOUR,
        BYTE_FOR_SECOND_OF_MINUTE, BYTE_FOR_HUNDREDS_0F_SECONDS };

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
        assertEquals(CALENDARNAME, activityCalendar.getCalendarName());
        this.checkByteArrayToCosemDateTimeMapping(activityCalendar.getActivatePassiveCalendarTime());
        assertEquals(SEASONPROFILENAME, activityCalendar.getSeasonProfileList().get(0).getSeasonProfileName());
        this.checkByteArrayToCosemDateTimeMapping(activityCalendar.getSeasonProfileList().get(0).getSeasonStart());
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
        assertEquals(new Integer(BigInteger.TEN.intValue()), dayProfile.getDayId());
        assertEquals(new Integer(BigInteger.ZERO.intValue()), dayProfile.getDayProfileActionList().get(0)
                .getScriptSelector());
        assertEquals(BYTE_FOR_HOUR_OF_DAY, dayProfile.getDayProfileActionList().get(0).getStartTime().getHour());
        assertEquals(BYTE_FOR_MINUTE_OF_HOUR, dayProfile.getDayProfileActionList().get(0).getStartTime().getMinute());
        assertEquals(BYTE_FOR_SECOND_OF_MINUTE, dayProfile.getDayProfileActionList().get(0).getStartTime().getSecond());
        assertEquals(BYTE_FOR_HUNDREDS_0F_SECONDS, dayProfile.getDayProfileActionList().get(0).getStartTime()
                .getHundredths());

    }

    /**
     * Method to check byte[] to CosemDateTime mapping
     */

    private void checkByteArrayToCosemDateTimeMapping(final CosemDateTime cosemDateTime) {

        assertEquals(FIRST_BYTE_FOR_YEAR, ((byte) (cosemDateTime.getDate().getYear() >> 8)));
        assertEquals(SECOND_BYTE_FOR_YEAR, ((byte) (cosemDateTime.getDate().getYear() & 0xFF)));
        assertEquals(BYTE_FOR_MONTH, cosemDateTime.getDate().getMonth());
        assertEquals(BYTE_FOR_DAY_OF_MONTH, cosemDateTime.getDate().getDayOfMonth());
        assertEquals(BYTE_FOR_DAY_OF_WEEK, ((byte) cosemDateTime.getDate().getDayOfWeek()));

        assertEquals(BYTE_FOR_HOUR_OF_DAY, cosemDateTime.getTime().getHour());
        assertEquals(BYTE_FOR_MINUTE_OF_HOUR, cosemDateTime.getTime().getMinute());
        assertEquals(BYTE_FOR_SECOND_OF_MINUTE, cosemDateTime.getTime().getSecond());
        assertEquals(BYTE_FOR_HUNDREDS_0F_SECONDS, cosemDateTime.getTime().getHundredths());
        assertEquals(FIRST_BYTE_FOR_DEVIATION, ((byte) (cosemDateTime.getDeviation() >> 8)));
        assertEquals(SECOND_BYTE_FOR_DEVIATION, ((byte) (cosemDateTime.getDeviation() & 0xFF)));

        assertEquals(BYTE_FOR_CLOCKSTATUS, ((byte) ClockStatus.STATUS_NOT_SPECIFIED));

    }

}
