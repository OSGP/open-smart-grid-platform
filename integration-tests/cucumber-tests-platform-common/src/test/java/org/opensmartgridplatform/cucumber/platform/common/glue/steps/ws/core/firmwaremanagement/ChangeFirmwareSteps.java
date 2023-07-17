// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.firmwaremanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.ChangeFirmwareResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.ChangeableFirmware;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreFirmwareManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the firmware requests steps */
public class ChangeFirmwareSteps extends FirmwareSteps {

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

    final ChangeableFirmware firmware = this.createAndGetChangeableFirmware(requestParameters);
    request.setFirmware(firmware);

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.changeFirmware(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
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
