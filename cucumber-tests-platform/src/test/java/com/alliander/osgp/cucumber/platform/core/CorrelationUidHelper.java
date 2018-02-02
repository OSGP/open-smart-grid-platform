package com.alliander.osgp.cucumber.platform.core;

import org.springframework.util.Assert;

import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;

public class CorrelationUidHelper {

    /**
     * Check the correlationUid in the response and save it in the current
     * scenarioContext.
     *
     * @param response
     *            The response to find the correlationUid in.
     * @param organizationIdentification
     *            The organizationIdentifier used; if null or empty, the default
     *            test-org will be used.
     * @throws Throwable
     */
    public static void saveCorrelationUidInScenarioContext(final String correlationUid,
            String organizationIdentification) throws Throwable {
        if (organizationIdentification == null || organizationIdentification.isEmpty()) {
            organizationIdentification = PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION;
        }

        // Validate the correlation-id starts with correct organization
        Assert.isTrue(correlationUid.startsWith(organizationIdentification));
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, correlationUid);
    }

}
