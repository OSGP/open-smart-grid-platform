/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.basicosgpfunctions;

import io.cucumber.java.en.When;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;

/** Class with all the AuthorizeDeviceFunctions steps */
public class ProtocolSequenceNumberSteps {

  @When("^the device adds \"([^\"]*)\" to the sequencenumber in the \"([^\"]*)\" response$")
  public void receivingAConfirmRequest(final Integer number, final String protocol)
      throws Throwable {
    ScenarioContext.current().put(PlatformKeys.NUMBER_TO_ADD_TO_SEQUENCE_NUMBER, number);
  }
}
