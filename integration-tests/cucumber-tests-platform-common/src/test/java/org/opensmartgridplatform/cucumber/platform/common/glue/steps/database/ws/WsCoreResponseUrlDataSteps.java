//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.ws.ResponseUrlDataSteps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class WsCoreResponseUrlDataSteps extends ResponseUrlDataSteps {

  @Autowired
  public WsCoreResponseUrlDataSteps(
      final WsCoreResponseUrlDataRepository responseUrlDataRepository) {
    super(responseUrlDataRepository);
  }

  @Given("^a response url data record in ws-core")
  @Transactional("txMgrWsCore")
  public void aResponseUrlDataRecordInWsCore(final Map<String, String> settings) throws Throwable {
    this.aResponseUrlDataRecord(settings);
  }

  @Then("^the response url data in ws-core has values$")
  public void theResponseUrlDataInWsCoreHasValues(final Map<String, String> settings)
      throws Throwable {
    this.theResponseUrlDataHasValues(settings);
  }

  @Then(
      "^the response url data record in ws-core with correlation uid \\\"(.*)\\\" should be deleted$")
  public void theResponseUrlDataRecordInWsCoreShouldBeDeleted(final String correlationUid) {
    this.theResponseUrlDataRecordShouldBeDeleted(correlationUid);
  }

  @Override
  @Then(
      "^the response url data record in ws-core with correlation uid \\\"(.*)\\\" should not be deleted$")
  public void theResponseUrlDataRecordShouldNotBeDeleted(final String correlationUid) {
    this.theResponseUrlDataRecordShouldNotBeDeleted(correlationUid);
  }
}
