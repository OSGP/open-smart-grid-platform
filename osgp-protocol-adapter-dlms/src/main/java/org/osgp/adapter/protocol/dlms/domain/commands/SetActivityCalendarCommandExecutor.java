package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.GetRequestParameter;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar;

@Component()
public class SetActivityCalendarCommandExecutor implements CommandExecutor<ActivityCalendar, AccessResultCode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetActivityCalendarCommandExecutor.class);

    private static final int CLASS_ID = 20;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.13.0.0.255");
    private static final int ATTRIBUTE_ID = 2;

    @Override
    public AccessResultCode execute(final ClientConnection conn, final ActivityCalendar activityCalendar)
            throws IOException {
        LOGGER.debug("SetActivityCalendarCommandExecutor.execute {} called!! :-)", activityCalendar.getCalendarName());

        // final RequestParameterFactory factory = new
        // RequestParameterFactory(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

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
        return AccessResultCode.SUCCESS;
        // final DataObject obj = DataObject.newUInteger32Data(100l);

        // final SetRequestParameter request =
        // factory.createSetRequestParameter(obj);
        // final List<AccessResultCode> l = conn.set(request);
        // final AccessResultCode r = l.get(0);
        // return r;
        // return conn.set(request).get(0);
    }
}
