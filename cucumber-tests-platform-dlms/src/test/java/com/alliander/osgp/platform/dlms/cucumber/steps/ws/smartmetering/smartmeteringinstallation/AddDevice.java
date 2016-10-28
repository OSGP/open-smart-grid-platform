/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.smartmeteringinstallation;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AddDevice extends SmartMeteringStepsBase {
    private static final String PATH_RESULT = "/Envelope/Body/AddDeviceResponse/Result/text()";

    private static final String TEST_SUITE_XML = "SmartmeterInstallation";
    private static final String TEST_CASE_XML = "218 Retrieve AddDevice result";
    private static final String TEST_CASE_NAME_REQUEST = "AddDevice - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetAddDeviceResponse - Request 1";

    private static final String DEVICE_COMMUNICATIONMETHOD_LABEL = "CommunicationMethod";
    private static final String DEVICE_COMMUNICATIONPROVIDER_LABEL = "CommunicationProvider";
    private static final String DEVICE_ICCID_LABEL = "ICC_id";
    private static final String DEVICE_DSMRVERSION_LABEL = "DSMR_version";
    private static final String DEVICE_SUPPLIER_LABEL = "Supplier";
    private static final String DEVICE_HLS3ACTIVE_LABEL = "HLS3_active";
    private static final String DEVICE_HLS4ACTIVE_LABEL = "HLS4_active";
    private static final String DEVICE_HLS5ACTIVE_LABEL = "HLS5_active";
    private static final String DEVICE_MASTERKEY_LABEL = "Master_key";

    @When("^receiving an add device request$")
    public void receiving_an_add_device_request(final Map<String, String> requestData) throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_LABEL, getString(requestData, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(DEVICE_TYPE_LABEL, getString(requestData, "DeviceType", Defaults.DEFAULT_DEVICE_TYPE));
        PROPERTIES_MAP.put(DEVICE_COMMUNICATIONMETHOD_LABEL, requestData.get("CommunicationMethod"));
        PROPERTIES_MAP.put(DEVICE_COMMUNICATIONPROVIDER_LABEL, requestData.get("CommunicationProvider"));
        PROPERTIES_MAP.put(DEVICE_ICCID_LABEL, requestData.get("ICC_id"));
        PROPERTIES_MAP.put(DEVICE_DSMRVERSION_LABEL, requestData.get("DSMR_version"));
        PROPERTIES_MAP.put(DEVICE_SUPPLIER_LABEL, requestData.get("Supplier"));
        PROPERTIES_MAP.put(DEVICE_HLS3ACTIVE_LABEL, requestData.get("HLS3_active"));
        PROPERTIES_MAP.put(DEVICE_HLS4ACTIVE_LABEL, requestData.get("HLS4_active"));
        PROPERTIES_MAP.put(DEVICE_HLS5ACTIVE_LABEL, requestData.get("HLS5_active"));
        PROPERTIES_MAP.put(DEVICE_MASTERKEY_LABEL, requestData.get("Master_key"));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the add device response contains$")
    public void the_add_device_request_contains(final Map<String, String> expectedResponseData) throws Throwable {
    	this.runXpathResult.assertXpath(this.response, PATH_DEVICE_IDENTIFICATION, getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    	this.runXpathResult.assertNotNull(this.response, PATH_CORRELATION_UID);

    	// Save the returned CorrelationUid in the Scenario related context for further use.
    	saveCorrelationUidInScenarioContext(
    	    this.runXpathResult.getValue(this.response, PATH_CORRELATION_UID),
    	    getString(expectedResponseData, "OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    }

    @Then("^receiving an get add device response request$")
    public void receiving_an_get_add_device_response_request(final Map<String, String> requestData) throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, ScenarioContext.Current().get(CORRELATION_UID_LABEL).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the get add device request response should be ok$")
    public void the_get_add_device_request_response_should_be_ok() throws Throwable {
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, Defaults.EXPECTED_RESULT_OK));
    }
}
