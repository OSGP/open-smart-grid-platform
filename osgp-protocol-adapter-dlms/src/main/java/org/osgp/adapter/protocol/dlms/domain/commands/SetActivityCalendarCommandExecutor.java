package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RequestParameterFactory;
import org.openmuc.jdlms.SetRequestParameter;
import org.osgp.adapter.protocol.dlms.application.mapping.ConfigurationMapper;
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
    private ConfigurationMapper configurationMapper;

    @Override
    public AccessResultCode execute(final ClientConnection conn, final ActivityCalendar activityCalendar)
            throws IOException {
        LOGGER.debug("SetActivityCalendarCommandExecutor.execute {} called!! :-)", activityCalendar.getCalendarName());

        this.getValues(conn);

        final AccessResultCode accessResultCode = this.setCalendar(conn, activityCalendar);

        this.getValues(conn);

        return accessResultCode;
    }

    private AccessResultCode setCalendar(final ClientConnection conn, final ActivityCalendar activityCalendar)
            throws IOException {
        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 6);
        final DataObject obj = DataObject.newOctetStringData(activityCalendar.getCalendarName().getBytes());
        final SetRequestParameter request = factory.createSetRequestParameter(obj);
        final List<AccessResultCode> l = conn.set(request);

        AccessResultCode accessResultCode = this.setSeasons(conn, activityCalendar.getSeasonProfileList());
        if (accessResultCode != AccessResultCode.SUCCESS) {
            return accessResultCode;
        }
        final HashSet<WeekProfile> weekProfileSet = this.getWeekProfileSet(activityCalendar.getSeasonProfileList());
        accessResultCode = this.setWeeks(conn, weekProfileSet);
        if (accessResultCode != AccessResultCode.SUCCESS) {
            return accessResultCode;
        }
        return this.setDays(conn, this.getDayProfileSet(weekProfileSet));
    }

    private AccessResultCode setDays(final ClientConnection conn, final HashSet<DayProfile> dayProfileSet)
            throws IOException {
        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 9);

        final DataObject dayArray = this.configurationMapper.map(dayProfileSet, DataObject.class);

        final SetRequestParameter request = factory.createSetRequestParameter(dayArray);
        final List<AccessResultCode> l = conn.set(request);
        return l.get(0);

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

    private AccessResultCode setWeeks(final ClientConnection conn, final HashSet<WeekProfile> weekProfileSet)
            throws IOException {

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 8);

        final DataObject weekArray = this.configurationMapper.map(weekProfileSet, DataObject.class);

        final SetRequestParameter request = factory.createSetRequestParameter(weekArray);
        final List<AccessResultCode> l = conn.set(request);
        return l.get(0);
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

    private AccessResultCode setSeasons(final ClientConnection conn, final List<SeasonProfile> seasonProfileList)
            throws IOException {

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 7);

        final DataObject seasonsArray = this.configurationMapper.map(seasonProfileList, DataObject.class);

        final SetRequestParameter request = factory.createSetRequestParameter(seasonsArray);
        final List<AccessResultCode> l = conn.set(request);
        return l.get(0);
    }

    /**
     * Method for debugging purposes. Can be removed.
     *
     * @param conn
     * @throws IOException
     */
    private void getValues(final ClientConnection conn) throws IOException {
        final GetRequestParameter reqParamC = new GetRequestParameter(CLASS_ID, OBIS_CODE, 6);
        final GetRequestParameter reqParamS = new GetRequestParameter(CLASS_ID, OBIS_CODE, 7);
        final GetRequestParameter reqParamW = new GetRequestParameter(CLASS_ID, OBIS_CODE, 8);
        final GetRequestParameter reqParamD = new GetRequestParameter(CLASS_ID, OBIS_CODE, 9);
        final GetRequestParameter reqParamT = new GetRequestParameter(CLASS_ID, OBIS_CODE, 10);
        final List<GetResult> getResultListC = conn.get(reqParamC);
        final List<GetResult> getResultListS = conn.get(reqParamS);
        final List<GetResult> getResultListW = conn.get(reqParamW);
        final List<GetResult> getResultListD = conn.get(reqParamD);
        final List<GetResult> getResultListT = conn.get(reqParamT);
        LOGGER.debug("...");
    }
}
