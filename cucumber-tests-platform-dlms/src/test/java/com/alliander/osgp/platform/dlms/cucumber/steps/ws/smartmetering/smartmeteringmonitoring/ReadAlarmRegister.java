/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.junit.Assert.assertTrue;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ReadAlarmRegister extends SmartMeteringStepsBase {
    private static final String PATH_RESULT_ALARMTYPES = "/Envelope/Body/ReadAlarmRegisterResponse/AlarmRegister/AlarmTypes/text()";

    private static final String XPATH_MATCHER_RESULT_ALARMTYPES = "\\w[A-Z]";

    private static final String TEST_SUITE_XML = "SmartmeterMonitoring";
    private static final String TEST_CASE_XML = "192 Read alarm register";
    private static final String TEST_CASE_NAME_REQUEST = "ReadAlarmRegister - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetReadAlarmRegisterResponse - Request 1";

    @When("^the get read alarm register request is received$")
    public void theGetActualMeterReadsRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_LABEL, ScenarioContext.Current().get("DeviceIdentification", Defaults.DEFAULT_DEVICE_IDENTIFICATION).toString());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, ScenarioContext.Current().get("OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the alarm register should be returned$")
    public void theActualMeterReadsResultShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, ScenarioContext.Current().get("CorrelationUid").toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ALARMTYPES,
                XPATH_MATCHER_RESULT_ALARMTYPES));
    }
}
