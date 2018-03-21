/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.glue.steps.ws.basicosgpfunctions;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getBoolean;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getEnum;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeviceAuthorisation;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeviceFunctionGroup;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDeviceAuthorisationsRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDeviceAuthorisationsResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateDeviceAuthorisationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.Configuration;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindEventsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareRequest;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.common.PlatformCommonDefaults;
import com.alliander.osgp.cucumber.platform.common.PlatformCommonKeys;
import com.alliander.osgp.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import com.alliander.osgp.cucumber.platform.common.support.ws.core.CoreAdHocManagementClient;
import com.alliander.osgp.cucumber.platform.common.support.ws.core.CoreConfigurationManagementClient;
import com.alliander.osgp.cucumber.platform.common.support.ws.core.CoreDeviceInstallationClient;
import com.alliander.osgp.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;
import com.alliander.osgp.cucumber.platform.common.support.ws.core.CoreFirmwareManagementClient;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the AuthorizeDeviceFunctions steps
 */
public class AuthorizeDeviceFunctionsSteps {

    @Autowired
    private AdminDeviceManagementClient adminDeviceManagementClient;

    @Autowired
    private CoreDeviceInstallationClient coreDeviceInstallationClient;

    @Autowired
    private CoreDeviceManagementClient coreDeviceManagementClient;

    @Autowired
    private CoreConfigurationManagementClient coreConfigurationManagementClient;

    @Autowired
    private CoreAdHocManagementClient coreAdHocManagementClient;

