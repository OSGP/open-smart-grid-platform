/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActivityCalendar;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClockStatus;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDate;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDateTime;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemTime;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DayProfile;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DayProfileAction;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SeasonProfile;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.WeekProfile;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActivityCalendarDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClockStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DayProfileActionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DayProfileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SeasonProfileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.WeekProfileDto;

// Test mapping of ActivityCalendar objects
public class ActivityCalendarMappingTest {

  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();
  private CosemDateTime cosemDateTime;

  @BeforeEach
  public void init() {
    this.cosemDateTime =
        new CosemDateTime(
            new CosemDate(2016, 3, 16),
            new CosemTime(11, 45, 33),
            1,
            new ClockStatus(ClockStatus.STATUS_NOT_SPECIFIED));
  }

  // Neither the CosemDateTime or List<SeasonProfile> of a ActivityCalendar
  // may ever be null. Tests to make sure a NullPointerException is thrown
  // when one is.
  @Test
  public void testNullCosemDateTime() {
    final String calendarName = "calendar";
    final CosemDateTime activePassiveCalendarTime = null;
    final List<SeasonProfile> seasonProfileList = new ArrayList<>();

    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () -> {
              new ActivityCalendar(calendarName, activePassiveCalendarTime, seasonProfileList);
            });
  }

  // Neither the CosemDateTime or List<SeasonProfile> of a ActivityCalendar
  // may ever be null. Tests to make sure a NullPointerException is thrown
  // when one is.
  @Test
  public void testNullList() {
    final String calendarName = "calendar";
    final CosemDateTime activePassiveCalendarTime = new CosemDateTime();
    final List<SeasonProfile> seasonProfileList = null;

    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () -> {
              new ActivityCalendar(calendarName, activePassiveCalendarTime, seasonProfileList);
            });
  }

  // Test mapping with a CosemDateTime object AND an empty list
  @Test
  public void testWithCosemDateTimeAndEmptyList() {
    // build test data
    final ActivityCalendar activityCalendar =
        new ActivityCalendarBuilder().withCosemDateTime(this.cosemDateTime).build();

    // actual mapping
    final ActivityCalendarDto activityCalendarDto =
        this.configurationMapper.map(activityCalendar, ActivityCalendarDto.class);

    // check if mapping succeeded
    assertThat(activityCalendarDto).isNotNull();
    assertThat(activityCalendarDto.getActivatePassiveCalendarTime()).isNotNull();
    assertThat(activityCalendarDto.getSeasonProfileList()).isNotNull();

    assertThat(activityCalendarDto.getCalendarName()).isEqualTo(activityCalendar.getCalendarName());

    this.checkEmptyListMapping(
        activityCalendar.getSeasonProfileList(), activityCalendarDto.getSeasonProfileList());
    this.checkCosemDateTimeMapping(
        activityCalendar.getActivatePassiveCalendarTime(),
        activityCalendarDto.getActivatePassiveCalendarTime());
  }

  // Test the mapping of a complete ActivityCalendar object
  @Test
  public void testCompleteMapping() {
    // build test data
    final ActivityCalendar activityCalendar =
        new ActivityCalendarBuilder()
            .withCosemDateTime(this.cosemDateTime)
            .withFilledList()
            .build();

    // actual mapping
    final ActivityCalendarDto activityCalendarDto =
        this.configurationMapper.map(activityCalendar, ActivityCalendarDto.class);

    // check if mapping succeeded
    assertThat(activityCalendarDto).isNotNull();
    assertThat(activityCalendarDto.getActivatePassiveCalendarTime()).isNotNull();
    assertThat(activityCalendarDto.getSeasonProfileList()).isNotNull();

    assertThat(activityCalendarDto.getCalendarName()).isEqualTo(activityCalendar.getCalendarName());

    this.checkListMapping(
        activityCalendar.getSeasonProfileList(), activityCalendarDto.getSeasonProfileList());
    this.checkCosemDateTimeMapping(
        activityCalendar.getActivatePassiveCalendarTime(),
        activityCalendarDto.getActivatePassiveCalendarTime());
  }

  // method to test mapping of Filled Lists
  private void checkListMapping(
      final List<SeasonProfile> seasonProfileList,
      final List<SeasonProfileDto> seasonProfileDtoList) {

    assertThat(seasonProfileList).isNotNull();
    assertThat(seasonProfileDtoList).isNotNull();
    assertThat(seasonProfileList.isEmpty()).isFalse();
    assertThat(seasonProfileDtoList.isEmpty()).isFalse();
    final SeasonProfile seasonProfile = seasonProfileList.get(0);
    final SeasonProfileDto seasonProfileDto = seasonProfileDtoList.get(0);
    assertThat(seasonProfileDto.getSeasonProfileName())
        .isEqualTo(seasonProfile.getSeasonProfileName());
    this.checkCosemDateTimeMapping(
        seasonProfile.getSeasonStart(), seasonProfileDto.getSeasonStart());

    final WeekProfile weekProfile = seasonProfile.getWeekProfile();
    final WeekProfileDto weekProfileDto = seasonProfileDto.getWeekProfile();
    assertThat(weekProfileDto.getWeekProfileName()).isEqualTo(weekProfile.getWeekProfileName());

    final DayProfile dayProfile = weekProfile.getMonday();
    final DayProfileDto dayProfileDto = weekProfileDto.getMonday();
    assertThat(dayProfileDto.getDayId()).isEqualTo(dayProfile.getDayId());
    assertThat(dayProfile.getDayProfileActionList().size())
        .isEqualTo(dayProfile.getDayProfileActionList().size());

    final DayProfileAction dayProfileAction = dayProfile.getDayProfileActionList().get(0);
    final DayProfileActionDto dayProfileActionDto = dayProfileDto.getDayProfileActionList().get(0);
    assertThat(dayProfileActionDto.getScriptSelector())
        .isEqualTo(dayProfileAction.getScriptSelector());

    final CosemTime cosemTime = dayProfileAction.getStartTime();
    final CosemTimeDto cosemTimeDto = dayProfileActionDto.getStartTime();
    assertThat(cosemTimeDto.getHour()).isEqualTo(cosemTime.getHour());
    assertThat(cosemTimeDto.getMinute()).isEqualTo(cosemTime.getMinute());
    assertThat(cosemTimeDto.getSecond()).isEqualTo(cosemTime.getSecond());
    assertThat(cosemTimeDto.getHundredths()).isEqualTo(cosemTime.getHundredths());
  }

  // method to test mapping of Empty lists
  private void checkEmptyListMapping(
      final List<SeasonProfile> seasonProfileList,
      final List<SeasonProfileDto> seasonProfileDtoList) {

    assertThat(seasonProfileList).isNotNull();
    assertThat(seasonProfileDtoList).isNotNull();
    assertThat(seasonProfileList).isEmpty();
    assertThat(seasonProfileDtoList).isEmpty();
  }

  // method to test mapping of CosemDateTime objects
  private void checkCosemDateTimeMapping(
      final CosemDateTime cosemDateTime, final CosemDateTimeDto cosemDateTimeDto) {

    // make sure neither is null
    assertThat(cosemDateTime).isNotNull();
    assertThat(cosemDateTimeDto).isNotNull();

    // check variables
    assertThat(cosemDateTimeDto.getDeviation()).isEqualTo(cosemDateTime.getDeviation());

    final ClockStatus clockStatus = cosemDateTime.getClockStatus();
    final ClockStatusDto clockStatusDto = cosemDateTimeDto.getClockStatus();
    assertThat(clockStatusDto.getStatus()).isEqualTo(clockStatus.getStatus());
    assertThat(clockStatusDto.isSpecified()).isEqualTo(clockStatus.isSpecified());

    final CosemDate cosemDate = cosemDateTime.getDate();
    final CosemDateDto cosemDateDto = cosemDateTimeDto.getDate();
    assertThat(cosemDateDto.getYear()).isEqualTo(cosemDate.getYear());
    assertThat(cosemDateDto.getMonth()).isEqualTo(cosemDate.getMonth());
    assertThat(cosemDateDto.getDayOfMonth()).isEqualTo(cosemDate.getDayOfMonth());
    assertThat(cosemDateDto.getDayOfWeek()).isEqualTo(cosemDate.getDayOfWeek());

    final CosemTime cosemTime = cosemDateTime.getTime();
    final CosemTimeDto cosemTimeDto = cosemDateTimeDto.getTime();
    assertThat(cosemTimeDto.getHour()).isEqualTo(cosemTime.getHour());
    assertThat(cosemTimeDto.getMinute()).isEqualTo(cosemTime.getMinute());
    assertThat(cosemTimeDto.getSecond()).isEqualTo(cosemTime.getSecond());
    assertThat(cosemTimeDto.getHundredths()).isEqualTo(cosemTime.getHundredths());
  }
}
