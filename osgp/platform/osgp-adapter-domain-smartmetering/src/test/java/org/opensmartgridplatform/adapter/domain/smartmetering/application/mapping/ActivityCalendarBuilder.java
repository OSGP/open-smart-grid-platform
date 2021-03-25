/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.List;

import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActivityCalendar;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClockStatus;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDate;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDateTime;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemTime;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DayProfile;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DayProfileAction;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SeasonProfile;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.WeekProfile;

public class ActivityCalendarBuilder {

    private String calendarName;
    private List<SeasonProfile> seasonProfileList = new ArrayList<>();
    private CosemDateTime activatePassiveCalendarTime;

    public ActivityCalendarBuilder withCosemDateTime(final CosemDate cosemDate, final CosemTime cosemTime,
            final int deviation, final ClockStatus clockStatus) {

        this.activatePassiveCalendarTime = new CosemDateTime(cosemDate, cosemTime, deviation, clockStatus);
        return this;
    }

    public ActivityCalendarBuilder withCosemDateTime(final CosemDateTime cosemDateTime) {

        this.activatePassiveCalendarTime = cosemDateTime;
        return this;
    }

    public ActivityCalendarBuilder withFilledList() {
        final CosemDate date = new CosemDate(2016, 3, 16);
        final CosemTime time = new CosemTime(11, 45, 33);
        final int deviation = 1;
        final ClockStatus clockStatus = new ClockStatus(ClockStatus.STATUS_NOT_SPECIFIED);

        final CosemDateTime seasonStart = new CosemDateTime(date, time, deviation, clockStatus);

        final CosemTime startTime = time;
        final DayProfileAction dayProfileAction = new DayProfileAction(new Integer(10), startTime);
        final List<DayProfileAction> dayProfileActionList = new ArrayList<>();
        dayProfileActionList.add(dayProfileAction);
        final DayProfile dayProfile = new DayProfile(new Integer(10), dayProfileActionList);

        final WeekProfile weekProfile = WeekProfile.newBuilder().withWeekProfileName("weekProfile1")
                .withMonday(dayProfile).withTuesday(dayProfile).withWednesday(dayProfile).withThursday(dayProfile)
                .withFriday(dayProfile).withSaturday(dayProfile).withSunday(dayProfile).build();

        final SeasonProfile seasonProfile = new SeasonProfile("profile1", seasonStart, weekProfile);
        this.seasonProfileList.add(seasonProfile);

        return this;

    }

    public ActivityCalendar build() {
        return new ActivityCalendar(this.calendarName, this.activatePassiveCalendarTime, this.seasonProfileList);
    }

}
