package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.dto.valueobjects.smartmetering.SeasonProfile;
import com.alliander.osgp.dto.valueobjects.smartmetering.WeekProfile;

@Component()
public class SetActivityCalendarCommandExecutor implements CommandExecutor<ActivityCalendar, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetActivityCalendarCommandExecutor.class);

    private static final int CLASS_ID = 20;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.13.0.0.255");
    private static final int ATTRIBUTE_ID = 2;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public AccessResultCode execute(final ClientConnection conn, final ActivityCalendar activityCalendar)
            throws IOException {
        LOGGER.debug("SetActivityCalendarCommandExecutor.execute {} called!! :-)", activityCalendar.getCalendarName());

        this.setCalendar(conn, activityCalendar);

        final RequestParameterFactory factoryC = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 6);
        final RequestParameterFactory factoryS = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 7);
        final RequestParameterFactory factoryW = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 8);
        final RequestParameterFactory factoryD = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 9);
        final RequestParameterFactory factoryT = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 10);

        this.getValues(conn);

        final DataObject obj = DataObject.newOctetStringData("fooBar".getBytes());

        final SetRequestParameter request = factoryC.createSetRequestParameter(obj);

        final List<AccessResultCode> l = conn.set(request);

        this.getValues(conn);

        // final AccessResultCode r = l.get(0);
        // return r;
        // return conn.set(request).get(0);
        return AccessResultCode.SUCCESS;
    }

    private void setCalendar(final ClientConnection conn, final ActivityCalendar activityCalendar) throws IOException {
        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 6);
        final DataObject obj = DataObject.newOctetStringData(activityCalendar.getCalendarName().getBytes());
        final SetRequestParameter request = factory.createSetRequestParameter(obj);
        final List<AccessResultCode> l = conn.set(request);

        this.setSeasons(conn, activityCalendar.getSeasonProfileList());
        final HashSet<WeekProfile> weekProfileSet = this.getWeekProfileSet(activityCalendar.getSeasonProfileList());
        this.setWeeks(conn, weekProfileSet);
        // TODO
        // this.setDays(conn, this.getDayProfileSet(weekProfileSet);
    }

    private void setWeeks(final ClientConnection conn, final HashSet<WeekProfile> weekProfileSet) throws IOException {

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 8);
        final DataObject weekArray = DataObject.newArrayData(this.getWeekList(weekProfileSet));
        final SetRequestParameter request = factory.createSetRequestParameter(weekArray);
        final List<AccessResultCode> l = conn.set(request);

    }

    private List<DataObject> getWeekList(final HashSet<WeekProfile> weekProfileSet) {
        final List<DataObject> weekList = new ArrayList<>();
        for (final WeekProfile weekProfile : weekProfileSet) {

            final DataObject weekStructure = DataObject.newStructureData(this.getWeekStructure(weekProfile));

            weekList.add(weekStructure);
        }
        return weekList;
    }

    private List<DataObject> getWeekStructure(final WeekProfile weekProfile) {
        final List<DataObject> weekElements = new ArrayList<>();

        weekElements.add(DataObject.newOctetStringData(weekProfile.getWeekProfileName().getBytes()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getMonday().getDayId()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getTuesday().getDayId()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getWednesday().getDayId()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getThursday().getDayId()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getFriday().getDayId()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getSaturday().getDayId()));
        weekElements.add(DataObject.newUInteger32Data(weekProfile.getSunday().getDayId()));

        return weekElements;
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

    private void setSeasons(final ClientConnection conn, final List<SeasonProfile> seasonProfileList)
            throws IOException {

        final RequestParameterFactory factory = new RequestParameterFactory(CLASS_ID, OBIS_CODE, 7);
        final DataObject seasonsArray = DataObject.newArrayData(this.getSeasonList(conn, seasonProfileList));
        final SetRequestParameter request = factory.createSetRequestParameter(seasonsArray);
        final List<AccessResultCode> l = conn.set(request);
    }

    private List<DataObject> getSeasonList(final ClientConnection conn, final List<SeasonProfile> seasonProfileList)
            throws IOException {
        final List<DataObject> seasonList = new ArrayList<>();
        for (final SeasonProfile seasonProfile : seasonProfileList) {
            final DataObject seasonStructure = DataObject.newStructureData(this.getSeason(conn, seasonProfile));
            seasonList.add(seasonStructure);
        }
        return seasonList;
    }

    private List<DataObject> getSeason(final ClientConnection conn, final SeasonProfile seasonProfile)
            throws IOException {
        final List<DataObject> seasonElements = new ArrayList<>();

        seasonProfile.getSeasonProfileName();
        seasonProfile.getSeasonStart();
        seasonProfile.getWeekProfile().getWeekProfileName();

        final DataObject seasonProfileNameObject = DataObject.newOctetStringData(seasonProfile.getSeasonProfileName()
                .getBytes());
        seasonElements.add(seasonProfileNameObject);

        final DateTime dt = new DateTime(seasonProfile.getSeasonStart());
        final DataObject seasonStartObject = this.dlmsHelperService.asDataObject(dt);
        seasonElements.add(seasonStartObject);

        final DataObject seasonWeekProfileNameObject = DataObject.newOctetStringData(seasonProfile.getWeekProfile()
                .getWeekProfileName().getBytes());
        seasonElements.add(seasonWeekProfileNameObject);

        return seasonElements;
    }

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
