package com.alliander.osgp.platform.cucumber.smartmeteringconfiguration;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.SmartMetering;
import com.alliander.osgp.platform.cucumber.smartmeteringmonitoring.ActualMeterReadsGas;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;
import com.alliander.osgp.platform.cucumber.support.ServiceEndpoint;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class UpdateFirmware extends SmartMetering {

    private static final String FIRMWARE_IDENTIFIER_LABEL = "FirmwareIdentifier";
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsGas.class);

    private static final String PATH_RESULT_STATUS = "/Envelope/Body/GetUpdateFirmwareResponse/Result/text()";
    private static final String PATH_RESULT_FIRMWAREVERSION_TYPE = "/Envelope/Body/GetUpdateFirmwareResponse/FirmwareVersion/FirmwareModuleType";
    private static final String PATH_RESULT_FIRMWAREVERSION_VERSION = "/Envelope/Body/GetUpdateFirmwareResponse/FirmwareVersion/Version";

    private static final String PATH_SOAP_FAULT_FAULTSTRING = "/Envelope/Body/Fault/faultstring/text()";

    private static final String XPATH_MATCHER_RESULT_STATUS = "OK";
    private static final String XPATH_MATCHER_FIRMWAREVERSION_TYPE = "(MODULE_|COMMUNICATION_MODULE_)?ACTIVE_FIRMWARE";
    private static final String XPATH_MATCHER_FIRMWAREVERSION_VERSION = ".+";

    private static final String TEST_SUITE_XML = "SmartmeterConfiguration";
    private static final String TEST_CASE_XML = "268 Update firmware";
    private static final String TEST_CASE_NAME_REQUEST = "UpdateFirmware";
    private static final String TEST_CASE_NAME_RESPONSE = "GetUpdateFirmwareResponse";

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @Given("^a request for a firmware upgrade for device (.+) from a client$")
    public void aRequestForAFirmwareUpgradeForDeviceFromAClient(final String deviceIdentification) throws Throwable {
        this.deviceId.setDeviceIdE(deviceIdentification);
    }

    @Given("^the installation file of version (.+) is available$")
    public void theInstallationFileOfVersionIsAvailable(final String version) throws Throwable {
        PROPERTIES_MAP.put(FIRMWARE_IDENTIFIER_LABEL, version);
    }

    @When("^the request for a firmware upgrade is received$")
    public void theRequestForAFirmwareUpgradeIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
        PROPERTIES_MAP.put(ENDPOINT_LABEL, this.serviceEndpoint.getServiceEndpoint());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^firmware should be updated$")
    public void firmwareShouldBeUpdated() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_STATUS, XPATH_MATCHER_RESULT_STATUS));

        this.runXpathResult.assertXpathList(this.response, PATH_RESULT_FIRMWAREVERSION_TYPE,
                XPATH_MATCHER_FIRMWAREVERSION_TYPE, 3);
        this.runXpathResult.assertXpathList(this.response, PATH_RESULT_FIRMWAREVERSION_VERSION,
                XPATH_MATCHER_FIRMWAREVERSION_VERSION, 3);
    }

    @Then("^the database should be updated so it indicates that device (.+) is using firmware version KFPP_V(\\d+)FF$")
    public void theDatabaseShouldBeUpdatedSoItIndicatesThatDeviceEIsUsingFirmwareVersion(
            final String deviceIdentification, final String firmwareVersion) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^the installation file of version (.+) is not available$")
    public void theInstallationFileOfVersionIsNotAvailable(final String version) throws Throwable {
        PROPERTIES_MAP.put(FIRMWARE_IDENTIFIER_LABEL, version);
    }

    @Then("^the message \"([^\"]*)\" should be given$")
    public void theMessageShouldBeGiven(final String message) throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        this.runXpathResult.assertXpath(this.response, PATH_SOAP_FAULT_FAULTSTRING, message);
    }

    @Given("^the installation file is corrupt$")
    public void theInstallationFileIsCorrupt() throws Throwable {
        // Not influenced by cucumber at this time. firmwareIdentifier
        // should point to a corrupt file available on the server. This should
        // be changed in the new cucumber project setup.
    }

    @When("^the upgrade of firmware did not succeed$")
    public void theUpgradeOfFirmwareDidNotSucceed() throws Throwable {
        // Check response for NOT OK
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
