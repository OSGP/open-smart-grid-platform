//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.core.config;

import org.opensmartgridplatform.cucumber.core.Keys;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/** Base class for the application configuration. */
public abstract class BaseApplicationConfiguration extends AbstractConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseApplicationConfiguration.class);

  @Value("${timeout}")
  private Integer timeout;

  @Value("${sleeptime}")
  private Integer sleeptime;

  @Value("${tempdir}")
  private String tempdir;

  /**
   * Gets the timeout. Either from the configuration (in war or
   * /etc/osp/test/global-cucumber.properties), or from the scenariocontext (for a specific test).
   *
   * @return An integer representing the timeout.
   */
  public Integer getTimeout() {

    Integer retval = this.timeout;

    // For certain scenario's it is necessary to enlarge the timeout.
    if (ScenarioContext.current().get(Keys.TIMEOUT) != null) {
      retval = Integer.parseInt(ScenarioContext.current().get(Keys.TIMEOUT).toString());
    }

    LOGGER.debug("Using timeout [{}] seconds in the tests.", retval);

    return retval;
  }

  public Integer getSleepTime() {
    return this.sleeptime;
  }

  public String getTempDir() {
    return this.tempdir;
  }
}
