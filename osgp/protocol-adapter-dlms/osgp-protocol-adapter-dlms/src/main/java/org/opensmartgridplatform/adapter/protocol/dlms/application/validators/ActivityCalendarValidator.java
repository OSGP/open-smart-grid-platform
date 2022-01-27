/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.validators;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActivityCalendarDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DayProfileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SeasonProfileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.WeekProfileDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;

public class ActivityCalendarValidator {

  private static final int MAX_NUMBER_OF_SEASONS = 4;
  private static final int MAX_NUMBER_OF_WEEKS = 4;
  private static final int MAX_NUMBER_OF_DAYS = 4;

  public static void validate(final ActivityCalendarDto activityCalendarDto)
      throws FunctionalException {
    final List<SeasonProfileDto> seasonProfiles = activityCalendarDto.getSeasonProfileList();

    // Check the number of seasons
    if (seasonProfiles.size() > MAX_NUMBER_OF_SEASONS) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.PROTOCOL_DLMS,
          new IllegalArgumentException(
              "Maximum number of seasons supported ("
                  + MAX_NUMBER_OF_SEASONS
                  + ") is exceeded: "
                  + seasonProfiles.size()));
    }
    // Check if each season has an unique season name
    final long numberOfUniqueSeasonNames =
        seasonProfiles.stream().map(SeasonProfileDto::getSeasonProfileName).distinct().count();
    if (seasonProfiles.size() > numberOfUniqueSeasonNames) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.PROTOCOL_DLMS,
          new IllegalArgumentException("Not all seasons have a unique name"));
    }

    // Get list of weeks of all seasons
    final List<WeekProfileDto> weekProfiles =
        seasonProfiles.stream().map(SeasonProfileDto::getWeekProfile).collect(Collectors.toList());

    // Check if number of unique weeks (with unique week profile name)
    final long numberOfUniqueWeekNames =
        weekProfiles.stream().map(WeekProfileDto::getWeekProfileName).distinct().count();
    if (numberOfUniqueWeekNames > MAX_NUMBER_OF_WEEKS) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.PROTOCOL_DLMS,
          new IllegalArgumentException(
              "Maximum number of weeks supported ("
                  + MAX_NUMBER_OF_WEEKS
                  + ") is exceeded: "
                  + numberOfUniqueWeekNames));
    }

    // Check if no weekProfiles exist with identical weekProfileName but with different DaySchedules
    final Map<String, List<WeekProfileDto>> weekProfilesByName =
        weekProfiles.stream()
            .collect(
                Collectors.groupingBy(WeekProfileDto::getWeekProfileName, Collectors.toList()));
    for (final List<WeekProfileDto> weekProfile : weekProfilesByName.values()) {
      final long numberOfUniqueWeekProfilesByDayId = weekProfile.stream().distinct().count();
      if (numberOfUniqueWeekProfilesByDayId > 1) {
        throw new FunctionalException(
            FunctionalExceptionType.VALIDATION_ERROR,
            ComponentType.PROTOCOL_DLMS,
            new IllegalArgumentException(
                "Weekprofiles with same name have different day profiles"));
      }
    }

    // Check -over all seasons- the number of unique dayprofiles. The dayId make the dayprofile
    // unique
    final List<DayProfileDto> dayProfiles =
        weekProfiles.stream().map(WeekProfileDto::getMonday).collect(Collectors.toList());
    dayProfiles.addAll(
        weekProfiles.stream().map(WeekProfileDto::getTuesday).collect(Collectors.toList()));
    dayProfiles.addAll(
        weekProfiles.stream().map(WeekProfileDto::getWednesday).collect(Collectors.toList()));
    dayProfiles.addAll(
        weekProfiles.stream().map(WeekProfileDto::getThursday).collect(Collectors.toList()));
    dayProfiles.addAll(
        weekProfiles.stream().map(WeekProfileDto::getFriday).collect(Collectors.toList()));
    dayProfiles.addAll(
        weekProfiles.stream().map(WeekProfileDto::getSaturday).collect(Collectors.toList()));
    dayProfiles.addAll(
        weekProfiles.stream().map(WeekProfileDto::getSunday).collect(Collectors.toList()));
    final long numberOfUniqueDayIds =
        dayProfiles.stream().map(DayProfileDto::getDayId).distinct().count();
    if (numberOfUniqueDayIds > MAX_NUMBER_OF_DAYS) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.PROTOCOL_DLMS,
          new IllegalArgumentException(
              "Maximum number of days supported ("
                  + MAX_NUMBER_OF_DAYS
                  + ") is exceeded: "
                  + numberOfUniqueDayIds));
    }

    // Check if no dayProfiles exist with identical dayId but with different DayProfileActions
    final Map<Integer, List<DayProfileDto>> daysById =
        dayProfiles.stream()
            .collect(Collectors.groupingBy(DayProfileDto::getDayId, Collectors.toList()));
    for (final List<DayProfileDto> dayProfile : daysById.values()) {
      final long numberOfUniqueDayProfilesByDayId = dayProfile.stream().distinct().count();
      if (numberOfUniqueDayProfilesByDayId > 1) {
        throw new FunctionalException(
            FunctionalExceptionType.VALIDATION_ERROR,
            ComponentType.PROTOCOL_DLMS,
            new IllegalArgumentException(
                "Dayprofiles with same dayid have different switching points"));
      }
    }
  }
}
