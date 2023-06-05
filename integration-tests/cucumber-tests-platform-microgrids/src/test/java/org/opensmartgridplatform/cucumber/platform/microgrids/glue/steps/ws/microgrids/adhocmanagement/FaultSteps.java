// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.ws.microgrids.adhocmanagement;

import io.cucumber.java.en.Then;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement.AdHocManagementClient;
import org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement.GetDataRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class FaultSteps {

  @Autowired private AdHocManagementClient client;

  @Then("^a SOAP fault should be returned$")
  public void aSoapFaultShouldBeReturned(final Map<String, String> responseParameters)
      throws Throwable {

    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
    final Map<String, String> extendedParameters =
        SettingsHelper.addDefault(
            responseParameters, PlatformKeys.KEY_CORRELATION_UID, correlationUid);

    final GetDataAsyncRequest getDataAsyncRequest =
        GetDataRequestBuilder.fromParameterMapAsync(extendedParameters);

    try {
      final GetDataResponse response = this.client.getData(getDataAsyncRequest);
      Assertions.fail(
          "Expected a SOAP fault, but got a GetDataResponse with result "
              + response.getResult().value()
              + ".");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }

    GenericResponseSteps.verifySoapFault(responseParameters);
  }
}
