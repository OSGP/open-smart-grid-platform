/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.glue.steps.ws.core.firmwaremanagement;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getBoolean;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getEnum;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.Firmware;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.common.PlatformCommonKeys;
import com.alliander.osgp.cucumber.platform.common.support.ws.core.CoreFirmwareManagementClient;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.domain.core.repositories.FirmwareFileRepository;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the firmware requests steps
 */
public class ChangeFirmwareSteps {

    @Autowired
    private CoreFirmwareManagementClient client;

    @Autowired
    private FirmwareFileRepository firmwareFileRepository;

    /**
     * Sends a Change Firmware request to the platform for a given device
     * identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving an change firmware request$")
    public void receivingAnChangeFirmwareRequest(final Map<String, String> requestParameters) throws Throwable {

        final ChangeFirmwareRequest request = new ChangeFirmwareRequest();

        long firmwareFileId = 0;
        if (this.firmwareFileRepository.findAll() != null && this.firmwareFileRepository.count() > 0) {
            firmwareFileId = this.firmwareFileRepository.findAll().get(0).getId();
        }

        request.setId((int) firmwareFileId);

        request.setFirmware(this.createAndGetFirmware(firmwareFileId, requestParameters));

        try {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.changeFirmware(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
        }
    }

    private Firmware createAndGetFirmware(final long firmwareFileId, final Map<String, String> requestParameters)
            throws Throwable {
        final Firmware firmware = new Firmware();
        firmware.setId((int) firmwareFileId);
        firmware.setFilename(getString(requestParameters, PlatformKeys.FIRMWARE_FILE_FILENAME, ""));
        firmware.setDescription(getString(requestParameters, PlatformKeys.FIRMWARE_DESCRIPTION, ""));
        firmware.setPushToNewDevices(getBoolean(requestParameters, PlatformKeys.FIRMWARE_PUSH_TO_NEW_DEVICES,
                PlatformDefaults.FIRMWARE_PUSH_TO_NEW_DEVICE));
        firmware.setFirmwareModuleData(new FirmwareModuleData());
        firmware.setManufacturer(getString(requestParameters, PlatformKeys.MANUFACTURER_NAME,
                PlatformDefaults.DEFAULT_MANUFACTURER_NAME));
        firmware.setModelCode(getString(requestParameters, PlatformKeys.DEVICEMODEL_MODELCODE,
                PlatformDefaults.DEVICE_MODEL_MODEL_CODE));
        return firmware;
    }

    @Then("^the change firmware response contains$")
    public void theChangeFirmwareResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        final ChangeFirmwareResponse response = (ChangeFirmwareResponse) ScenarioContext.current()
                .get(PlatformCommonKeys.RESPONSE);

        Assert.assertEquals(getEnum(expectedResponseData, PlatformKeys.KEY_RESULT, OsgpResultType.class),
                response.getResult());
    }

    @Then("^the change firmware response contains soap fault$")
    public void theChangeFirmwareResponseContainsSoapFault(final Map<String, String> expectedResponseData)
            throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResponseData);
    }
}