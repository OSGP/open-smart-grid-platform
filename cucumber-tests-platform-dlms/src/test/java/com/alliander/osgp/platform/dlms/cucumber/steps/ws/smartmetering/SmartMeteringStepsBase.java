/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.SoapUiRunner;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

/**
 * Super class for TestCase runner implementations that communicate with
 * SmartMetering web services. Each Runner will be called from a subclass.
 */
public abstract class SmartMeteringStepsBase extends SoapUiRunner {

    /**
     * Constructor.
     * The steps in this folder use the SmartMetering SoapUI project.
     */
    protected SmartMeteringStepsBase() {
    	super("soap-ui-project/Dlms-SoapUI-project.xml");
    }
    
    /**
     * Because the smartmetering scenarios are missing the verify check for the response from the platform (which 
     * would contain the device identification and the correlation uid) this check has been put here.
     * It would be better to make this a separate step, as you definitely want to check this response in my opinion.
     */
    @Override
    protected void requestRunner(final TestStepStatus testStepStatus, final Map<String, String> propertiesMap,
            final String testCaseNameRequest, final String testCaseXml, final String testSuiteXml) throws Throwable {

        super.requestRunner(testStepStatus, propertiesMap, testCaseNameRequest, testCaseXml, testSuiteXml);

        // Save the returned CorrelationUid in the Scenario related context for further use.
        saveCorrelationUidInScenarioContext(
            this.runXpathResult.getValue(this.response, PATH_CORRELATION_UID),
            getString(propertiesMap, "OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    }
}
