// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.glue.steps.common;

import io.cucumber.java.en.Given;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;

public class TimeoutSteps {

  @Given("^a timeout of \"([^\"]*)\" seconds$")
  public void aTimeoutOfSeconds(final String seconds) {
    ScenarioContext.current().put(PlatformKeys.TIMEOUT, seconds);
  }
}
