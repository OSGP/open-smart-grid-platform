// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.simulator;

import io.cucumber.java.en.Then;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefinableLoadProfileSteps {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefinableLoadProfileSteps.class);

  @Then("^the Definable Load Profile of \"([^\"]*)\" contains$")
  public void theDefinableLoadProfileOfContains(
      final String deviceIdentification, final Map<String, String> settings) throws Throwable {

    LOGGER.info(
        "Ignoring verification of Definable Load Profile configuration on simulator."
            + " SimulatorTriggerClient and DlmsAttributeValuesResources should be improved to be able to deal with non-integer"
            + " attribute values like the capture object definition array before this can be implemented.");
  }
}
