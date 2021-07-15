/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
