/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
