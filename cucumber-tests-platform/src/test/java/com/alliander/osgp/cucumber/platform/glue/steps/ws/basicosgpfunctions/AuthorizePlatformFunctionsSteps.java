/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.basicosgpfunctions;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeactivateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindMessageLogsRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.GetProtocolInfosRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.Organisation;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ProtocolInfo;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RevokeKeyRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateDeviceProtocolRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateKeyRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindScheduledTasksRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.AddDeviceModelRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.AddFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.AddManufacturerRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeDeviceModelRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeManufacturerRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.DeviceModel;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FindAllDeviceModelsRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FindAllManufacturersRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FindFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.Firmware;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.Manufacturer;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.RemoveDeviceModelRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.RemoveFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.RemoveManufacturerRequest;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.support.ws.admin.AdminDeviceManagementClient;
import com.alliander.osgp.cucumber.platform.support.ws.core.CoreDeviceManagementClient;
import com.alliander.osgp.cucumber.platform.support.ws.core.CoreFirmwareManagementClient;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunction;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the AuthorizeDeviceFunctions steps
 */
public class AuthorizePlatformFunctionsSteps {

    @Autowired
    private AdminDeviceManagementClient adminDeviceManagementClient;

    @Autowired
    private CoreDeviceManagementClient coreDeviceManagementClient;

