/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.firmwaremanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.Firmware;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleData;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreFirmwareManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the firmware requests steps */
public class ChangeFirmwareSteps {

  @Autowired private CoreFirmwareManagementClient client;

  @Autowired private FirmwareFileRepository firmwareFileRepository;

  /**
   * Sends a Change Firmware request to the platform for a given device identification.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable
   */
  @When("^receiving an change firmware request$")
  public void receivingAnChangeFirmwareRequest(final Map<String, String> requestParameters)
      throws Throwable {

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

  private Firmware createAndGetFirmware(
      final long firmwareFileId, final Map<String, String> requestParameters) throws Throwable {
    final Firmware firmware = new Firmware();
    firmware.setId((int) firmwareFileId);
    firmware.setFilename(getString(requestParameters, PlatformKeys.FIRMWARE_FILE_FILENAME, ""));
    firmware.setDescription(getString(requestParameters, PlatformKeys.FIRMWARE_DESCRIPTION, ""));
    firmware.setPushToNewDevices(
        getBoolean(
            requestParameters,
            PlatformKeys.FIRMWARE_PUSH_TO_NEW_DEVICES,
            PlatformDefaults.FIRMWARE_PUSH_TO_NEW_DEVICE));
    firmware.setFirmwareModuleData(new FirmwareModuleData());
    firmware.setManufacturer(
        getString(
            requestParameters,
            PlatformKeys.MANUFACTURER_NAME,
            PlatformDefaults.DEFAULT_MANUFACTURER_NAME));
    firmware.setModelCode(
        getString(
            requestParameters,
            PlatformKeys.DEVICEMODEL_MODELCODE,
            PlatformDefaults.DEVICE_MODEL_MODEL_CODE));
    return firmware;
  }

  @Then("^the change firmware response contains$")
  public void theChangeFirmwareResponseContains(final Map<String, String> expectedResponseData)
      throws Throwable {
    final ChangeFirmwareResponse response =
        (ChangeFirmwareResponse) ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);

    assertThat(response.getResult())
        .isEqualTo(getEnum(expectedResponseData, PlatformKeys.KEY_RESULT, OsgpResultType.class));
  }

  @Then("^the change firmware response contains soap fault$")
  public void theChangeFirmwareResponseContainsSoapFault(
      final Map<String, String> expectedResponseData) throws Throwable {
    GenericResponseSteps.verifySoapFault(expectedResponseData);
  }
}
