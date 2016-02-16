/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DayProfile;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DayProfileAction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SeasonProfile;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.WeekProfile;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime;

public class ActivityCalendarConverter extends
CustomConverter<ActivityCalendar, com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar> {

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar convert(final ActivityCalendar source,
            final Type<? extends com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar> destinationType) {
        if (source == null) {
            return null;
        }

        final CosemDateTime activatePassiveCalendarTime = this.mapperFacade.map(
                source.getActivatePassiveCalendarTime(), CosemDateTime.class);

        return new com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar(source.getCalendarName(),
                activatePassiveCalendarTime, this.processSeasonProfile(source.getSeasonProfileList()));
    }

    private List<com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile> processSeasonProfile(
            final List<SeasonProfile> seasonProfiles) {
        final List<com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile> spl = new ArrayList<>();

        for (final SeasonProfile sp : seasonProfiles) {
            spl.add(this.processSeasonProfile(sp));
        }

        return spl;
    }

    private com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile processSeasonProfile(final SeasonProfile sp) {
        final CosemDateTime seasonStart = this.mapperFacade.map(sp.getSeasonStart(), CosemDateTime.class);

        return new com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile(sp.getSeasonProfileName(),
                seasonStart, this.processWeekProfile(sp.getWeekProfile()));
    }

    private com.alliander.osgp.dto.valueobjects.smartmetering.WeekProfile processWeekProfile(
            final WeekProfile weekProfile) {
        return new com.alliander.osgp.dto.valueobjects.smartmetering.WeekProfile(weekProfile.getWeekProfileName(),
                this.processDayProfile(weekProfile.getMonday()), this.processDayProfile(weekProfile.getTuesday()),
                this.processDayProfile(weekProfile.getWednesday()), this.processDayProfile(weekProfile.getThursday()),
                this.processDayProfile(weekProfile.getFriday()), this.processDayProfile(weekProfile.getSaturday()),
                this.processDayProfile(weekProfile.getSunday()));
    }

    private com.alliander.osgp.dto.valueobjects.smartmetering.DayProfile processDayProfile(final DayProfile dp) {
        return new com.alliander.osgp.dto.valueobjects.smartmetering.DayProfile(dp.getDayId(),
                this.processDayProfileAction(dp.getDayProfileActionList()));
    }

    private List<com.alliander.osgp.dto.valueobjects.smartmetering.DayProfileAction> processDayProfileAction(
            final List<DayProfileAction> dpas) {
        final List<com.alliander.osgp.dto.valueobjects.smartmetering.DayProfileAction> dpaDto = new ArrayList<>();

        for (final DayProfileAction dpat : dpas) {
            dpaDto.add(this.processDayProfileActionType(dpat));
        }
        return dpaDto;
    }

    private com.alliander.osgp.dto.valueobjects.smartmetering.DayProfileAction processDayProfileActionType(
            final DayProfileAction dpat) {

        final com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime startTime = this.mapperFacade.map(
                dpat.getStartTime(), com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime.class);

        return new com.alliander.osgp.dto.valueobjects.smartmetering.DayProfileAction(dpat.getScriptSelector(),
                startTime);
    }

}
