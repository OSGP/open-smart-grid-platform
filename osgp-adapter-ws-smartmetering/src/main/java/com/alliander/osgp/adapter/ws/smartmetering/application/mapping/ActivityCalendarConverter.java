/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.List;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.DayProfileActionType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.DayProfileActionsType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.DayType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SeasonType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SeasonsType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.WeekType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDateTime;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemTime;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DayProfile;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DayProfileAction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SeasonProfile;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WeekProfile;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class ActivityCalendarConverter extends
        CustomConverter<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType, ActivityCalendar> {

    @Override
    public ActivityCalendar convert(final ActivityCalendarType source,
            final Type<? extends ActivityCalendar> destinationType, final MappingContext context) {
        if (source == null) {
            return null;
        }

        final CosemDateTime activatePassiveCalendarTime = this.mapperFacade.map(source.getActivatePassiveCalendarTime(),
                CosemDateTime.class);

        return new ActivityCalendar(source.getCalendarName(), activatePassiveCalendarTime,
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
        final CosemDateTime seasonStart = this.mapperFacade.map(st.getSeasonStart(), CosemDateTime.class);

        return new SeasonProfile(st.getSeasonProfileName(), seasonStart, this.processWeekProfile(st.getWeekProfile()));
    }

    private WeekProfile processWeekProfile(final WeekType weekProfile) {
        return WeekProfile.newBuilder().withWeekProfileName(weekProfile.getWeekProfileName())
                .withMonday(this.processDayProfile(weekProfile.getMonday()))
                .withTuesday(this.processDayProfile(weekProfile.getTuesday()))
                .withWednesday(this.processDayProfile(weekProfile.getWednesday()))
                .withThursday(this.processDayProfile(weekProfile.getThursday()))
                .withFriday(this.processDayProfile(weekProfile.getFriday()))
                .withSaturday(this.processDayProfile(weekProfile.getSaturday()))
                .withSunday(this.processDayProfile(weekProfile.getSunday())).build();
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
        final CosemTime startTime = this.mapperFacade.map(dpat.getStartTime(), CosemTime.class);
        return new DayProfileAction(scriptSelector, startTime);
    }

}
