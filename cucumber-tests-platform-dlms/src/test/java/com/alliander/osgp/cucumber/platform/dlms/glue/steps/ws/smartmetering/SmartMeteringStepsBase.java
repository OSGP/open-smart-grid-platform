/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.Map;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.core.Helpers;
import com.alliander.osgp.cucumber.platform.dlms.SoapUiRunner;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

/**
 * Super class for TestCase runner implementations that communicate with
 * SmartMetering web services. Each Runner will be called from a subclass.
 */
public abstract class SmartMeteringStepsBase extends SoapUiRunner {

    /**
     * Constructor. The steps in this folder use the SmartMetering SoapUI
     * project.
     */
    protected SmartMeteringStepsBase() {
        super("soap-ui-project/Dlms-SoapUI-project.xml");
    }

    /**
     * Because the smartmetering scenarios are missing the verify check for the
     * response from the platform (which would contain the device identification
     * and the correlation uid) this check has been put here. It would be better
     * to make this a separate step, as you definitely want to check this
     * response in my opinion.
     */
    @Deprecated
    @Override
    protected void requestRunner(final TestStepStatus testStepStatus, final Map<String, String> propertiesMap,
            final String testCaseNameRequest, final String testCaseXml, final String testSuiteXml) throws Throwable {

        super.requestRunner(testStepStatus, propertiesMap, testCaseNameRequest, testCaseXml, testSuiteXml);

        final String correlId = this.runXpathResult.getValue(this.response, PATH_CORRELATION_UID);
        if (correlId != null && !correlId.isEmpty()) {
            this.saveCorrelationId();
        }
    }

    /**
     * Saves the returned CorrelationUid in the Scenario related context for
     * further use.
     * 
     * @throws Throwable
     */
    protected void saveCorrelationId() throws Throwable {
        Helpers.saveCorrelationUidInScenarioContext(this.runXpathResult.getValue(this.response, PATH_CORRELATION_UID),
                getString(PROPERTIES_MAP, "OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

    }
}
