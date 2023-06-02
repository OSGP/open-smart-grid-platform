//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
