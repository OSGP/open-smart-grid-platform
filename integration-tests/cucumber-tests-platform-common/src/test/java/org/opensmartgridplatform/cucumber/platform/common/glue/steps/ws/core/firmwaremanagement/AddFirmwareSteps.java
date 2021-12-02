/*
 * Copyright 2021 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.firmwaremanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getHexDecoded;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.AddFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.AddFirmwareResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.Firmware;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreFirmwareManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the firmware requests steps */
public class AddFirmwareSteps extends FirmwareSteps {

  @Autowired private CoreFirmwareManagementClient client;

  /**
   * Sends a Add Firmware request to the platform.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable
   */
  @When("^receiving an add firmware request$")
  public void receivingAnAddFirmwareRequest(final Map<String, String> requestParameters)
      throws Throwable {

    final AddFirmwareRequest request = new AddFirmwareRequest();

    final Firmware firmware = this.createAndGetFirmware(requestParameters);
    if (requestParameters.containsKey(PlatformKeys.FIRMWARE_FILE)) {
      firmware.setFile(getHexDecoded(requestParameters, PlatformKeys.FIRMWARE_FILE, ""));
    }

    request.setFirmware(firmware);

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.addFirmware(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  @Then("^the add firmware response contains$")
  public void theAddFirmwareResponseContains(final Map<String, String> expectedResponseData)
      throws Throwable {
    final AddFirmwareResponse response =
        (AddFirmwareResponse) ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);

    assertThat(response.getResult())
        .isEqualTo(getEnum(expectedResponseData, PlatformKeys.KEY_RESULT, OsgpResultType.class));
  }

  @Then("^the add firmware response contains soap fault$")
  public void theAddFirmwareResponseContainsSoapFault(
      final Map<String, String> expectedResponseData) throws Throwable {
    GenericResponseSteps.verifySoapFault(expectedResponseData);
  }
}
