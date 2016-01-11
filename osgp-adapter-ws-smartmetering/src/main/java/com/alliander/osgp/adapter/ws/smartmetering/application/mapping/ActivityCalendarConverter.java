/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

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
BidirectionalConverter<ActivityCalendar, com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType convertTo(
            final ActivityCalendar source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType> destinationType) {

        throw new IllegalStateException("convertTo is not supported");
    }

    @Override
    public ActivityCalendar convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType source,
            final Type<ActivityCalendar> destinationType) {
        if (source == null) {
            return null;
        }

        return new ActivityCalendar(source.getCalendarName(), source.getActivatePassiveCalendarTime() != null ? source
                .getActivatePassiveCalendarTime().toGregorianCalendar().getTime() : null,
                this.processSeasonProfile(source.getSeasonProfile()));
    }

    private List<SeasonProfile> processSeasonProfile(final SeasonsType seasonsType) {
        final List<SeasonProfile> spl = new ArrayList<>();

        for (final SeasonType st : seasonsType.getSeason()) {
            spl.add(this.processSeasonType(st));
        }

        return spl;
    }

    private SeasonProfile processSeasonType(final SeasonType st) {
        return new SeasonProfile(st.getSeasonProfileName(), st.getSeasonStart() != null ? st.getSeasonStart()
                .toGregorianCalendar().getTime() : null, this.processWeekProfile(st.getWeekProfile()));
    }

    private WeekProfile processWeekProfile(final WeekType weekProfile) {
        return new WeekProfile(weekProfile.getWeekProfileName(), this.processDayProfile(weekProfile.getMonday()),
                this.processDayProfile(weekProfile.getTuesday()), this.processDayProfile(weekProfile.getWednesday()),
                this.processDayProfile(weekProfile.getThursday()), this.processDayProfile(weekProfile.getFriday()),
                this.processDayProfile(weekProfile.getSaturday()), this.processDayProfile(weekProfile.getSunday()));
    }

    private DayProfile processDayProfile(final DayType day) {
        final Integer dayId = day.getDayId() != null ? day.getDayId().intValue() : null;
        return new DayProfile(dayId, this.processDayProfileAction(day.getDaySchedule()));
    }

    private List<DayProfileAction> processDayProfileAction(final DayProfileActionsType dayScheduleActionsType) {
        final List<DayProfileAction> dayProfileActionList = new ArrayList<>();

        for (final DayProfileActionType dpat : dayScheduleActionsType.getDayProfileAction()) {
            dayProfileActionList.add(this.processDayProfileActionType(dpat));
        }

        return dayProfileActionList;
    }

    private DayProfileAction processDayProfileActionType(final DayProfileActionType dpat) {
        final Integer scriptSelector = dpat.getScriptSelector() != null ? dpat.getScriptSelector().intValue() : null;
        final Date startTime = dpat.getStartTime() != null ? dpat.getStartTime().toGregorianCalendar().getTime() : null;
        return new DayProfileAction(scriptSelector, startTime);
    }
}