    @Autowired
    private CoreFirmwareManagementClient firmwareManagementClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizePlatformFunctionsSteps.class);

    private PlatformFunction platformFunction;
    private Throwable throwable;

    @When("receiving a platform function request")
    public void receivingADeviceFunctionRequest(final Map<String, String> requestParameters)
            throws OperationNotSupportedException, WebServiceSecurityException, GeneralSecurityException, IOException {
        this.platformFunction = getEnum(requestParameters, Keys.KEY_PLATFORM_FUNCTION_GROUP, PlatformFunction.class);

        try {
            switch (this.platformFunction) {
            case CREATE_ORGANISATION:
                this.createOrganisation(requestParameters);
                break;
            case REMOVE_ORGANISATION:
                this.removeOrganisation(requestParameters);
                break;
            case CHANGE_ORGANISATION:
                this.changeOrganisation(requestParameters);
                break;
            case GET_ORGANISATIONS:
                this.getOrganisations(requestParameters);
                break;
            case GET_MESSAGES:
                this.getMessages(requestParameters);
                break;
            case GET_DEVICE_NO_OWNER:
                this.getDevicesWithoutOwner(requestParameters);
                break;
            case FIND_DEVICES:
                this.findDevices(requestParameters);
                break;
            case SET_OWNER:
                this.setOwner(requestParameters);
                break;
            case UPDATE_KEY:
                this.updateKey(requestParameters);
                break;
            case REVOKE_KEY:
                this.revokeKey(requestParameters);
                break;
            case FIND_SCHEDULED_TASKS:
                this.findScheduledTasks(requestParameters);
                break;
            case CREATE_MANUFACTURER:
                this.createManufacturer(requestParameters);
                break;
            case REMOVE_MANUFACTURER:
                this.removeManufacturer(requestParameters);
                break;
            case CHANGE_MANUFACTURER:
                this.changeManufacturer(requestParameters);
                break;
            case GET_MANUFACTURERS:
                this.getManufacturers(requestParameters);
                break;
            case DEACTIVATE_DEVICE:
                this.deactivateDevice(requestParameters);
                break;
            case GET_PROTOCOL_INFOS:
                this.getProtocolInfos(requestParameters);
                break;
            case UPDATE_DEVICE_PROTOCOL:
                this.updateDeviceProtocol(requestParameters);
                break;
            case GET_DEVICE_MODELS:
                this.getDeviceModels(requestParameters);
                break;
            case CREATE_DEVICE_MODEL:
                this.createDeviceModel(requestParameters);
                break;
            case REMOVE_DEVICE_MODEL:
                this.removeDeviceModel(requestParameters);
                break;
            case CHANGE_DEVICE_MODEL:
                this.changeDeviceModel(requestParameters);
                break;
            case GET_FIRMWARE:
                this.getFirmware(requestParameters);
                break;
            case CREATE_FIRMWARE:
                this.createFirmware(requestParameters);
                break;
            case CHANGE_FIRMWARE:
                this.changeFirmware(requestParameters);
                break;
            case REMOVE_FIRMWARE:
                this.removeFirmware(requestParameters);
                break;
            case ACTIVATE_DEVICE:
                this.activateDevice(requestParameters);
                break;
            default:
                throw new OperationNotSupportedException(
                        "PlatformFunction " + this.platformFunction + " does not exist.");
            }
        } catch (final Throwable t) {
            LOGGER.info("Exception: {}", t.getClass().getSimpleName());
            this.throwable = t;
        }
    }

    @Then("the platform function response is \"([^\"]*)\"")
    public void theDeviceFunctionResponseIsSuccessful(final Boolean allowed) {
        final Object response = ScenarioContext.Current().get(Keys.RESPONSE);

        if (allowed) {
            Assert.assertTrue(!(response instanceof SoapFaultClientException));
        } else {
            Assert.assertTrue(this.throwable != null);
        }
    }

    private void createOrganisation(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final CreateOrganisationRequest request = new CreateOrganisationRequest();

        final Organisation organisation = new Organisation();
        organisation.setName(Defaults.DEFAULT_ORGANIZATION_NAME);
        organisation.setOrganisationIdentification(Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
        organisation.setFunctionGroup(
                getEnum(requestParameters, Keys.KEY_PLATFORM_FUNCTION_GROUP, PlatformFunctionGroup.class));
        organisation.setPrefix(Defaults.DEFAULT_ORGANIZATION_PREFIX);
        organisation.setEnabled(Defaults.DEFAULT_ORGANIZATION_ENABLED);
        request.setOrganisation(organisation);

        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.createOrganization(request));
    }

    private void removeOrganisation(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final RemoveOrganisationRequest request = new RemoveOrganisationRequest();
        request.setOrganisationIdentification(Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.removeOrganization(request));
    }

    private void changeOrganisation(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final ChangeOrganisationRequest request = new ChangeOrganisationRequest();
        request.setOrganisationIdentification(Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
        request.setNewOrganisationName(Defaults.DEFAULT_NEW_ORGANIZATION_NAME);
        request.setNewOrganisationPlatformFunctionGroup(Defaults.DEFAULT_NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP);
        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.changeOrganization(request));
    }

    private void getOrganisations(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        ScenarioContext.Current().put(Keys.RESPONSE,
                this.adminDeviceManagementClient.findAllOrganizations(new FindAllOrganisationsRequest()));
    }

    private void getMessages(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final FindMessageLogsRequest request = new FindMessageLogsRequest();

        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.findMessageLogs(request));
    }

    private void getDevicesWithoutOwner(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        ScenarioContext.Current().put(Keys.RESPONSE,
                this.adminDeviceManagementClient.findDevicesWithoutOwner(new FindDevicesWhichHaveNoOwnerRequest()));
    }

    private void findDevices(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        ScenarioContext.Current().put(Keys.RESPONSE,
                this.coreDeviceManagementClient.findDevices(new FindDevicesRequest()));
    }

    private void setOwner(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final SetOwnerRequest request = new SetOwnerRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        request.setOrganisationIdentification(getString(requestParameters, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.setOwner(request));
    }

    private void updateKey(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final UpdateKeyRequest request = new UpdateKeyRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        request.setProtocolInfoId(Defaults.DEFAULT_PROTOCOL_INFO_ID);
        request.setPublicKey(Defaults.DEFAULT_PUBLIC_KEY);

        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.getUpdateKeyResponse(request));
    }

    private void revokeKey(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final RevokeKeyRequest request = new RevokeKeyRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.getRevokeKeyResponse(request));
    }

    private void activateDevice(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final ActivateDeviceRequest request = new ActivateDeviceRequest();
        request.setDeviceIdentification(Defaults.DEFAULT_DEVICE_IDENTIFICATION);
        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.activateDevice(request));
    }

    private void removeFirmware(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        final RemoveFirmwareRequest request = new RemoveFirmwareRequest();
        request.setId(Defaults.FIRMWARE_ID);
        ScenarioContext.Current().put(Keys.RESPONSE, this.firmwareManagementClient.removeFirmware(request));
    }

    private void changeFirmware(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final ChangeFirmwareRequest request = new ChangeFirmwareRequest();
        request.setId(Defaults.FIRMWARE_ID);
        final Firmware firmware = new Firmware();
        firmware.setDescription(Defaults.FIRMWARE_DESCRIPTION);
        firmware.setManufacturer(Defaults.MANUFACTURER_CODE);
        firmware.setModelCode(Defaults.DEVICE_MODEL_MODEL_CODE);
        firmware.setPushToNewDevices(Defaults.FIRMWARE_PUSH_TO_NEW_DEVICE);
        request.setFirmware(firmware);
        ScenarioContext.Current().put(Keys.RESPONSE, this.firmwareManagementClient.changeFirmware(request));
    }

    private void createFirmware(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        final AddFirmwareRequest request = new AddFirmwareRequest();
        final Firmware firmware = new Firmware();
        firmware.setDescription(Defaults.FIRMWARE_DESCRIPTION);
        firmware.setManufacturer(Defaults.MANUFACTURER_CODE);
        firmware.setModelCode(Defaults.DEVICE_MODEL_MODEL_CODE);
        firmware.setPushToNewDevices(Defaults.FIRMWARE_PUSH_TO_NEW_DEVICE);
        request.setFirmware(firmware);
        ScenarioContext.Current().put(Keys.RESPONSE, this.firmwareManagementClient.addFirmware(request));
    }

    private void getFirmware(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        final FindFirmwareRequest request = new FindFirmwareRequest();
        request.setFirmwareId(Defaults.FIRMWARE_ID);
        ScenarioContext.Current().put(Keys.RESPONSE, this.firmwareManagementClient.findFirmware(request));
    }

    private void changeDeviceModel(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        final ChangeDeviceModelRequest request = new ChangeDeviceModelRequest();
        final DeviceModel deviceModel = new DeviceModel();
        deviceModel.setDescription(Defaults.DEFAULT_DEVICE_MODEL_DESCRIPTION);
        deviceModel.setManufacturer(Defaults.MANUFACTURER_CODE);
        deviceModel.setMetered(Defaults.DEFAULT_DEVICE_MODEL_METERED);
        deviceModel.setModelCode(Defaults.DEVICE_MODEL_MODEL_CODE);
        request.setDeviceModel(deviceModel);
        ScenarioContext.Current().put(Keys.RESPONSE, this.firmwareManagementClient.changeDeviceModel(request));
    }

    private void removeDeviceModel(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        final RemoveDeviceModelRequest request = new RemoveDeviceModelRequest();
        request.setDeviceManufacturerId(Defaults.DEFAULT_MANUFACTURER_ID);
        request.setDeviceModelId(Defaults.DEVICE_MODEL_MODEL_CODE);
        ScenarioContext.Current().put(Keys.RESPONSE, this.firmwareManagementClient.removeDeviceModel(request));
    }

    private void createDeviceModel(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        final AddDeviceModelRequest request = new AddDeviceModelRequest();
        final DeviceModel deviceModel = new DeviceModel();
        deviceModel.setDescription(Defaults.DEFAULT_DEVICE_MODEL_DESCRIPTION);
        deviceModel.setManufacturer(Defaults.MANUFACTURER_CODE);
        deviceModel.setMetered(Defaults.DEFAULT_DEVICE_MODEL_METERED);
        deviceModel.setModelCode(Defaults.DEVICE_MODEL_MODEL_CODE);
        request.setDeviceModel(deviceModel);
        ScenarioContext.Current().put(Keys.RESPONSE, this.firmwareManagementClient.addDeviceModel(request));
    }

    private void getDeviceModels(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        ScenarioContext.Current().put(Keys.RESPONSE,
                this.firmwareManagementClient.findAllDeviceModels(new FindAllDeviceModelsRequest()));
    }

    private void updateDeviceProtocol(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        final UpdateDeviceProtocolRequest request = new UpdateDeviceProtocolRequest();
        request.setDeviceIdentification(Defaults.DEFAULT_DEVICE_IDENTIFICATION);
        final ProtocolInfo protocolInfo = new ProtocolInfo();
        protocolInfo.setProtocol(Defaults.DEFAULT_PROTOCOL);
        protocolInfo.setProtocolVersion(Defaults.DEFAULT_PROTOCOL_VERSION);
        request.setProtocolInfo(protocolInfo);
        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.updateDeviceProtocol(request));
    }

    private void getProtocolInfos(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        ScenarioContext.Current().put(Keys.RESPONSE,
                this.adminDeviceManagementClient.getProtocolInfos(new GetProtocolInfosRequest()));
    }

    private void deactivateDevice(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final DeactivateDeviceRequest request = new DeactivateDeviceRequest();
        request.setDeviceIdentification(Defaults.DEFAULT_DEVICE_IDENTIFICATION);
        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.deactivateDevice(request));
    }

    private void getManufacturers(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        ScenarioContext.Current().put(Keys.RESPONSE,
                this.adminDeviceManagementClient.findAllManufacturers(new FindAllManufacturersRequest()));
    }

    private void changeManufacturer(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        final ChangeManufacturerRequest request = new ChangeManufacturerRequest();
        final Manufacturer manufacturer = new Manufacturer();
        manufacturer.setManufacturerId(Defaults.MANUFACTURER_CODE);
        manufacturer.setName(Defaults.MANUFACTURER_NAME);
        manufacturer.setUsePrefix(Defaults.MANUFACTURER_USE_PREFIX);
        request.setManufacturer(manufacturer);
        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.changeManufacturer(request));
    }

    private void removeManufacturer(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        final RemoveManufacturerRequest request = new RemoveManufacturerRequest();
        request.setManufacturerId(Defaults.MANUFACTURER_CODE);
        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.removeManufacturer(request));
    }

    private void createManufacturer(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        final AddManufacturerRequest request = new AddManufacturerRequest();
        final Manufacturer manufacturer = new Manufacturer();
        manufacturer.setManufacturerId(Defaults.MANUFACTURER_CODE);
        manufacturer.setName(Defaults.MANUFACTURER_NAME);
        manufacturer.setUsePrefix(Defaults.MANUFACTURER_USE_PREFIX);
        request.setManufacturer(manufacturer);
        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.addManufacturer(request));
    }

    private void findScheduledTasks(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        final FindScheduledTasksRequest request = new FindScheduledTasksRequest();
        request.setDeviceIdentification(Defaults.DEFAULT_DEVICE_IDENTIFICATION);
        ScenarioContext.Current().put(Keys.RESPONSE, this.coreDeviceManagementClient.findScheduledTasks(request));
    }

}