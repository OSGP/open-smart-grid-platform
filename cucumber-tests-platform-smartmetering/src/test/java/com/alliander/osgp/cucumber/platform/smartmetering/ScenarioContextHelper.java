package com.alliander.osgp.cucumber.platform.smartmetering;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.AsyncResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;

public class ScenarioContextHelper {
    /**
     * Store the correlationUid and deviceIdentification in the ScenarioContext,
     * given the AsyncResponse
     *
     * @param asyncResponse
     *            The AsyncResponse used to retrieve the values (mentioned
     *            above) to store in the ScenarioContext
     *
     * @throws Throwable
     */
    public static void saveAsyncResponse(final AsyncResponse asyncResponse) throws Throwable {
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
        ScenarioContext.current().put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, asyncResponse.getDeviceIdentification());
    }

}
