package com.alliander.osgp.platform.cucumber.smartmeteringconfiguration;

import java.util.HashMap;
import java.util.Map;

import com.alliander.osgp.platform.cucumber.SmartMetering;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class UpdateFirmware extends SmartMetering {

    private static final String FIRMWARE_IDENTIFIER_LABEL = "FirmwareIdentifier";
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Given("^a request for a firmware upgrade for device (.+) from a client$")
    public void aRequestForAFirmwareUpgradeForDeviceFromAClient(String deviceIdentification) throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, deviceIdentification);
    }

    @Given("^the installation file of version (.+) is available$")
    public void theInstallationFileOfVersionIsAvailable(String version) throws Throwable {
        PROPERTIES_MAP.put(FIRMWARE_IDENTIFIER_LABEL, version);
    }

    @When("^the request for a firmware upgrade is received$")
    public void theRequestForAFirmwareUpgradeIsReceived() throws Throwable {
        // Call request runner.
        throw new PendingException();
    }

    @Then("^firmware should be updated$")
    public void firmwareShouldBeUpdated() throws Throwable {
        // Check response for OK.
        throw new PendingException();
    }

    @Then("^the database should be updated so it indicates that device (.+) is using firmware version KFPP_V(\\d+)FF$")
    public void theDatabaseShouldBeUpdatedSoItIndicatesThatDeviceEIsUsingFirmwareVersion(String deviceIdentification,
            String firmwareVersion) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^the installation file of version (.+) is not available$")
    public void theInstallationFileOfVersionIsNotAvailable(String version) throws Throwable {
        PROPERTIES_MAP.put(FIRMWARE_IDENTIFIER_LABEL, version);
    }

    @Then("^the message \"([^\"]*)\" should be given$")
    public void theMessageShouldBeGiven(String arg1) throws Throwable {
        // Check response contains message.
        throw new PendingException();
    }

    @Given("^the installation file is corrupt$")
    public void theInstallationFileIsCorrupt() throws Throwable {
        // TODO: Not influenced by cucumber at this time. firmwareIdentifier
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
