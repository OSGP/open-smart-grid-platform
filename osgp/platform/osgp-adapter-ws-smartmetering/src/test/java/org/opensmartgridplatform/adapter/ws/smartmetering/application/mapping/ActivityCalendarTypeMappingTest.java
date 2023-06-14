// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.DayProfileActionType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.DayProfileActionsType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.DayType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SeasonType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SeasonsType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.WeekType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActivityCalendar;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DayProfile;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.WeekProfile;

public class ActivityCalendarTypeMappingTest {

  private static final String CALENDARNAME = "calendar1";
  private static final String SEASONPROFILENAME = "seasonProfile1";
  private static final String WEEKPROFILENAME = "weekProfile1";
  private static final byte[] COSEMDATETIME_BYTE_ARRAY = {
    (byte) 0x07, (byte) 0xE0, 4, 7, (byte) 0xFF, 10, 34, 35, 10, -1, -120, (byte) 0xFF
  };
  private static final byte[] COSEMTIME_BYTE_ARRAY = {10, 34, 35, 10};
  private ConfigurationMapper configurationMapper = new ConfigurationMapper();

  /** Method to build an ActivityCalendarType object */
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

  /** Method to check DayType mapping */
  private void checkDayTypeMapping(final DayProfile dayProfile) {
    assertThat(dayProfile).isNotNull();
    assertThat(dayProfile.getDayId()).isNotNull();
    assertThat(dayProfile.getDayProfileActionList()).isNotNull();
    assertThat(dayProfile.getDayProfileActionList().get(0)).isNotNull();

    assertThat(dayProfile.getDayId()).isEqualTo(new Integer(BigInteger.TEN.intValue()));
    assertThat(dayProfile.getDayProfileActionList().get(0).getScriptSelector())
        .isEqualTo(new Integer(BigInteger.ZERO.intValue()));

    // For more info on byte[] to CosemTime object mapping, refer to the
    // CosemTimeConverterTest.
    assertThat(dayProfile.getDayProfileActionList().get(0).getStartTime()).isNotNull();
  }

  /** Method to check WeekProfile mapping */
  private void checkWeekProfileMapping(final WeekProfile weekProfile) {
    assertThat(weekProfile).isNotNull();
    assertThat(weekProfile.getWeekProfileName()).isEqualTo(WEEKPROFILENAME);
    this.checkDayTypeMapping(weekProfile.getSunday());
    this.checkDayTypeMapping(weekProfile.getMonday());
    this.checkDayTypeMapping(weekProfile.getTuesday());
    this.checkDayTypeMapping(weekProfile.getWednesday());
    this.checkDayTypeMapping(weekProfile.getThursday());
    this.checkDayTypeMapping(weekProfile.getFriday());
    this.checkDayTypeMapping(weekProfile.getSaturday());
  }

  /** Method to test mapping from ActivityCalendarType to ActivityCalendar. */
  @Test
  public void testActivityCalendarTypeMapping() {

    // build test data
    final ActivityCalendarType activityCalendarType = this.buildActivityCalendarTypeObject();

    // actual mapping
    final ActivityCalendar activityCalendar =
        this.configurationMapper.map(activityCalendarType, ActivityCalendar.class);

    // check mapping
    assertThat(activityCalendar).isNotNull();
    assertThat(activityCalendar.getSeasonProfileList()).isNotNull();
    assertThat(activityCalendar.getSeasonProfileList().get(0)).isNotNull();

    // For more info on byte[] to CosemDateTime object mapping, refer to the
    // CosemDateTimeConverterTest.
    assertThat(activityCalendar.getActivatePassiveCalendarTime()).isNotNull();
    assertThat(activityCalendar.getSeasonProfileList().get(0).getSeasonStart()).isNotNull();

    assertThat(activityCalendar.getCalendarName()).isEqualTo(CALENDARNAME);
    assertThat(activityCalendar.getSeasonProfileList().get(0).getSeasonProfileName())
        .isEqualTo(SEASONPROFILENAME);

    this.checkWeekProfileMapping(activityCalendar.getSeasonProfileList().get(0).getWeekProfile());
  }
}
