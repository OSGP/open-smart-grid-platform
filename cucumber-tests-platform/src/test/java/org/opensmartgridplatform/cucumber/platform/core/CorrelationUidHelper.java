package org.opensmartgridplatform.cucumber.platform.core;

import org.springframework.util.Assert;

import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;

public class CorrelationUidHelper {

    /**
     * Check the correlationUid in the response and save it in the current
     * scenarioContext.
     *
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
