/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.core.builders.FirmwareBuilder;
import com.alliander.osgp.cucumber.platform.glue.steps.database.core.DeviceModelSteps;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.FirmwareRepository;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@Transactional(value = "txMgrCore")
public class UpdateFirmware extends SmartMeteringStepsBase {

    private static final String PATH_RESULT_STATUS = "/Envelope/Body/UpdateFirmwareResponse/Result/text()";
    private static final String PATH_RESULT_FIRMWAREVERSION_TYPE = "/Envelope/Body/UpdateFirmwareResponse/FirmwareVersion/FirmwareModuleType";
    private static final String PATH_RESULT_FIRMWAREVERSION_VERSION = "/Envelope/Body/UpdateFirmwareResponse/FirmwareVersion/Version";

    private static final String PATH_SOAP_FAULT_FAULTSTRING = "/Envelope/Body/Fault/faultstring/text()";
    private static final String PATH_SOAP_FAULT_FUNCTIONAL_FAULT_MESSAGE = "/Envelope/Body/Fault/detail/FunctionalFault/Message/text()";
    private static final String PATH_SOAP_FAULT_FUNCTIONAL_FAULT_INNER_MESSAGE = "/Envelope/Body/Fault/detail/FunctionalFault/InnerMessage/text()";
    private static final String PATH_SOAP_FAULT_TECHNICAL_FAULT_MESSAGE = "/Envelope/Body/Fault/detail/TechnicalFault/Message/text()";
    private static final String PATH_SOAP_FAULT_TECHNICAL_FAULT_INNER_MESSAGE = "/Envelope/Body/Fault/detail/TechnicalFault/InnerMessage/text()";

    private static final String XPATH_MATCHER_RESULT_STATUS = "OK";
    private static final String XPATH_MATCHER_FIRMWAREVERSION_TYPE = "(MODULE_|COMMUNICATION_MODULE_)?ACTIVE_FIRMWARE";
    private static final String XPATH_MATCHER_FIRMWAREVERSION_VERSION = ".+";

    private static final String TEST_SUITE_XML = "SmartmeterConfiguration";
    private static final String TEST_CASE_XML = "268 Update firmware";
    private static final String TEST_CASE_NAME_REQUEST = "UpdateFirmware";
    private static final String TEST_CASE_NAME_RESPONSE = "GetUpdateFirmwareResponse";

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private FirmwareRepository firmwareRepository;

    @Autowired
    private DeviceModelSteps deviceModelSteps;

    @Given("a firmware$")
    public void aFirmware(final Map<String, String> settings) {
        this.insertCoreFirmware(settings);
    }

    @Given("^a request for a firmware upgrade for device \"([^\"]*)\" from a client$")
    public void aRequestForAFirmwareUpgradeForDeviceFromAClient(final String deviceIdentification) throws Throwable {
        ScenarioContext.current().put("DeviceIdentification", deviceIdentification);
    }

    @Given("^the installation file of version \"([^\"]*)\" is available$")
    public void theInstallationFileOfVersionIsAvailable(final String version) throws Throwable {
        PROPERTIES_MAP.put(PlatformKeys.KEY_FIRMWARE_IDENTIFICATION, version);
    }