    @Autowired
    private CoreFirmwareManagementClient coreFirmwareManagementClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeDeviceFunctionsSteps.class);

    private DeviceFunction deviceFunction;
    private Exception exception;

    @When("receiving a set device authorization request")
    public void receivingADeviceAuthorizationRequest(final Map<String, String> requestParameters) {
        try {
            this.setDeviceAuthorization(requestParameters);
        } catch (final Exception e) {
            LOGGER.info("Exception: {}, message {}", e.getClass().getSimpleName(), e.getMessage());
            this.exception = e;
        }
    }

    @When("receiving a device function request")
    public void receivingADeviceFunctionRequest(final Map<String, String> requestParameters) {
        this.deviceFunction = getEnum(requestParameters, PlatformCommonKeys.DEVICE_FUNCTION, DeviceFunction.class);

        try {
            if (requestParameters.containsKey(PlatformCommonKeys.DELEGATE_FUNCTION_GROUP)) {
                this.findDeviceAuthorisations(requestParameters);
            } else {
                switch (this.deviceFunction) {
                case START_SELF_TEST:
                    this.startSelfTest(requestParameters);
                    break;
                case STOP_SELF_TEST:
                    this.stopSelfTest(requestParameters);
                    break;
                case GET_STATUS:
                    this.getStatus(requestParameters);
                    break;
                case GET_DEVICE_AUTHORIZATION:
                    this.getDeviceAuthorization(requestParameters);
                    break;
                case SET_DEVICE_AUTHORIZATION:
                    this.setDeviceAuthorization(requestParameters);
                    break;
                case SET_EVENT_NOTIFICATIONS:
                    this.setEventNotifications(requestParameters);
                    break;
                case GET_EVENT_NOTIFICATIONS:
                    this.getEventNotifications(requestParameters);
                    break;
                case UPDATE_FIRMWARE:
                    this.updateFirmware(requestParameters);
                    break;
                case GET_FIRMWARE_VERSION:
                    this.getFirmwareVersion(requestParameters);
                    break;
                case SET_CONFIGURATION:
                    this.setConfiguration(requestParameters);
                    break;
                case GET_CONFIGURATION:
                    this.getConfiguration(requestParameters);
                    break;
                case REMOVE_DEVICE:
                    this.removeDevice(requestParameters);
                    break;
                case SET_REBOOT:
                    this.setReboot(requestParameters);
                    break;
                default:
                    throw new OperationNotSupportedException(
                            "DeviceFunction " + this.deviceFunction + " does not exist.");
                }
            }
        } catch (final Exception e) {
            LOGGER.info("Exception: {}, message {}", e.getClass().getSimpleName(), e.getMessage());
            this.exception = e;
        }
    }

    @Then("the device function response is \"([^\"]*)\"")
    public void theDeviceFunctionResponseIsSuccessful(final Boolean allowed) {
        if (allowed) {
            final Object response = ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);
            Assert.assertNotNull("Response is null, which indicates an exception occurred", response);
            Assert.assertFalse(response instanceof SoapFaultClientException);
        } else {
            Assert.assertNotNull(this.exception);

            if (!this.exception.getMessage().equals("METHOD_NOT_ALLOWED_FOR_OWNER")) {
                Assert.assertEquals("UNAUTHORIZED", this.exception.getMessage());
            }
        }
    }

    @Then("^device \"([^\"]*)\" has (\\d++) device authorizations$")
    public void deviceHasDeviceAuthorizations(final String deviceIdentification,
            final int expectedCountDeviceAuthorizations) {
        final FindDeviceAuthorisationsRequest findDeviceAuthorisationsRequest = new FindDeviceAuthorisationsRequest();
        findDeviceAuthorisationsRequest.setDeviceIdentification(deviceIdentification);

        Wait.until(() -> {
            FindDeviceAuthorisationsResponse response;
            try {
                response = this.adminDeviceManagementClient.findDeviceAuthorisations(findDeviceAuthorisationsRequest);

                Assert.assertEquals(expectedCountDeviceAuthorizations, response.getDeviceAuthorisations().size());
            } catch (final Exception e) {
                final String message = String.format("An exception occurred while retrieving the authorizations for %s",
                        deviceIdentification);
                LOGGER.warn(message, e);
                Assert.fail(message);
            }
        });
    }

    private void findDeviceAuthorisations(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final FindDeviceAuthorisationsRequest findDeviceAuthorisationsRequest = new FindDeviceAuthorisationsRequest();
        findDeviceAuthorisationsRequest.setDeviceIdentification(getString(requestParameters,
                PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION, PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        final FindDeviceAuthorisationsResponse response = this.adminDeviceManagementClient
                .findDeviceAuthorisations(findDeviceAuthorisationsRequest);

        final UpdateDeviceAuthorisationsRequest updateDeviceAuthorisationsRequest = new UpdateDeviceAuthorisationsRequest();

        final DeviceAuthorisation deviceAuthorisation = response.getDeviceAuthorisations().get(0);
        deviceAuthorisation.setFunctionGroup(
                getEnum(requestParameters, PlatformCommonKeys.KEY_DEVICE_FUNCTION_GROUP, DeviceFunctionGroup.class));

        updateDeviceAuthorisationsRequest.getDeviceAuthorisations().add(deviceAuthorisation);

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE,
                this.adminDeviceManagementClient.updateDeviceAuthorisations(updateDeviceAuthorisationsRequest));
    }

    private void startSelfTest(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final StartDeviceTestRequest request = new StartDeviceTestRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE,
                this.coreDeviceInstallationClient.startDeviceTest(request));
    }

    private void stopSelfTest(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final StopDeviceTestRequest request = new StopDeviceTestRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE,
                this.coreDeviceInstallationClient.stopDeviceTest(request));
    }

    private void getStatus(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusRequest request = new com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE,
                this.coreDeviceInstallationClient.getStatus(request));
    }

    private void getDeviceAuthorization(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final FindDeviceAuthorisationsRequest request = new FindDeviceAuthorisationsRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE,
                this.adminDeviceManagementClient.findDeviceAuthorisations(request));
    }

    private void setEventNotifications(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final SetEventNotificationsRequest request = new SetEventNotificationsRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE,
                this.coreDeviceManagementClient.setEventNotifications(request));
    }

    private void getEventNotifications(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final FindEventsRequest request = new FindEventsRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE,
                this.coreDeviceManagementClient.findEventsResponse(request));
    }

    private void updateFirmware(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final UpdateFirmwareRequest request = new UpdateFirmwareRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        request.setFirmwareIdentification(getString(requestParameters, PlatformCommonKeys.KEY_FIRMWARE_IDENTIFICATION,
                PlatformCommonDefaults.FIRMWARE_IDENTIFICATION));

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE,
                this.coreFirmwareManagementClient.updateFirmware(request));
    }

    private void getFirmwareVersion(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final GetFirmwareVersionRequest request = new GetFirmwareVersionRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE,
                this.coreFirmwareManagementClient.getFirmwareVersion(request));
    }

    private void setConfiguration(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final SetConfigurationRequest request = new SetConfigurationRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        final Configuration config = new Configuration();

        config.setLightType(PlatformCommonDefaults.CONFIGURATION_LIGHTTYPE);
        config.setPreferredLinkType(PlatformCommonDefaults.CONFIGURATION_PREFERRED_LINKTYPE);
        config.setMeterType(PlatformCommonDefaults.CONFIGURATION_METER_TYPE);
        config.setShortTermHistoryIntervalMinutes(PlatformCommonDefaults.SHORT_INTERVAL);

        request.setConfiguration(config);

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE,
                this.coreConfigurationManagementClient.setConfiguration(request));
    }

    private void getConfiguration(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final GetConfigurationRequest request = new GetConfigurationRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE,
                this.coreConfigurationManagementClient.getConfiguration(request));
    }

    private void removeDevice(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final RemoveDeviceRequest request = new RemoveDeviceRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE,
                this.adminDeviceManagementClient.removeDevice(request));
    }

    private void setReboot(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final SetRebootRequest request = new SetRebootRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, this.coreAdHocManagementClient.setReboot(request));
    }

    private void setDeviceAuthorization(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final UpdateDeviceAuthorisationsRequest request = new UpdateDeviceAuthorisationsRequest();
        final DeviceAuthorisation deviceAuthorisation = new DeviceAuthorisation();
        deviceAuthorisation.setDeviceIdentification(getString(requestParameters,
                PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION, PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        deviceAuthorisation.setOrganisationIdentification(
                getString(requestParameters, PlatformCommonKeys.DELEGATE_ORGANIZATION_IDENTIFICATION,
                        PlatformCommonDefaults.DEFAULT_DELEGATE_ORGANIZATION_IDENTIFICATION));

        final String functionGroupString = getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_FUNCTION_GROUP);
        final DeviceFunctionGroup deviceFunctionGroup = DeviceFunctionGroup.fromValue(functionGroupString);
        deviceAuthorisation.setFunctionGroup(deviceFunctionGroup);

        deviceAuthorisation.setRevoked(
                getBoolean(requestParameters, PlatformCommonKeys.KEY_REVOKED, PlatformCommonDefaults.REVOKED));
        request.getDeviceAuthorisations().add(deviceAuthorisation);
        ScenarioContext.current().put(PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION,
                getString(requestParameters, PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformCommonDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        ScenarioContext.current().put(PlatformCommonKeys.RESPONSE,
                this.adminDeviceManagementClient.updateDeviceAuthorisations(request));

    }
}