package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.DataObject;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RequestParameterFactory;
import org.openmuc.jdlms.SetRequestParameter;
import org.osgp.adapter.protocol.dlms.application.mapping.DayProfileConverter;
import org.osgp.adapter.protocol.dlms.application.mapping.SeasonProfileConverter;
import org.osgp.adapter.protocol.dlms.application.mapping.WeekProfileConverter;
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
            throws IOException {
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

        // Set activation time to now.
        final SetRequestParameter activateTimeNowRequest = this.getActivateTimeNow();

        LOGGER.info("Calling the DLMS library conn.set with timeout of {}", DlmsHelperService.LONG_CONNECTION_TIMEOUT);

        final List<AccessResultCode> accessResultCodeList = conn.set(DlmsHelperService.LONG_CONNECTION_TIMEOUT,
                activityCalendarRequest, seasonsRequest, weeksRequest, dayRequest, activateTimeNowRequest);

        LOGGER.info("Finished calling conn.set");

        for (final AccessResultCode arc : accessResultCodeList) {
            if (AccessResultCode.SUCCESS != arc) {
                LOGGER.warn("Zero or more requests for setting ActivityCalendar failed");
                return arc;
            }
        }

        return AccessResultCode.SUCCESS;
    }

    private SetRequestParameter getActivateTimeNow() {
        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 10);
        final DateTime datetime = new DateTime();
        final DataObject seasonStartObject = this.dlmsHelperService.asDataObject(datetime);
        final SetRequestParameter request = factory.createSetRequestParameter(seasonStartObject);

        return request;
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

        final AccessResultCode accesResultCode = AccessResultCode.SUCCESS;
        final SetRequestParameter request = factory.createSetRequestParameter(seasonsArray);

        return request;
    }

    private enum CalendarElementsEnum {
        LOGICAL_NAME(1),
        CALENDAR_NAME_ACTIVE(2),
        SEASON_PROFILE_ACTIVE(3),
        WEEK_PROFILE_TABLE(4),
        DAY_PROFILE_TABLE_ACTIVE(5),
        CALENDAR_NAME_PASSIVE(6),
        SEASON_PROFILE_PASSIVE(7),
        WEEK_PROFILE_TABLE_PASSIVE(8),
        DAY_PROFILE_TABLE_PASSIVE(9),
        ACTIVATE_PASSIVE_CALENDAR_TIME(10);
        private int num;

        CalendarElementsEnum(final int i) {
            this.num = i;
        }

        public static CalendarElementsEnum getIt(final int i) {
            for (final CalendarElementsEnum e : CalendarElementsEnum.values()) {
                if (e.num == i) {
                    return e;
                }
            }
            return null;
        }
    }

    /**
     * Method for debugging purposes. Can be removed.
     *
     * @param conn
     * @throws IOException
     */
    private void printAllValues(final ClientConnection conn) throws IOException {

        for (int i = 1; i <= 10; i++) {
            final GetRequestParameter reqParam = new GetRequestParameter(CLASS_ID, OBIS_CODE, i);
            final List<GetResult> getResultList = conn.get(reqParam);
            LOGGER.info("\nFFFFFFor " + CalendarElementsEnum.getIt(i) + " result is: \n"
                    + this.dlmsHelperService.getDebugInfo(getResultList.get(0).resultData()));
        }

    }
}
