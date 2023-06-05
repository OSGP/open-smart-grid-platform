// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.math.BigInteger;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetActivityCalendarRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.DayProfileActionType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.DayProfileActionsType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.DayType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SeasonType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SeasonsType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.WeekType;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar.ActivityCalendar;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar.DayProfile;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar.DayProfileAction;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar.SeasonProfile;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar.WeekProfile;

public class SetActivityCalendarRequestBuilder {
  private static final ActivityCalendar DEFAULT_ACTIVITY_CALENDAR =
      new ActivityCalendar("ACTCAL01", "FFFFFFFEFFFFFFFFFF000000");

  static {
    final SeasonProfile seasonProfile = new SeasonProfile("1", "FFFF0101FFFFFFFFFF000000", "1");
    final WeekProfile weekProfile = new WeekProfile("1", 0, 0, 0, 0, 0, 0, 0);
    final DayProfileAction dayProfileAction = new DayProfileAction("00000000", 1);
    final DayProfile dayProfile = new DayProfile(0);
    dayProfile.getDayProfileActions().add(dayProfileAction);
    DEFAULT_ACTIVITY_CALENDAR.getSeasonProfiles().put(seasonProfile.getName(), seasonProfile);
    DEFAULT_ACTIVITY_CALENDAR.getWeekProfiles().put(weekProfile.getName(), weekProfile);
    DEFAULT_ACTIVITY_CALENDAR.getDayProfiles().put(dayProfile.getDayId(), dayProfile);
  }

  private ActivityCalendar activityCalendar;

  public SetActivityCalendarRequestBuilder withDefaults() {
    this.activityCalendar = DEFAULT_ACTIVITY_CALENDAR;
    return this;
  }

  public SetActivityCalendarRequestBuilder withActivityCalendar(
      final ActivityCalendar activityCalendar) {

    this.activityCalendar = activityCalendar;

    return this;
  }

  public SetActivityCalendarRequest build() {

    final SetActivityCalendarRequest request = new SetActivityCalendarRequest();
    request.setActivityCalendar(this.getActivityCalendarType());

    return request;
  }

  private ActivityCalendarType getActivityCalendarType() {
    final ActivityCalendarType act = new ActivityCalendarType();
    act.setCalendarName(this.activityCalendar.getName());
    act.setActivatePassiveCalendarTime(
        DatatypeConverter.parseHexBinary(this.activityCalendar.getActivatePassiveCalendarTime()));
    act.setSeasonProfile(this.getSeasonsType());
    return act;
  }

  private SeasonsType getSeasonsType() {
    final SeasonsType st = new SeasonsType();
    for (final SeasonProfile sp : this.activityCalendar.getSeasonProfiles().values()) {
      st.getSeason().add(this.getSeasonType(sp));
    }
    return st;
  }

  private SeasonType getSeasonType(final SeasonProfile sp) {
    final SeasonType st = new SeasonType();
    st.setSeasonProfileName(sp.getName());
    st.setSeasonStart(DatatypeConverter.parseHexBinary(sp.getStart()));
    st.setWeekProfile(this.getWeekType(sp.getWeekName()));
    return st;
  }

  private WeekType getWeekType(final String weekName) {
    final WeekType wt = new WeekType();
    final WeekProfile wp = this.activityCalendar.getWeekProfiles().get(weekName);
    wt.setWeekProfileName(wp.getName());
    wt.setMonday(this.getDayType(wp.getMondayDayId()));
    wt.setTuesday(this.getDayType(wp.getTuesdayDayId()));
    wt.setWednesday(this.getDayType(wp.getWednesdayDayId()));
    wt.setThursday(this.getDayType(wp.getThursdayDayId()));
    wt.setFriday(this.getDayType(wp.getFridayDayId()));
    wt.setSaturday(this.getDayType(wp.getSaturdayDayId()));
    wt.setSunday(this.getDayType(wp.getSundayDayId()));
    return wt;
  }

  private DayType getDayType(final int dayId) {
    final DayType dt = new DayType();
    final DayProfile dp = this.activityCalendar.getDayProfiles().get(dayId);
    dt.setDayId(BigInteger.valueOf(dp.getDayId()));
    dt.setDaySchedule(this.getDayProfileActionsType(dp.getDayProfileActions()));
    return dt;
  }

  private DayProfileActionsType getDayProfileActionsType(final List<DayProfileAction> actions) {
    final DayProfileActionsType dpat = new DayProfileActionsType();
    for (final DayProfileAction dpa : actions) {
      dpat.getDayProfileAction().add(this.getDayProfileActionType(dpa));
    }
    return dpat;
  }

  private DayProfileActionType getDayProfileActionType(final DayProfileAction dpa) {
    final DayProfileActionType dpat = new DayProfileActionType();
    dpat.setStartTime(DatatypeConverter.parseHexBinary(dpa.getStartTime()));
    dpat.setScriptSelector(BigInteger.valueOf(dpa.getScriptId()));
    return dpat;
  }
}
