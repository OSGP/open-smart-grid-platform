/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.publiclighting.database.Database;
import org.opensmartgridplatform.cucumber.platform.publiclighting.database.OslpDatabase;
import org.opensmartgridplatform.cucumber.platform.publiclighting.database.WsCoreDatabasePublicLightinng;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/** Class with all the scenario hooks when each scenario runs. */
public class ScenarioHooks {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioHooks.class);

  @Autowired private Database database;
  @Autowired private WsCoreDatabasePublicLightinng wsCoreDatabasePublicLightinng;

  @Autowired private OslpDatabase oslpDatabase;

  /**
   * Executed after each scenario.
   *
   * <p>Order 1000 ensures this will be run as one of the first hooks after the scenario.
   */
  @After(order = 1000)
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
   * <p>Order 1000 ensures this will be run as one of the first hooks before the scenario.
   */
  @Before(order = 1000)
  public void beforeScenario() {
    this.database.preparePublicLightingDatabaseForScenario();
    // this.database.prepareTariffSwitchingDatabaseForScenario();

    this.oslpDatabase.prepareDatabaseForScenario();
    if (!this.oslpDatabase.isOslpDeviceTableEmpty()
        || !this.oslpDatabase.isPendingSetScheduleRequestEmpty()) {
      LOGGER.warn(
          "OSLP device table or the 'pending set schedule request' table is not empty after inital delete! Trying once more...");
      this.oslpDatabase.prepareDatabaseForScenario();
    }

    // Make sure that the scenario context is clean before each test.
    ScenarioContext.context = null;
  }

  @Before(order = 1001)
  public void beforeScenarioAfterDefault() {
    this.wsCoreDatabasePublicLightinng.prepareDatabaseForScenario();
  }
}
