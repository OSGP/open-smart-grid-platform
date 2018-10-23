/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringinstallation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.AddDeviceRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SmartMeteringInstallationClient;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AddDeviceSteps extends AbstractSmartMeteringSteps {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @Autowired
    private SmartMeteringInstallationClient smartMeteringInstallationClient;

    @When("^receiving a smartmetering add device request$")
    public void receivingASmartmeteringAddDeviceRequest(final Map<String, String> settings) throws Throwable {
        final String deviceIdentification = settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION);
        ScenarioContext.current().put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, deviceIdentification);
        ScenarioContext.current().put(PlatformKeys.KEY_DEVICE_MASTERKEY,
                settings.get(PlatformKeys.KEY_DEVICE_MASTERKEY));
        ScenarioContext.current().put(PlatformKeys.KEY_DEVICE_AUTHENTICATIONKEY,
                settings.get(PlatformKeys.KEY_DEVICE_AUTHENTICATIONKEY));
        ScenarioContext.current().put(PlatformKeys.KEY_DEVICE_ENCRYPTIONKEY,
                settings.get(PlatformKeys.KEY_DEVICE_ENCRYPTIONKEY));
        ScenarioContext.current().put(PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY,
                settings.get(PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY));

        final AddDeviceRequest request = AddDeviceRequestFactory.fromParameterMap(settings);
        final AddDeviceAsyncResponse asyncResponse = this.smartMeteringInstallationClient.addDevice(request);

        this.checkAndSaveCorrelationId(asyncResponse.getCorrelationUid());

        assertEquals("Device identification in response", deviceIdentification,
                asyncResponse.getDeviceIdentification());
    }

    @Then("^the add device response should be returned$")
    public void theAddDeviceResponseShouldBeReturned(final Map<String, String> responseParameters) throws Throwable {

        final String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
        final Map<String, String> extendedParameters = SettingsHelper.addDefault(responseParameters,
                PlatformKeys.KEY_CORRELATION_UID, correlationUid);

        final AddDeviceAsyncRequest addDeviceAsyncRequest = AddDeviceRequestFactory
                .fromParameterMapAsync(extendedParameters);

        final AddDeviceResponse response = this.smartMeteringInstallationClient
                .getAddDeviceResponse(addDeviceAsyncRequest);

        final String expectedResult = responseParameters.get(PlatformKeys.KEY_RESULT);
        assertNotNull("Result", response.getResult());
        assertEquals("Result", expectedResult, response.getResult().name());
    }

    @Then("^retrieving the AddDevice response results in an exception$")
    public void retrievingTheAddDeviceResponseResultsInAnException() throws WebServiceSecurityException {

        final AddDeviceAsyncRequest addDeviceAsyncRequest = AddDeviceRequestFactory.fromScenarioContext();

        try {
            this.smartMeteringInstallationClient.getAddDeviceResponse(addDeviceAsyncRequest);
            fail("A SoapFaultClientException should be thrown");
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
        }
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
        this.configureForCommunicationWithDeviceSimulator(device);

        return this.deviceRepository.save(device);
    }

    private void configureForCommunicationWithDeviceSimulator(final Device device) {
        /*
         * This call also sets the device to be IN_USE and activated.
         */
        device.updateRegistrationData(PlatformSmartmeteringDefaults.NETWORK_ADDRESS, device.getDeviceType());

        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository
                .findByDeviceIdentification(device.getDeviceIdentification());

        assertNotNull(
                "Device should be in the DLMS protocol database for identification " + device.getDeviceIdentification(),
                dlmsDevice);

        dlmsDevice.setIpAddressIsStatic(true);
        dlmsDevice.setPort(PlatformSmartmeteringDefaults.PORT);
        this.dlmsDeviceRepository.save(dlmsDevice);
    }

    private String getDeviceIdentificationFromContext() {
        final String keyDeviceIdentification = PlatformKeys.KEY_DEVICE_IDENTIFICATION;
        final String deviceIdentification = (String) ScenarioContext.current().get(keyDeviceIdentification);
        assertNotNull("Device identification must be in the scenario context for key " + keyDeviceIdentification,
                deviceIdentification);
        return deviceIdentification;
    }

    private Device findDeviceByDeviceIdentification(final String deviceIdentification) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        assertNotNull("Device must exist for identification " + deviceIdentification, device);
        return device;
    }
}
