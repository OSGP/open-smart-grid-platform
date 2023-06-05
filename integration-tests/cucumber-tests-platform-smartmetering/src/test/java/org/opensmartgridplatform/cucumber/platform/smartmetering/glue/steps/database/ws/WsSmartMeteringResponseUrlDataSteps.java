// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.ws.ResponseUrlDataSteps;
import org.springframework.beans.factory.annotation.Autowired;

public class WsSmartMeteringResponseUrlDataSteps extends ResponseUrlDataSteps {

  @Autowired
  public WsSmartMeteringResponseUrlDataSteps(
      final WsSmartMeteringResponseUrlDataRepository responseUrlDataRepository) {
    super(responseUrlDataRepository);
  }

  @Given("^a response url data record in ws-smartmetering$")
  public void aResponseUrlDataRecordInWsSmartMetering(final Map<String, String> settings)
      throws Throwable {
    this.aResponseUrlDataRecord(settings);
  }

  @Then("^the response url data in ws-smartmetering has values$")
  public void theResponseUrlDataInWsSmartMeteringHasValues(final Map<String, String> settings)
      throws Throwable {
    this.theResponseUrlDataHasValues(settings);
  }

  @Then(
      "^the response url data record in ws-smartmetering with correlation uid \\\"(.*)\\\" should be deleted$")
  public void theResponseUrlDataRecordInWsSmartMeteringShouldBeDeleted(
      final String correlationUid) {
    this.theResponseUrlDataRecordShouldBeDeleted(correlationUid);
  }

  @Then(
      "^the response url data record in ws-smartmetering with correlation uid \\\"(.*)\\\" should not be deleted$")
  public void theResponseUrlDataRecordInWsSmartMeteringShouldNotBeDeleted(
      final String correlationUid) {
    this.theResponseUrlDataRecordShouldNotBeDeleted(correlationUid);
  }
}
