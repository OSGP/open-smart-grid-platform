/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RequestParameterFactory;
import org.openmuc.jdlms.SetRequestParameter;
import org.osgp.adapter.protocol.dlms.application.mapping.DayProfileConverter;
import org.osgp.adapter.protocol.dlms.application.mapping.SeasonProfileConverter;
import org.osgp.adapter.protocol.dlms.application.mapping.WeekProfileConverter;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.dto.valueobjects.smartmetering.DayProfile;
import com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile;
import com.alliander.osgp.dto.valueobjects.smartmetering.WeekProfile;

@Component()
public class SetActivityCalendarCommandExecutor implements CommandExecutor<ActivityCalendar, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetActivityCalendarCommandExecutor.class);

    private static final int CLASS_ID = 20;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.13.0.0.255");

    @Autowired
    private SeasonProfileConverter seasonProfileConverter;

    @Autowired
    private WeekProfileConverter weekProfileConverter;

    @Autowired
    private DayProfileConverter dayProfileConverter;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public AccessResultCode execute(final ClientConnection conn, final ActivityCalendar activityCalendar)
            throws IOException, ProtocolAdapterException {
        LOGGER.debug("SetActivityCalendarCommandExecutor.execute {} called!! :-)", activityCalendar.getCalendarName());

        // Set calendar Name
        final SetRequestParameter activityCalendarRequest = this.getCalendarNameRequest(activityCalendar);

        // Set seasons
        final List<SeasonProfile> seasonProfileList = activityCalendar.getSeasonProfileList();
        final SetRequestParameter seasonsRequest = this.getSeasonsRequest(conn, seasonProfileList);

        // Set weeks
        final HashSet<WeekProfile> weekProfileSet = this.getWeekProfileSet(seasonProfileList);
        final SetRequestParameter weeksRequest = this.getWeeksRequest(conn, weekProfileSet);
        // Set days
        final SetRequestParameter dayRequest = this.getDaysRequest(conn, this.getDayProfileSet(weekProfileSet));

        final Map<String, AccessResultCode> allAccessResultCodeMap = new HashMap<>();

        List<AccessResultCode> resultCode = conn
                .set(DlmsHelperService.LONG_CONNECTION_TIMEOUT, activityCalendarRequest);
        allAccessResultCodeMap.put("Activity Calender Request", resultCode.get(0));

        LOGGER.info("WRITING SEASONS");
        resultCode = conn.set(DlmsHelperService.LONG_CONNECTION_TIMEOUT, seasonsRequest);
        allAccessResultCodeMap.put("Seasons Request", resultCode.get(0));

        LOGGER.info("WRITING DAYS");
        resultCode = conn.set(DlmsHelperService.LONG_CONNECTION_TIMEOUT, dayRequest);
        allAccessResultCodeMap.put("Day Request", resultCode.get(0));

        LOGGER.info("WRITING WEEKS");
        resultCode = conn.set(DlmsHelperService.LONG_CONNECTION_TIMEOUT, weeksRequest);
        allAccessResultCodeMap.put("Weeks Request", resultCode.get(0));

        final Map<String, AccessResultCode> failureAccessResultMap = new HashMap<>();

        for (final Map.Entry<String, AccessResultCode> entry : allAccessResultCodeMap.entrySet()) {
            final String key = entry.getKey();
            final AccessResultCode value = entry.getValue();

            if (AccessResultCode.SUCCESS != value) {
                failureAccessResultMap.put(key, value);
            }
        }

        if (!failureAccessResultMap.isEmpty()) {
            this.throwProtocolAdapterException(failureAccessResultMap);
        }

        LOGGER.info("Finished calling conn.set");

        return AccessResultCode.SUCCESS;
    }

    private void throwProtocolAdapterException(final Map<String, AccessResultCode> failureAccessResultMap)
            throws ProtocolAdapterException {

        String keyValues = "";

        for (final Map.Entry<String, AccessResultCode> entry : failureAccessResultMap.entrySet()) {
            final String keyValueString = entry.getKey() + ": " + entry.getValue();
            if ("".equals(keyValues)) {
                keyValues = keyValueString;
            } else {
                keyValues += ", " + keyValueString;
            }
        }

        LOGGER.error("ActivityCalendar: Requests failed for: {}", keyValues);
        throw new ProtocolAdapterException("ActivityCalendar: Requests failed for: " + keyValues);
    }

    private SetRequestParameter getCalendarNameRequest(final ActivityCalendar activityCalendar) {
        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 6);
        final DataObject obj = DataObject.newOctetStringData(activityCalendar.getCalendarName().getBytes());
        return factory.createSetRequestParameter(obj);
    }

    private SetRequestParameter getDaysRequest(final ClientConnection conn, final HashSet<DayProfile> dayProfileSet)
            throws IOException {
        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 9);
        final DataObject dayArray = this.dayProfileConverter.convert(dayProfileSet);

        LOGGER.info("DayRequest to set is: {}", this.dlmsHelperService.getDebugInfo(dayArray));

        return factory.createSetRequestParameter(dayArray);
    }

    /**
     * get all day profiles from all the week profiles
     *
     * @param weekProfileSet
     * @return
     */
    private HashSet<DayProfile> getDayProfileSet(final HashSet<WeekProfile> weekProfileSet) {
        final HashSet<DayProfile> dayProfileHashSet = new HashSet<>();

        for (final WeekProfile weekProfile : weekProfileSet) {
            dayProfileHashSet.addAll(weekProfile.getAllDaysAsList());
        }

        return dayProfileHashSet;
    }

    private SetRequestParameter getWeeksRequest(final ClientConnection conn, final HashSet<WeekProfile> weekProfileSet)
            throws IOException {

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 8);
        final DataObject weekArray = this.weekProfileConverter.convert(weekProfileSet);

        LOGGER.info("WeekArray to set is: {}", this.dlmsHelperService.getDebugInfo(weekArray));

        return factory.createSetRequestParameter(weekArray);
    }

    private HashSet<WeekProfile> getWeekProfileSet(final List<SeasonProfile> seasonProfileList) {
        // Use HashSet to ensure that unique WeekProfiles are returned. For
        // there can be duplicates.
        final HashSet<WeekProfile> weekProfileSet = new HashSet<>();

        for (final SeasonProfile seasonProfile : seasonProfileList) {
            weekProfileSet.add(seasonProfile.getWeekProfile());
        }
        return weekProfileSet;
    }

    private SetRequestParameter getSeasonsRequest(final ClientConnection conn,
            final List<SeasonProfile> seasonProfileList) throws IOException {

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 7);

        final DataObject seasonsArray = this.seasonProfileConverter.convert(seasonProfileList);

        LOGGER.info("getSeasonsRequest: debug output: {}", this.dlmsHelperService.getDebugInfo(seasonsArray));

        final SetRequestParameter request = factory.createSetRequestParameter(seasonsArray);

        return request;
    }

}