    @When("^the request for a firmware upgrade is received$")
    public void theRequestForAFirmwareUpgradeIsReceived() throws Throwable {
        final Object obj = ScenarioContext.current().get(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION);
        final String organisationIdentification = obj == null ? PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION
                : obj.toString();
        PROPERTIES_MAP.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION_E_LABEL, organisationIdentification);
        PROPERTIES_MAP.put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^firmware should be updated$")
    public void firmwareShouldBeUpdated() throws Throwable {
        PROPERTIES_MAP.put(PlatformKeys.KEY_CORRELATION_UID,
                ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, TEST_CASE_XML, TEST_SUITE_XML);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_STATUS, XPATH_MATCHER_RESULT_STATUS));

        this.runXpathResult.assertXpathList(this.response, PATH_RESULT_FIRMWAREVERSION_TYPE,
                XPATH_MATCHER_FIRMWAREVERSION_TYPE, 3);
        this.runXpathResult.assertXpathList(this.response, PATH_RESULT_FIRMWAREVERSION_VERSION,
                XPATH_MATCHER_FIRMWAREVERSION_VERSION, 3);
    }

    @Then("^the database should be updated so it indicates that device \"([^\"]*)\" is using firmware version \"([^\"]*)\"$")
    public void theDatabaseShouldBeUpdatedSoItIndicatesThatDeviceEIsUsingFirmwareVersion(
            final String deviceIdentification, final String firmwareVersion) throws Throwable {

        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        assertNotNull("Device not found.", device);

        final Firmware firmware = this.firmwareRepository.findByFilename(firmwareVersion);
        assertNotNull("Firmware not found.", firmware);

        final Firmware activeFirmware = device.getActiveFirmware();
        assertNotNull("No active firmware found for device.", activeFirmware);

        assertEquals("Firmware filenames do not match.", firmware.getFilename(), activeFirmware.getFilename());
        assertEquals("Firmware fields module_version_comm do not match.", firmware.getModuleVersionComm(),
                activeFirmware.getModuleVersionComm());
        assertEquals("Firmware fields module_version_func do not match.", firmware.getModuleVersionFunc(),
                activeFirmware.getModuleVersionFunc());
        assertEquals("Firmware fields module_version_ma do not match.", firmware.getModuleVersionMa(),
                activeFirmware.getModuleVersionMa());
    }

    @Given("^the installation file of version \"([^\"]*)\" is not available$")
    public void theInstallationFileOfVersionIsNotAvailable(final String version) throws Throwable {
        PROPERTIES_MAP.put(PlatformKeys.KEY_FIRMWARE_IDENTIFICATION, version);
    }

    @Then("^the message \"([^\"]*)\" should be given$")
    public void theMessageShouldBeGiven(final String message) throws Throwable {
        PROPERTIES_MAP.put(PlatformKeys.KEY_CORRELATION_UID,
                ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, TEST_CASE_XML, TEST_SUITE_XML);

        assertTrue("Message '" + message + "' not found in response.", this.responseMessagesContain(message));
    }

    @Given("^the installation file is corrupt$")
    public void theInstallationFileIsCorrupt() throws Throwable {
        // Not influenced by cucumber at this time. firmwareIdentifier
        // should point to a corrupt file available on the server. This should
        // be changed in the new cucumber project setup.
    }

    @When("^the upgrade of firmware did not succeed$")
    public void theUpgradeOfFirmwareDidNotSucceed() throws Throwable {
        // Not influenced by cucumber at this time. Nothing can be done here to
        // make the update fail.
    }

    private boolean responseMessagesContain(final String text) {
        try {
            return this.runXpathResult.assertXpath(this.response, PATH_SOAP_FAULT_FAULTSTRING, text)
                    || this.runXpathResult.assertXpath(this.response, PATH_SOAP_FAULT_FUNCTIONAL_FAULT_MESSAGE, text)
                    || this.runXpathResult.assertXpath(this.response, PATH_SOAP_FAULT_FUNCTIONAL_FAULT_INNER_MESSAGE,
                            text)
                    || this.runXpathResult.assertXpath(this.response, PATH_SOAP_FAULT_TECHNICAL_FAULT_MESSAGE, text)
                    || this.runXpathResult.assertXpath(this.response, PATH_SOAP_FAULT_TECHNICAL_FAULT_INNER_MESSAGE,
                            text);
        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
            throw new AssertionError("Exception not expected on XPath assert with text: " + text, e);
        }
    }

    private void insertCoreFirmware(final Map<String, String> settings) {
        final com.alliander.osgp.domain.core.entities.DeviceModel deviceModel = this.deviceModelSteps
                .insertDeviceModel(settings);
        com.alliander.osgp.domain.core.entities.Firmware firmware = new FirmwareBuilder().withSettings(settings)
                .withDeviceModel(deviceModel).build();
        firmware = this.firmwareRepository.save(firmware);
    }
}