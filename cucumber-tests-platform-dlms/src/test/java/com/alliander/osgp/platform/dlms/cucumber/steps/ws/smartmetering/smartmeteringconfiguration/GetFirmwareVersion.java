/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertTrue;

import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetFirmwareVersion extends SmartMeteringStepsBase {
    private static final String PATH_RESULT_STATUS = "/Envelope/Body/GetFirmwareVersionResponse/Result/text()";
    private static final String PATH_RESULT_FIRMWAREVERSION_TYPE = "/Envelope/Body/GetFirmwareVersionResponse/FirmwareVersion/type";
    private static final String PATH_RESULT_FIRMWAREVERSION_VERSION = "/Envelope/Body/GetFirmwareVersionResponse/FirmwareVersion/version";

    private static final String XPATH_MATCHER_FIRMWAREVERSION_TYPE = "(MODULE_|COMMUNICATION_MODULE_)?ACTIVE_FIRMWARE";
    private static final String XPATH_MATCHER_FIRMWAREVERSION_VERSION = ".+";

    private static final String TEST_SUITE_XML = "SmartmeterConfiguration";
    private static final String TEST_CASE_XML = "261 Retrieve firmware version";
    private static final String TEST_CASE_NAME_REQUEST = "GetFirmwareVersion - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetGetFirmwareVersionResponse - Request 1";

    @When("^the get firmware version request is received$")
    public void theGetFirmwareVersionRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_LABEL, ScenarioContext.Current().get("DeviceIdentification", Defaults.DEFAULT_DEVICE_IDENTIFICATION).toString());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, ScenarioContext.Current().get("OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the firmware version result should be returned$")
    public void theFirmwareVersionResultShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, ScenarioContext.Current().get("CorrelationUid").toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_STATUS, Defaults.EXPECTED_RESULT));

        this.runXpathResult.assertXpathList(this.response, PATH_RESULT_FIRMWAREVERSION_TYPE,
                XPATH_MATCHER_FIRMWAREVERSION_TYPE, 3);
        this.runXpathResult.assertXpathList(this.response, PATH_RESULT_FIRMWAREVERSION_VERSION,
                XPATH_MATCHER_FIRMWAREVERSION_VERSION, 3);
    }

}
