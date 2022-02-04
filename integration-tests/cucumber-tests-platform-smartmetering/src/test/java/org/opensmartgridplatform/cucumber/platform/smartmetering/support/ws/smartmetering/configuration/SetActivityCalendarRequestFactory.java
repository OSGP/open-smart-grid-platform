/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.DayProfileActionType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.DayProfileActionsType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.DayType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SeasonType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SeasonsType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.WeekType;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetActivityCalendarRequestFactory {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SetActivityCalendarRequestFactory.class);

  private SetActivityCalendarRequestFactory() {
    // Private constructor for utility class
  }

  public static SetActivityCalendarRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final SetActivityCalendarRequest setActivityCalendarRequest = new SetActivityCalendarRequest();
    setActivityCalendarRequest.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    final ActivityCalendarType activityCalendarType = fetchActivityCalendar();

    final SetActivityCalendarRequestData setActivityCalendarRequestData =
        new SetActivityCalendarRequestData();
    setActivityCalendarRequestData.setActivityCalendar(activityCalendarType);

    setActivityCalendarRequest.setActivityCalendarData(setActivityCalendarRequestData);

    return setActivityCalendarRequest;
  }

  public static SetActivityCalendarAsyncRequest fromScenarioContext() {
    final SetActivityCalendarAsyncRequest setActivityCalendarAsyncRequest =
        new SetActivityCalendarAsyncRequest();
    setActivityCalendarAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    setActivityCalendarAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return setActivityCalendarAsyncRequest;
  }

  private static ActivityCalendarType fetchActivityCalendar() {
    final ActivityCalendarType activityCalendarType = new ActivityCalendarType();
    final String activatePassiveCalendarTime = "FFFFFFFEFFFFFFFFFF000000";
    final byte[] decodedActivatePassiveCalendarTime;
    try {
      decodedActivatePassiveCalendarTime = Hex.decodeHex(activatePassiveCalendarTime.toCharArray());
      activityCalendarType.setActivatePassiveCalendarTime(decodedActivatePassiveCalendarTime);
    } catch (final DecoderException e) {
      LOGGER.error("Unexpected exception during decode activatePassiveCalendarTime.", e);
    }

    activityCalendarType.setCalendarName("Cal Name");

    final SeasonsType seasonsType = new SeasonsType();
    final List<SeasonType> seasons = new ArrayList<>();
    final SeasonType season = new SeasonType();
    season.setSeasonProfileName("1");
    final String seasonStart = "FFFF0C03FFFFFFFFFF000000";
    final byte[] decodedSeasonStart;
    try {
      decodedSeasonStart = Hex.decodeHex(seasonStart.toCharArray());
      season.setSeasonStart(decodedSeasonStart);
    } catch (final DecoderException e) {
      LOGGER.error("Unexpected exception during decode seasonStart.", e);
    }

    final WeekType weekType = new WeekType();

    // Monday
    final DayType dayTypeMonday = new DayType();
    dayTypeMonday.setDayId(BigInteger.valueOf(1L));
    final List<DayProfileActionType> dayProfileActionTypes = new ArrayList<>();
    final DayProfileActionType dayProfileActionType = new DayProfileActionType();
    dayProfileActionType.setScriptSelector(BigInteger.valueOf(1L));
    final String startTime = "06050000";
    byte[] decodedStartTime = null;
    try {
      decodedStartTime = Hex.decodeHex(startTime.toCharArray());
      dayProfileActionType.setStartTime(decodedStartTime);
    } catch (final DecoderException e) {
      LOGGER.error("Unexpected exception during decode startTime from dayProfileActionType.", e);
    }

    dayProfileActionTypes.add(dayProfileActionType);
    final DayProfileActionsType dayProfilesActionType = new DayProfileActionsType();
    dayProfilesActionType.getDayProfileAction().addAll(dayProfileActionTypes);
    dayTypeMonday.setDaySchedule(dayProfilesActionType);

    // Tuesday
    final DayType dayTypeTuesday = new DayType();
    dayTypeTuesday.setDayId(BigInteger.valueOf(2L));
    dayProfileActionType.setScriptSelector(BigInteger.valueOf(1L));
    dayProfileActionType.setStartTime(decodedStartTime);
    dayProfileActionTypes.add(dayProfileActionType);
    dayProfilesActionType.getDayProfileAction().addAll(dayProfileActionTypes);
    dayTypeTuesday.setDaySchedule(dayProfilesActionType);

    // Wednesday
    final DayType dayTypeWednesday = new DayType();
    dayTypeWednesday.setDayId(BigInteger.valueOf(3L));
    dayProfileActionType.setScriptSelector(BigInteger.valueOf(1L));
    dayProfileActionType.setStartTime(decodedStartTime);
    dayProfileActionTypes.add(dayProfileActionType);
    dayProfilesActionType.getDayProfileAction().addAll(dayProfileActionTypes);
    dayTypeWednesday.setDaySchedule(dayProfilesActionType);

    // Thursday
    final DayType dayTypeThursday = new DayType();
    dayTypeThursday.setDayId(BigInteger.valueOf(4L));
    dayProfileActionType.setScriptSelector(BigInteger.valueOf(1L));
    dayProfileActionType.setStartTime(decodedStartTime);
    dayProfileActionTypes.add(dayProfileActionType);
    dayProfilesActionType.getDayProfileAction().addAll(dayProfileActionTypes);
    dayTypeThursday.setDaySchedule(dayProfilesActionType);

    // Friday
    final DayType dayTypeFriday = new DayType();
    dayTypeFriday.setDayId(BigInteger.valueOf(5L));
    dayProfileActionType.setScriptSelector(BigInteger.valueOf(1L));
    dayProfileActionType.setStartTime(decodedStartTime);
    dayProfileActionTypes.add(dayProfileActionType);
    dayProfilesActionType.getDayProfileAction().addAll(dayProfileActionTypes);
    dayTypeFriday.setDaySchedule(dayProfilesActionType);

    // Saturday
    final DayType dayTypeSaturday = new DayType();
    dayTypeSaturday.setDayId(BigInteger.valueOf(6L));
    dayProfileActionType.setScriptSelector(BigInteger.valueOf(1L));
    dayProfileActionType.setStartTime(decodedStartTime);
    dayProfileActionTypes.add(dayProfileActionType);
    dayProfilesActionType.getDayProfileAction().addAll(dayProfileActionTypes);
    dayTypeSaturday.setDaySchedule(dayProfilesActionType);

    // Sunday
    final DayType dayTypeSunday = new DayType();
    dayTypeSunday.setDayId(BigInteger.valueOf(7L));
    dayProfileActionType.setScriptSelector(BigInteger.valueOf(1L));
    dayProfileActionType.setStartTime(decodedStartTime);
    dayProfileActionTypes.add(dayProfileActionType);
    dayProfilesActionType.getDayProfileAction().addAll(dayProfileActionTypes);
    dayTypeSunday.setDaySchedule(dayProfilesActionType);

    weekType.setMonday(dayTypeMonday);
    weekType.setTuesday(dayTypeTuesday);
    weekType.setWednesday(dayTypeWednesday);
    weekType.setThursday(dayTypeThursday);
    weekType.setFriday(dayTypeFriday);
    weekType.setSaturday(dayTypeSaturday);
    weekType.setSunday(dayTypeSunday);

    weekType.setWeekProfileName("1");
    season.setWeekProfile(weekType);
    seasons.add(season);
    seasonsType.getSeason().addAll(seasons);

    activityCalendarType.setSeasonProfile(seasonsType);

    return activityCalendarType;
  }
}
