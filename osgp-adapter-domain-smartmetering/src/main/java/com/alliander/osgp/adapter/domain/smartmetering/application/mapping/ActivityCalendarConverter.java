/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.Collection;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DayProfile;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DayProfileAction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SeasonProfile;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WeekProfile;

public class ActivityCalendarConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar, ActivityCalendar> {

    @Override
    public ActivityCalendar convertTo(final com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar source,
            final Type<ActivityCalendar> destinationType) {
        if (source == null) {
            return null;
        }

        final ActivityCalendar activityCalendar = new ActivityCalendar();

        return activityCalendar;
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar convertFrom(
            final ActivityCalendar source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar> destinationType) {
        if (source == null) {
            return null;
        }

        final com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar activityCalendar = new com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar();

        activityCalendar.setCalendarName(source.getCalendarName());
        activityCalendar.setLogicalName(source.getLogicalName());
        activityCalendar.setSeasonProfileCollection(this.processSeasonProfile(source.getSeasonProfileCollection()));

        return activityCalendar;
    }

    private Collection<com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile> processSeasonProfile(
            final Collection<SeasonProfile> seasonProfiles) {
        final Collection<com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile> spl = new ArrayList<>();

        for (final SeasonProfile sp : seasonProfiles) {
            spl.add(this.processSeasonProfile(sp));
        }

        return spl;
    }

    private com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile processSeasonProfile(final SeasonProfile sp) {
        final com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile spDto = new com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile();
        spDto.setSeasonProfileName(sp.getSeasonProfileName());
        if (sp.getSeasonStart() != null) {
            spDto.setSeasonStart(sp.getSeasonStart());
        }
        spDto.setWeekProfile(this.processWeekProfile(sp.getWeekProfile()));
        return spDto;
    }

    private com.alliander.osgp.dto.valueobjects.smartmetering.WeekProfile processWeekProfile(
            final WeekProfile weekProfile) {
        final com.alliander.osgp.dto.valueobjects.smartmetering.WeekProfile wp = new com.alliander.osgp.dto.valueobjects.smartmetering.WeekProfile();
        wp.setMonday(this.processDayProfile(weekProfile.getMonday()));
        wp.setTuesday(this.processDayProfile(weekProfile.getTuesday()));
        wp.setWednesday(this.processDayProfile(weekProfile.getWednesday()));
        wp.setThursday(this.processDayProfile(weekProfile.getThursday()));
        wp.setFriday(this.processDayProfile(weekProfile.getFriday()));
        wp.setSaturday(this.processDayProfile(weekProfile.getSaturday()));
        wp.setSunday(this.processDayProfile(weekProfile.getSunday()));
        return wp;
    }

    private com.alliander.osgp.dto.valueobjects.smartmetering.DayProfile processDayProfile(final DayProfile dp) {
        final com.alliander.osgp.dto.valueobjects.smartmetering.DayProfile dpDto = new com.alliander.osgp.dto.valueobjects.smartmetering.DayProfile();
        if (dp.getDayId() != null) {
            dpDto.setDayId(dp.getDayId().intValue());
        }
        dpDto.setDayProfileActionCollection(this.processDayProfileAction(dp.getDayProfileActionCollection()));
        return dpDto;
    }

    private Collection<com.alliander.osgp.dto.valueobjects.smartmetering.DayProfileAction> processDayProfileAction(
            final Collection<DayProfileAction> dpas) {
        final Collection<com.alliander.osgp.dto.valueobjects.smartmetering.DayProfileAction> dpaDto = new ArrayList<>();

        for (final DayProfileAction dpat : dpas) {
            dpaDto.add(this.processDayProfileActionType(dpat));
        }

        return dpaDto;
    }

    private com.alliander.osgp.dto.valueobjects.smartmetering.DayProfileAction processDayProfileActionType(
            final DayProfileAction dpat) {
        final com.alliander.osgp.dto.valueobjects.smartmetering.DayProfileAction dpaDto = new com.alliander.osgp.dto.valueobjects.smartmetering.DayProfileAction();

        dpaDto.setScript_selector(dpat.getScript_selector());
        dpaDto.setScriptLogicalName(dpat.getScriptLogicalName());
        dpaDto.setStart_time(dpat.getStart_time());
        return dpaDto;
    }
}