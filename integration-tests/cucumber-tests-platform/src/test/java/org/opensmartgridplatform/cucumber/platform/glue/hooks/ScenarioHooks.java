/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.database.CoreDatabase;
import org.springframework.beans.factory.annotation.Autowired;

/** Class with all the scenario hooks when each scenario runs. */
public class ScenarioHooks extends GlueBase {

  @Autowired private CoreDatabase databaseSteps;

  /**
   * Executed after each scenario.
   *
   * <p>Order 99999 ensures this will be run as one of the first hooks after the scenario.
   */
  @After(order = 99999)
  public void afterScenario() {
    // Destroy scenario context as the scenario is finished.
    ScenarioContext.context = null;
  }

  /**
   * Executed before each scenario.
   *
   * <p>Remove all stuff from the database before each test. Each test should stand on its own.
   * Therefore you should guarantee that the scenario is complete.
   *
   * <p>Order 0 ensures this will be run as first hook.
   */
  @Before(order = 0)
  public void beforeScenario() {
    this.databaseSteps.prepareDatabaseForScenario();
    this.databaseSteps.removeLeftOvers();

    this.databaseSteps.insertDefaultData();

    // Make sure that the scenario context is clean before each test.
    ScenarioContext.context = null;
  }
}
