// Copyright 2021 Smart Society Services B.V.
// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.firmwaremanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.AddOrChangeFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.AddOrChangeFirmwareResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.Firmware;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreFirmwareManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the firmware requests steps */
public class AddOrChangeFirmwareSteps extends FirmwareSteps {

  @Autowired private CoreFirmwareManagementClient client;

  /**
   * Sends a Add or Change Firmware request to the platform.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable
   */
  @When("^receiving an add or change firmware request$")
  public void receivingAnAddOrChangeFirmwareRequest(final Map<String, String> requestParameters)
      throws Throwable {

    final AddOrChangeFirmwareRequest request = new AddOrChangeFirmwareRequest();

    final Firmware firmware = this.createAndGetFirmware(requestParameters);

    request.setFirmware(firmware);

    try {
      ScenarioContext.current()
          .put(PlatformKeys.RESPONSE, this.client.addOrChangeFirmware(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  @Then("^the add or change firmware response contains$")
  public void theAddOrChangeFirmwareResponseContains(final Map<String, String> expectedResponseData)
      throws Throwable {
    final AddOrChangeFirmwareResponse response =
        (AddOrChangeFirmwareResponse) ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);

    assertThat(response.getResult())
        .isEqualTo(getEnum(expectedResponseData, PlatformKeys.KEY_RESULT, OsgpResultType.class));
  }

  @Then("^the add or change firmware response contains soap fault$")
  public void theAddOrChangeFirmwareResponseContainsSoapFault(
      final Map<String, String> expectedResponseData) throws Throwable {
    GenericResponseSteps.verifySoapFault(expectedResponseData);
  }

  @Then("the firmware file {string} exists")
  public void theFirmwareFileExists(
      final String identification, final Map<String, String> firmwareFileProperties)
      throws Throwable {
    this.assertFirmwareFileExists(identification, firmwareFileProperties);
  }

  @Then("the firmware file {string} has module versions")
  public void theFirmwareFileHasModuleVersions(
      final String identification, final Map<String, String> expectedmoduleVersions)
      throws Throwable {
    this.assertFirmwareFileHasModuleVersions(identification, expectedmoduleVersions);
  }

  @Then("the firmware file {string} has device models")
  public void theFirmwareFileHasDeviceModels(
      final String identification, final Map<String, String> expectedDeviceModels)
      throws Throwable {
    this.assertFirmwareFileHasDeviceModels(identification, expectedDeviceModels);
  }
}
