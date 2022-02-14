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

    final DayType normalDay = createDayType(1, "06050000");
    final DayType weekendDay = createDayType(2, "07050000");

    weekType.setMonday(normalDay);
    weekType.setTuesday(normalDay);
    weekType.setWednesday(normalDay);
    weekType.setThursday(normalDay);
    weekType.setFriday(normalDay);
    weekType.setSaturday(weekendDay);
    weekType.setSunday(weekendDay);

    weekType.setWeekProfileName("1");
    season.setWeekProfile(weekType);
    seasons.add(season);
    seasonsType.getSeason().addAll(seasons);

    activityCalendarType.setSeasonProfile(seasonsType);

    return activityCalendarType;
  }

  private static DayType createDayType(final int dayId, final String startTime) {
    final DayType dayType = new DayType();
    dayType.setDayId(BigInteger.valueOf(dayId));
    final List<DayProfileActionType> dayProfileActionTypes = new ArrayList<>();
    final DayProfileActionType dayProfileActionType = new DayProfileActionType();
    dayProfileActionType.setScriptSelector(BigInteger.valueOf(1L));
    final byte[] decodedStartTime;
    try {
      decodedStartTime = Hex.decodeHex(startTime.toCharArray());
      dayProfileActionType.setStartTime(decodedStartTime);
    } catch (final DecoderException e) {
      LOGGER.error("Unexpected exception during decode startTime from dayProfileActionType.", e);
    }
    dayProfileActionTypes.add(dayProfileActionType);
    final DayProfileActionsType dayProfilesActionType = new DayProfileActionsType();
    dayProfilesActionType.getDayProfileAction().addAll(dayProfileActionTypes);
    dayType.setDaySchedule(dayProfilesActionType);

    return dayType;
  }
}
