//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.glue.steps.common;

import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitSteps {

  private static final Logger LOGGER = LoggerFactory.getLogger(WaitSteps.class);

  @Then("^I wait (\\d+) seconds$")
  public void iWaitXSeconds(final Integer seconds) {
    try {
      Thread.sleep(seconds * 1000);
    } catch (final InterruptedException e) {
      LOGGER.error("Caught InterruptedException", e);
      Thread.currentThread().interrupt();
    }
  }
}
