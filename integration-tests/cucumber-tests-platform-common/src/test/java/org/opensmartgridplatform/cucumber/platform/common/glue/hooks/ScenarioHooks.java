// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.common.glue.database.WsCoreNotificationDatabase;
import org.springframework.beans.factory.annotation.Autowired;

/** Class with all the scenario hooks when each scenario runs. */
public class ScenarioHooks {

  @Autowired private WsCoreNotificationDatabase wsCoreNotificationDatabase;

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
    this.wsCoreNotificationDatabase.prepareDatabaseForScenario();
  }

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
}
