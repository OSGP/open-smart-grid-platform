// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering;

import java.util.List;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.support.ws.BaseClient;

/** Super class for Smartmetering general methods. */
public abstract class AbstractSmartMeteringSteps extends BaseClient {

  protected void checkAndSaveCorrelationId(final String correlationUid) {

    if (correlationUid == null) {
      throw new AssertionError("Correlation Uid should be given");
    }
    ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, correlationUid);
  }

  protected boolean checkDescription(final String description, final List<String> resultList) {

    if (description == null) {
      return true;
    }

    for (final String item : resultList) {
      if (!description.contains(item)) {
        return false;
      }
    }
    return true;
  }
}
