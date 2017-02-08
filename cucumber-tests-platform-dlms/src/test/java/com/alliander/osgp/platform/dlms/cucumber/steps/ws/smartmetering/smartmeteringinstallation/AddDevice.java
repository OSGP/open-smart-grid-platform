/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.smartmeteringinstallation;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Assert;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusResponse;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.platform.dlms.cucumber.support.ws.smartmetering.SmartMeteringConfigurationManagementClient;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AddDevice extends SmartMeteringStepsBase {
    private static final String PATH_RESULT = "/Envelope/Body/AddDeviceResponse/Result/text()";

    private static final String TEST_SUITE_XML = "SmartmeterInstallation";
    private static final String TEST_CASE_XML = "218 Retrieve AddDevice result";
    private static final String TEST_CASE_NAME_REQUEST = "AddDevice - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetAddDeviceResponse - Request 1";

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    SmartMeteringConfigurationManagementClient smartMeteringConfigurationClient;

    @When("^receiving an smartmetering add device request$")
    public void receiving_an_smartmetering_add_device_request(final Map<String, String> setings) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(setings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP
                .put(Keys.KEY_DEVICE_TYPE, getString(setings, Keys.KEY_DEVICE_TYPE, Defaults.DEFAULT_DEVICE_TYPE));
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_COMMUNICATIONMETHOD, setings.get(Keys.KEY_DEVICE_COMMUNICATIONMETHOD));
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_COMMUNICATIONPROVIDER, setings.get(Keys.KEY_DEVICE_COMMUNICATIONPROVIDER));
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_ICCID, setings.get(Keys.KEY_DEVICE_ICCID));
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_DSMRVERSION, setings.get(Keys.KEY_DEVICE_DSMRVERSION));
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_SUPPLIER, setings.get(Keys.KEY_DEVICE_SUPPLIER));
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_HLS3ACTIVE, setings.get(Keys.KEY_DEVICE_HLS3ACTIVE));
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_HLS4ACTIVE, setings.get(Keys.KEY_DEVICE_HLS4ACTIVE));
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_HLS5ACTIVE, setings.get(Keys.KEY_DEVICE_HLS5ACTIVE));
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_MASTERKEY, setings.get(Keys.KEY_DEVICE_MASTERKEY));
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_AUTHENTICATIONKEY, setings.get(Keys.KEY_DEVICE_AUTHENTICATIONKEY));
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_ENCRYPTIONKEY, setings.get(Keys.KEY_DEVICE_ENCRYPTIONKEY));

        ScenarioContext.Current().put(Keys.KEY_DEVICE_IDENTIFICATION, setings.get(Keys.KEY_DEVICE_IDENTIFICATION));
        ScenarioContext.Current().put(Keys.KEY_DEVICE_MASTERKEY, setings.get(Keys.KEY_DEVICE_MASTERKEY));
        ScenarioContext.Current().put(Keys.KEY_DEVICE_AUTHENTICATIONKEY,
                setings.get(Keys.KEY_DEVICE_AUTHENTICATIONKEY));
        ScenarioContext.Current().put(Keys.KEY_DEVICE_ENCRYPTIONKEY, setings.get(Keys.KEY_DEVICE_ENCRYPTIONKEY));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the smartmetering add device response contains$")
    public void the_add_device_request_contains(final Map<String, String> settings) throws Throwable {
        this.runXpathResult
                .assertXpath(
                        this.response,
                        PATH_DEVICE_IDENTIFICATION,
                        getString(settings, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                                Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        this.runXpathResult.assertNotNull(this.response, PATH_CORRELATION_UID);

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(this.runXpathResult.getValue(this.response, PATH_CORRELATION_UID),
                getString(settings, "OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    }

    @Then("^receiving an get add device response request$")
    public void receiving_an_get_add_device_response_request(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP
                .put(Keys.KEY_CORRELATION_UID, ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML,
                TEST_SUITE_XML);
    }

    @Then("^the get add device request response should be ok$")
    public void the_get_add_device_request_response_should_be_ok() throws Throwable {
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, Defaults.EXPECTED_RESULT_OK));
    }

    @Then("^a request to the device can be performed after activation$")
    public void aRequestToTheDeviceCanBePerformedAfterActivation() throws Throwable {

        final Device device = this.activateDevice();

        /*
         * Fire any request that causes communication to the device, and check
         * that the actual response is not an error.
         */

        final GetAdministrativeStatusRequest request = new GetAdministrativeStatusRequest();
        request.setDeviceIdentification(device.getDeviceIdentification());
        final GetAdministrativeStatusAsyncResponse getAdministrativeStatusAsyncResponse = this.smartMeteringConfigurationClient
                .getAdministrativeStatus(request);

        final GetAdministrativeStatusAsyncRequest asyncRequest = new GetAdministrativeStatusAsyncRequest();
        asyncRequest.setCorrelationUid(getAdministrativeStatusAsyncResponse.getCorrelationUid());
        asyncRequest.setDeviceIdentification(device.getDeviceIdentification());
        final GetAdministrativeStatusResponse getAdministrativeStatusResponse = this.smartMeteringConfigurationClient
                .retrieveGetAdministrativeStatusResponse(asyncRequest);

        assertNotNull("Administrative status should contain information if it is enabled",
                getAdministrativeStatusResponse.getEnabled());
    }

    private Device activateDevice() {
        final String deviceIdentification = this.getDeviceIdentificationFromContext();
        final Device device = this.findDeviceByDeviceIdentification(deviceIdentification);

        assertNotNull("Device should be in the core database for identification " + deviceIdentification, device);

        /*
         * The default result of adding a device through a service call is that
         * the device is configured to have a dynamic IP address to be obtained
         * from Jasper Wireless.
         */
        this.configureNotToUseJasperWireless(device);

        return this.deviceRepository.save(device);
    }

    private void configureNotToUseJasperWireless(final Device device) {
        /*
         * This call also sets the device to be active and activated.
         */
        device.updateRegistrationData(com.alliander.osgp.platform.dlms.cucumber.steps.Defaults.NETWORK_ADDRESS,
                device.getDeviceType());

        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository
                .findByDeviceIdentification(device.getDeviceIdentification());

        assertNotNull(
                "Device should be in the DLMS protocol database for identification " + device.getDeviceIdentification(),
                dlmsDevice);

        dlmsDevice.setIpAddressIsStatic(true);
        dlmsDevice.setPort(com.alliander.osgp.platform.dlms.cucumber.steps.Defaults.PORT);
        this.dlmsDeviceRepository.save(dlmsDevice);
    }

    private String getDeviceIdentificationFromContext() {
        final String keyDeviceIdentification = Keys.KEY_DEVICE_IDENTIFICATION;
        final String deviceIdentification = (String) ScenarioContext.Current().get(keyDeviceIdentification);
        assertNotNull("Device identification must be in the scenario context for key " + keyDeviceIdentification,
                deviceIdentification);
        return deviceIdentification;
    }

    private Device findDeviceByDeviceIdentification(final String deviceIdentification) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        assertNotNull("Device must exist for identification " + deviceIdentification, device);
        return device;
    }

    private Device activateDevice(final Device device) {
        device.setActivated(true);
        device.setActive(true);
        return this.deviceRepository.save(device);
    }
}
