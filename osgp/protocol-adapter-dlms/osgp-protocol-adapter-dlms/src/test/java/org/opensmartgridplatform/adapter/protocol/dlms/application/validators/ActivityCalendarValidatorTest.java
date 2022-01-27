/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActivityCalendarDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DayProfileActionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DayProfileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SeasonProfileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.WeekProfileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.WeekProfileDto.Builder;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class ActivityCalendarValidatorTest {

  enum TestAnomaly {
    DUPLICATE_SEASON,
    FIFTH_SEASON,
    DUPLICATE_WEEK,
    DUPLICATE_DAY,
    FIFTH_DAY,
    MULTIPLE_USES_SAME_WEEK
  };

  ActivityCalendarDto createActivityCalendarDto(final TestAnomaly testAnomaly)
      throws FunctionalException {
    final DayProfileActionDto dayAction1 = new DayProfileActionDto(1, new CosemTimeDto());
    final DayProfileActionDto dayAction2 = new DayProfileActionDto(2, new CosemTimeDto());
    final DayProfileActionDto dayAction3 = new DayProfileActionDto(3, new CosemTimeDto());
    final DayProfileActionDto dayAction4 = new DayProfileActionDto(4, new CosemTimeDto());
    final DayProfileActionDto dayAction5 = new DayProfileActionDto(5, new CosemTimeDto());

    final List<DayProfileActionDto> dayActions1 = Arrays.asList(dayAction1, dayAction2);
    final List<DayProfileActionDto> dayActions2 = Arrays.asList(dayAction3, dayAction4, dayAction5);

    int dayId = 2;
    if (testAnomaly == TestAnomaly.DUPLICATE_DAY) {
      // a second day with id 1 will be created with different day actions
      dayId = 1;
    }
    final DayProfileDto dayProfile1 = new DayProfileDto(1, dayActions1);
    final DayProfileDto dayProfile2 = new DayProfileDto(dayId, dayActions2);
    final DayProfileDto dayProfile3 = new DayProfileDto(3, dayActions2);
    final DayProfileDto dayProfile4 = new DayProfileDto(4, dayActions2);
    final DayProfileDto dayProfile5 = new DayProfileDto(5, dayActions2);

    final WeekProfileDto weekProfileDto1 =
        this.createWeekProfile("Week___1", dayProfile1, dayProfile2);

    final SeasonProfileDto seasonProfileDto1 =
        new SeasonProfileDto("Season_1", new CosemDateTimeDto(), weekProfileDto1);

    String weekProfileName = "Week___2";
    if (testAnomaly == TestAnomaly.DUPLICATE_WEEK) {
      // a second week with name 'Week___1' will be created with different day profiles
      weekProfileName = "Week___1";
    }
    final Builder weekProfileBuilder2 = WeekProfileDto.newBuilder();
    weekProfileBuilder2.withWeekProfileName(weekProfileName);
    weekProfileBuilder2.withMonday(dayProfile3);
    weekProfileBuilder2.withTuesday(dayProfile3);
    weekProfileBuilder2.withWednesday(dayProfile3);
    weekProfileBuilder2.withThursday(dayProfile3);
    weekProfileBuilder2.withFriday(dayProfile3);
    weekProfileBuilder2.withSaturday(dayProfile4);
    if (testAnomaly == TestAnomaly.FIFTH_DAY) {
      // an overall fifth day profile will be added
      weekProfileBuilder2.withSunday(dayProfile5);
    } else {
      weekProfileBuilder2.withSunday(dayProfile4);
    }
    final WeekProfileDto weekProfileDto2 = weekProfileBuilder2.build();

    String seasonProfileName = "Season_2";
    if (testAnomaly == TestAnomaly.DUPLICATE_SEASON) {
      // a second season with name 'Season_1' will be created with a different week profile
      seasonProfileName = "Season_1";
    }

    WeekProfileDto weekProfileDto = weekProfileDto2;
    if (testAnomaly == TestAnomaly.MULTIPLE_USES_SAME_WEEK) {
      // the same week profile is used in multiple seasons
      weekProfileDto = weekProfileDto1;
    }
    final SeasonProfileDto seasonProfileDto2 =
        new SeasonProfileDto(seasonProfileName, new CosemDateTimeDto(), weekProfileDto);

    final List<SeasonProfileDto> seasonList = new ArrayList<>();
    seasonList.addAll(Arrays.asList(seasonProfileDto1, seasonProfileDto2));

    if (testAnomaly == TestAnomaly.FIFTH_SEASON) {
      // a third, fourth and fifth season will be added to the activity calendar
      seasonList.addAll(
          Arrays.asList(
              new SeasonProfileDto(
                  "Season_3",
                  new CosemDateTimeDto(),
                  WeekProfileDto.newBuilder().withWeekProfileName("Week___3").build()),
              new SeasonProfileDto(
                  "Season_4",
                  new CosemDateTimeDto(),
                  WeekProfileDto.newBuilder().withWeekProfileName("Week___4").build()),
              new SeasonProfileDto(
                  "Season_5",
                  new CosemDateTimeDto(),
                  WeekProfileDto.newBuilder().withWeekProfileName("Week___5").build())));
    }

    return new ActivityCalendarDto("Calendar", new CosemDateTimeDto(), seasonList);
  }

  private WeekProfileDto createWeekProfile(
      final String weekProfileName,
      final DayProfileDto dayProfile1,
      final DayProfileDto dayProfile2) {
    final Builder weekProfileBuilder1 = WeekProfileDto.newBuilder();
    weekProfileBuilder1.withWeekProfileName(weekProfileName);
    weekProfileBuilder1.withMonday(dayProfile1);
    weekProfileBuilder1.withTuesday(dayProfile1);
    weekProfileBuilder1.withWednesday(dayProfile1);
    weekProfileBuilder1.withThursday(dayProfile1);
    weekProfileBuilder1.withFriday(dayProfile1);
    weekProfileBuilder1.withSaturday(dayProfile2);
    weekProfileBuilder1.withSunday(dayProfile2);
    final WeekProfileDto weekProfileDto1 = weekProfileBuilder1.build();
    return weekProfileDto1;
  }

  @Test
  void testValidateActivityCalendar() {
    assertDoesNotThrow(
        () -> ActivityCalendarValidator.validate(this.createActivityCalendarDto(null)));
  }

  @Test
  void testNotUniqueSeasons() {
    final FunctionalException exception =
        assertThrows(
            FunctionalException.class,
            () ->
                ActivityCalendarValidator.validate(
                    this.createActivityCalendarDto(TestAnomaly.DUPLICATE_SEASON)));
    assertThat(exception).getCause().hasMessage("Not all seasons have a unique name");
  }

  @Test
  void testTooManySeasons() {
    final FunctionalException exception =
        assertThrows(
            FunctionalException.class,
            () ->
                ActivityCalendarValidator.validate(
                    this.createActivityCalendarDto(TestAnomaly.FIFTH_SEASON)));
    assertThat(exception).getCause().hasMessageStartingWith("Maximum number of seasons supported");
  }

  @Test
  void testTooManyDays() {
    final FunctionalException exception =
        assertThrows(
            FunctionalException.class,
            () ->
                ActivityCalendarValidator.validate(
                    this.createActivityCalendarDto(TestAnomaly.FIFTH_DAY)));
    assertThat(exception).getCause().hasMessageStartingWith("Maximum number of days supported");
  }

  @Test
  void testNotUniqueWeeks() {
    final FunctionalException exception =
        assertThrows(
            FunctionalException.class,
            () ->
                ActivityCalendarValidator.validate(
                    this.createActivityCalendarDto(TestAnomaly.DUPLICATE_WEEK)));
    assertThat(exception)
        .getCause()
        .hasMessage("Weekprofiles with same name have different day profiles");
  }

  @Test
  void testNotUniqueDays() {
    final FunctionalException exception =
        assertThrows(
            FunctionalException.class,
            () ->
                ActivityCalendarValidator.validate(
                    this.createActivityCalendarDto(TestAnomaly.DUPLICATE_DAY)));
    assertThat(exception)
        .getCause()
        .hasMessage("Dayprofiles with same dayid have different switching points");
  }

  @Test
  void testMultipleUsesOfSameWeek() {
    assertDoesNotThrow(
        () ->
            ActivityCalendarValidator.validate(
                this.createActivityCalendarDto(TestAnomaly.MULTIPLE_USES_SAME_WEEK)));
  }
}
