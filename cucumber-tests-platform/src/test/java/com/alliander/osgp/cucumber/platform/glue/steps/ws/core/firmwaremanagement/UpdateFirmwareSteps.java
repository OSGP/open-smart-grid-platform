/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.core.firmwaremanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.Firmware;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.cucumber.platform.support.ws.core.CoreFirmwareManagementClient;
import com.alliander.osgp.domain.core.repositories.FirmwareRepository;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the firmware requests steps
 */
public class UpdateFirmwareSteps {

    @Autowired
    private CoreFirmwareManagementClient client;

    @Autowired
    private FirmwareRepository firmwareRepo;

    /**
     * Sends a Update Firmware request to the platform for a given device
     * identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving an update firmware request$")
    public void receivingAnUpdateFirmwareRequest(final Map<String, String> requestParameters) throws Throwable {

        final ChangeFirmwareRequest request = new ChangeFirmwareRequest();

        long firmwareId = 0;
        if (this.firmwareRepo.findAll() != null && this.firmwareRepo.count() > 0) {
            firmwareId = this.firmwareRepo.findAll().get(0).getId();
        }

        request.setId((int) firmwareId);

        request.setFirmware(this.createAndGetFirmware(firmwareId, requestParameters));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.changeFirmware(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    private Firmware createAndGetFirmware(final long firmwareId, final Map<String, String> requestParameters)
            throws Throwable {
        final Firmware firmware = new Firmware();
        firmware.setId((int) firmwareId);
        firmware.setFilename(getString(requestParameters, Keys.FIRMWARE_FILENAME, ""));
        firmware.setDescription(getString(requestParameters, Keys.FIRMWARE_DESCRIPTION, ""));
        firmware.setPushToNewDevices(
                getBoolean(requestParameters, Keys.FIRMWARE_PUSH_TO_NEW_DEVICES, Defaults.FIRMWARE_PUSH_TO_NEW_DEVICE));
        firmware.setFirmwareModuleData(new FirmwareModuleData());
        firmware.setManufacturer(getString(requestParameters, Keys.MANUFACTURER_NAME, Defaults.MANUFACTURER_NAME));
        firmware.setModelCode(
                getString(requestParameters, Keys.DEVICEMODEL_MODELCODE, Defaults.DEVICE_MODEL_MODEL_CODE));
        return firmware;
    }

    @Then("^the update firmware response contains$")
    public void theUpdateFirmwareResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        final ChangeFirmwareResponse response = (ChangeFirmwareResponse) ScenarioContext.Current().get(Keys.RESPONSE);

        Assert.assertEquals(getEnum(expectedResponseData, Keys.KEY_RESULT, OsgpResultType.class), response.getResult());
    }

    @Then("^the update firmware response contains soap fault$")
    public void theUpdateFirmwareResponseContainsSoapFault(final Map<String, String> expectedResponseData)
            throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResponseData);
    }
}