//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.glue.steps.ws;

import io.cucumber.java.en.Then;
import java.util.Map;

public class FaultSteps {

  @Then("^a SOAP fault should have been returned$")
  public void aSoapFaultShouldHaveBeenReturned(final Map<String, String> responseParameters)
      throws Throwable {

    GenericResponseSteps.verifySoapFault(responseParameters);
  }
}
