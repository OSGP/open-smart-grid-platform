/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.Collection;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.DayProfileActionType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.DayProfileActionsType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.DayType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SeasonType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SeasonsType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.WeekType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DayProfile;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DayProfileAction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SeasonProfile;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WeekProfile;

public class ActivityCalendarConverter
extends
        BidirectionalConverter<ActivityCalendar, com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendar> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityCalendarConverter.class);

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendar convertTo(
            final ActivityCalendar source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendar> destinationType) {

        return new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendar();
    }

    @Override
    public ActivityCalendar convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendar source,
            final Type<ActivityCalendar> destinationType) {
        if (source == null) {
            return null;
        }

        final ActivityCalendar activityCalendar = new ActivityCalendar();

        activityCalendar.setCalendarName(source.getCalendarName());
        activityCalendar.setLogicalName(source.getLogicalName());
        activityCalendar.setSeasonProfileCollection(this.processSeasonProfile(source.getSeasonProfile()));

        return activityCalendar;
    }

    private Collection<SeasonProfile> processSeasonProfile(final SeasonsType seasonsType) {
        final Collection<SeasonProfile> spl = new ArrayList<>();

        for (final SeasonType st : seasonsType.getSeason()) {
            spl.add(this.processSeasonType(st));
        }

        return spl;
    }

    private SeasonProfile processSeasonType(final SeasonType st) {
        final SeasonProfile sp = new SeasonProfile();
        sp.setSeasonProfileName(st.getSeasonProfileName());
        if (st.getSeasonStart() != null) {
            sp.setSeasonStart(st.getSeasonStart().toGregorianCalendar().getTime());
        }
        sp.setWeekProfile(this.processWeekProfile(st.getWeekProfile()));
        return sp;
    }

    private WeekProfile processWeekProfile(final WeekType weekProfile) {
        final WeekProfile wp = new WeekProfile();
        wp.setMonday(this.processDayProfile(weekProfile.getMonday()));
        wp.setTuesday(this.processDayProfile(weekProfile.getTuesday()));
        wp.setWednesday(this.processDayProfile(weekProfile.getWednesday()));
        wp.setThursday(this.processDayProfile(weekProfile.getThursday()));
        wp.setFriday(this.processDayProfile(weekProfile.getFriday()));
        wp.setSaturday(this.processDayProfile(weekProfile.getSaturday()));
        wp.setSunday(this.processDayProfile(weekProfile.getSunday()));
        return wp;
    }

    private DayProfile processDayProfile(final DayType day) {
        final DayProfile dp = new DayProfile();
        if (day.getDayId() != null) {
            dp.setDayId(day.getDayId().intValue());
        }
        dp.setDayProfileActionCollection(this.processDayProfileAction(day.getDaySchedule()));
        return dp;
    }

    private Collection<DayProfileAction> processDayProfileAction(final DayProfileActionsType dayScheduleActionsType) {
        final Collection<DayProfileAction> dayProfileActionList = new ArrayList<>();

        for (final DayProfileActionType dpat : dayScheduleActionsType.getDayProfileAction()) {
            dayProfileActionList.add(this.processDayProfileActionType(dpat));
        }

        return dayProfileActionList;
    }

    private DayProfileAction processDayProfileActionType(final DayProfileActionType dpat) {
        final DayProfileAction dayProfileAction = new DayProfileAction();

        dayProfileAction.setScript_selector(dpat.getScriptSelector().intValue());
        dayProfileAction.setScriptLogicalName(dpat.getScriptLogicalName());
        if (dpat.getStartTime() != null) {
            dayProfileAction.setStart_time(dpat.getStartTime().toGregorianCalendar().getTime());
        }
        return dayProfileAction;
    }
}
